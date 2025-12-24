package com.multiverse.pvp.listeners;

import com. multiverse.pvp.PvPCore;
import com. multiverse.pvp.data.PvPZone;
import com.multiverse.pvp.enums.ZoneType;
import com.multiverse.pvp.utils.MessageUtil;
import org.bukkit.Location;
import org. bukkit.entity. Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org. bukkit.event. entity.EntityDamageByEntityEvent;
import org.bukkit. event.player.PlayerMoveEvent;
import org.bukkit. event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class ZoneListener implements Listener {

    private final PvPCore plugin;

    public ZoneListener(PvPCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 플레이어 이동 시 지역 체크
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        // 블록 변경이 없으면 무시 (머리 회전 등)
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) {
            return;
        }

        if (from.getBlockX() == to.getBlockX() &&
            from.getBlockY() == to.getBlockY() &&
            from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        plugin.getZoneManager().updatePlayerZone(player);
    }

    /**
     * 텔레포트 시 지역 체크
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        // 약간의 딜레이 후 지역 체크 (텔레포트 완료 후)
        org.bukkit. Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                plugin.getZoneManager().updatePlayerZone(player);
            }
        }, 1L);
    }

    /**
     * 안전 지역 내 PvP 차단
     */
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

        // 피해자 위치 지역 체크
        PvPZone victimZone = plugin.getZoneManager().getZone(victim. getLocation());
        if (victimZone != null && victimZone.getType() == ZoneType. SAFE) {
            event.setCancelled(true);
            MessageUtil.sendMessage(attacker, "&c상대방이 안전 지역에 있습니다.");
            return;
        }

        // 공격자 위치 지역 체크
        PvPZone attackerZone = plugin. getZoneManager().getZone(attacker.getLocation());
        if (attackerZone != null && attackerZone.getType() == ZoneType. SAFE) {
            event.setCancelled(true);
            MessageUtil.sendMessage(attacker, "&c안전 지역에서는 공격할 수 없습니다.");
            return;
        }

        // 레벨 제한 체크
        if (victimZone != null && ! plugin.getZoneManager().meetsLevelRequirement(attacker, victimZone)) {
            event. setCancelled(true);
            MessageUtil.sendMessage(attacker, "&c이 지역의 레벨 제한을 충족하지 않습니다.");
        }
    }

    /**
     * 안전 지역 비행 허용
     */
    @EventHandler(priority = EventPriority. NORMAL)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        // 듀얼/아레나 중이면 다른 리스너에서 처리
        if (plugin.getDuelManager().isInDuel(player) ||
            plugin.getArenaManager().isInArena(player)) {
            return;
        }

        PvPZone zone = plugin.getZoneManager().getZone(player.getLocation());

        if (zone == null) {
            return;
        }

        if (! zone.isAllowFlight() && event.isFlying()) {
            // 비행 권한이 있어도 지역에서 비행 불가
            if (! player.hasPermission("pvp.admin")) {
                event. setCancelled(true);
                player.setAllowFlight(false);
                MessageUtil.sendMessage(player, "&c이 지역에서는 비행이 불가능합니다.");
            }
        }
    }

    /**
     * 지역 입장 시 비행 해제
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMoveFlightCheck(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) {
            return;
        }

        // 블록 변경 없으면 무시
        if (from. getBlockX() == to.getBlockX() &&
            from.getBlockY() == to.getBlockY() &&
            from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();

        // 비행 중이 아니면 무시
        if (! player.isFlying()) {
            return;
        }

        // 관리자는 무시
        if (player. hasPermission("pvp.admin")) {
            return;
        }

        // 듀얼/아레나 중이면 무시 (다른 리스너에서 처리)
        if (plugin.getDuelManager().isInDuel(player) ||
            plugin.getArenaManager().isInArena(player)) {
            return;
        }

        PvPZone fromZone = plugin. getZoneManager().getZone(from);
        PvPZone toZone = plugin.getZoneManager().getZone(to);

        // 비행 가능 지역에서 불가능 지역으로 이동
        boolean wasAllowed = fromZone == null || fromZone.isAllowFlight();
        boolean isAllowed = toZone == null || toZone.isAllowFlight();

        if (wasAllowed && ! isAllowed) {
            player.setFlying(false);
            player.setAllowFlight(false);
            MessageUtil.sendMessage(player, "&c이 지역에서는 비행이 불가능합니다.");
        }
    }

    /**
     * 지역 간 텔레포트 제한
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeleportRestriction(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        // 관리자는 무시
        if (player.hasPermission("pvp.admin")) {
            return;
        }

        Location to = event.getTo();
        if (to == null) {
            return;
        }

        PvPZone fromZone = plugin. getZoneManager().getZone(event.getFrom());
        PvPZone toZone = plugin.getZoneManager().getZone(to);

        // 혼돈 지역에서 나가기 제한 (전투 중)
        if (fromZone != null && fromZone.getType() == ZoneType. CHAOS) {
            if (plugin.getPvPModeManager().isInCombat(player)) {
                if (event.getCause() == PlayerTeleportEvent.TeleportCause. COMMAND ||
                    event. getCause() == PlayerTeleportEvent. TeleportCause.PLUGIN) {
                    event.setCancelled(true);
                    MessageUtil.sendMessage(player, "&c전투 중에는 혼돈 지역을 떠날 수 없습니다.");
                    return;
                }
            }
        }

        // 전투 지역 텔레포트 제한
        if (toZone != null && ! toZone.isAllowTeleport()) {
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND ||
                event.getCause() == PlayerTeleportEvent.TeleportCause. PLUGIN ||
                event.getCause() == PlayerTeleportEvent.TeleportCause. ENDER_PEARL) {
                
                // 같은 지역 내 텔레포트는 허용
                if (fromZone == null || ! fromZone.getZoneId().equals(toZone.getZoneId())) {
                    event.setCancelled(true);
                    MessageUtil.sendMessage(player, "&c이 지역으로 텔레포트할 수 없습니다.");
                }
            }
        }

        // 레벨 제한 체크
        if (toZone != null && !plugin.getZoneManager().meetsLevelRequirement(player, toZone)) {
            event.setCancelled(true);
            MessageUtil. sendMessage(player, "&c레벨 제한으로 이 지역에 입장할 수 없습니다.");
            MessageUtil.sendMessage(player, "&7필요 레벨: " + toZone. getMinLevel() + " ~ " + toZone.getMaxLevel());
        }
    }

    /**
     * 지역 진입 시 레벨 체크
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMoveLevelCheck(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) {
            return;
        }

        // 블록 변경 없으면 무시
        if (from.getBlockX() == to.getBlockX() &&
            from. getBlockY() == to.getBlockY() &&
            from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();

        // 관리자는 무시
        if (player.hasPermission("pvp.admin")) {
            return;
        }

        PvPZone fromZone = plugin. getZoneManager().getZone(from);
        PvPZone toZone = plugin.getZoneManager().getZone(to);

        // 다른 지역으로 이동할 때만 체크
        if (toZone == null) {
            return;
        }

        if (fromZone != null && fromZone.getZoneId().equals(toZone.getZoneId())) {
            return;
        }

        // 레벨 제한 체크
        if (! plugin.getZoneManager().meetsLevelRequirement(player, toZone)) {
            event. setCancelled(true);
            
            // 이전 위치로 밀어내기
            player.teleport(from);
            
            MessageUtil.sendMessage(player, "&c레벨 제한으로 이 지역에 입장할 수 없습니다.");
            MessageUtil.sendMessage(player, "&7필요 레벨: " + toZone.getMinLevel() + " ~ " + toZone.getMaxLevel());
        }
    }

    /**
     * 혼돈 지역 전투 로그 방지
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuitInChaosZone(org.bukkit.event.player.PlayerQuitEvent event) {
        Player player = event. getPlayer();

        PvPZone zone = plugin.getZoneManager().getPlayerZone(player);
        if (zone == null || zone.getType() != ZoneType. CHAOS) {
            return;
        }

        // 전투 중 로그아웃 시 패널티
        if (plugin. getPvPModeManager().isInCombat(player)) {
            // 사망 처리 또는 아이템 드롭 등의 패널티
            // 실제 구현은 서버 정책에 따라 다름
            plugin.getLogger().info(player.getName() + "님이 혼돈 지역에서 전투 중 로그아웃했습니다.");
        }
    }

    /**
     * 공격자 추출
     */
    private Player getAttacker(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            return (Player) event.getDamager();
        }

        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                return (Player) projectile.getShooter();
            }
        }

        return null;
    }
}