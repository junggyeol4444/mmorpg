package com.multiverse.core.listeners;

import com.multiverse.core.MultiverseCore;
import com.multiverse.core.managers.TeleportManager;
import com.multiverse.core.managers.PortalManager;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class PlayerListener implements Listener {
    private final MultiverseCore plugin;
    private final TeleportManager teleportManager;
    private final PortalManager portalManager;

    public PlayerListener(MultiverseCore plugin) {
        this.plugin = plugin;
        this.teleportManager = plugin.getTeleportManager();
        this.portalManager = plugin.getPortalManager();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // 웜업 중 움직이면 텔레포트 취소
        if (teleportManager.isWarming(player)) {
            if (!event.getFrom().equals(event.getTo())) {
                teleportManager.cancelWarmup(player);
            }
        }

        // 포탈 영역 감지 (WorldGuard로 대체 가능)
        // stub: 실제 portalManager.isInPortalRegion(player) 사용
        // if (portalManager.isInPortalRegion(player)) { ... }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        // 웜업 중 피해 시 텔레포트 취소
        if (teleportManager.isWarming(player)) {
            teleportManager.cancelWarmup(player);
            player.sendMessage(plugin.getMessageUtil().get("teleport.warmup-cancelled"));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // 웜업, 쿨다운 데이터 저장
        // 실제 구현에서는 YAMLDataManager로 저장
        plugin.getDataManager().save();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // 귀환석 사용 감지
        if (event.getAction() == Action.RIGHT_CLICK_AIR ||
            event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (item != null && item.getType() == Material.NETHER_STAR) {
                // 귀환석 확인 및 사용
                teleportManager.useReturnStone(event.getPlayer(), item);
                event.setCancelled(true);
            }
        }
    }
}