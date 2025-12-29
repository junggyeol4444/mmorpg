package com.multiverse.pet.  task;

import com.multiverse.pet.PetCore;
import com. multiverse.  pet.model.Pet;
import com.  multiverse.pet.  model.acquisition.PetEgg;
import com.multiverse.pet.util.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit.  Particle;
import org. bukkit.Sound;
import org.  bukkit.entity. Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.  util.List;
import java.util.  UUID;

/**
 * 알 부화 태스크
 * 부화 중인 알의 진행 상황 체크
 */
public class EggHatchTask extends BukkitRunnable {

    private final PetCore plugin;

    public EggHatchTask(PetCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        checkAllEggs();
    }

    /**
     * 모든 부화 중인 알 체크
     */
    private void checkAllEggs() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            checkPlayerEggs(player);
        }
    }

    /**
     * 플레이어의 알 체크
     */
    public void checkPlayerEggs(Player player) {
        UUID playerId = player.getUniqueId();
        List<PetEgg> eggs = plugin.getPetAcquisitionManager().getPlayerEggs(playerId);

        for (PetEgg egg :   new ArrayList<>(eggs)) {
            if (egg.isHatching()) {
                processEgg(player, egg);
            }
        }
    }

    /**
     * 개별 알 처리
     */
    private void processEgg(Player player, PetEgg egg) {
        // 부화 완료 확인
        if (egg.isReadyToHatch()) {
            hatchEgg(player, egg);
            return;
        }

        // 부화 조건 유지 확인
        if (! checkHatchConditions(player, egg)) {
            // 조건 미충족 시 일시 정지
            pauseHatching(player, egg);
            return;
        }

        // 진행률 업데이트 (UI 등에서 사용)
        updateProgress(player, egg);
    }

    /**
     * 부화 조건 확인
     */
    private boolean checkHatchConditions(Player player, PetEgg egg) {
        // 바이옴 조건
        if (egg. getRequiredBiome() != null) {
            String currentBiome = player.getLocation().getBlock().getBiome().name();
            if (!currentBiome.equalsIgnoreCase(egg.getRequiredBiome())) {
                return false;
            }
        }

        // 시간 조건
        long worldTime = player.getWorld().getTime();
        boolean isDaytime = worldTime < 13000;

        if (egg.isRequiresDaytime() && !isDaytime) {
            return false;
        }

        if (egg.isRequiresNighttime() && isDaytime) {
            return false;
        }

        // 플레이어 레벨 조건
        if (egg.getRequiredPlayerLevel() > 0) {
            if (player.getLevel() < egg.getRequiredPlayerLevel()) {
                return false;
            }
        }

        return true;
    }

    /**
     * 부화 일시 정지
     */
    private void pauseHatching(Player player, PetEgg egg) {
        if (! egg.isPaused()) {
            egg.setPaused(true);

            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("egg.hatching-paused")
                    .replace("{name}", egg.getName())
                    .replace("{reason}", egg.getCannotHatchReason(
                            player.getLevel(),
                            player. getLocation().getBlock().getBiome().name(),
                            player.getWorld().getTime() < 13000
                    )));
        }
    }

    /**
     * 진행률 업데이트
     */
    private void updateProgress(Player player, PetEgg egg) {
        // 일시 정지 상태였으면 재개
        if (egg.isPaused()) {
            egg.setPaused(false);
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("egg.hatching-resumed")
                    .replace("{name}", egg.getName()));
        }

        // 진행 이펙트 (10% 단위)
        double progress = egg.getHatchProgress();
        int progressStep = (int) (progress / 10);

        if (progressStep > egg.getLastProgressStep()) {
            egg.setLastProgressStep(progressStep);

            // 이펙트
            if (progressStep % 2 == 0) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5f, 1.0f + progressStep * 0.05f);
            }

            // 알림 (50%, 75%, 90%)
            if (progressStep == 5 || progressStep == 7 || progressStep == 9) {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("egg.hatching-progress")
                        .replace("{name}", egg.getName())
                        .replace("{progress}", String. format("%.0f", progress)));
            }
        }
    }

    /**
     * 알 부화
     */
    private void hatchEgg(Player player, PetEgg egg) {
        UUID playerId = player. getUniqueId();

        // 보관함 여유 확인
        if (plugin.getPetStorageManager().isStorageFull(playerId)) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("egg.storage-full")
                    .replace("{name}", egg.getName()));

            // 부화 대기 상태로 유지
            return;
        }

        // 종족 결정
        String speciesId = egg. determineSpecies();
        if (speciesId == null) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("egg.hatch-failed")
                    .replace("{name}", egg.getName()));

            plugin.getPetAcquisitionManager().removeEgg(playerId, egg.getEggId());
            return;
        }

        // 펫 생성
        Pet pet = createPetFromEgg(egg, speciesId, playerId);

        // 펫 추가
        if (plugin.getPetManager().addNewPet(playerId, pet)) {
            // 알 제거
            plugin.getPetAcquisitionManager().removeEgg(playerId, egg.getEggId());

            // 이펙트
            playHatchEffect(player);

            // 알림
            String message = plugin.getConfigManager().getMessage("egg.hatched")
                    . replace("{egg}", egg.getName())
                    .replace("{pet}", pet.getPetName())
                    .replace("{rarity}", pet.getRarity().getColoredName());

            MessageUtil.sendMessage(player, message);

            // 변이 알림
            if (pet.isMutation()) {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("egg.mutation-hatched"));
                player.sendTitle("§d✦ 변이 발생!  ✦", "§f" + pet.getPetName(), 10, 60, 20);
            } else {
                player.sendTitle("§a알 부화!", "§f" + pet.getPetName() + " §7탄생!", 10, 40, 10);
            }

            if (plugin.isDebugMode()) {
                plugin.getLogger().info("[DEBUG] 알 부화:  " + egg.getName() + " -> " + 
                        pet.getPetName() + " (" + pet.getRarity() + ")");
            }
        }
    }

    /**
     * 알에서 펫 생성
     */
    private Pet createPetFromEgg(PetEgg egg, String speciesId, UUID ownerId) {
        var species = plugin.getSpeciesCache().getSpecies(speciesId);

        Pet pet = new Pet();
        pet.setOwnerId(ownerId);
        pet.setSpeciesId(speciesId);
        pet.setPetName(species != null ? species.getName() : speciesId);
        pet.setType(species != null ? species. getType() : null);
        pet.setRarity(egg.determineRarity());
        pet.setLevel(1);

        if (species != null) {
            pet. setEntityType(species.getEntityType());
            pet.setBaseStats(species.getAllStatsAtLevel(1));
        }

        pet.setMaxHealth(pet.getTotalStat("health"));
        pet.setHealth(pet.getMaxHealth());
        pet.setHunger(100);
        pet.setHappiness(100);

        // 알 보너스 스탯 적용
        for (var entry : egg.getStatBonuses().entrySet()) {
            double current = pet.getBaseStats().getOrDefault(entry.getKey(), 0.0);
            pet.setBaseStat(entry. getKey(), current + entry.getValue());
        }

        // 변이 확인
        double mutationChance = egg.getMutationChance();
        if (egg.rollMutation(mutationChance)) {
            pet. setMutation(true);
            // 변이 보너스
            for (String stat : pet.getBaseStats().keySet()) {
                double current = pet.getBaseStats().get(stat);
                pet.setBaseStat(stat, current * 1.15); // 15% 보너스
            }
        }

        return pet;
    }

    /**
     * 부화 이펙트
     */
    private void playHatchEffect(Player player) {
        player.getWorld().spawnParticle(
                Particle. TOTEM,
                player.getLocation().add(0, 1, 0),
                50, 0.5, 0.5, 0.5, 0.2
        );
        player.getWorld().spawnParticle(
                Particle. FIREWORKS_SPARK,
                player. getLocation().add(0, 1, 0),
                30, 0.5, 0.5, 0.5, 0.1
        );

        player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5f, 1.0f);
    }

    /**
     * 태스크 시작
     */
    public void start() {
        // 10초(200틱)마다 실행
        this.runTaskTimer(plugin, 200L, 200L);
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