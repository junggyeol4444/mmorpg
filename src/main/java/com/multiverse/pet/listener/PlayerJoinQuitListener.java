package com.multiverse.pet.listener;

import com.multiverse. pet.PetCore;
import com. multiverse.pet. entity.PetEntity;
import com.multiverse. pet.model.Pet;
import com.multiverse. pet.util.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;
import org.bukkit.event.EventHandler;
import org. bukkit.event. EventPriority;
import org.bukkit. event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org. bukkit.event. player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util. UUID;

/**
 * 플레이어 입장/퇴장 관련 리스너
 * 데이터 로드/저장, 펫 자동 소환/해제
 */
public class PlayerJoinQuitListener implements Listener {

    private final PetCore plugin;

    public PlayerJoinQuitListener(PetCore plugin) {
        this.plugin = plugin;
    }

    // ===== 플레이어 입장 =====

    /**
     * 플레이어 입장 처리
     */
    @EventHandler(priority = EventPriority. NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // 비동기로 데이터 로드
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // 펫 데이터 로드
                plugin.getPetDataManager().loadPlayerData(playerId);

                // 메인 스레드에서 후속 처리
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (player.isOnline()) {
                        onDataLoaded(player);
                    }
                });

            } catch (Exception e) {
                plugin.getLogger().warning("플레이어 데이터 로드 실패: " + player.getName() + " - " + e.getMessage());
            }
        });
    }

    /**
     * 데이터 로드 완료 후 처리
     */
    private void onDataLoaded(Player player) {
        UUID playerId = player. getUniqueId();

        // 펫 관련 알림
        sendPetNotifications(player);

        // 자동 소환
        if (plugin.getConfigManager().getPetSettings().isAutoSummonOnJoin()) {
            autoSummonLastPet(player);
        }

        // 케어가 필요한 펫 알림
        checkPetsNeedingCare(player);

        // 교배 완료 알림
        checkCompletedBreedings(player);

        // 알 부화 완료 알림
        checkHatchedEggs(player);

        // 디버그 로그
        if (plugin.isDebugMode()) {
            int petCount = plugin.getPetManager().getAllPets(playerId).size();
            plugin.getLogger().info("[DEBUG] " + player.getName() + " 입장 - 펫 " + petCount + "마리 로드됨");
        }
    }

    /**
     * 펫 관련 알림 전송
     */
    private void sendPetNotifications(Player player) {
        UUID playerId = player. getUniqueId();
        List<Pet> pets = plugin.getPetManager().getAllPets(playerId);

        if (pets.isEmpty()) {
            // 첫 접속 또는 펫 없음
            if (plugin.getConfigManager().getPetSettings().isShowWelcomeMessage()) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) {
                        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("welcome.no-pets"));
                    }
                }, 40L); // 2초 후
            }
        } else {
            // 펫 보유 알림
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("welcome.has-pets")
                            .replace("{count}", String.valueOf(pets. size())));
                }
            }, 40L);
        }
    }

    /**
     * 마지막 활성 펫 자동 소환
     */
    private void autoSummonLastPet(Player player) {
        UUID playerId = player.getUniqueId();
        List<Pet> pets = plugin.getPetManager().getAllPets(playerId);

        Pet lastActivePet = null;

        // wasLastActive 플래그가 있는 펫 찾기
        for (Pet pet : pets) {
            if (pet.wasLastActive()) {
                lastActivePet = pet;
                break;
            }
        }

        // 없으면 즐겨찾기 펫
        if (lastActivePet == null) {
            for (Pet pet : pets) {
                if (pet.isFavorite() && pet.getStatus().canBeSummoned()) {
                    lastActivePet = pet;
                    break;
                }
            }
        }

        // 그래도 없으면 첫 번째 소환 가능한 펫
        if (lastActivePet == null) {
            for (Pet pet : pets) {
                if (pet.getStatus().canBeSummoned()) {
                    lastActivePet = pet;
                    break;
                }
            }
        }

        // 소환
        if (lastActivePet != null) {
            final Pet petToSummon = lastActivePet;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    plugin.getPetManager().summonPet(player, petToSummon. getPetId());
                }
            }, 60L); // 3초 후
        }
    }

    /**
     * 케어가 필요한 펫 확인
     */
    private void checkPetsNeedingCare(Player player) {
        UUID playerId = player. getUniqueId();
        List<Pet> needsCare = plugin.getPetCareManager().getPetsNeedingCare(playerId);

        if (!needsCare.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    StringBuilder sb = new StringBuilder();
                    sb. append(plugin.getConfigManager().getMessage("care. pets-need-attention"));
                    
                    for (Pet pet : needsCare) {
                        sb.append("\n§7- §f").append(pet.getPetName());
                        
                        if (pet.getHunger() < 30) {
                            sb.append(" §e🍖");
                        }
                        if (pet.getHappiness() < 30) {
                            sb.append(" §d😢");
                        }
                        if (pet.getHealth() < pet.getMaxHealth() * 0.5) {
                            sb.append(" §c❤");
                        }
                    }

                    MessageUtil. sendMessage(player, sb.toString());
                }
            }, 80L); // 4초 후
        }
    }

    /**
     * 완료된 교배 확인
     */
    private void checkCompletedBreedings(Player player) {
        UUID playerId = player.getUniqueId();
        int completedCount = plugin.getBreedingManager().getCompletedBreedingCount(playerId);

        if (completedCount > 0) {
            Bukkit. getScheduler().runTaskLater(plugin, () -> {
                if (player. isOnline()) {
                    MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.completed-waiting")
                            . replace("{count}", String.valueOf(completedCount)));
                }
            }, 100L); // 5초 후
        }
    }

    /**
     * 부화된 알 확인
     */
    private void checkHatchedEggs(Player player) {
        UUID playerId = player.getUniqueId();
        // 부화 완료된 알 확인 로직
        // PetAcquisitionManager에서 처리
    }

    // ===== 플레이어 퇴장 =====

    /**
     * 플레이어 퇴장 처리
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        handlePlayerLeave(event.getPlayer());
    }

    /**
     * 플레이어 킥 처리
     */
    @EventHandler(priority = EventPriority. NORMAL)
    public void onPlayerKick(PlayerKickEvent event) {
        handlePlayerLeave(event.getPlayer());
    }

    /**
     * 플레이어 퇴장 공통 처리
     */
    private void handlePlayerLeave(Player player) {
        UUID playerId = player.getUniqueId();

        // 마지막 활성 펫 기록
        List<PetEntity> activePets = plugin. getPetManager().getActivePets(playerId);
        for (PetEntity petEntity : activePets) {
            Pet pet = petEntity. getPet();
            pet.setLastActive(true);
        }

        // 모든 활성 펫 해제
        plugin. getPetManager().unsummonAllPets(player);

        // 배틀 중이면 항복 처리
        if (plugin.getPetBattleManager().isInBattle(playerId)) {
            plugin.getPetBattleManager().surrender(player);
        }

        // 비동기로 데이터 저장
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                plugin.getPetDataManager().savePlayerData(playerId);

                if (plugin.isDebugMode()) {
                    plugin. getLogger().info("[DEBUG] " + player.getName() + " 퇴장 - 데이터 저장 완료");
                }

            } catch (Exception e) {
                plugin.getLogger().warning("플레이어 데이터 저장 실패: " + player.getName() + " - " + e.getMessage());
            }
        });

        // 캐시 정리 (지연)
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // 다시 접속하지 않았으면 캐시 정리
            if (Bukkit.getPlayer(playerId) == null) {
                plugin.getPetStorageManager().cleanupPlayerData(playerId);
                plugin.getPetCache().removePlayer(playerId);
            }
        }, 600L); // 30초 후
    }
}