package com.multiverse.pvp. listeners;

import com.multiverse.pvp.PvPCore;
import com. multiverse.pvp.data.PvPZone;
import com.multiverse.pvp.enums. StreakLevel;
import com. multiverse.pvp.enums.ZoneType;
import com.multiverse. pvp.utils.MessageUtil;
import org. bukkit.Material;
import org. bukkit.entity. Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit. event.Listener;
import org. bukkit.event. entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org. bukkit.event. player.PlayerJoinEvent;
import org.bukkit. event.player.PlayerQuitEvent;
import org.bukkit. event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java. util.Map;
import java.util. UUID;

public class PvPListener implements Listener {

    private final PvPCore plugin;
    
    // 어시스트 추적 (피해자 UUID -> 공격자 UUID -> 데미지)
    private final Map<UUID, Map<UUID, Double>> assistTracker;
    
    // 멀티킬 추적 (킬러 UUID -> 마지막 킬 시간)
    private final Map<UUID, Long> lastKillTime;
    private final Map<UUID, Integer> multiKillCount;
    
    private static final long MULTI_KILL_WINDOW = 10000; // 10초
    private static final double ASSIST_THRESHOLD = 20.0; // 최소 20 데미지

    public PvPListener(PvPCore plugin) {
        this.plugin = plugin;
        this.assistTracker = new HashMap<>();
        this.lastKillTime = new HashMap<>();
        this.multiKillCount = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player attacker = getAttacker(event);

        if (attacker == null || attacker.equals(victim)) {
            return;
        }

        // PvP 가능 여부 확인
        if (!plugin. getPvPModeManager().canAttack(attacker, victim)) {
            event.setCancelled(true);
            
            String reason = plugin.getPvPModeManager().getBlockReason(attacker, victim);
            MessageUtil.sendMessage(attacker, "&c" + reason);
            return;
        }

        // 데미지 기록
        double damage = event.getFinalDamage();
        
        // 통계 기록
        plugin. getStatisticsManager().recordDamageDealt(attacker, damage);
        plugin.getStatisticsManager().recordDamageReceived(victim, damage);

        // 듀얼 중이면 듀얼 매니저에 기록
        if (plugin.getDuelManager().isInDuel(attacker)) {
            plugin. getDuelManager().recordDamage(attacker, victim, damage);
        }

        // 어시스트 추적
        trackAssist(victim. getUniqueId(), attacker.getUniqueId(), damage);

        // 전투 태그 업데이트
        plugin.getPvPModeManager().updateCombatTag(attacker, victim);
        plugin.getPvPModeManager().updateCombatTag(victim, attacker);
    }

    @EventHandler(priority = EventPriority. HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        // 킬러가 없으면 처리 안함
        if (killer == null) {
            // 전투 중 사망 체크 (낙사 등)
            if (plugin.getPvPModeManager().isInCombat(victim)) {
                UUID lastAttacker = plugin.getPvPModeManager().getPvPMode(victim).getLastAttacker();
                if (lastAttacker != null) {
                    killer = org.bukkit. Bukkit.getPlayer(lastAttacker);
                }
            }
        }

        if (killer == null || killer.equals(victim)) {
            // PvP가 아닌 사망
            handleNonPvPDeath(victim, event);
            return;
        }

        // PvP 사망 처리
        handlePvPDeath(victim, killer, event);
    }

    /**
     * PvP 사망 처리
     */
    private void handlePvPDeath(Player victim, Player killer, PlayerDeathEvent event) {
        // 듀얼 중이면 듀얼 매니저에서 처리
        if (plugin.getDuelManager().isInDuel(victim)) {
            plugin. getDuelManager().handlePlayerDeath(victim, killer);
            
            // 듀얼에서는 아이템 드롭 안함
            event.getDrops().clear();
            event.setDroppedExp(0);
            return;
        }

        // 아레나 중이면 아레나에서 처리
        if (plugin.getArenaManager().isInArena(victim)) {
            handleArenaDeath(victim, killer, event);
            return;
        }

        // 일반 PvP 사망 처리
        
        // 무기 확인
        ItemStack weapon = killer. getInventory().getItemInMainHand();
        Material weaponType = weapon != null ? weapon.getType() : Material.AIR;

        // 통계 기록
        plugin.getStatisticsManager().recordKill(killer, victim, weaponType);

        // 랭킹 기록
        plugin. getRankingManager().recordKill(killer);
        plugin.getRankingManager().recordDeath(victim);

        // 킬 스트릭 처리
        StreakLevel newLevel = null;
        if (plugin.getKillStreakManager() != null) {
            plugin.getKillStreakManager().addKill(killer, victim. getUniqueId());
            
            // 셧다운 보상
            StreakLevel victimLevel = plugin. getKillStreakManager().resetStreakAndGetLevel(victim);
            if (victimLevel != null) {
                plugin.getKillStreakManager().giveShutdownReward(killer, victim);
            }
        }

        // 리더보드 업데이트
        plugin. getLeaderboardManager().recordKill(killer. getUniqueId());

        // 멀티킬 체크
        checkMultiKill(killer);

        // 어시스트 처리
        processAssists(victim, killer);

        // 보상 지급
        plugin.getRewardManager().giveKillReward(killer, victim);

        // 칭호 체크
        plugin. getTitleManager().checkTitleUnlock(killer);

        // 전투 태그 초기화
        plugin.getPvPModeManager().clearCombatTag(victim);

        // 지역별 아이템 드롭 처리
        handleDeathDrops(victim, event);

        // 킬 메시지
        String killMessage = formatKillMessage(killer, victim, weaponType);
        event.setDeathMessage(MessageUtil.colorize(killMessage));
    }

    /**
     * 아레나 사망 처리
     */
    private void handleArenaDeath(Player victim, Player killer, PlayerDeathEvent event) {
        var arena = plugin.getArenaManager().getPlayerArena(victim);
        if (arena == null) {
            return;
        }

        // 킬/데스 기록
        arena.addKill(killer. getUniqueId());
        arena.addDeath(victim.getUniqueId());

        // 통계
        ItemStack weapon = killer. getInventory().getItemInMainHand();
        plugin.getStatisticsManager().recordKill(killer, victim, weapon != null ? weapon.getType() : Material.AIR);

        // 킬 스트릭
        plugin.getKillStreakManager().addKill(killer, victim.getUniqueId());
        plugin.getKillStreakManager().resetStreak(victim);

        // 아이템 드롭 안함
        event. getDrops().clear();
        event.setDroppedExp(0);

        // 아레나에 킬 메시지
        plugin.getArenaManager().broadcastToArena(arena, 
                "&c" + victim.getName() + "&7님이 &c" + killer.getName() + "&7님에게 처치당했습니다.");
    }

    /**
     * 비 PvP 사망 처리
     */
    private void handleNonPvPDeath(Player victim, PlayerDeathEvent event) {
        // 듀얼 중 비정상 사망
        if (plugin.getDuelManager().isInDuel(victim)) {
            plugin.getDuelManager().handlePlayerDeath(victim, null);
            event.getDrops().clear();
            event.setDroppedExp(0);
            return;
        }

        // 아레나 중 비정상 사망
        if (plugin.getArenaManager().isInArena(victim)) {
            var arena = plugin.getArenaManager().getPlayerArena(victim);
            if (arena != null) {
                arena.addDeath(victim.getUniqueId());
            }
            event. getDrops().clear();
            event. setDroppedExp(0);
            return;
        }

        // 킬 스트릭 초기화
        plugin.getKillStreakManager().resetStreak(victim);

        // 전투 태그 초기화
        plugin.getPvPModeManager().clearCombatTag(victim);
    }

    /**
     * 어시스트 추적
     */
    private void trackAssist(UUID victimId, UUID attackerId, double damage) {
        Map<UUID, Double> attackers = assistTracker. computeIfAbsent(victimId, k -> new HashMap<>());
        attackers. put(attackerId, attackers.getOrDefault(attackerId, 0.0) + damage);
    }

    /**
     * 어시스트 처리
     */
    private void processAssists(Player victim, Player killer) {
        Map<UUID, Double> attackers = assistTracker.remove(victim.getUniqueId());
        if (attackers == null) {
            return;
        }

        for (Map.Entry<UUID, Double> entry : attackers.entrySet()) {
            UUID assisterId = entry.getKey();
            double damage = entry.getValue();

            // 킬러 제외, 최소 데미지 이상
            if (! assisterId.equals(killer.getUniqueId()) && damage >= ASSIST_THRESHOLD) {
                Player assister = org. bukkit.Bukkit.getPlayer(assisterId);
                if (assister != null && assister.isOnline()) {
                    plugin.getStatisticsManager().recordAssist(assister);
                    plugin. getRankingManager().recordAssist(assister);
                    MessageUtil.sendMessage(assister, "&e어시스트!  &7(+" + (int) damage + " 데미지)");
                }
            }
        }
    }

    /**
     * 멀티킬 체크
     */
    private void checkMultiKill(Player killer) {
        UUID killerId = killer.getUniqueId();
        long currentTime = System. currentTimeMillis();
        Long lastKill = lastKillTime.get(killerId);

        if (lastKill != null && (currentTime - lastKill) <= MULTI_KILL_WINDOW) {
            int count = multiKillCount.getOrDefault(killerId, 1) + 1;
            multiKillCount.put(killerId, count);

            if (count == 2) {
                plugin.getStatisticsManager().recordDoubleKill(killer);
            } else if (count == 3) {
                plugin.getStatisticsManager().recordTripleKill(killer);
            } else if (count > 3) {
                plugin.getStatisticsManager().recordMultiKill(killer);
            }
        } else {
            multiKillCount.put(killerId, 1);
        }

        lastKillTime.put(killerId, currentTime);
    }

    /**
     * 사망 시 아이템 드롭 처리
     */
    private void handleDeathDrops(Player victim, PlayerDeathEvent event) {
        PvPZone zone = plugin.getZoneManager().getZone(victim. getLocation());

        if (zone != null) {
            if (zone.isKeepInventory()) {
                event.setKeepInventory(true);
                event.getDrops().clear();
                event.setKeepLevel(true);
                event.setDroppedExp(0);
            } else if (! zone.isDropItems()) {
                event.getDrops().clear();
            }
        }
    }

    /**
     * 킬 메시지 포맷
     */
    private String formatKillMessage(Player killer, Player victim, Material weapon) {
        String killerName = plugin.getTitleManager().formatPlayerName(killer);
        String victimName = plugin.getTitleManager().formatPlayerName(victim);
        
        String weaponName = getWeaponName(weapon);
        int streak = plugin.getKillStreakManager().getCurrentStreak(killer);

        StringBuilder message = new StringBuilder();
        message.append("&c").append(victimName);
        message.append(" &7was slain by ");
        message.append("&c").append(killerName);
        
        if (weaponName != null) {
            message. append(" &7using &f").append(weaponName);
        }

        if (streak >= 5) {
            message.append(" &6[").append(streak).append(" 스트릭]");
        }

        return message. toString();
    }

    /**
     * 무기 이름 반환
     */
    private String getWeaponName(Material material) {
        if (material == null || material == Material.AIR) {
            return null;
        }

        // 한글 무기 이름 매핑
        switch (material) {
            case DIAMOND_SWORD:  return "다이아몬드 검";
            case IRON_SWORD: return "철 검";
            case GOLDEN_SWORD: return "황금 검";
            case STONE_SWORD: return "돌 검";
            case WOODEN_SWORD: return "나무 검";
            case NETHERITE_SWORD: return "네더라이트 검";
            case BOW: return "활";
            case CROSSBOW: return "석궁";
            case TRIDENT: return "삼지창";
            case DIAMOND_AXE: return "다이아몬드 도끼";
            case IRON_AXE: return "철 도끼";
            case NETHERITE_AXE: return "네더라이트 도끼";
            default: return material.name().replace("_", " ").toLowerCase();
        }
    }

    /**
     * 공격자 추출
     */
    private Player getAttacker(EntityDamageByEntityEvent event) {
        if (event. getDamager() instanceof Player) {
            return (Player) event.getDamager();
        }

        if (event. getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                return (Player) projectile.getShooter();
            }
        }

        return null;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 플레이어 데이터 로드
        plugin.getPlayerDataStorage().loadPlayer(player. getUniqueId());

        // 첫 접속 시 보호 적용
        if (!player.hasPlayedBefore()) {
            plugin.getPvPModeManager().applyNewPlayerProtection(player);
        }

        // 대기 중인 시즌 보상 확인
        plugin.getSeasonManager().checkPendingRewards(player);

        // 플레이 시간 업데이트 시작
        plugin. getStatisticsManager().updatePlayTime(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event. getPlayer();
        UUID playerId = player.getUniqueId();

        // 듀얼 중 접속 종료
        if (plugin.getDuelManager().isInDuel(player)) {
            plugin.getDuelManager().handlePlayerDisconnect(player);
        }

        // 아레나 중 접속 종료
        if (plugin.getArenaManager().isInArena(player)) {
            plugin.getArenaManager().leaveArena(player);
        }

        // 매칭 대기열에서 제거
        plugin.getArenaManager().cancelQueue(player);

        // 플레이 시간 업데이트
        plugin.getStatisticsManager().updatePlayTime(player);

        // 킬 스트릭 세션 초기화
        plugin.getKillStreakManager().resetSession(player);

        // 전투 태그 초기화
        plugin.getPvPModeManager().clearCombatTag(player);

        // 지역 퇴장 처리
        plugin. getZoneManager().unloadPlayerData(playerId);

        // 어시스트 추적 정리
        assistTracker.remove(playerId);
        lastKillTime.remove(playerId);
        multiKillCount.remove(playerId);

        // 플레이어 데이터 저장
        plugin. getPlayerDataStorage().savePlayer(playerId);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        // 아레나에서 사망 시 아레나 스폰으로
        if (plugin. getArenaManager().isInArena(player)) {
            var arena = plugin.getArenaManager().getPlayerArena(player);
            if (arena != null && arena.getSpectatorSpawn() != null) {
                event.setRespawnLocation(arena.getSpectatorSpawn());
                
                // 관전 모드로 전환
                org.bukkit. Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.setGameMode(org.bukkit.GameMode. SPECTATOR);
                }, 1L);
            }
        }
    }
}