package com.multiverse.crafting.tasks;

import com.multiverse.crafting.managers.CraftingManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * (Optional) Progress task placeholder for timed crafting bars.
 * Currently unused; can be hooked to show action bars or boss bars.
 */
public class CraftingProgressTask extends BukkitRunnable {

    private final Player player;
    private final CraftingManager craftingManager;

    public CraftingProgressTask(Player player, CraftingManager craftingManager) {
        this.player = player;
        this.craftingManager = craftingManager;
    }

    @Override
    public void run() {
        // Implement action bar/boss bar updates if desired
        // Example: player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Crafting..."));
    }
}