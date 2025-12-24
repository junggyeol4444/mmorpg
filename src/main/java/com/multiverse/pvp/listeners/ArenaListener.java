package com.multiverse.pvp.listeners;

import com. multiverse.pvp.PvPCore;
import com. multiverse.pvp.data.PvPArena;
import com.multiverse.pvp.enums. ArenaStatus;
import com.multiverse.pvp.utils.MessageUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org. bukkit.entity. Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit. event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit. event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org. bukkit.event. entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;

public class ArenaListener implements Listener {

    private final PvPCore plugin;

    public ArenaListener(PvPCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 아레나 내 블록 파괴 방지
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (! plugin.getArenaManager().isInArena(player)) {
            return;
        }

        // 관리자 권한이 없으면 차단
        if (! player.hasPermission("pvp.admin")) {
            event.setCancelled(true);
        }
    }

    /**
     * 아레나 내 블록 설치 방지
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getArenaManager().isInArena(player)) {
            return;
        }

        // 관리자 권한이 없으면 차단
        if (!player.hasPermission("pvp.admin")) {
            event. setCancelled(true);
        }
    }

    /**
     * 아레나 내 아이템 드롭 방지
     */
    @EventHandler(priority = EventPriority. HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (!plugin. getArenaManager().isInArena(player)) {
            return;
        }

        event.setCancelled(true);
        MessageUtil.sendMessage(player, "&c아레나에서는 아이템을 버릴 수 없습니다.");
    }

    /**
     * 아레나 밖으로 이동 방지
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getArenaManager().isInArena(player)) {
            return;
        }

        PvPArena arena = plugin.getArenaManager().getPlayerArena(player);
        if (arena == null) {
            return;
        }

        Location to = event.getTo();
        if (to == null) {
            return;
        }

        // 아레나 경계 체크
        if (arena.getCorner1() != null && arena.getCorner2() != null) {
            if (! arena.isInArena(to)) {
                // 경계 밖으로 나가려고 함
                event.setCancelled(true);
                MessageUtil.sendMessage(player, "&c아레나 경계를 벗어날 수 없습니다!");
                
                // 중앙으로 밀어내기
                Location center = arena.getCenter();
                if (center != null) {
                    player.setVelocity(center.toVector().subtract(player.getLocation().toVector()).normalize().multiply(0.5));
                }
            }
        }

        // Y 좌표 체크 (낙사 방지)
        if (to.getY() < 0) {
            // 스폰으로 텔레포트
            Location spawn = arena.getRandomSpawnPoint();
            if (spawn != null) {
                player.teleport(spawn);
                
                // 데미지 처리
                player.damage(10. 0);
            }
        }
    }

    /**
     * 아레나 대기 중 데미지 방지
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!plugin.getArenaManager().isInArena(player)) {
            return;
        }

        PvPArena arena = plugin.getArenaManager().getPlayerArena(player);
        if (arena == null) {
            return;
        }

        // 대기/준비 중에는 데미지 무효
        if (! arena.getStatus().canFight()) {
            event. setCancelled(true);
        }

        // 관전자는 데미지 무효
        if (arena.isSpectator(player. getUniqueId())) {
            event.setCancelled(true);
        }
    }

    /**
     * 아레나 내 허기 감소 방지
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!plugin.getArenaManager().isInArena(player)) {
            return;
        }

        PvPArena arena = plugin.getArenaManager().getPlayerArena(player);
        if (arena == null) {
            return;
        }

        // 대기/준비 중에는 허기 유지
        if (!arena.getStatus().canFight()) {
            event.setCancelled(true);
            player.setFoodLevel(20);
            player.setSaturation(20f);
        }
    }

    /**
     * 아레나 명령어 제한
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (!plugin. getArenaManager().isInArena(player)) {
            return;
        }

        // 관리자는 제한 없음
        if (player.hasPermission("pvp.admin")) {
            return;
        }

        String command = event.getMessage().toLowerCase().split(" ")[0];

        // 허용된 명령어
        if (command. equals("/pvp") || 
            command.equals("/duel") ||
            command.equals("/msg") ||
            command.equals("/r") ||
            command.equals("/tell")) {
            return;
        }

        event.setCancelled(true);
        MessageUtil.sendMessage(player, "&c아레나에서는 해당 명령어를 사용할 수 없습니다.");
    }

    /**
     * 아레나 텔레포트 방지
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event. getPlayer();

        if (!plugin.getArenaManager().isInArena(player)) {
            return;
        }

        // 관리자는 제한 없음
        if (player.hasPermission("pvp.admin")) {
            return;
        }

        PlayerTeleportEvent. TeleportCause cause = event. getCause();

        // 플러그인/명령어 텔레포트만 차단 (엔더펄 등은 허용)
        if (cause == PlayerTeleportEvent. TeleportCause.COMMAND ||
            cause == PlayerTeleportEvent. TeleportCause.PLUGIN) {
            
            PvPArena arena = plugin.getArenaManager().getPlayerArena(player);
            if (arena != null) {
                Location to = event.getTo();
                if (to != null && ! arena.isInArena(to)) {
                    event. setCancelled(true);
                    MessageUtil.sendMessage(player, "&c아레나 진행 중에는 텔레포트할 수 없습니다.");
                }
            }
        }
    }

    /**
     * 아레나 비행 방지
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getArenaManager().isInArena(player)) {
            return;
        }

        // 관전자는 비행 허용
        PvPArena arena = plugin.getArenaManager().getPlayerArena(player);
        if (arena != null && arena.isSpectator(player.getUniqueId())) {
            return;
        }

        // 관리자도 아레나에서는 비행 제한
        if (event.isFlying()) {
            event. setCancelled(true);
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    /**
     * 아레나 게임모드 변경 방지
     */
    @EventHandler(priority = EventPriority. HIGH)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getArenaManager().isInArena(player)) {
            return;
        }

        // 관리자는 제한 없음
        if (player.hasPermission("pvp.admin")) {
            return;
        }

        PvPArena arena = plugin.getArenaManager().getPlayerArena(player);
        if (arena == null) {
            return;
        }

        GameMode newMode = event.getNewGameMode();

        // 관전자가 아닌데 관전 모드로 변경 시도
        if (! arena.isSpectator(player.getUniqueId()) && newMode == GameMode.SPECTATOR) {
            event.setCancelled(true);
        }

        // 크리에이티브 모드 차단
        if (newMode == GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    /**
     * 아레나 내 상호작용 제한
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event. getPlayer();

        if (!plugin.getArenaManager().isInArena(player)) {
            return;
        }

        PvPArena arena = plugin. getArenaManager().getPlayerArena(player);
        if (arena == null) {
            return;
        }

        // 관전자는 상호작용 불가
        if (arena.isSpectator(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        // 대기/준비 중에는 일부 상호작용 제한
        if (! arena.getStatus().canFight()) {
            // 블록 상호작용 차단
            if (event.getClickedBlock() != null) {
                switch (event.getClickedBlock().getType()) {
                    case CHEST:
                    case TRAPPED_CHEST: 
                    case ENDER_CHEST:
                    case BARREL:
                    case SHULKER_BOX:
                    case LEVER:
                    case STONE_BUTTON: 
                    case OAK_BUTTON:
                        event.setCancelled(true);
                        break;
                }
            }
        }
    }

    /**
     * 아레나 엔더펄 제한
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeleportByEnderPearl(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause. ENDER_PEARL) {
            return;
        }

        Player player = event. getPlayer();

        if (!plugin.getArenaManager().isInArena(player)) {
            return;
        }

        PvPArena arena = plugin. getArenaManager().getPlayerArena(player);
        if (arena == null) {
            return;
        }

        Location to = event.getTo();
        if (to == null) {
            return;
        }

        // 아레나 밖으로 엔더펄 텔레포트 방지
        if (! arena.isInArena(to)) {
            event.setCancelled(true);
            MessageUtil. sendMessage(player, "&c아레나 밖으로 엔더펄을 사용할 수 없습니다!");
        }
    }
}