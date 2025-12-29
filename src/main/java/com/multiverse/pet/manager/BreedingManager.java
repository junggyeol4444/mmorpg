package com.multiverse.pet.manager;

import com. multiverse.pet. PetCore;
import com. multiverse.pet. api.event.PetBreedingCompleteEvent;
import com.multiverse. pet.api.event.PetBreedingStartEvent;
import com.multiverse.pet.model.Pet;
import com. multiverse.pet. model.PetRarity;
import com.multiverse.pet.model.PetSpecies;
import com.multiverse. pet.model.PetStatus;
import com.multiverse. pet.model.PetType;
import com. multiverse.pet. model.breeding.BreedingStatus;
import com.multiverse.pet.model.breeding. PetBreeding;
import com.multiverse.pet.model.breeding.PetGenetics;
import com.multiverse.pet.util.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;
import org.bukkit. scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 교배 매니저 클래스
 * 펫 교배 시작, 진행, 완료 관리
 */
public class BreedingManager {

    private final PetCore plugin;

    // 진행 중인 교배 (교배ID -> 교배 데이터)
    private final Map<UUID, PetBreeding> activeBreedings;

    // 플레이어별 진행 중인 교배
    private final Map<UUID, List<UUID>> playerBreedings;

    // 교배 타이머
    private BukkitTask breedingTask;

    // 설정 값
    private long baseBreedingTime;
    private double baseBreedingCost;
    private double mutationBaseChance;
    private int maxConcurrentBreedings;
    private int breedingCooldown;
    private int minBreedingLevel;
    private double minBreedingHappiness;
    private boolean allowCrossSpeciesBreeding;

    /**
     * 생성자
     */
    public BreedingManager(PetCore plugin) {
        this.plugin = plugin;
        this.activeBreedings = new ConcurrentHashMap<>();
        this.playerBreedings = new ConcurrentHashMap<>();
        loadSettings();
        startBreedingTask();
    }

    /**
     * 설정 로드
     */
    private void loadSettings() {
        this.baseBreedingTime = plugin.getBreedingSettings().getBaseBreedingTime();
        this.baseBreedingCost = plugin.getBreedingSettings().getBaseCost();
        this.mutationBaseChance = plugin.getBreedingSettings().getMutationChance();
        this.maxConcurrentBreedings = plugin.getBreedingSettings().getMaxConcurrent();
        this.breedingCooldown = plugin.getBreedingSettings().getCooldown();
        this.minBreedingLevel = plugin. getBreedingSettings().getMinLevel();
        this.minBreedingHappiness = plugin.getBreedingSettings().getMinHappiness();
        this.allowCrossSpeciesBreeding = plugin. getBreedingSettings().isAllowCrossSpecies();
    }

    /**
     * 교배 타이머 시작
     */
    private void startBreedingTask() {
        breedingTask = Bukkit.getScheduler().runTaskTimer(plugin, this::checkBreedings, 20L, 20L);
    }

    /**
     * 교배 타이머 중지
     */
    public void stopBreedingTask() {
        if (breedingTask != null && !breedingTask.isCancelled()) {
            breedingTask.cancel();
        }
    }

    // ===== 교배 시작 =====

    /**
     * 교배 시작
     *
     * @param player 플레이어
     * @param pet1 부모 펫 1
     * @param pet2 부모 펫 2
     * @return 교배 성공 여부
     */
    public boolean startBreeding(Player player, Pet pet1, Pet pet2) {
        UUID playerId = player.getUniqueId();

        // 동일 펫 체크
        if (pet1.getPetId().equals(pet2.getPetId())) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.same-pet"));
            return false;
        }

        // 동시 교배 수 제한
        int currentBreedings = getPlayerBreedingCount(playerId);
        if (currentBreedings >= maxConcurrentBreedings) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.max-concurrent")
                    . replace("{max}", String.valueOf(maxConcurrentBreedings)));
            return false;
        }

        // 펫 상태 확인
        if (! canBreed(pet1)) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.pet-cannot-breed")
                    .replace("{name}", pet1.getPetName())
                    .replace("{reason}", getCannotBreedReason(pet1)));
            return false;
        }

        if (!canBreed(pet2)) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.pet-cannot-breed")
                    .replace("{name}", pet2.getPetName())
                    .replace("{reason}", getCannotBreedReason(pet2)));
            return false;
        }

        // 종족 호환성 확인
        if (!areCompatible(pet1, pet2)) {
            MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("breeding. incompatible-species"));
            return false;
        }

        // 쿨다운 확인
        if (isOnBreedingCooldown(pet1)) {
            long remaining = getBreedingCooldownRemaining(pet1);
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.cooldown")
                    .replace("{name}", pet1.getPetName())
                    .replace("{time}", formatTime(remaining)));
            return false;
        }

        if (isOnBreedingCooldown(pet2)) {
            long remaining = getBreedingCooldownRemaining(pet2);
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.cooldown")
                    .replace("{name}", pet2.getPetName())
                    .replace("{time}", formatTime(remaining)));
            return false;
        }

        // 비용 계산
        double cost = calculateBreedingCost(pet1, pet2);

        // 비용 확인
        if (! plugin.getPlayerDataCoreHook().hasGold(playerId, cost)) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.not-enough-gold")
                    .replace("{cost}", String.format("%.0f", cost)));
            return false;
        }

        // 교배 시간 계산
        long duration = calculateBreedingDuration(pet1, pet2);

        // 교배 데이터 생성
        PetBreeding breeding = new PetBreeding(playerId, pet1.getPetId(), pet2.getPetId(), duration);
        breeding.setParent1SpeciesId(pet1.getSpeciesId());
        breeding.setParent2SpeciesId(pet2.getSpeciesId());
        breeding.setGoldCost(cost);

        // 이벤트 발생
        PetBreedingStartEvent event = new PetBreedingStartEvent(player, pet1, pet2, breeding);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        // 비용 차감
        plugin.getPlayerDataCoreHook().withdrawGold(playerId, cost);

        // 펫 상태 변경
        pet1.setStatus(PetStatus. BREEDING);
        pet2.setStatus(PetStatus. BREEDING);

        // 활성 펫이면 해제
        if (pet1.isActive()) {
            plugin.getPetManager().unsummonPet(player, pet1.getPetId());
        }
        if (pet2.isActive()) {
            plugin.getPetManager().unsummonPet(player, pet2.getPetId());
        }

        // 교배 등록
        activeBreedings.put(breeding.getBreedingId(), breeding);
        playerBreedings.computeIfAbsent(playerId, k -> new ArrayList<>()).add(breeding.getBreedingId());

        // 데이터 저장
        plugin.getPetManager().savePetData(playerId, pet1);
        plugin.getPetManager().savePetData(playerId, pet2);

        // 알림
        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.started")
                .replace("{pet1}", pet1.getPetName())
                .replace("{pet2}", pet2.getPetName())
                .replace("{time}", formatTime(duration)));

        return true;
    }

    /**
     * 교배 가능 여부 확인
     */
    public boolean canBreed(Pet pet) {
        if (pet == null) return false;
        if (pet.getStatus() != PetStatus.STORED) return false;
        if (pet. getLevel() < minBreedingLevel) return false;
        if (pet.getHappiness() < minBreedingHappiness) return false;
        if (pet. getHealth() <= 0) return false;
        return true;
    }

    /**
     * 교배 불가 이유 반환
     */
    public String getCannotBreedReason(Pet pet) {
        if (pet == null) return "펫을 찾을 수 없습니다. ";
        if (pet.getStatus() != PetStatus. STORED) return "보관 상태가 아닙니다.  (" + pet.getStatus().getDisplayName() + ")";
        if (pet.getLevel() < minBreedingLevel) return "레벨이 부족합니다.  (필요:  " + minBreedingLevel + ")";
        if (pet.getHappiness() < minBreedingHappiness) return "행복도가 부족합니다.  (필요: " + minBreedingHappiness + ")";
        if (pet.getHealth() <= 0) return "체력이 0입니다. ";
        return null;
    }

    /**
     * 종족 호환성 확인
     */
    public boolean areCompatible(Pet pet1, Pet pet2) {
        // 동일 종족이면 항상 호환
        if (pet1.getSpeciesId().equals(pet2.getSpeciesId())) {
            return true;
        }

        // 크로스 교배 허용 여부
        if (! allowCrossSpeciesBreeding) {
            return false;
        }

        // 동일 타입이면 호환
        if (pet1.getType() == pet2.getType()) {
            return true;
        }

        // 종족 데이터에서 호환 종족 확인
        PetSpecies species1 = plugin.getSpeciesCache().getSpecies(pet1.getSpeciesId());
        if (species1 != null && species1.canBreedWith(pet2.getSpeciesId())) {
            return true;
        }

        return false;
    }

    // ===== 교배 진행 확인 =====

    /**
     * 교배 진행 확인 (틱마다 호출)
     */
    private void checkBreedings() {
        Iterator<Map.Entry<UUID, PetBreeding>> iterator = activeBreedings.entrySet().iterator();

        while (iterator. hasNext()) {
            Map.Entry<UUID, PetBreeding> entry = iterator.next();
            PetBreeding breeding = entry.getValue();

            if (breeding. isTimeCompleted() && breeding.isInProgress()) {
                // 교배 완료 처리
                completeBreeding(breeding);
            }
        }
    }

    /**
     * 교배 완료 처리
     */
    private void completeBreeding(PetBreeding breeding) {
        UUID ownerId = breeding. getOwnerId();
        Player owner = Bukkit.getPlayer(ownerId);

        // 부모 펫 가져오기
        Pet parent1 = plugin.getPetManager().getPetById(ownerId, breeding.getParent1Id());
        Pet parent2 = plugin. getPetManager().getPetById(ownerId, breeding.getParent2Id());

        if (parent1 == null || parent2 == null) {
            breeding.fail("부모 펫을 찾을 수 없습니다.");
            cleanupBreeding(breeding);
            return;
        }

        // 자손 생성
        Pet offspring = createOffspring(parent1, parent2, breeding);

        if (offspring == null) {
            breeding.fail("자손 생성에 실패했습니다.");
            cleanupBreeding(breeding);
            return;
        }

        // 유전 정보 생성
        PetGenetics genetics = new PetGenetics(parent1, parent2);

        // 변이 확인
        double mutationChance = genetics.calculateMutationChance(mutationBaseChance);
        if (Math.random() * 100 < mutationChance) {
            String mutationType = PetGenetics.generateRandomMutationType();
            double mutationBonus = PetGenetics.getMutationBonusValue(mutationType);
            genetics.applyMutation(mutationType, mutationBonus);
            breeding.setMutation(true);
            breeding.setMutationType(mutationType);
        }

        // 유전 정보 적용
        offspring.setGenetics(genetics);
        applyGeneticsToOffspring(offspring, genetics);

        // 교배 완료 처리
        breeding. complete(offspring. getPetId(), genetics);
        breeding.setOffspringSpeciesId(offspring. getSpeciesId());
        breeding.setStatus(BreedingStatus. AWAITING_COLLECTION);

        // 부모 펫 상태 복구
        parent1.setStatus(PetStatus. STORED);
        parent2.setStatus(PetStatus. STORED);

        // 쿨다운 설정
        long cooldownEnd = System.currentTimeMillis() + (breedingCooldown * 1000L);
        parent1.setBreedingCooldownEnd(cooldownEnd);
        parent2.setBreedingCooldownEnd(cooldownEnd);

        // 저장
        plugin. getPetManager().savePetData(ownerId, parent1);
        plugin.getPetManager().savePetData(ownerId, parent2);

        // 이벤트 발생
        if (owner != null) {
            PetBreedingCompleteEvent event = new PetBreedingCompleteEvent(owner, parent1, parent2, offspring, breeding);
            Bukkit.getPluginManager().callEvent(event);

            // 알림
            String message = plugin.getConfigManager().getMessage("breeding.complete")
                    . replace("{pet1}", parent1.getPetName())
                    .replace("{pet2}", parent2.getPetName())
                    .replace("{offspring}", offspring. getPetName());

            if (breeding.isMutation()) {
                message += "\n" + plugin. getConfigManager().getMessage("breeding.mutation")
                        .replace("{type}", breeding.getMutationType());
            }

            MessageUtil.sendMessage(owner, message);
        }
    }

    /**
     * 자손 펫 생성
     */
    private Pet createOffspring(Pet parent1, Pet parent2, PetBreeding breeding) {
        Pet offspring = new Pet();
        offspring.setOwnerId(breeding.getOwnerId());

        // 종족 결정 (부모 중 랜덤 또는 특수 조합)
        String speciesId = determineOffspringSpecies(parent1, parent2);
        offspring. setSpeciesId(speciesId);

        // 종족 데이터 가져오기
        PetSpecies species = plugin. getSpeciesCache().getSpecies(speciesId);
        if (species == null) {
            return null;
        }

        // 기본 정보 설정
        offspring.setPetName(species.getName() + " 새끼");
        offspring.setType(species.getType());
        offspring. setEntityType(species.getEntityType());

        // 희귀도 결정 (부모 중 높은 쪽 또는 한 단계 낮은 것)
        PetRarity rarity = determineOffspringRarity(parent1.getRarity(), parent2.getRarity());
        offspring.setRarity(rarity);

        // 초기 레벨 1
        offspring.setLevel(1);
        offspring.setExperience(0);

        // 진화 단계 1
        offspring.setEvolutionStage(1);

        // 상태 초기화
        offspring.setStatus(PetStatus.STORED);
        offspring.setHealth(100);
        offspring. setMaxHealth(100);
        offspring.setHunger(100);
        offspring. setHappiness(100);

        // 기본 스탯 설정 (종족 기본값)
        offspring.setBaseStats(species. getAllStatsAtLevel(1));

        return offspring;
    }

    /**
     * 자손 종족 결정
     */
    private String determineOffspringSpecies(Pet parent1, Pet parent2) {
        // 동일 종족이면 그대로
        if (parent1.getSpeciesId().equals(parent2.getSpeciesId())) {
            return parent1.getSpeciesId();
        }

        // 다른 종족이면 랜덤
        return Math.random() < 0.5 ?  parent1.getSpeciesId() : parent2.getSpeciesId();
    }

    /**
     * 자손 희귀도 결정
     */
    private PetRarity determineOffspringRarity(PetRarity rarity1, PetRarity rarity2) {
        // 기본적으로 낮은 희귀도
        PetRarity baseRarity = rarity1.ordinal() < rarity2.ordinal() ? rarity1 : rarity2;

        // 10% 확률로 부모 중 높은 희귀도
        if (Math.random() < 0.1) {
            baseRarity = rarity1.ordinal() > rarity2.ordinal() ? rarity1 : rarity2;
        }

        // 5% 확률로 한 단계 상승
        if (Math.random() < 0.05) {
            baseRarity = baseRarity.getNextRarity();
        }

        return baseRarity;
    }

    /**
     * 유전 정보를 자손에 적용
     */
    private void applyGeneticsToOffspring(Pet offspring, PetGenetics genetics) {
        // 상속 스탯 적용
        for (Map.Entry<String, Double> entry : genetics. getInheritedStats().entrySet()) {
            offspring.setBaseStat(entry.getKey(), entry.getValue());
        }

        // 상속 스킬 적용
        for (String skillId : genetics.getInheritedSkills()) {
            plugin.getSkillManager().unlockSkill(offspring, skillId);
        }

        // 세대 설정
        // (Pet 클래스에 generation 필드 필요 시 추가)

        // 변이 보너스 적용 (이미 genetics에서 처리됨)
    }

    // ===== 교배 수령 =====

    /**
     * 교배 결과 수령
     */
    public Pet collectOffspring(Player player, UUID breedingId) {
        UUID playerId = player. getUniqueId();
        PetBreeding breeding = activeBreedings.get(breedingId);

        if (breeding == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.not-found"));
            return null;
        }

        if (!breeding.getOwnerId().equals(playerId)) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.not-owner"));
            return null;
        }

        if (breeding.getStatus() != BreedingStatus. AWAITING_COLLECTION) {
            if (breeding.isInProgress()) {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.still-in-progress")
                        .replace("{time}", breeding.getRemainingTimeFormatted()));
            } else {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.cannot-collect"));
            }
            return null;
        }

        // 자손 펫 가져오기 (캐시에서)
        Pet offspring = plugin.getPetCache().getPet(breeding.getOffspringId());

        if (offspring == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.offspring-not-found"));
            cleanupBreeding(breeding);
            return null;
        }

        // 보관함에 추가
        boolean added = plugin.getPetManager().addNewPet(playerId, offspring);
        if (! added) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("breeding.storage-full"));
            return null;
        }

        // 교배 정리
        cleanupBreeding(breeding);

        // 알림
        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.collected")
                .replace("{name}", offspring.getPetName())
                .replace("{species}", offspring.getSpeciesId())
                .replace("{rarity}", offspring.getRarity().getDisplayName()));

        return offspring;
    }

    // ===== 교배 취소 =====

    /**
     * 교배 취소
     */
    public boolean cancelBreeding(Player player, UUID breedingId) {
        UUID playerId = player.getUniqueId();
        PetBreeding breeding = activeBreedings.get(breedingId);

        if (breeding == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.not-found"));
            return false;
        }

        if (!breeding. getOwnerId().equals(playerId)) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.not-owner"));
            return false;
        }

        if (! breeding.getStatus().isCancellable()) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("breeding.cannot-cancel"));
            return false;
        }

        // 취소 처리
        breeding. cancel();

        // 부모 펫 상태 복구
        Pet parent1 = plugin.getPetManager().getPetById(playerId, breeding.getParent1Id());
        Pet parent2 = plugin.getPetManager().getPetById(playerId, breeding.getParent2Id());

        if (parent1 != null) {
            parent1.setStatus(PetStatus.STORED);
            plugin.getPetManager().savePetData(playerId, parent1);
        }

        if (parent2 != null) {
            parent2.setStatus(PetStatus.STORED);
            plugin.getPetManager().savePetData(playerId, parent2);
        }

        // 부분 환불
        double refund = breeding.getGoldCost() * breeding.getStatus().getRefundRate();
        if (refund > 0) {
            plugin.getPlayerDataCoreHook().depositGold(playerId, refund);
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("breeding.refunded")
                    . replace("{amount}", String.format("%.0f", refund)));
        }

        // 정리
        cleanupBreeding(breeding);

        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.cancelled"));

        return true;
    }

    /**
     * 교배 정리
     */
    private void cleanupBreeding(PetBreeding breeding) {
        activeBreedings.remove(breeding.getBreedingId());

        List<UUID> playerBreedingList = playerBreedings. get(breeding.getOwnerId());
        if (playerBreedingList != null) {
            playerBreedingList.remove(breeding.getBreedingId());
            if (playerBreedingList.isEmpty()) {
                playerBreedings.remove(breeding. getOwnerId());
            }
        }
    }

    // ===== 쿨다운 =====

    /**
     * 교배 쿨다운 중인지 확인
     */
    public boolean isOnBreedingCooldown(Pet pet) {
        long cooldownEnd = pet.getBreedingCooldownEnd();
        return System.currentTimeMillis() < cooldownEnd;
    }

    /**
     * 교배 쿨다운 남은 시간
     */
    public long getBreedingCooldownRemaining(Pet pet) {
        long cooldownEnd = pet.getBreedingCooldownEnd();
        return Math.max(0, cooldownEnd - System.currentTimeMillis());
    }

    // ===== 비용/시간 계산 =====

    /**
     * 교배 비용 계산
     */
    public double calculateBreedingCost(Pet pet1, Pet pet2) {
        double cost = baseBreedingCost;

        // 희귀도에 따른 비용 증가
        cost *= (1 + pet1.getRarity().ordinal() * 0.2);
        cost *= (1 + pet2.getRarity().ordinal() * 0.2);

        // 레벨에 따른 비용 증가
        cost *= (1 + (pet1.getLevel() + pet2.getLevel()) / 100.0);

        return cost;
    }

    /**
     * 교배 시간 계산
     */
    public long calculateBreedingDuration(Pet pet1, Pet pet2) {
        long duration = baseBreedingTime;

        // 희귀도에 따른 시간 증가
        duration += pet1.getRarity().ordinal() * 600000L; // 10분씩
        duration += pet2.getRarity().ordinal() * 600000L;

        // 행복도에 따른 시간 감소
        double happinessBonus = (pet1.getHappiness() + pet2.getHappiness()) / 200.0;
        duration = (long) (duration * (1 - happinessBonus * 0.2));

        return duration;
    }

    // ===== 유틸리티 =====

    /**
     * 플레이어의 진행 중인 교배 수
     */
    public int getPlayerBreedingCount(UUID playerId) {
        List<UUID> breedings = playerBreedings.get(playerId);
        return breedings != null ? breedings.size() : 0;
    }

    /**
     * 플레이어의 진행 중인 교배 목록
     */
    public List<PetBreeding> getPlayerBreedings(UUID playerId) {
        List<PetBreeding> result = new ArrayList<>();
        List<UUID> breedingIds = playerBreedings.get(playerId);

        if (breedingIds != null) {
            for (UUID id :  breedingIds) {
                PetBreeding breeding = activeBreedings.get(id);
                if (breeding != null) {
                    result.add(breeding);
                }
            }
        }

        return result;
    }

    /**
     * 시간 포맷팅
     */
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if (hours > 0) {
            return String.format("%d시간 %d분", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d분 %d초", minutes, seconds % 60);
        } else {
            return String.format("%d초", seconds);
        }
    }

    /**
     * 설정 리로드
     */
    public void reload() {
        loadSettings();
    }

    /**
     * 종료 처리
     */
    public void shutdown() {
        stopBreedingTask();
        // 진행 중인 교배 데이터 저장 필요 시 처리
    }

    // ===== Getter =====

    public long getBaseBreedingTime() {
        return baseBreedingTime;
    }

    public double getBaseBreedingCost() {
        return baseBreedingCost;
    }

    public double getMutationBaseChance() {
        return mutationBaseChance;
    }

    public int getMaxConcurrentBreedings() {
        return maxConcurrentBreedings;
    }

    public int getMinBreedingLevel() {
        return minBreedingLevel;
    }

    public double getMinBreedingHappiness() {
        return minBreedingHappiness;
    }
}