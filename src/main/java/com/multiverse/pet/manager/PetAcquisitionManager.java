package com.multiverse.pet.manager;

import com. multiverse.pet. PetCore;
import com. multiverse.pet. api.event.PetCaptureEvent;
import com.multiverse. pet.model.Pet;
import com. multiverse.pet. model.PetRarity;
import com.multiverse.pet.model.PetSpecies;
import com.multiverse. pet.model.PetStatus;
import com. multiverse.pet. model.acquisition. CaptureBall;
import com. multiverse.pet. model.acquisition.PetEgg;
import com.multiverse.pet.model.acquisition. PetSummonScroll;
import com. multiverse.pet. util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org. bukkit. Particle;
import org.bukkit.Sound;
import org. bukkit.entity.Entity;
import org. bukkit.entity. EntityType;
import org.bukkit.entity.LivingEntity;
import org. bukkit.entity. Player;
import org. bukkit.scheduler.BukkitTask;

import java. util.*;
import java.util.concurrent. ConcurrentHashMap;

/**
 * 펫 획득 매니저 클래스
 * 펫 포획, 알 부화, 소환서 사용 관리
 */
public class PetAcquisitionManager {

    private final PetCore plugin;

    // 플레이어별 보유 알 목록
    private final Map<UUID, List<PetEgg>> playerEggs;

    // 부화 중인 알 (알ID -> 알)
    private final Map<UUID, PetEgg> hatchingEggs;

    // 포획 시도 쿨다운
    private final Map<UUID, Long> captureCooldowns;

    // 부화 체크 태스크
    private BukkitTask hatchCheckTask;

    // 설정 값
    private double baseCaptureRate;
    private int captureCooldown;
    private long baseHatchTime;
    private int maxEggsPerPlayer;
    private boolean allowWildCapture;
    private List<String> capturableEntityTypes;

    /**
     * 생성자
     */
    public PetAcquisitionManager(PetCore plugin) {
        this.plugin = plugin;
        this.playerEggs = new ConcurrentHashMap<>();
        this.hatchingEggs = new ConcurrentHashMap<>();
        this.captureCooldowns = new ConcurrentHashMap<>();
        loadSettings();
        startHatchCheckTask();
    }

    /**
     * 설정 로드
     */
    private void loadSettings() {
        this.baseCaptureRate = plugin.getConfigManager().getPetSettings().getBaseCaptureRate();
        this.captureCooldown = plugin. getConfigManager().getPetSettings().getCaptureCooldown();
        this.baseHatchTime = plugin.getConfigManager().getPetSettings().getBaseHatchTime();
        this.maxEggsPerPlayer = plugin.getConfigManager().getPetSettings().getMaxEggsPerPlayer();
        this.allowWildCapture = plugin.getConfigManager().getPetSettings().isAllowWildCapture();
        this.capturableEntityTypes = plugin.getConfigManager().getPetSettings().getCapturableEntityTypes();
    }

    /**
     * 부화 체크 태스크 시작
     */
    private void startHatchCheckTask() {
        hatchCheckTask = Bukkit. getScheduler().runTaskTimer(plugin, this::checkHatchingEggs, 20L, 20L);
    }

    /**
     * 부화 체크 태스크 중지
     */
    public void stopHatchCheckTask() {
        if (hatchCheckTask != null && ! hatchCheckTask. isCancelled()) {
            hatchCheckTask.cancel();
        }
    }

    // ===== 포획 시스템 =====

    /**
     * 엔티티 포획 시도
     */
    public boolean attemptCapture(Player player, LivingEntity target, CaptureBall ball) {
        UUID playerId = player. getUniqueId();

        if (! allowWildCapture) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("capture.disabled"));
            return false;
        }

        if (isOnCaptureCooldown(playerId)) {
            long remaining = getCaptureCooldownRemaining(playerId);
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("capture.cooldown")
                    .replace("{time}", String.valueOf(remaining / 1000)));
            return false;
        }

        if (plugin.getPetStorageManager().isStorageFull(playerId)) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("capture.storage-full"));
            return false;
        }

        EntityType entityType = target.getType();
        if (!isCapturableEntity(entityType)) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("capture.cannot-capture-type"));
            return false;
        }

        if (plugin.getPetManager().isPetEntity(target)) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("capture.already-pet"));
            return false;
        }

        PetSpecies species = getSpeciesForEntity(entityType);
        if (species == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("capture.no-species-data"));
            return false;
        }

        PetRarity rarity = determineWildPetRarity(target);
        int level = determineWildPetLevel(target);

        if (! ball.canCapture(entityType, species. getSpeciesId(), rarity, level)) {
            String reason = ball.getCannotCaptureReason(entityType, species.getSpeciesId(), rarity, level);
            MessageUtil.sendMessage(player, "&c" + reason);
            return false;
        }

        double healthPercent = (target.getHealth() / target.getMaxHealth()) * 100;

        CaptureBall. CaptureResult result = ball.attemptCapture(rarity, healthPercent, level);

        setCaptureCooldown(playerId);
        playCaptureEffect(target. getLocation(), result.isSuccess());

        if (result.isSuccess()) {
            return processCaptureSuccess(player, target, species, rarity, level, ball, result. isCritical());
        } else {
            MessageUtil.sendMessage(player, result.getMessage());

            if (! ball.isPreventFlee() && Math.random() < 0.3) {
                target.setAI(true);
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("capture.fled"));
            }

            return false;
        }
    }

    /**
     * 포획 성공 처리
     */
    private boolean processCaptureSuccess(Player player, LivingEntity target, PetSpecies species,
                                          PetRarity rarity, int level, CaptureBall ball, boolean critical) {
        UUID playerId = player. getUniqueId();

        Pet pet = createPetFromCapture(target, species, rarity, level);
        pet.setOwnerId(playerId);

        PetCaptureEvent event = new PetCaptureEvent(player, pet, target);
        Bukkit.getPluginManager().callEvent(event);

        if (event. isCancelled()) {
            return false;
        }

        target.remove();

        if (! plugin.getPetManager().addNewPet(playerId, pet)) {
            MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("capture. add-failed"));
            return false;
        }

        if (ball.isContainsPet()) {
            ball.setCapturedPet(pet. getPetId(), pet.getSpeciesId(), pet.getRarity());
        }

        String message = critical
                ? plugin.getConfigManager().getMessage("capture.critical-success")
                : plugin.getConfigManager().getMessage("capture.success");

        MessageUtil.sendMessage(player, message
                .replace("{name}", pet.getPetName())
                .replace("{species}", species.getName())
                .replace("{rarity}", rarity.getDisplayName())
                .replace("{level}", String.valueOf(level)));

        return true;
    }

    /**
     * 포획으로 펫 생성
     */
    private Pet createPetFromCapture(LivingEntity entity, PetSpecies species, PetRarity rarity, int level) {
        Pet pet = new Pet();
        pet.setSpeciesId(species.getSpeciesId());
        pet.setPetName(species.getName());
        pet.setType(species.getType());
        pet.setRarity(rarity);
        pet.setLevel(level);
        pet.setEntityType(entity.getType());
        pet.setStatus(PetStatus. STORED);

        pet.setBaseStats(species.getAllStatsAtLevel(level));
        pet.setMaxHealth(pet.getTotalStat("health"));
        pet.setHealth(pet. getMaxHealth());
        pet.setHunger(100);
        pet.setHappiness(50);

        if (entity. getCustomName() != null) {
            pet. setPetName(entity. getCustomName());
        }

        return pet;
    }

    /**
     * 야생 펫 희귀도 결정
     */
    private PetRarity determineWildPetRarity(LivingEntity entity) {
        double roll = Math.random() * 100;

        if (roll < 1) return PetRarity.LEGENDARY;
        if (roll < 5) return PetRarity.EPIC;
        if (roll < 15) return PetRarity. RARE;
        if (roll < 40) return PetRarity.UNCOMMON;
        return PetRarity. COMMON;
    }

    /**
     * 야생 펫 레벨 결정
     */
    private int determineWildPetLevel(LivingEntity entity) {
        double maxHealth = entity. getMaxHealth();
        int baseLevel = (int) (maxHealth / 5);
        int variation = (int) (Math.random() * 5) - 2;
        return Math.max(1, Math.min(50, baseLevel + variation));
    }

    /**
     * 엔티티 타입에 맞는 종족 가져오기
     */
    private PetSpecies getSpeciesForEntity(EntityType entityType) {
        return plugin.getSpeciesCache().getSpeciesByEntityType(entityType);
    }

    /**
     * 포획 가능한 엔티티인지 확인
     */
    public boolean isCapturableEntity(EntityType entityType) {
        if (capturableEntityTypes. isEmpty()) {
            return entityType. isAlive();
        }
        return capturableEntityTypes.contains(entityType. name());
    }

    /**
     * 포획 쿨다운 설정
     */
    private void setCaptureCooldown(UUID playerId) {
        captureCooldowns. put(playerId, System.currentTimeMillis() + (captureCooldown * 1000L));
    }

    /**
     * 포획 쿨다운 중인지 확인
     */
    public boolean isOnCaptureCooldown(UUID playerId) {
        Long cooldownEnd = captureCooldowns.get(playerId);
        if (cooldownEnd == null) return false;
        if (System.currentTimeMillis() >= cooldownEnd) {
            captureCooldowns. remove(playerId);
            return false;
        }
        return true;
    }

    /**
     * 포획 쿨다운 남은 시간
     */
    public long getCaptureCooldownRemaining(UUID playerId) {
        Long cooldownEnd = captureCooldowns.get(playerId);
        if (cooldownEnd == null) return 0;
        return Math.max(0, cooldownEnd - System.currentTimeMillis());
    }

    /**
     * 포획 이펙트 재생
     */
    private void playCaptureEffect(Location location, boolean success) {
        if (success) {
            location.getWorld().spawnParticle(Particle. VILLAGER_HAPPY, location.add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0. 1);
            location. getWorld().playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        } else {
            location.getWorld().spawnParticle(Particle.SMOKE_NORMAL, location.add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.05);
            location. getWorld().playSound(location, Sound. ENTITY_ITEM_BREAK, 1.0f, 0.8f);
        }
    }

    // ===== 알 시스템 =====

    /**
     * 알 추가
     */
    public boolean addEgg(Player player, PetEgg egg) {
        UUID playerId = player.getUniqueId();

        List<PetEgg> eggs = playerEggs. computeIfAbsent(playerId, k -> new ArrayList<>());

        if (eggs.size() >= maxEggsPerPlayer) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("egg.max-eggs")
                    .replace("{max}", String.valueOf(maxEggsPerPlayer)));
            return false;
        }

        egg.setOwnerId(playerId);
        eggs.add(egg);

        MessageUtil. sendMessage(player, plugin. getConfigManager().getMessage("egg.received")
                .replace("{name}", egg.getName()));

        return true;
    }

    /**
     * 알 부화 시작
     */
    public boolean startHatching(Player player, UUID eggId) {
        UUID playerId = player.getUniqueId();

        PetEgg egg = getPlayerEgg(playerId, eggId);
        if (egg == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("egg.not-found"));
            return false;
        }

        if (egg. isHatching()) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("egg.already-hatching"));
            return false;
        }

        String biome = player. getLocation().getBlock().getBiome().name();
        boolean isDay = player. getWorld().getTime() < 13000;

        if (! egg.canHatch(player.getLevel(), biome, isDay)) {
            String reason = egg. getCannotHatchReason(player. getLevel(), biome, isDay);
            MessageUtil.sendMessage(player, "&c" + reason);
            return false;
        }

        egg.startHatching();
        hatchingEggs.put(egg.getEggId(), egg);

        MessageUtil. sendMessage(player, plugin. getConfigManager().getMessage("egg.hatching-started")
                .replace("{name}", egg.getName())
                .replace("{time}", egg.getRemainingHatchTimeFormatted()));

        return true;
    }

    /**
     * 알 부화 취소
     */
    public boolean cancelHatching(Player player, UUID eggId) {
        UUID playerId = player.getUniqueId();

        PetEgg egg = getPlayerEgg(playerId, eggId);
        if (egg == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("egg.not-found"));
            return false;
        }

        if (!egg.isHatching()) {
            MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("egg.not-hatching"));
            return false;
        }

        egg.cancelHatching();
        hatchingEggs.remove(eggId);

        MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("egg.hatching-cancelled")
                .replace("{name}", egg. getName()));

        return true;
    }

    /**
     * 부화 중인 알 체크
     */
    private void checkHatchingEggs() {
        Iterator<Map.Entry<UUID, PetEgg>> iterator = hatchingEggs. entrySet().iterator();

        while (iterator.hasNext()) {
            Map. Entry<UUID, PetEgg> entry = iterator.next();
            PetEgg egg = entry. getValue();

            if (egg.isReadyToHatch()) {
                completeHatching(egg);
                iterator. remove();
            }
        }
    }

    /**
     * 알 부화 완료
     */
    private void completeHatching(PetEgg egg) {
        UUID ownerId = egg. getOwnerId();
        Player owner = Bukkit.getPlayer(ownerId);

        // 종족 결정
        String speciesId = egg.determineSpecies();
        if (speciesId == null) {
            if (owner != null) {
                MessageUtil.sendMessage(owner, plugin.getConfigManager().getMessage("egg.hatch-failed"));
            }
            return;
        }

        // 펫 생성
        Pet pet = createPetFromEgg(egg, speciesId);

        // 보관함에 추가
        if (! plugin.getPetManager().addNewPet(ownerId, pet)) {
            if (owner != null) {
                MessageUtil.sendMessage(owner, plugin.getConfigManager().getMessage("egg.storage-full"));
            }
            return;
        }

        // 알 제거
        removePlayerEgg(ownerId, egg.getEggId());

        // 알림
        if (owner != null) {
            MessageUtil.sendMessage(owner, plugin.getConfigManager().getMessage("egg.hatched")
                    . replace("{egg}", egg.getName())
                    .replace("{pet}", pet.getPetName())
                    .replace("{rarity}", pet.getRarity().getDisplayName()));

            // 이펙트
            owner.playSound(owner.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
            owner.getWorld().spawnParticle(Particle. TOTEM, owner.getLocation().add(0, 1, 0), 50, 0.5, 0.5, 0.5, 0.2);
        }
    }

    /**
     * 알에서 펫 생성
     */
    private Pet createPetFromEgg(PetEgg egg, String speciesId) {
        PetSpecies species = plugin.getSpeciesCache().getSpecies(speciesId);

        Pet pet = new Pet();
        pet.setOwnerId(egg. getOwnerId());
        pet.setSpeciesId(speciesId);
        pet.setPetName(species != null ? species.getName() + " 새끼" : speciesId);
        pet.setType(species != null ? species.getType() : null);
        pet.setRarity(egg.determineRarity());
        pet.setLevel(1);
        pet.setStatus(PetStatus. STORED);

        if (species != null) {
            pet.setEntityType(species.getEntityType());
            pet.setBaseStats(species.getAllStatsAtLevel(1));
        }

        pet.setMaxHealth(pet.getTotalStat("health"));
        pet.setHealth(pet. getMaxHealth());
        pet.setHunger(100);
        pet.setHappiness(100);

        // 알 보너스 스탯 적용
        for (Map.Entry<String, Double> bonus : egg.getStatBonuses().entrySet()) {
            double current = pet.getBaseStats().getOrDefault(bonus.getKey(), 0.0);
            pet.setBaseStat(bonus. getKey(), current + bonus.getValue());
        }

        // 변이 확인
        if (egg.rollMutation(5. 0)) {
            pet.setMutation(true);
            // 변이 보너스 적용
            for (String stat : pet.getBaseStats().keySet()) {
                double current = pet.getBaseStats().get(stat);
                pet.setBaseStat(stat, current * 1.1);
            }
        }

        return pet;
    }

    /**
     * 플레이어 알 가져오기
     */
    public PetEgg getPlayerEgg(UUID playerId, UUID eggId) {
        List<PetEgg> eggs = playerEggs.get(playerId);
        if (eggs == null) return null;

        return eggs.stream()
                .filter(egg -> egg.getEggId().equals(eggId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 플레이어 알 제거
     */
    public boolean removePlayerEgg(UUID playerId, UUID eggId) {
        List<PetEgg> eggs = playerEggs. get(playerId);
        if (eggs == null) return false;

        return eggs.removeIf(egg -> egg. getEggId().equals(eggId));
    }

    /**
     * 플레이어의 모든 알 가져오기
     */
    public List<PetEgg> getPlayerEggs(UUID playerId) {
        return new ArrayList<>(playerEggs.getOrDefault(playerId, new ArrayList<>()));
    }

    /**
     * 부화 중인 알 목록
     */
    public List<PetEgg> getHatchingEggs(UUID playerId) {
        List<PetEgg> result = new ArrayList<>();
        for (PetEgg egg :  hatchingEggs.values()) {
            if (egg. getOwnerId().equals(playerId)) {
                result.add(egg);
            }
        }
        return result;
    }

    // ===== 소환서 시스템 =====

    /**
     * 소환서 사용
     */
    public boolean useSummonScroll(Player player, PetSummonScroll scroll) {
        UUID playerId = player. getUniqueId();

        // 사용 가능 여부 확인
        if (!scroll.canUse(player. getLevel())) {
            String reason = scroll.getCannotUseReason(player.getLevel());
            MessageUtil. sendMessage(player, "&c" + reason);
            return false;
        }

        // 보관함 여유 확인
        if (plugin.getPetStorageManager().isStorageFull(playerId)) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("scroll.storage-full"));
            return false;
        }

        // 소울바운드 확인
        if (scroll.isSoulbound() && !scroll.isBoundTo(playerId)) {
            MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("scroll. soulbound-other"));
            return false;
        }

        // 소환서 사용
        if (! scroll.use()) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("scroll.use-failed"));
            return false;
        }

        // 펫 생성
        Pet pet = createPetFromScroll(scroll, playerId);

        // 보관함에 추가
        if (!plugin. getPetManager().addNewPet(playerId, pet)) {
            MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("scroll. add-failed"));
            return false;
        }

        // 이펙트
        Location loc = player.getLocation();
        loc.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, loc.add(0, 1, 0), 100, 1, 1, 1, 0.5);
        loc.getWorld().playSound(loc, Sound. ENTITY_EVOKER_PREPARE_SUMMON, 1.0f, 1.0f);

        // 알림
        MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("scroll. used")
                .replace("{scroll}", scroll.getName())
                .replace("{pet}", pet.getPetName())
                .replace("{rarity}", pet.getRarity().getDisplayName()));

        return true;
    }

    /**
     * 소환서에서 펫 생성
     */
    private Pet createPetFromScroll(PetSummonScroll scroll, UUID ownerId) {
        String speciesId = scroll.determineSpecies();
        PetSpecies species = speciesId != null ? plugin.getSpeciesCache().getSpecies(speciesId) : null;

        Pet pet = new Pet();
        pet.setOwnerId(ownerId);
        pet.setSpeciesId(speciesId != null ? speciesId : "unknown");
        pet.setRarity(scroll.determineRarity());
        pet.setLevel(scroll.getInitialLevel());
        pet.setStatus(PetStatus. STORED);

        if (species != null) {
            pet. setPetName(species. getName());
            pet.setType(species.getType());
            pet.setEntityType(species.getEntityType());
            pet.setBaseStats(species.getAllStatsAtLevel(scroll.getInitialLevel()));
        } else {
            pet.setPetName("신비한 펫");
        }

        // 커스텀 이름
        if (scroll. getCustomPetName() != null && !scroll.getCustomPetName().isEmpty()) {
            pet.setPetName(scroll.getCustomPetName());
            if (scroll.isNameLocked()) {
                pet.setNameLocked(true);
            }
        }

        pet.setMaxHealth(pet.getTotalStat("health"));
        pet.setHealth(pet.getMaxHealth());
        pet.setHunger(100);
        pet.setHappiness(100);

        // 보너스 스탯 적용
        for (Map. Entry<String, Double> bonus : scroll. getBonusStats().entrySet()) {
            double current = pet.getBaseStats().getOrDefault(bonus.getKey(), 0.0);
            pet.setBaseStat(bonus.getKey(), current + bonus.getValue());
        }

        // 초기 스킬 추가
        for (String skillId : scroll.getInitialSkills()) {
            plugin.getPetSkillManager().unlockSkill(pet, skillId);
        }

        // 변이 확인
        if (scroll.rollMutation()) {
            pet. setMutation(true);
        }

        return pet;
    }

    // ===== 유틸리티 =====

    /**
     * 랜덤 알 생성
     */
    public PetEgg createRandomEgg(String eggType, PetRarity minRarity) {
        PetEgg egg = new PetEgg();
        egg.setEggType(eggType);
        egg.setName(eggType + " 알");
        egg.setHatchTime(baseHatchTime);

        // 가능한 종족 설정
        List<String> possibleSpecies = plugin.getSpeciesCache().getSpeciesIdsByRarity(minRarity);
        egg.setPossibleSpecies(possibleSpecies);

        return egg;
    }

    /**
     * 랜덤 소환서 생성
     */
    public PetSummonScroll createRandomScroll(PetRarity guaranteedRarity) {
        switch (guaranteedRarity) {
            case COMMON:
                return PetSummonScroll.createCommonScroll();
            case UNCOMMON:
                return PetSummonScroll.createUncommonScroll();
            case RARE:
                return PetSummonScroll.createRareScroll();
            case EPIC: 
                return PetSummonScroll. createEpicScroll();
            case LEGENDARY:
                return PetSummonScroll.createLegendaryScroll();
            case MYTHIC: 
                return PetSummonScroll. createMythicScroll();
            default:
                return PetSummonScroll.createRandomScroll();
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
        stopHatchCheckTask();
    }

    // ===== Getter =====

    public double getBaseCaptureRate() {
        return baseCaptureRate;
    }

    public int getCaptureCooldown() {
        return captureCooldown;
    }

    public long getBaseHatchTime() {
        return baseHatchTime;
    }

    public int getMaxEggsPerPlayer() {
        return maxEggsPerPlayer;
    }

    public boolean isAllowWildCapture() {
        return allowWildCapture;
    }
}