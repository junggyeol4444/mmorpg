package com.multiverse.crafting.gui;

import com.multiverse.crafting.managers.CraftingManager;
import com.multiverse.crafting.models.CraftingRecipe;
import com.multiverse.crafting.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI to select mass crafting amount presets.
 */
public class MassCraftingGUI {

    private final CraftingManager craftingManager;

    public MassCraftingGUI(CraftingManager craftingManager) {
        this.craftingManager = craftingManager;
    }

    public void open(Player player, CraftingRecipe recipe, int maxAmount) {
        Inventory inv = Bukkit.createInventory(player, 27, MessageUtil.color("&8대량 제작: " + recipe.getName()));
        int[] presets = new int[]{1, 5, 10, 50, 100, 500};
        int slot = 10;
        for (int preset : presets) {
            if (preset > maxAmount) continue;
            inv.setItem(slot++, makeButton(recipe, preset));
        }
        player.openInventory(inv);
    }

    public void handleClick(Player player, InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (!e.getView().getTitle().startsWith(MessageUtil.color("&8대량 제작"))) return;
        e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLocalizedName()) return;
        String[] parts = meta.getLocalizedName().split(":");
        if (parts.length != 2) return;
        String recipeId = parts[0];
        int amount = Integer.parseInt(parts[1]);
        CraftingRecipe recipe = craftingManager.getRecipeManager().getRecipe(recipeId);
        if (recipe == null) {
            player.sendMessage(MessageUtil.color("&c레시피를 찾을 수 없습니다."));
            player.closeInventory();
            return;
        }
        craftingManager.startCrafting(player, recipe, amount);
        player.closeInventory();
    }

    private ItemStack makeButton(CraftingRecipe recipe, int amount) {
        ItemStack item = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(MessageUtil.color("&a" + amount + "개 제작"));
            List<String> lore = new ArrayList<>();
            lore.add(MessageUtil.color("&7클릭 시 " + amount + "개 제작"));
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.setLocalizedName(recipe.getRecipeId() + ":" + amount);
            item.setItemMeta(meta);
        }
        return item;
    }
}