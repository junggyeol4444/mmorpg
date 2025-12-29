package com. multiverse.pet.  manager;

import com.multiverse.pet.PetCore;
import com. multiverse.pet. api.event.PetEvolutionEvent;
import com.multiverse. pet.model.Pet;
import com.multiverse. pet.model.  PetRarity;
import com.  multiverse.pet.  model.PetSpecies;
import com.multiverse.pet.model.evolution. EvolutionType;
import com. multiverse.pet. model.evolution.PetEvolution;
import com.multiverse.pet.util.  MessageUtil;
import org.bukkit.  Bukkit;
import org.bukkit. Location;
import org. bukkit. Particle;
import org.bukkit.Sound;
import org. bukkit.block. Biome;
import org.bukkit. entity.  EntityType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 진화 매니저 클래스
 * 펫 진화 조건 확인, 진화 실행 관리
 */
public class EvolutionManager {

    private final PetCore plugin;

    // 진화 템플릿 저장소 (진화ID -> 진화)
    private final Map<String, PetEvolution> evolutionTemplates;

    // 종족별 진화 목록 (종족ID -> 진화 목록)
    private final Map<String, List<PetEvolution>> speciesEvolutions;

    // 메가 진화 상태 (펫ID -> 종료 시간)
    private final Map<UUID, Long> megaEvolutionTimers;

    // 설정 값
    private double evolutionSuccessBonus;
    private double itemConsumeOnFailChance;
    private boolean allowEvolutionCancel;
    private int megaEvolutionDuration;

    /**
     * 생성자
     */
    public EvolutionManager(PetCore plugin) {
        this.plugin = plugin;
        this.evolutionTemplates = new ConcurrentHashMap<>();
        this.speciesEvolutions = new ConcurrentHashMap<>();
        this.megaEvolutionTimers = new ConcurrentHashMap<>();
        loadSettings();
    }

    /**
     * 설정 로드
     */
    private void loadSettings() {
        this.evolutionSuccessBonus = plugin. getEvolutionSettings().getSuccessBonus();
        this.itemConsumeOnFailChance = plugin. getEvolutionSettings().getItemConsumeOnFailChance();
        this.allowEvolutionCancel = plugin.getEvolutionSettings().isAllowCancel();
        this.megaEvolutionDuration = plugin.getEvolutionSettings().getMegaEvolutionDuration();
    }

    // ===== 진화 템플릿 관리 =====

    /**
     * 진화 템플릿 등록
     */
    public void registerEvolution(PetEvolution evolution) {
        evolutionTemplates. put(evolution.getEvolutionId(), evolution);

        // 종족별 목록에도 추가
        speciesEvolutions
                .computeIfAbsent(evolution.getFromSpeciesId(), k -> new ArrayList<>())
                .add(evolution);
    }

    /**
     * 진화 템플릿 가져오기
     */
    public PetEvolution getEvolution(String evolutionId) {
        return evolutionTemplates.get(evolutionId);
    }

    /**
     * 종족의 가능한 진화 목록 가져오기
     */
    public List<PetEvolution> getEvolutionsForSpecies(String speciesId) {
        return speciesEvolutions.getOrDefault(speciesId, Collections.emptyList());
    }

    /**
     * 펫이 사용 가능한 진화 목록 가져오기
     */
    public List<PetEvolution> getAvailableEvolutions(Pet pet, Player player) {
        List<PetEvolution> available = new ArrayList<>();
        List<PetEvolution> evolutions = getEvolutionsForSpecies(pet.getSpeciesId());

        // 플레이어 정보 수집
        Map<String, Integer> playerItems = getPlayerItems(player);
        double playerGold = plugin.getPlayerDataCoreHook().getGold(player. getUniqueId());
        List<String> completedQuests = getCompletedQuests(player);
        String currentBiome = player.getLocation().getBlock().getBiome().name();
        boolean isDay = isDayTime(player.getWorld());
        String weather = getWeather(player.  getWorld());

        for (PetEvolution evolution : evolutions) {
            if (evolution.canEvolve(pet, playerItems, playerGold, completedQuests, 
                                     currentBiome, isDay, weather)) {
                available. add(evolution);
            }
        }

        return available;
    }

    /**
     * 진화 가능 여부 확인
     */
    public boolean hasAvailableEvolution(Pet pet) {
        Player player = Bukkit.getPlayer(pet.  getOwnerId());
        if (player == null) return false;

        return ! getAvailableEvolutions(pet, player).isEmpty();
    }

    // ===== 진화 실행 =====

    /**
     * 진화 실행
     *
     * @param player 플레이어
     * @param pet 진화할 펫
     * @param evolutionId 진화 ID
     * @return 진화 성공 여부
     */
    public boolean evolve(Player player, Pet pet, String evolutionId) {
        PetEvolution evolution = getEvolution(evolutionId);
        if (evolution == null) {
            MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("evolution.not-found"));
            return false;
        }

        // 조건 재확인
        Map<String, Integer> playerItems = getPlayerItems(player);
        double playerGold = plugin.getPlayerDataCoreHook().getGold(player.getUniqueId());
        List<String> completedQuests = getCompletedQuests(player);
        String currentBiome = player.getLocation().getBlock().getBiome().name();
        boolean isDay = isDayTime(player.getWorld());
        String weather = getWeather(player. getWorld());

        if (!evolution.canEvolve(pet, playerItems, playerGold, completedQuests, 
                                 currentBiome, isDay, weather)) {
            // 충족되지 않은 조건 표시
            List<String> unmet = evolution.getUnmetConditions(pet, playerItems, playerGold, 
                                                               completedQuests, currentBiome, isDay, weather);
            MessageUtil. sendMessage(player, plugin.  getConfigManager().getMessage("evolution.conditions-not-met"));
            for (String condition : unmet) {
                MessageUtil.  sendMessage(player, "&c- " + condition);
            }
            return false;
        }

        // 활성 펫이면 먼저 해제
        if (pet. isActive()) {
            plugin.getPetManager().unsummonPet(player, pet. getPetId());
        }

        // 성공 확률 계산
        double successChance = evolution.getSuccessChance() + evolutionSuccessBonus;

        // 행복도 보너스
        if (pet.getHappiness() >= 80) {
            successChance += 5;
        }

        // 성공 여부 결정
        boolean success = evolution.rollSuccess() || Math.random() * 100 < successChance;

        // 아이템 소비 (실패 시 확률적)
        boolean consumeItems = success || Math.random() * 100 < itemConsumeOnFailChance;
        if (consumeItems) {
            consumeEvolutionItems(player, evolution);
        }

        // 골드 소비
        if (evolution.getGoldCost() > 0) {
            plugin.getPlayerDataCoreHook().withdrawGold(player. getUniqueId(), evolution.getGoldCost());
        }

        if (! success) {
            // 진화 실패
            MessageUtil.sendMessage(player, plugin.  getConfigManager().getMessage("evolution.failed")
                    .replace("{name}", pet.getPetName()));
            playFailEffect(player. getLocation());
            return false;
        }

        // 진화 이벤트 발생
        PetEvolutionEvent evolutionEvent = new PetEvolutionEvent(player, pet, evolution);
        Bukkit.  getPluginManager().callEvent(evolutionEvent);

        if (evolutionEvent. isCancelled()) {
            return false;
        }

        // 진화 적용
        applyEvolution(pet, evolution);

        // 저장
        plugin. getPetManager().savePetData(pet.getOwnerId(), pet);

        // 효과 재생
        playEvolutionEffect(player. getLocation());

        // 알림
        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("evolution.success")
                .replace("{name}", pet.getPetName())
                .replace("{species}", evolution.getToSpeciesId()));

        return true;
    }

    /**
     * 진화 적용
     */
    private void applyEvolution(Pet pet, PetEvolution evolution) {
        // 종족 변경
        pet.setSpeciesId(evolution.getToSpeciesId());

        // 진화 단계 증가
        pet. setEvolutionStage(evolution.getToStage());

        // 진화 경로 기록
        String path = pet.getEvolutionPath();
        if (path == null || path.isEmpty()) {
            path = evolution.getEvolutionId();
        } else {
            path += " -> " + evolution. getEvolutionId();
        }
        pet.setEvolutionPath(path);

        // 스탯 보너스 적용
        Map<String, Double> bonuses = evolution.getStatBonuses();
        for (Map.Entry<String, Double> entry : bonuses.entrySet()) {
            double current = pet.getBaseStats().getOrDefault(entry. getKey(), 0.0);
            pet.setBaseStat(entry.getKey(), current + entry.getValue());
        }

        // 스탯 배율 적용
        if (evolution.getStatMultiplier() > 1.0) {
            for (String statName : pet.getBaseStats().keySet()) {
                double current = pet.getBaseStats().get(statName);
                pet.setBaseStat(statName, current * evolution.  getStatMultiplier());
            }
        }

        // 희귀도 변경
        if (evolution.getNewRarity() != null) {
            pet.setRarity(evolution.getNewRarity());
        }

        // 엔티티 타입 변경
        if (evolution. getNewEntityType() != null && ! evolution.getNewEntityType().isEmpty()) {
            try {
                EntityType newType = EntityType.valueOf(evolution.getNewEntityType().toUpperCase());
                pet.setEntityType(newType);
            } catch (IllegalArgumentException ignored) {
            }
        }

        // 커스텀 모델 변경
        if (evolution.getNewCustomModelId() != null) {
            pet.setCustomModelId(evolution.  getNewCustomModelId());
        }

        // 새 스킬 추가
        for (String skillId : evolution.getNewSkills()) {
            plugin.getSkillManager().unlockSkill(pet, skillId);
        }

        // 최대 체력 재계산 및 회복
        pet. setMaxHealth(pet.getTotalStat("health"));
        pet.setHealth(pet.getMaxHealth());

        // 행복도 증가 (진화 보너스)
        pet.increaseHappiness(20);
    }

    /**
     * 진화 아이템 소비
     */
    private void consumeEvolutionItems(Player player, PetEvolution evolution) {
        for (PetEvolution.ItemRequirement req : evolution.getRequiredItems()) {
            // ItemCore를 통해 아이템 제거
            if (plugin.hasItemCore()) {
                plugin. getItemCoreHook().removeItem(player, req.getItemId(), req.getAmount());
            }
        }
    }

    // ===== 메가 진화 =====

    /**
     * 메가 진화 실행
     */
    public boolean megaEvolve(Player player, Pet pet, String evolutionId) {
        PetEvolution evolution = getEvolution(evolutionId);
        if (evolution == null || evolution.getType() != EvolutionType.MEGA) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("evolution.not-mega"));
            return false;
        }

        // 이미 메가 진화 중인지 확인
        if (isMegaEvolved(pet. getPetId())) {
            MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("evolution. already-mega"));
            return false;
        }

        // 기본 진화 조건 확인 및 실행
        if (!evolve(player, pet, evolutionId)) {
            return false;
        }

        // 메가 진화 타이머 설정
        long endTime = System.currentTimeMillis() + (megaEvolutionDuration * 1000L);
        megaEvolutionTimers.put(pet. getPetId(), endTime);

        // 알림
        MessageUtil.  sendMessage(player, plugin.getConfigManager().getMessage("evolution. mega-started")
                .replace("{duration}", String.valueOf(megaEvolutionDuration)));

        return true;
    }

    /**
     * 메가 진화 상태인지 확인
     */
    public boolean isMegaEvolved(UUID petId) {
        Long endTime = megaEvolutionTimers.  get(petId);
        if (endTime == null) return false;

        if (System.currentTimeMillis() >= endTime) {
            megaEvolutionTimers.  remove(petId);
            return false;
        }
        return true;
    }

    /**
     * 메가 진화 남은 시간 (초)
     */
    public int getMegaEvolutionRemainingTime(UUID petId) {
        Long endTime = megaEvolutionTimers.get(petId);
        if (endTime == null) return 0;

        long remaining = endTime - System.currentTimeMillis();
        return (int) Math.max(0, remaining / 1000);
    }

    /**
     * 메가 진화 종료 처리
     */
    public void checkMegaEvolutions() {
        Iterator<Map.Entry<UUID, Long>> iterator = megaEvolutionTimers.entrySet().iterator();

        while (iterator. hasNext()) {
            Map.Entry<UUID, Long> entry = iterator.next();
            if (System.currentTimeMillis() >= entry.getValue()) {
                UUID petId = entry. getKey();
                iterator.  remove();

                // 원래 형태로 되돌리기
                revertMegaEvolution(petId);
            }
        }
    }

    /**
     * 메가 진화 되돌리기
     */
    private void revertMegaEvolution(UUID petId) {
        Pet pet = plugin.getPetCache().getPet(petId);
        if (pet == null) return;

        // 이전 종족으로 되돌리기 (진화 경로에서 추출)
        String path = pet.getEvolutionPath();
        if (path != null && path.contains(" -> ")) {
            String[] parts = path.split(" -> ");
            if (parts.length >= 2) {
                String lastEvolutionId = parts[parts.length - 1];
                PetEvolution lastEvolution = getEvolution(lastEvolutionId);
                if (lastEvolution != null) {
                    pet. setSpeciesId(lastEvolution.getFromSpeciesId());
                    pet.setEvolutionStage(lastEvolution.getFromStage());
                }
            }
        }

        // 저장
        plugin. getPetManager().savePetData(pet.getOwnerId(), pet);

        // 알림
        Player owner = Bukkit. getPlayer(pet. getOwnerId());
        if (owner != null) {
            MessageUtil.sendMessage(owner, plugin.getConfigManager().getMessage("evolution.mega-ended")
                    . replace("{name}", pet.getPetName()));
        }
    }

    // ===== 퇴화 =====

    /**
     * 펫 퇴화
     */
    public boolean devolve(Player player, Pet pet) {
        if (pet.getEvolutionStage() <= 1) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("evolution.cannot-devolve"));
            return false;
        }

        // 퇴화 진화 찾기
        PetEvolution devolution = findDevolution(pet);
        if (devolution == null) {
            MessageUtil.  sendMessage(player, plugin.getConfigManager().getMessage("evolution. no-devolution"));
            return false;
        }

        return evolve(player, pet, devolution. getEvolutionId());
    }

    /**
     * 퇴화 진화 찾기
     */
    private PetEvolution findDevolution(Pet pet) {
        for (PetEvolution evolution :   evolutionTemplates.values()) {
            if (evolution.getType() == EvolutionType.DEVOLUTION &&
                evolution.getFromSpeciesId().equals(pet.  getSpeciesId())) {
                return evolution;
            }
        }
        return null;
    }

    // ===== 유틸리티 메서드 =====

    /**
     * 플레이어 아이템 맵 생성
     */
    private Map<String, Integer> getPlayerItems(Player player) {
        Map<String, Integer> items = new HashMap<>();

        if (plugin.hasItemCore()) {
            // ItemCore 사용
            items = plugin.getItemCoreHook().getPlayerItemCounts(player);
        } else {
            // 바닐라 인벤토리 확인
            for (org.bukkit.inventory.ItemStack item : player. getInventory().getContents()) {
                if (item != null && item.  getType() != org.bukkit.Material.AIR) {
                    String itemId = item. getType().name();
                    items.merge(itemId, item.getAmount(), Integer::sum);
                }
            }
        }

        return items;
    }

    /**
     * 완료한 퀘스트 목록 가져오기
     */
    private List<String> getCompletedQuests(Player player) {
        // PlayerDataCore에서 퀘스트 데이터 가져오기
        return plugin.getPlayerDataCoreHook().getCompletedQuests(player. getUniqueId());
    }

    /**
     * 낮 시간인지 확인
     */
    private boolean isDayTime(org.bukkit.World world) {
        long time = world. getTime();
        return time >= 0 && time < 13000;
    }

    /**
     * 날씨 확인
     */
    private String getWeather(org.bukkit.World world) {
        if (world.isThundering()) {
            return "THUNDER";
        } else if (world. hasStorm()) {
            return "RAIN";
        }
        return "CLEAR";
    }

    /**
     * 진화 성공 효과 재생
     */
    private void playEvolutionEffect(Location location) {
        location.getWorld().spawnParticle(Particle.TOTEM, location.add(0, 1, 0), 100, 1, 1, 1, 0.5);
        location.getWorld().playSound(location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
    }

    /**
     * 진화 실패 효과 재생
     */
    private void playFailEffect(Location location) {
        location.getWorld().spawnParticle(Particle.SMOKE_LARGE, location.add(0, 1, 0), 50, 0.5, 0.5, 0.5, 0.1);
        location.getWorld().playSound(location, Sound. ENTITY_ITEM_BREAK, 1.0f, 0.5f);
    }

    /**
     * 설정 리로드
     */
    public void reload() {
        loadSettings();
    }

    // ===== Getter =====

    public Map<String, PetEvolution> getEvolutionTemplates() {
        return Collections.unmodifiableMap(evolutionTemplates);
    }

    public int getMegaEvolutionDuration() {
        return megaEvolutionDuration;
    }
}