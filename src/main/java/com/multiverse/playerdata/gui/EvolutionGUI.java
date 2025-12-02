package com.multiverse.playerdata.gui;

import com.multiverse.playerdata.models.Evolution;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventHandler;

import java.util.*;

public class EvolutionGUI implements Listener {

    private final String GUI_TITLE = "종족 진화";
    private final int SIZE = 27;

    public void open(Player player, List<Evolution> evolutions) {
        Inventory inv = Bukkit.createInventory(player, SIZE, GUI_TITLE);

        int slot = 10;
        for (Evolution evo : evolutions) {
            ItemStack item = new ItemStack(Material.ENDER_EYE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§b" + evo.getName());
            List<String> lore = new ArrayList<>();
            lore.add(evo.getDescription());
            lore.add("→ 진화 목표: " + evo.getToRaceId());
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(GUI_TITLE)) {
            event.setCancelled(true);
            // 클릭 시 진화 트리 설명, 조건 안내 등 확장 가능
        }
    }
}