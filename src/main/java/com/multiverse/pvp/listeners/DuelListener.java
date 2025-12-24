package com.multiverse.pvp.listeners;

import com.multiverse.pvp.PvPCore;
import com.multiverse.pvp.data. Duel;
import com.multiverse. pvp.utils.MessageUtil;
import org.bukkit.Location;
import org. bukkit.entity. Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit. event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org. bukkit.event. block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit. event.entity.EntityDamageEvent;
import org.bukkit.event.entity. FoodLevelChangeEvent;
import org. bukkit.event. player.*;

public class DuelListener implements Listener {

    private final PvPCore plugin;

    public DuelListener(PvPCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 듀얼 중 블록 파괴 방지
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getDuelManager().isInDuel(player)) {
            return;
        }

        event.setCancelled(true);
    }

    /**
     * 듀얼 중 블록 설치 방지
     */
    @EventHandler(priority = EventPriority. HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getDuelManager().isInDuel(player)) {
            return;
        }

        event.setCancelled(true);
    }

    /**
     * 듀얼 중 아이템 드롭 방지
     */
    @EventHandler(priority = EventPriority. HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event. getPlayer();

        if (!plugin.getDuelManager().isInDuel(player)) {
            return;
        }

        event.setCancelled(true);
        MessageUtil.sendMessage(player, "&c듀얼 중에는 아이템을 버릴 수 없습니다.");
    }

    /**
     * 듀얼 중 외부 데미지 차단
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();

        if (!plugin. getDuelManager().isInDuel(victim)) {
            return;
        }

        Duel duel = plugin.getDuelManager().getActiveDuel(victim);
        if (duel == null) {
            return;
        }

        // 대기/준비 중에는 데미지 무효
        if (!duel.isActive()) {
            event.setCancelled(true);
            return;
        }

        // 공격자 확인
        Player attacker = null;
        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        } else if (event. getDamager() instanceof org.bukkit.entity.Projectile) {
            org.bukkit.entity.Projectile projectile = (org. bukkit.entity. Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                attacker = (Player) projectile.getShooter();
            }
        }

        if (attacker == null) {
            return;
        }

        // 듀얼 상대가 아니면 차단
        if (!duel.isParticipant(attacker. getUniqueId())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(attacker, "&c듀얼에 참여하지 않은 플레이어를 공격할 수 없습니다.");
        }
    }

    /**
     * 듀얼 중 환경 데미지 처리
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!plugin.getDuelManager().isInDuel(player)) {
            return;
        }

        Duel duel = plugin.getDuelManager().getActiveDuel(player);
        if (duel == null) {
            return;
        }

        // 대기/준비 중에는 모든 데미지 무효
        if (! duel.isActive()) {
            event.setCancelled(true);
            return;
        }

        // 환경 데미지는 허용 (낙사, 용암 등)
        EntityDamageEvent. DamageCause cause = event.getCause();
        
        switch (cause) {
            case VOID:
                // 보이드 데미지 시 즉시 패배
                Player opponent = org.bukkit. Bukkit.getPlayer(duel.getOpponentOf(player. getUniqueId()));
                if (opponent != null) {
                    plugin.getDuelManager().endDuel(duel, opponent. getUniqueId(), 
                            com.multiverse.pvp.enums. DuelEndReason.DEATH);
                }
                event.setCancelled(true);
                break;
                
            case FALL:
            case LAVA:
            case FIRE:
            case FIRE_TICK:
            case DROWNING:
            case SUFFOCATION: 
                // 환경 데미지 허용
                break;
                
            default:
                // 그 외 데미지는 PvP 데미지로 처리
                break;
        }
    }

    /**
     * 듀얼 중 허기 유지
     */
    @EventHandler(priority = EventPriority. HIGH)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!plugin.getDuelManager().isInDuel(player)) {
            return;
        }

        Duel duel = plugin.getDuelManager().getActiveDuel(player);
        if (duel == null) {
            return;
        }

        // 대기/준비 중에는 허기 유지
        if (!duel. isActive()) {
            event.setCancelled(true);
            player.setFoodLevel(20);
            player.setSaturation(20f);
        }
    }

    /**
     * 듀얼 중 이동 제한
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!plugin. getDuelManager().isInDuel(player)) {
            return;
        }

        Duel duel = plugin.getDuelManager().getActiveDuel(player);
        if (duel == null) {
            return;
        }

        // 준비 중에는 이동 제한
        if (duel.getStatus() == com.multiverse.pvp.enums. DuelStatus. PREPARING) {
            Location from = event.getFrom();
            Location to = event.getTo();
            
            if (to != null && (from. getX() != to.getX() || from.getZ() != to.getZ())) {
                event.setTo(from);
            }
        }

        // 듀얼 아레나 밖으로 이동 방지
        if (duel.getArenaId() != null) {
            var arena = plugin.getArenaManager().getArena(duel.getArenaId());
            if (arena != null && arena.getCorner1() != null && arena.getCorner2() != null) {
                Location to = event.getTo();
                if (to != null && ! arena.isInArena(to)) {
                    event. setCancelled(true);
                    MessageUtil.sendMessage(player, "&c듀얼 아레나를 벗어날 수 없습니다!");
                }
            }
        }
    }

    /**
     * 듀얼 중 명령어 제한
     */
    @EventHandler(priority = EventPriority. HIGH)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event. getPlayer();

        if (!plugin.getDuelManager().isInDuel(player)) {
            return;
        }

        // 관리자는 제한 없음
        if (player.hasPermission("pvp.admin")) {
            return;
        }

        String command = event.getMessage().toLowerCase().split(" ")[0];

        // 허용된 명령어
        if (command.equals("/pvp") ||
            command.equals("/duel") ||
            command.equals("/surrender") ||
            command.equals("/msg") ||
            command.equals("/r") ||
            command. equals("/tell")) {
            return;
        }

        event.setCancelled(true);
        MessageUtil.sendMessage(player, "&c듀얼 중에는 해당 명령어를 사용할 수 없습니다.");
        MessageUtil.sendMessage(player, "&7항복하려면 &e/pvp surrender&7를 입력하세요.");
    }

    /**
     * 듀얼 중 텔레포트 방지
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getDuelManager().isInDuel(player)) {
            return;
        }

        // 관리자는 제한 없음
        if (player. hasPermission("pvp.admin")) {
            return;
        }

        PlayerTeleportEvent. TeleportCause cause = event.getCause();

        // 플러그인/명령어 텔레포트 차단
        if (cause == PlayerTeleportEvent.TeleportCause.COMMAND ||
            cause == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, "&c듀얼 중에는 텔레포트할 수 없습니다.");
        }

        // 엔더펄 텔레포트 - 아레나 밖 방지
        if (cause == PlayerTeleportEvent.TeleportCause. ENDER_PEARL) {
            Duel duel = plugin.getDuelManager().getActiveDuel(player);
            if (duel != null && duel.getArenaId() != null) {
                var arena = plugin.getArenaManager().getArena(duel.getArenaId());
                if (arena != null && event.getTo() != null && !arena. isInArena(event.getTo())) {
                    event.setCancelled(true);
                    MessageUtil.sendMessage(player, "&c듀얼 아레나 밖으로 엔더펄을 사용할 수 없습니다!");
                }
            }
        }
    }

    /**
     * 듀얼 중 비행 방지
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (!plugin. getDuelManager().isInDuel(player)) {
            return;
        }

        if (event.isFlying()) {
            event. setCancelled(true);
            player. setAllowFlight(false);
            player.setFlying(false);
        }
    }

    /**
     * 듀얼 중 게임모드 변경 방지
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getDuelManager().isInDuel(player)) {
            return;
        }

        // 관리자도 듀얼 중에는 게임모드 변경 불가
        if (event.getNewGameMode() != org.bukkit.GameMode.SURVIVAL) {
            event.setCancelled(true);
        }
    }

    /**
     * 듀얼 중 아이템 픽업 제한
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerPickupItem(PlayerAttemptPickupItemEvent event) {
        Player player = event.getPlayer();

        if (!plugin. getDuelManager().isInDuel(player)) {
            return;
        }

        // 듀얼 중에는 아이템 픽업 불가
        event. setCancelled(true);
    }

    /**
     * 듀얼 중 인벤토리 클릭 제한 (갑옷 탈의 방지 등)
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (!plugin.getDuelManager().isInDuel(player)) {
            return;
        }

        Duel duel = plugin.getDuelManager().getActiveDuel(player);
        if (duel == null) {
            return;
        }

        // 준비 중에는 인벤토리 조작 불가
        if (duel.getStatus() == com.multiverse.pvp. enums.DuelStatus.PREPARING) {
            event. setCancelled(true);
        }
    }

    /**
     * 듀얼 중 체력 회복 아이템 제한 (선택적)
     */
    @EventHandler(priority = EventPriority. NORMAL)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getDuelManager().isInDuel(player)) {
            return;
        }

        // 황금 사과 등의 사용은 허용
        // 특정 아이템만 제한하려면 여기서 처리
    }
}