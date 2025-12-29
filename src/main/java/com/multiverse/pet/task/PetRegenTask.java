package com.multiverse.pet.task;

import com. multiverse.pet. PetCore;
import com.multiverse.pet.entity. PetEntity;
import com.multiverse.pet.model. Pet;
import com.multiverse.pet.model.PetStatus;
import org.bukkit. Bukkit;
import org.bukkit. entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit. scheduler.BukkitRunnable;

import java.util. List;
import java. util.UUID;

/**
 * 펫 재생 태스크
 * 펫 체력 자연 회복 처리
 */
public class PetRegenTask extends BukkitRunnable {

    private final PetCore plugin;

    // 설정값
    private double baseRegenRate;           // 기본 재생률 (초당 체력 %)
    private double outOfCombatMultiplier;   // 비전투 시 재생 배율
    private double lowHealthMultiplier;     // 낮은 체력 시 재생 배율
    private double happinessRegenBonus;     // 행복도 보너스 (100일 때)
    private int combatCooldown;             // 전투 후 재생 시작까지 시간 (초)

    public PetRegenTask(PetCore plugin) {
        this.plugin = plugin;
        loadSettings();
    }

    /**
     * 설정 로드
     */
    private void loadSettings() {
        var careSettings = plugin.getConfigManager().getCareSettings();
        this.baseRegenRate = careSettings.getHealthRegenRate();
        this.outOfCombatMultiplier = careSettings.getOutOfCombatRegenMultiplier();
        this.lowHealthMultiplier = careSettings.getLowHealthRegenMultiplier();
        this.happinessRegenBonus = careSettings.getHappinessRegenBonus();
        this.combatCooldown = careSettings. getCombatRegenCooldown();
    }

    @Override
    public void run() {
        for (Player player : Bukkit. getOnlinePlayers()) {
            processPlayer(player);
        }
    }

    /**
     * 플레이어의 펫들 처리
     */
    private void processPlayer(Player player) {
        UUID playerId = player. getUniqueId();

        // 활성 펫만 체력 재생
        List<PetEntity> activePets = plugin.getPetManager().getActivePets(playerId);

        for (PetEntity petEntity : activePets) {
            processPet(player, petEntity);
        }

        // 보관 중인 펫도 느린 재생
        List<Pet> allPets = plugin. getPetManager().getAllPets(playerId);
        for (Pet pet :  allPets) {
            if (! pet.isActive() && pet.getStatus() == PetStatus.STORED) {
                processStoredPet(player, pet);
            }
        }
    }

    /**
     * 활성 펫 재생 처리
     */
    private void processPet(Player player, PetEntity petEntity) {
        Pet pet = petEntity. getPet();
        LivingEntity entity = petEntity.getEntity();

        if (entity == null || entity. isDead()) {
            return;
        }

        // 기절 상태면 스킵
        if (pet.getStatus() == PetStatus. FAINTED) {
            return;
        }

        // 이미 최대 체력이면 스킵
        if (pet.getHealth() >= pet.getMaxHealth()) {
            return;
        }

        // 전투 중이면 스킵 (또는 감소된 재생)
        if (petEntity.isInCombat()) {
            long lastCombat = petEntity.getLastCombatTime();
            if (System.currentTimeMillis() - lastCombat < combatCooldown * 1000) {
                return;
            }
        }

        // 재생량 계산
        double regenAmount = calculateRegenAmount(pet, petEntity);

        // 체력 회복
        double newHealth = Math.min(pet. getHealth() + regenAmount, pet.getMaxHealth());
        pet.setHealth(newHealth);

        // 엔티티 체력 동기화
        syncEntityHealth(entity, pet);

        // 저장 (5초마다)
        if (System.currentTimeMillis() % 5000 < 1000) {
            plugin.getPetManager().savePetData(player.getUniqueId(), pet);
        }
    }

    /**
     * 보관 중인 펫 재생 처리
     */
    private void processStoredPet(Player player, Pet pet) {
        // 기절 상태면 스킵
        if (pet. getStatus() == PetStatus.FAINTED) {
            return;
        }

        // 이미 최대 체력이면 스킵
        if (pet.getHealth() >= pet.getMaxHealth()) {
            return;
        }

        // 보관 중인 펫은 절반 속도로 재생
        double regenAmount = calculateStoredRegenAmount(pet);

        // 체력 회복
        double newHealth = Math.min(pet. getHealth() + regenAmount, pet. getMaxHealth());
        pet.setHealth(newHealth);

        // 저장 (10초마다)
        if (System. currentTimeMillis() % 10000 < 1000) {
            plugin. getPetManager().savePetData(player.getUniqueId(), pet);
        }
    }

    /**
     * 재생량 계산 (활성 펫)
     */
    private double calculateRegenAmount(Pet pet, PetEntity petEntity) {
        double maxHealth = pet.getMaxHealth();
        double currentHealth = pet.getHealth();
        double healthPercent = currentHealth / maxHealth * 100;

        // 기본 재생량 (최대 체력의 %)
        double regenAmount = maxHealth * (baseRegenRate / 100);

        // 비전투 보너스
        if (! petEntity.isInCombat()) {
            regenAmount *= outOfCombatMultiplier;
        }

        // 낮은 체력 보너스 (30% 이하일 때)
        if (healthPercent < 30) {
            regenAmount *= lowHealthMultiplier;
        }

        // 행복도 보너스
        double happinessPercent = pet.getHappiness() / 100;
        regenAmount *= (1 + happinessRegenBonus * happinessPercent);

        // 배고픔 페널티
        if (pet.getHunger() < 30) {
            regenAmount *= 0.5;
        }
        if (pet.getHunger() <= 0) {
            regenAmount = 0;
        }

        // 스탯 보너스 (재생 스탯이 있는 경우)
        double regenStat = pet.getTotalStat("regeneration");
        if (regenStat > 0) {
            regenAmount += regenStat * 0.1;
        }

        return regenAmount;
    }

    /**
     * 재생량 계산 (보관 펫)
     */
    private double calculateStoredRegenAmount(Pet pet) {
        double maxHealth = pet.getMaxHealth();

        // 기본 재생량의 절반
        double regenAmount = maxHealth * (baseRegenRate / 100) * 0.5;

        // 행복도 보너스
        double happinessPercent = pet.getHappiness() / 100;
        regenAmount *= (1 + happinessRegenBonus * happinessPercent * 0.5);

        return regenAmount;
    }

    /**
     * 엔티티 체력 동기화
     */
    private void syncEntityHealth(LivingEntity entity, Pet pet) {
        double healthRatio = pet.getHealth() / pet.getMaxHealth();
        double entityMaxHealth = entity.getMaxHealth();
        double newEntityHealth = entityMaxHealth * healthRatio;

        if (newEntityHealth > 0 && newEntityHealth <= entityMaxHealth) {
            entity.setHealth(newEntityHealth);
        }
    }

    /**
     * 설정 리로드
     */
    public void reloadSettings() {
        loadSettings();
    }

    /**
     * 태스크 시작
     */
    public void start() {
        // 1초(20틱)마다 실행
        this.runTaskTimer(plugin, 20L, 20L);
    }

    /**
     * 태스크 중지
     */
    public void stop() {
        try {
            this.cancel();
        } catch (IllegalStateException ignored) {
        }
    }
}