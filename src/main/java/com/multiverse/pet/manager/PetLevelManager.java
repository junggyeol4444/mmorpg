package com.multiverse.pet.manager;

import com. multiverse.pet. PetCore;
import com. multiverse.pet. api.event.PetLevelUpEvent;
import com.multiverse.pet.model.Pet;
import com. multiverse.pet. model.PetRarity;
import com. multiverse.pet. model.PetSpecies;
import com.multiverse.pet.util.ExpCalculator;
import com.multiverse.pet.util.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. Sound;
import org. bukkit.entity. Player;

import java.util. Map;
import java. util.UUID;

/**
 * 펫 레벨 매니저 클래스
 * 펫 경험치 획득, 레벨업 관리
 */
public class PetLevelManager {

    private final PetCore plugin;

    // 설정 값
    private double baseExpMultiplier;
    private double mobKillExpMultiplier;
    private double playerKillExpMultiplier;
    private double miningExpMultiplier;
    private double fishingExpMultiplier;
    private double expShareRatio;
    private boolean enableExpShare;
    private int maxLevel;

    /**
     * 생성자
     */
    public PetLevelManager(PetCore plugin) {
        this.plugin = plugin;
        loadSettings();
    }

    /**
     * 설정 로드
     */
    private void loadSettings() {
        this.baseExpMultiplier = plugin.getLevelSettings().getBaseExpMultiplier();
        this.mobKillExpMultiplier = plugin.getLevelSettings().getMobKillExpMultiplier();
        this.playerKillExpMultiplier = plugin.getLevelSettings().getPlayerKillExpMultiplier();
        this.miningExpMultiplier = plugin.getLevelSettings().getMiningExpMultiplier();
        this.fishingExpMultiplier = plugin.getLevelSettings().getFishingExpMultiplier();
        this.expShareRatio = plugin.getLevelSettings().getExpShareRatio();
        this.enableExpShare = plugin.getLevelSettings().isEnableExpShare();
        this.maxLevel = plugin.getLevelSettings().getMaxLevel();
    }

    // ===== 경험치 획득 =====

    /**
     * 경험치 추가
     *
     * @param pet 펫
     * @param baseExp 기본 경험치
     * @param source 경험치 소스
     * @return 레벨업 여부
     */
    public boolean addExperience(Pet pet, long baseExp, ExpSource source) {
        if (! canGainExp(pet)) {
            return false;
        }

        // 경험치 계산
        long finalExp = calculateFinalExp(pet, baseExp, source);

        // 경험치 추가
        boolean needsLevelUp = pet.addExperience(finalExp);

        // 레벨업 처리
        if (needsLevelUp) {
            return processLevelUps(pet);
        }

        // 데이터 저장
        savePetData(pet);

        return false;
    }

    /**
     * 몹 처치 경험치 획득
     */
    public boolean addMobKillExp(Pet pet, int mobLevel, boolean isBoss) {
        long baseExp = ExpCalculator.calculateMobKillExp(mobLevel, isBoss);
        return addExperience(pet, baseExp, ExpSource.MOB_KILL);
    }

    /**
     * 플레이어 처치 경험치 획득 (PvP)
     */
    public boolean addPlayerKillExp(Pet pet, int victimLevel) {
        long baseExp = ExpCalculator.calculatePlayerKillExp(victimLevel);
        return addExperience(pet, baseExp, ExpSource.PLAYER_KILL);
    }

    /**
     * 채광 경험치 획득
     */
    public boolean addMiningExp(Pet pet, String blockType) {
        long baseExp = ExpCalculator.calculateMiningExp(blockType);
        return addExperience(pet, baseExp, ExpSource. MINING);
    }

    /**
     * 낚시 경험치 획득
     */
    public boolean addFishingExp(Pet pet, String fishType) {
        long baseExp = ExpCalculator.calculateFishingExp(fishType);
        return addExperience(pet, baseExp, ExpSource.FISHING);
    }

    /**
     * 대결 경험치 획득
     */
    public boolean addBattleExp(Pet pet, long exp, boolean isWinner) {
        ExpSource source = isWinner ? ExpSource. BATTLE_WIN : ExpSource.BATTLE_LOSE;
        return addExperience(pet, exp, source);
    }

    /**
     * 퀘스트 완료 경험치 획득
     */
    public boolean addQuestExp(Pet pet, long exp) {
        return addExperience(pet, exp, ExpSource.QUEST);
    }

    /**
     * 직접 경험치 추가 (관리자용)
     */
    public boolean addDirectExp(Pet pet, long exp) {
        return addExperience(pet, exp, ExpSource.ADMIN);
    }

    // ===== 경험치 계산 =====

    /**
     * 최종 경험치 계산
     */
    private long calculateFinalExp(Pet pet, long baseExp, ExpSource source) {
        double multiplier = baseExpMultiplier;

        // 소스별 배율 적용
        switch (source) {
            case MOB_KILL:
                multiplier *= mobKillExpMultiplier;
                break;
            case PLAYER_KILL: 
                multiplier *= playerKillExpMultiplier;
                break;
            case MINING:
                multiplier *= miningExpMultiplier;
                break;
            case FISHING: 
                multiplier *= fishingExpMultiplier;
                break;
            case BATTLE_WIN:
                multiplier *= 1.5;
                break;
            case BATTLE_LOSE: 
                multiplier *= 0.5;
                break;
            case ADMIN:
                multiplier = 1.0; // 관리자 경험치는 배율 적용 안함
                break;
            default:
                break;
        }

        // 희귀도 보너스 적용
        if (pet. getRarity() != null) {
            multiplier *= pet.getRarity().getExpMultiplier();
        }

        // 타입 보너스 (채집형은 채집 경험치 보너스)
        if (pet.getType() != null) {
            if (source == ExpSource. MINING || source == ExpSource. FISHING) {
                multiplier *= pet.getType().getGatheringMultiplier();
            } else if (source == ExpSource.MOB_KILL || source == ExpSource. PLAYER_KILL) {
                multiplier *= pet.getType().getAttackMultiplier();
            }
        }

        // 행복도에 따른 보너스 (80% 이상이면 10% 추가)
        if (pet.getHappiness() >= 80) {
            multiplier *= 1.1;
        }

        return (long) (baseExp * multiplier);
    }

    /**
     * 경험치 획득 가능 여부
     */
    public boolean canGainExp(Pet pet) {
        // 최대 레벨 체크
        if (pet.getLevel() >= getMaxLevelForPet(pet)) {
            return false;
        }

        // 상태 체크
        if (! pet.getStatus().canGainExp()) {
            return false;
        }

        // 사망 상태 체크
        if (pet. getHealth() <= 0) {
            return false;
        }

        return true;
    }

    /**
     * 펫의 최대 레벨 반환
     */
    public int getMaxLevelForPet(Pet pet) {
        // 희귀도별 최대 레벨
        if (pet.getRarity() != null) {
            return Math. min(maxLevel, pet.getRarity().getMaxLevel());
        }
        return maxLevel;
    }

    // ===== 레벨업 처리 =====

    /**
     * 레벨업 처리 (다중 레벨업 지원)
     */
    private boolean processLevelUps(Pet pet) {
        boolean leveledUp = false;
        int levelsGained = 0;

        while (pet.getExperience() >= pet.getExpToNext() && 
               pet.getLevel() < getMaxLevelForPet(pet)) {
            
            int oldLevel = pet.getLevel();
            pet.levelUp();
            int newLevel = pet. getLevel();
            levelsGained++;
            leveledUp = true;

            // 스탯 성장 적용
            applyStatGrowth(pet);

            // 스킬 해금 확인
            checkSkillUnlock(pet, newLevel);

            // 진화 가능 여부 확인
            checkEvolutionAvailable(pet, newLevel);

            // 레벨업 이벤트 발생
            Player owner = Bukkit. getPlayer(pet. getOwnerId());
            if (owner != null) {
                PetLevelUpEvent event = new PetLevelUpEvent(owner, pet, oldLevel, newLevel);
                Bukkit.getPluginManager().callEvent(event);
            }
        }

        // 레벨업 알림
        if (leveledUp) {
            notifyLevelUp(pet, levelsGained);
        }

        // 데이터 저장
        savePetData(pet);

        return leveledUp;
    }

    /**
     * 스탯 성장 적용
     */
    private void applyStatGrowth(Pet pet) {
        // 종족 데이터에서 성장률 가져오기
        PetSpecies species = plugin.getSpeciesCache().getSpecies(pet.getSpeciesId());
        if (species == null) return;

        Map<String, Double> growthRates = species.getStatGrowth();
        for (Map.Entry<String, Double> entry : growthRates.entrySet()) {
            String statName = entry. getKey();
            double growth = entry.getValue();

            double currentBase = pet.getBaseStats().getOrDefault(statName, 0.0);
            pet.setBaseStat(statName, currentBase + growth);
        }

        // 체력 회복
        pet. setMaxHealth(pet.getTotalStat("health"));
        pet.heal(pet.getMaxHealth() * 0.2); // 레벨업 시 20% 회복
    }

    /**
     * 스킬 해금 확인
     */
    private void checkSkillUnlock(Pet pet, int newLevel) {
        PetSpecies species = plugin. getSpeciesCache().getSpecies(pet.getSpeciesId());
        if (species == null) return;

        String newSkillId = species.getNewSkillAtLevel(newLevel);
        if (newSkillId != null && ! pet.hasSkill(newSkillId)) {
            // 스킬 매니저를 통해 스킬 해금
            plugin.getSkillManager().unlockSkill(pet, newSkillId);

            // 알림
            Player owner = Bukkit.getPlayer(pet.getOwnerId());
            if (owner != null) {
                MessageUtil.sendMessage(owner, plugin.getConfigManager().getMessage("pet.skill-unlocked")
                        .replace("{name}", pet.getPetName())
                        .replace("{skill}", newSkillId));
            }
        }
    }

    /**
     * 진화 가능 여부 확인
     */
    private void checkEvolutionAvailable(Pet pet, int newLevel) {
        // 진화 매니저에서 확인
        if (plugin.getEvolutionManager().hasAvailableEvolution(pet)) {
            Player owner = Bukkit.getPlayer(pet.getOwnerId());
            if (owner != null) {
                MessageUtil. sendMessage(owner, plugin.getConfigManager().getMessage("pet.evolution-available")
                        .replace("{name}", pet.getPetName()));
            }
        }
    }

    /**
     * 레벨업 알림
     */
    private void notifyLevelUp(Pet pet, int levelsGained) {
        Player owner = Bukkit.getPlayer(pet.getOwnerId());
        if (owner == null) return;

        String message;
        if (levelsGained == 1) {
            message = plugin.getConfigManager().getMessage("pet.level-up")
                    .replace("{name}", pet. getPetName())
                    .replace("{level}", String.valueOf(pet. getLevel()));
        } else {
            message = plugin.getConfigManager().getMessage("pet.multi-level-up")
                    .replace("{name}", pet. getPetName())
                    .replace("{levels}", String.valueOf(levelsGained))
                    .replace("{level}", String. valueOf(pet.getLevel()));
        }

        MessageUtil. sendMessage(owner, message);

        // 효과음
        owner.playSound(owner.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1. 0f, 1.0f);

        // 파티클 효과 (펫 엔티티가 있는 경우)
        if (pet.isActive()) {
            plugin.getEntityManager().playLevelUpEffect(pet. getPetId());
        }
    }

    // ===== 레벨 직접 조작 (관리자용) =====

    /**
     * 레벨 설정
     */
    public void setLevel(Pet pet, int level) {
        int targetLevel = Math.max(1, Math. min(level, getMaxLevelForPet(pet)));
        int currentLevel = pet.getLevel();

        if (targetLevel > currentLevel) {
            // 레벨업
            while (pet.getLevel() < targetLevel) {
                pet. levelUp();
                applyStatGrowth(pet);
                checkSkillUnlock(pet, pet.getLevel());
            }
        } else if (targetLevel < currentLevel) {
            // 레벨 다운 (스탯 재계산 필요)
            pet.setLevel(targetLevel);
            recalculateStats(pet);
        }

        pet.setExperience(0);
        pet.calculateExpToNext();

        savePetData(pet);
    }

    /**
     * 스탯 재계산 (레벨 다운 시)
     */
    private void recalculateStats(Pet pet) {
        PetSpecies species = plugin.getSpeciesCache().getSpecies(pet.getSpeciesId());
        if (species == null) return;

        // 기본 스탯으로 초기화
        pet.setBaseStats(species.getAllStatsAtLevel(pet.getLevel()));
        pet.setMaxHealth(pet.getTotalStat("health"));
        pet.setHealth(pet.getMaxHealth());
    }

    // ===== 경험치 공유 =====

    /**
     * 경험치 공유 (다중 펫 활성 시)
     */
    public void shareExperience(UUID ownerId, long totalExp, ExpSource source) {
        if (!enableExpShare) return;

        java.util.List<com.multiverse.pet.entity.PetEntity> activePets = 
            plugin.getPetManager().getActivePets(ownerId);

        if (activePets. isEmpty()) return;

        // 각 펫에게 분배
        long expPerPet = (long) (totalExp * expShareRatio / activePets.size());
        for (com.multiverse.pet.entity. PetEntity petEntity : activePets) {
            addExperience(petEntity. getPet(), expPerPet, source);
        }
    }

    // ===== 유틸리티 =====

    /**
     * 다음 레벨까지 필요 경험치 계산
     */
    public long calculateExpToNextLevel(int level) {
        return ExpCalculator.calculateRequiredExp(level);
    }

    /**
     * 특정 레벨까지 필요 총 경험치 계산
     */
    public long calculateTotalExpForLevel(int level) {
        long total = 0;
        for (int i = 1; i < level; i++) {
            total += calculateExpToNextLevel(i);
        }
        return total;
    }

    /**
     * 경험치 퍼센트 계산
     */
    public double getExpPercentage(Pet pet) {
        if (pet.getExpToNext() <= 0) return 100.0;
        return (double) pet.getExperience() / pet.getExpToNext() * 100.0;
    }

    /**
     * 펫 데이터 저장
     */
    private void savePetData(Pet pet) {
        plugin.getPetManager().savePetData(pet. getOwnerId(), pet);
    }

    /**
     * 설정 리로드
     */
    public void reload() {
        loadSettings();
    }

    // ===== Getter =====

    public double getBaseExpMultiplier() {
        return baseExpMultiplier;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public boolean isEnableExpShare() {
        return enableExpShare;
    }

    public double getExpShareRatio() {
        return expShareRatio;
    }

    // ===== 경험치 소스 열거형 =====

    /**
     * 경험치 소스
     */
    public enum ExpSource {
        MOB_KILL,
        PLAYER_KILL,
        MINING,
        FISHING,
        BATTLE_WIN,
        BATTLE_LOSE,
        QUEST,
        TRAINING,
        EXPLORING,
        ADMIN
    }
}