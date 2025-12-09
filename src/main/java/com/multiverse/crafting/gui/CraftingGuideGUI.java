package com.multiverse.crafting.gui;

import com.multiverse.crafting.managers.RecipeManager;
import com.multiverse.crafting.models.CraftingRecipe;
import com.multiverse.crafting.models.CraftingMaterial;
import com.multiverse.crafting.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows recipe requirements, missing materials info placeholder.
 */
public class CraftingGuideGUI {

    private final RecipeManager recipeManager;

    public CraftingGuideGUI(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    public void open(Player player, CraftingRecipe recipe) {
        Inventory inv = Bukkit.createInventory(player, 54, MessageUtil.color("&8제작 가이드: " + recipe.getName()));
        // Slot 4: recipe result
        inv.setItem(4, infoItem(recipe));
        // Materials displayed starting slot 20
        int slot = 20;
        for (CraftingMaterial mat : recipe.getMaterials()) {
            inv.setItem(slot++, materialItem(player, mat));
            if (slot % 9 == 0) slot += 2; // simple layout
        }
        player.openInventory(inv);
    }

    private ItemStack infoItem(CraftingRecipe recipe) {
        ItemStack item = recipe.getResult().clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(MessageUtil.color("&e" + recipe.getName()));
            List<String> lore = new ArrayList<>();
            lore.add(MessageUtil.color("&7타입: " + recipe.getType() + " / 카테고리: " + recipe.getCategory()));
            lore.add(MessageUtil.color("&7필요 레벨: " + recipe.getRequiredLevel() + ", 스킬: " + recipe.getRequiredSkillLevel()));
            lore.add(MessageUtil.color("&7성공률: " + recipe.getSuccessRate() + "%, 시간: " + recipe.getCraftingTime() + "s"));
            lore.add(MessageUtil.color("&7대량 제작: " + (recipe.isMassCraftable() ? ("최대 " + recipe.getMaxMassAmount()) : "불가")));
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack materialItem(Player player, CraftingMaterial mat) {
        ItemStack item = mat.getItem().clone();
        ItemMeta meta = item.getItemMeta();
        int need = mat.getAmount();
        int have = countMaterial(player, mat.getItem());
        if (meta != null) {
            meta.setDisplayName(MessageUtil.color("&f재료: " + item.getType()));
            List<String> lore = new ArrayList<>();
            lore.add(MessageUtil.color("&7필요: " + need + "개 / 보유: " + have + "개"));
            lore.add(MessageUtil.color(mat.isConsumeOnFail() ? "&c실패 시 소모됨" : "&a실패 시 보존됨"));
            if (have < need) lore.add(MessageUtil.color("&c부족: " + (need - have)));
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }

    private int countMaterial(Player player, ItemStack target) {
        if (target == null || target.getType() == Material.AIR) return 0;
        int total = 0;
        for (ItemStack is : player.getInventory().getContents()) {
            if (is == null) continue;
            if (is.isSimilar(target)) {
                total += is.getAmount();
            }
        }
        return total;
    }
}