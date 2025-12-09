package com.multiverse.crafting.gui;

import com.multiverse.crafting.managers.CraftingManager;
import com.multiverse.crafting.managers.RecipeManager;
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
 * Simple crafting confirmation GUI showing materials and success rate.
 * Player clicks green pane to start crafting.
 */
public class CraftingGUI {

    private final RecipeManager recipeManager;
    private final CraftingManager craftingManager;

    public CraftingGUI(RecipeManager recipeManager, CraftingManager craftingManager) {
        this.recipeManager = recipeManager;
        this.craftingManager = craftingManager;
    }

    public void open(Player player, CraftingRecipe recipe, int amount, double successRate) {
        Inventory inv = Bukkit.createInventory(player, 27, MessageUtil.color("&8제작 확인: " + recipe.getName()));
        // Slot 11: recipe icon/result
        inv.setItem(11, decorateItem(recipe.getResult(), "&e결과물", List.of(
                "&7최소~최대: " + recipe.getMinAmount() + "~" + recipe.getMaxAmount(),
                "&7제작 수량: " + amount
        )));
        // Slot 13: success rate
        inv.setItem(13, makePane(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "&b성공률: " + String.format("%.1f", successRate) + "%", List.of(
                "&7기본: " + recipe.getSuccessRate() + "%",
                "&7필요 레벨: " + recipe.getRequiredLevel(),
                "&7필요 스킬레벨: " + recipe.getRequiredSkillLevel()
        )));
        // Slot 15: start button
        inv.setItem(15, makePane(Material.LIME_STAINED_GLASS_PANE, "&a[시작]", List.of("&7클릭하면 제작을 시작합니다.")));
        player.openInventory(inv);
    }

    public void handleClick(Player player, InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (!e.getView().getTitle().startsWith(MessageUtil.color("&8제작 확인"))) return;
        e.setCancelled(true);
        int slot = e.getRawSlot();
        if (slot != 15) return; // start button
        String title = e.getView().getTitle();
        // Extract recipe name part after prefix; we stored recipe name but need id from lore? We'll store recipeId in item meta
        ItemStack button = e.getCurrentItem();
        if (button == null || !button.hasItemMeta()) return;
        ItemMeta meta = button.getItemMeta();
        if (meta == null || !meta.hasLocalizedName()) return;
        String recipeId = meta.getLocalizedName();
        CraftingRecipe recipe = recipeManager.getRecipe(recipeId);
        if (recipe == null) {
            player.sendMessage(MessageUtil.color("&c레시피를 찾을 수 없습니다."));
            player.closeInventory();
            return;
        }
        // default amount 1; could be enhanced to store in NBT/lore; here assume 1
        craftingManager.startCrafting(player, recipe, 1);
        player.closeInventory();
    }

    private ItemStack decorateItem(ItemStack base, String name, List<String> lore) {
        ItemStack item = base.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(MessageUtil.color(name));
            if (lore != null && !lore.isEmpty()) {
                List<String> colored = new ArrayList<>();
                lore.forEach(l -> colored.add(MessageUtil.color(l)));
                meta.setLore(colored);
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack makePane(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(MessageUtil.color(name));
            if (lore != null && !lore.isEmpty()) {
                List<String> colored = new ArrayList<>();
                lore.forEach(l -> colored.add(MessageUtil.color(l)));
                meta.setLore(colored);
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.setLocalizedName(""); // empty by default
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Utility to stamp recipeId into the start button.
     */
    public ItemStack makeStartButton(String recipeId) {
        ItemStack item = makePane(Material.LIME_STAINED_GLASS_PANE, "&a[시작]", List.of("&7클릭하면 제작을 시작합니다."));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLocalizedName(recipeId);
            item.setItemMeta(meta);
        }
        return item;
    }
}