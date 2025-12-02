package com.multiverse.playerdata.gui;

import com.multiverse.playerdata.models.Transcendence;
import com.multiverse.playerdata.models.enums.TranscendentPower;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventHandler;

import java.util.*;

public class TranscendenceGUI implements Listener {

    private final String GUI_TITLE = "초월 능력 선택";
    private final int SIZE = 27;

    public void open(Player player, Transcendence transcendence) {
        Inventory inv = Bukkit.createInventory(player, SIZE, GUI_TITLE);

        int slot = 10;
        for (TranscendentPower power : transcendence.getUnlockedPowers()) {
            ItemStack item = new ItemStack(Material.NETHER_STAR);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§d" + power.name());
            List<String> lore = new ArrayList<>();
            lore.add("초월 등급: " + transcendence.getTranscendLevel());
            if (power == transcendence.getSelectedPower()) {
                lore.add("§a[현재 선택됨]");
            }
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
            // 클릭 시 초월 능력 상세 보기/선택 기능 확장 가능
        }
    }
}