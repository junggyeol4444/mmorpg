package com.multiverse.crafting.gui;

import com.multiverse.crafting.managers.RecipeManager;
import com.multiverse.crafting.models.CraftingRecipe;
import com.multiverse.crafting.models.enums.CraftingType;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Browsing GUI for known/available recipes.
 */
public class RecipeBrowserGUI {

    private final RecipeManager recipeManager;

    public RecipeBrowserGUI(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    public void open(Player player, CraftingType filter) {
        List<CraftingRecipe> list = recipeManager.getKnownRecipes(player);
        if (filter != null) {
            list = list.stream().filter(r -> r.getType() == filter).collect(Collectors.toList());
        }
        // sort by type then id
        list.sort(Comparator.comparing(CraftingRecipe::getType).thenComparing(CraftingRecipe::getRecipeId));
        int size = 54;
        Inventory inv = Bukkit.createInventory(player, size, MessageUtil.color("&8레시피 목록" + (filter != null ? " - " + filter : "")));
        int slot = 0;
        for (CraftingRecipe recipe : list) {
            if (slot >= size) break;
            inv.setItem(slot++, makeItem(recipe));
        }
        player.openInventory(inv);
    }

    public void handleClick(Player player, InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (!e.getView().getTitle().startsWith(MessageUtil.color("&8레시피 목록"))) return;
        e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLocalizedName()) return;
        String recipeId = meta.getLocalizedName();
        // open info/guide
        CraftingRecipe recipe = recipeManager.getRecipe(recipeId);
        if (recipe == null) {
            player.sendMessage(MessageUtil.color("&c레시피를 찾을 수 없습니다."));
            player.closeInventory();
            return;
        }
        // For simplicity, open crafting guide
        new CraftingGuideGUI(recipeManager).open(player, recipe);
    }

    private ItemStack makeItem(CraftingRecipe recipe) {
        ItemStack item = recipe.getResult().clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(MessageUtil.color("&e" + recipe.getName()));
            List<String> lore = new ArrayList<>();
            lore.add(MessageUtil.color("&7ID: " + recipe.getRecipeId()));
            lore.add(MessageUtil.color("&7타입: " + recipe.getType() + " / 카테고리: " + recipe.getCategory()));
            lore.add(MessageUtil.color("&7성공률: " + recipe.getSuccessRate() + "%"));
            lore.add(MessageUtil.color("&7필요 레벨: " + recipe.getRequiredLevel() + ", 스킬: " + recipe.getRequiredSkillLevel()));
            lore.add(MessageUtil.color("&7대량 제작: " + (recipe.isMassCraftable() ? ("최대 " + recipe.getMaxMassAmount()) : "불가")));
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.setLocalizedName(recipe.getRecipeId());
            item.setItemMeta(meta);
        }
        return item;
    }
}