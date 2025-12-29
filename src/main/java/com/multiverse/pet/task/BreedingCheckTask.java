package com.multiverse.pet.task;

import com. multiverse.pet. PetCore;
import com.multiverse.pet.model.Pet;
import com.multiverse. pet.model.breeding.BreedingStatus;
import com. multiverse.pet. model.breeding.PetBreeding;
import com.multiverse.pet.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org. bukkit.entity. Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java. util.List;
import java.util. UUID;

/**
 * 교배 체크 태스크
 * 진행 중인 교배 완료 여부 확인
 */
public class BreedingCheckTask extends BukkitRunnable {

    private final PetCore plugin;

    public BreedingCheckTask(PetCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        checkAllBreedings();
    }

    /**
     * 모든 교배 체크
     */
    private void checkAllBreedings() {
        List<PetBreeding> allBreedings = plugin.getBreedingManager().getAllActiveBreedings();

        for (PetBreeding breeding : new ArrayList<>(allBreedings)) {
            processBreeding(breeding);
        }
    }

    /**
     * 개별 교배 처리
     */
    private void processBreeding(PetBreeding breeding) {
        // 이미 완료되었거나 취소된 경우 스킵
        if (breeding.getStatus() != BreedingStatus.IN_PROGRESS) {
            return;
        }

        // 교배 완료 시간 확인
        if (! breeding.isComplete()) {
            return;
        }

        // 교배 완료 처리
        completeBreeding(breeding);
    }

    /**
     * 교배 완료 처리
     */
    private void completeBreeding(PetBreeding breeding) {
        UUID ownerId = breeding. getOwnerId();

        // 성공/실패 판정
        boolean success = rollBreedingSuccess(breeding);

        if (success) {
            // 자손 결정
            String offspringSpecies = determineOffspringSpecies(breeding);
            boolean isMutation = rollMutation(breeding);

            breeding.setOffspringSpeciesId(offspringSpecies);
            breeding.setMutation(isMutation);
            breeding.setStatus(BreedingStatus.AWAITING_COLLECTION);

            // 플레이어가 온라인이면 알림
            Player player = Bukkit.getPlayer(ownerId);
            if (player != null) {
                notifyBreedingComplete(player, breeding);
            }

            if (plugin.isDebugMode()) {
                plugin.getLogger().info("[DEBUG] 교배 완료: " + breeding. getBreedingId() + 
                        " -> " + offspringSpecies + (isMutation ? " (변이! )" : ""));
            }

        } else {
            breeding.setStatus(BreedingStatus. FAILED);

            // 실패 알림
            Player player = Bukkit.getPlayer(ownerId);
            if (player != null) {
                notifyBreedingFailed(player, breeding);
            }

            if (plugin. isDebugMode()) {
                plugin. getLogger().info("[DEBUG] 교배 실패: " + breeding.getBreedingId());
            }
        }

        // 저장
        plugin.getBreedingManager().updateBreeding(breeding);
    }

    /**
     * 교배 성공 판정
     */
    private boolean rollBreedingSuccess(PetBreeding breeding) {
        double baseChance = plugin. getBreedingManager().getBaseSuccessChance();

        // 부모 행복도 보너스
        Pet parent1 = plugin.getPetManager().getPetById(breeding.getOwnerId(), breeding.getParent1Id());
        Pet parent2 = plugin.getPetManager().getPetById(breeding.getOwnerId(), breeding.getParent2Id());

        if (parent1 != null && parent2 != null) {
            double avgHappiness = (parent1.getHappiness() + parent2.getHappiness()) / 2;
            baseChance += avgHappiness * 0.1; // 행복도 100이면 +10%
        }

        // 최대 95%
        baseChance = Math.min(baseChance, 95);

        return Math.random() * 100 < baseChance;
    }

    /**
     * 자손 종족 결정
     */
    private String determineOffspringSpecies(PetBreeding breeding) {
        List<String> possibleSpecies = plugin.getBreedingManager()
                .getPossibleOffspringSpecies(
                        plugin.getPetManager().getPetById(breeding.getOwnerId(), breeding.getParent1Id()),
                        plugin.getPetManager().getPetById(breeding.getOwnerId(), breeding.getParent2Id())
                );

        if (possibleSpecies.isEmpty()) {
            // 기본값:  부모 중 하나
            return breeding.getParent1SpeciesId();
        }

        // 랜덤 선택
        return possibleSpecies.get((int) (Math.random() * possibleSpecies. size()));
    }

    /**
     * 변이 판정
     */
    private boolean rollMutation(PetBreeding breeding) {
        Pet parent1 = plugin.getPetManager().getPetById(breeding.getOwnerId(), breeding.getParent1Id());
        Pet parent2 = plugin.getPetManager().getPetById(breeding.getOwnerId(), breeding.getParent2Id());

        if (parent1 == null || parent2 == null) {
            return false;
        }

        double mutationChance = plugin.getBreedingManager().calculateMutationChance(parent1, parent2);
        return Math.random() * 100 < mutationChance;
    }

    /**
     * 교배 완료 알림
     */
    private void notifyBreedingComplete(Player player, PetBreeding breeding) {
        // 메시지
        String message = plugin.getConfigManager().getMessage("breeding.complete")
                .replace("{parent1}", breeding.getParent1SpeciesId())
                .replace("{parent2}", breeding.getParent2SpeciesId());

        if (breeding.isMutation()) {
            message += " " + plugin.getConfigManager().getMessage("breeding.mutation-occurred");
        }

        MessageUtil.sendMessage(player, message);

        // 사운드
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);

        // 타이틀
        player.sendTitle(
                "§d교배 완료! ",
                "§7결과를 수령하세요",
                10, 40, 10
        );
    }

    /**
     * 교배 실패 알림
     */
    private void notifyBreedingFailed(Player player, PetBreeding breeding) {
        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.failed")
                .replace("{parent1}", breeding.getParent1SpeciesId())
                .replace("{parent2}", breeding.getParent2SpeciesId()));

        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
    }

    /**
     * 오프라인 플레이어 교배 완료 체크
     * (로그인 시 호출)
     */
    public void checkPlayerBreedings(Player player) {
        UUID playerId = player.getUniqueId();
        List<PetBreeding> breedings = plugin.getBreedingManager().getPlayerBreedings(playerId);

        int completedCount = 0;
        int failedCount = 0;

        for (PetBreeding breeding : breedings) {
            if (breeding.getStatus() == BreedingStatus.IN_PROGRESS && breeding.isComplete()) {
                processBreeding(breeding);
            }

            if (breeding. getStatus() == BreedingStatus. AWAITING_COLLECTION) {
                completedCount++;
            } else if (breeding.getStatus() == BreedingStatus.FAILED) {
                failedCount++;
            }
        }

        // 요약 알림
        if (completedCount > 0) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.completed-waiting")
                    . replace("{count}", String.valueOf(completedCount)));
        }

        if (failedCount > 0) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.failed-summary")
                    . replace("{count}", String.valueOf(failedCount)));
        }
    }

    /**
     * 태스크 시작
     */
    public void start() {
        // 30초(600틱)마다 실행
        this.runTaskTimer(plugin, 600L, 600L);
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