package com.multiverse.pet. task;

import com.multiverse.pet.PetCore;
import com. multiverse.pet. entity.PetEntity;
import com.multiverse. pet.model.Pet;
import com.multiverse. pet.model.PetStatus;
import com. multiverse.pet. util.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java. util.UUID;

/**
 * 펫 케어 태스크
 * 배고픔, 행복도 감소 및 경고 처리
 */
public class PetCareTask extends BukkitRunnable {

    private final PetCore plugin;

    // 설정값
    private double hungerDecreaseRate;      // 분당 배고픔 감소량
    private double happinessDecreaseRate;   // 분당 행복도 감소량
    private double activeHungerMultiplier;  // 활성 펫 배고픔 감소 배율
    private double activeHappinessMultiplier; // 활성 펫 행복도 감소 배율

    // 경고 임계값
    private static final double HUNGER_WARNING = 30.0;
    private static final double HUNGER_CRITICAL = 10.0;
    private static final double HAPPINESS_WARNING = 30.0;
    private static final double HAPPINESS_CRITICAL = 10.0;

    // 경고 쿨다운 (분)
    private static final int WARNING_COOLDOWN = 5;
    private final java.util.Map<UUID, Long> lastWarningTime;

    public PetCareTask(PetCore plugin) {
        this.plugin = plugin;
        this.lastWarningTime = new java.util.concurrent.ConcurrentHashMap<>();
        loadSettings();
    }

    /**
     * 설정 로드
     */
    private void loadSettings() {
        var careSettings = plugin.getConfigManager().getCareSettings();
        this.hungerDecreaseRate = careSettings.getHungerDecreaseRate();
        this.happinessDecreaseRate = careSettings. getHappinessDecreaseRate();
        this.activeHungerMultiplier = careSettings. getActiveHungerMultiplier();
        this.activeHappinessMultiplier = careSettings. getActiveHappinessMultiplier();
    }

    @Override
    public void run() {
        for (Player player :  Bukkit.getOnlinePlayers()) {
            processPlayer(player);
        }
    }

    /**
     * 플레이어의 모든 펫 처리
     */
    private void processPlayer(Player player) {
        UUID playerId = player.getUniqueId();
        List<Pet> allPets = plugin.getPetManager().getAllPets(playerId);

        for (Pet pet :  allPets) {
            processPet(player, pet);
        }
    }

    /**
     * 개별 펫 처리
     */
    private void processPet(Player player, Pet pet) {
        // 기절 상태면 스킵
        if (pet.getStatus() == PetStatus. FAINTED) {
            return;
        }

        boolean isActive = pet. isActive();
        boolean needsSave = false;

        // 배고픔 감소
        double hungerDecrease = hungerDecreaseRate;
        if (isActive) {
            hungerDecrease *= activeHungerMultiplier;
        }

        if (pet.getHunger() > 0) {
            pet.decreaseHunger(hungerDecrease);
            needsSave = true;

            // 배고픔 경고
            checkHungerWarning(player, pet);
        }

        // 행복도 감소
        double happinessDecrease = happinessDecreaseRate;
        if (isActive) {
            happinessDecrease *= activeHappinessMultiplier;
        }

        // 배고프면 행복도 더 빨리 감소
        if (pet.getHunger() < 30) {
            happinessDecrease *= 1.5;
        }

        if (pet. getHappiness() > 0) {
            pet.decreaseHappiness(happinessDecrease);
            needsSave = true;

            // 행복도 경고
            checkHappinessWarning(player, pet);
        }

        // 배고픔/행복도가 0이면 패널티
        applyLowStatPenalties(player, pet);

        // 저장
        if (needsSave) {
            plugin.getPetManager().savePetData(player. getUniqueId(), pet);
        }
    }

    /**
     * 배고픔 경고 체크
     */
    private void checkHungerWarning(Player player, Pet pet) {
        if (! canSendWarning(pet. getPetId())) {
            return;
        }

        if (pet.getHunger() <= HUNGER_CRITICAL && pet.getHunger() > 0) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.hunger-critical")
                    .replace("{name}", pet.getPetName()));
            markWarning(pet. getPetId());
        } else if (pet.getHunger() <= HUNGER_WARNING && pet.getHunger() > HUNGER_CRITICAL) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.hunger-warning")
                    .replace("{name}", pet. getPetName())
                    .replace("{hunger}", String.format("%.0f", pet.getHunger())));
            markWarning(pet.getPetId());
        }
    }

    /**
     * 행복도 경고 체크
     */
    private void checkHappinessWarning(Player player, Pet pet) {
        if (!canSendWarning(pet.getPetId())) {
            return;
        }

        if (pet.getHappiness() <= HAPPINESS_CRITICAL && pet.getHappiness() > 0) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.happiness-critical")
                    . replace("{name}", pet.getPetName()));
            markWarning(pet.getPetId());
        } else if (pet. getHappiness() <= HAPPINESS_WARNING && pet.getHappiness() > HAPPINESS_CRITICAL) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.happiness-warning")
                    .replace("{name}", pet. getPetName())
                    .replace("{happiness}", String.format("%.0f", pet.getHappiness())));
            markWarning(pet.getPetId());
        }
    }

    /**
     * 낮은 상태 패널티 적용
     */
    private void applyLowStatPenalties(Player player, Pet pet) {
        // 배고픔 0 -> 체력 감소
        if (pet. getHunger() <= 0 && pet.isActive()) {
            double healthDamage = pet. getMaxHealth() * 0.01; // 1%씩 감소
            pet.takeDamage(healthDamage);

            if (pet.getHealth() <= 0) {
                handlePetFaint(player, pet);
            }
        }

        // 행복도 0 -> 스탯 감소 (이미 Pet에서 처리)
        // 추가로 도망 확률 체크
        if (pet.getHappiness() <= 0 && plugin.getConfigManager().getCareSettings().isRunawayEnabled()) {
            checkRunaway(player, pet);
        }
    }

    /**
     * 펫 기절 처리
     */
    private void handlePetFaint(Player player, Pet pet) {
        pet.setStatus(PetStatus.FAINTED);
        pet.setHealth(0);

        // 활성 펫이면 해제
        if (pet.isActive()) {
            plugin.getPetManager().unsummonPet(player, pet. getPetId());
        }

        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.pet-fainted")
                .replace("{name}", pet.getPetName()));

        plugin.getPetManager().savePetData(player.getUniqueId(), pet);
    }

    /**
     * 도망 확률 체크
     */
    private void checkRunaway(Player player, Pet pet) {
        double runawayChance = plugin.getConfigManager().getCareSettings().getRunawayChance();

        if (Math.random() * 100 < runawayChance) {
            // 즐겨찾기 펫은 도망가지 않음
            if (pet.isFavorite()) {
                return;
            }

            // 희귀도가 높으면 도망 확률 감소
            if (pet.getRarity().ordinal() >= 3 && Math.random() > 0.3) {
                return;
            }

            // 도망 처리
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.pet-runaway")
                    .replace("{name}", pet.getPetName()));

            // 활성 펫이면 해제
            if (pet. isActive()) {
                plugin.getPetManager().unsummonPet(player, pet.getPetId());
            }

            // 펫 제거
            plugin.getPetManager().removePet(player. getUniqueId(), pet.getPetId());
        }
    }

    /**
     * 경고 전송 가능 여부
     */
    private boolean canSendWarning(UUID petId) {
        Long lastTime = lastWarningTime.get(petId);
        if (lastTime == null) {
            return true;
        }
        return System.currentTimeMillis() - lastTime > WARNING_COOLDOWN * 60 * 1000;
    }

    /**
     * 경고 시간 기록
     */
    private void markWarning(UUID petId) {
        lastWarningTime.put(petId, System.currentTimeMillis());
    }

    /**
     * 경고 쿨다운 정리
     */
    public void cleanupWarnings() {
        long threshold = System.currentTimeMillis() - WARNING_COOLDOWN * 60 * 1000 * 2;
        lastWarningTime.entrySet().removeIf(entry -> entry.getValue() < threshold);
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
        // 1분(1200틱)마다 실행
        this.runTaskTimer(plugin, 1200L, 1200L);
    }

    /**
     * 태스크 중지
     */
    public void stop() {
        try {
            this. cancel();
        } catch (IllegalStateException ignored) {
        }
    }
}