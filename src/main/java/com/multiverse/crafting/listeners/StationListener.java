package com.multiverse.crafting.listeners;

import com.multiverse.crafting.CraftingCore;
import com.multiverse.crafting.managers.CraftingStationManager;
import com.multiverse.crafting.models.enums.CraftingStationType;
import com.multiverse.crafting.utils.MessageUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.block.data.type.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Basic listener for placing crafting stations.
 * (Detailed validation delegated to CraftingStationManager)
 */
public class StationListener implements Listener {

    private final CraftingStationManager stationManager;

    public StationListener(CraftingCore plugin, CraftingStationManager stationManager) {
        this.stationManager = stationManager;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlockPlaced();
        CraftingStationType type = mapBlockToStation(block.getType());
        if (type == null) return;
        boolean ok = stationManager.placeStation(player, block.getLocation(), type.name());
        if (!ok) {
            e.setCancelled(true);
            player.sendMessage(MessageUtil.color("&c스테이션을 설치할 수 없습니다."));
        } else {
            player.sendMessage(MessageUtil.color("&a스테이션을 설치했습니다: " + type));
        }
    }

    private CraftingStationType mapBlockToStation(Material mat) {
        switch (mat) {
            case SMITHING_TABLE: return CraftingStationType.SMITHING_TABLE;
            case BREWING_STAND: return CraftingStationType.ALCHEMY_TABLE;
            case CAMPFIRE: // cook
            case FURNACE:
            case BLAST_FURNACE:
            case SMOKER:
                return CraftingStationType.COOKING_TABLE;
            case ENCHANTING_TABLE: return CraftingStationType.ENCHANTING_TABLE;
            case CARTOGRAPHY_TABLE: return CraftingStationType.JEWELCRAFTING_TABLE; // repurposed
            case LOOM: return CraftingStationType.TAILORING_TABLE;
            default: return null;
        }
    }
}