package com.multiverse.core.listeners;

import com.multiverse.core.MultiverseCore;
import com.multiverse.core.managers.DimensionManager;
import com.multiverse.core.managers.BalanceManager;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.entity.Player;

public class WorldListener implements Listener {
    private final MultiverseCore plugin;
    private final DimensionManager dimensionManager;
    private final BalanceManager balanceManager;

    public WorldListener(MultiverseCore plugin) {
        this.plugin = plugin;
        this.dimensionManager = plugin.getDimensionManager();
        this.balanceManager = plugin.getBalanceManager();
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        String worldName = event.getLocation().getWorld().getName();
        String dimensionId = dimensionManager.getWorldDimension(worldName);
        if (dimensionId != null) {
            double multiplier = balanceManager.getMonsterSpawnMultiplier(dimensionId);
            // 실제 스폰율 조정 로직 필요 (stub)
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String fromWorld = event.getFrom().getName();
        String toWorld = player.getWorld().getName();

        String fromDim = dimensionManager.getWorldDimension(fromWorld);
        String toDim = dimensionManager.getWorldDimension(toWorld);

        if (fromDim != null && toDim != null && !fromDim.equals(toDim)) {
            // DimensionChangeEvent 발생
            com.multiverse.core.events.DimensionChangeEvent changeEvent =
                    new com.multiverse.core.events.DimensionChangeEvent(player, fromDim, toDim);
            plugin.getServer().getPluginManager().callEvent(changeEvent);

            // 균형도 효과 적용
            balanceManager.applyPlayerEffects(player, toDim);
        }
    }
}