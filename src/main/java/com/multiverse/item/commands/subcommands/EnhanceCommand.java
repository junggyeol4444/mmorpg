package com.multiverse.item. commands. subcommands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.multiverse.item.ItemCore;
import com.multiverse. item.data.CustomItem;
import com.multiverse.item.data.EnhanceResult;
import com.multiverse. item.gui.EnhanceGUI;
import com.multiverse.item.utils.MessageUtil;

public class EnhanceCommand implements SubCommand {
    
    private ItemCore plugin;
    
    public EnhanceCommand(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void execute(Player player, String[] args) {
        
        // 손에 들고 있는 아이템 확인
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        
        if (itemStack == null || itemStack.getAmount() == 0) {
            MessageUtil.sendMessage(player, "&c손에 들고 있는 아이템이 없습니다!");
            return;
        }
        
        // CustomItem으로 변환
        CustomItem customItem = null;
        try {
            customItem = plugin. getItemManager().fromItemStack(itemStack);
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "&c이 아이템은 강화할 수 없습니다!");
            return;
        }
        
        if (customItem == null) {
            MessageUtil.sendMessage(player, "&c이 아이템은 강화할 수 없습니다!");
            return;
        }
        
        // 최대 강화 확인
        if (customItem.getEnhanceLevel() >= customItem.getMaxEnhance()) {
            MessageUtil.sendMessage(player, "&c이 아이템은 더 이상 강화할 수 없습니다!");
            MessageUtil.sendMessage(player, "&7현재 강화 레벨: &f+" + customItem.getEnhanceLevel());
            return;
        }
        
        // 강화 GUI 열기
        try {
            EnhanceGUI gui = new EnhanceGUI(plugin, player, customItem, itemStack);
            gui.openGUI(player);
        } catch (Exception e) {
            MessageUtil. sendMessage(player, "&cGUI를 열 수 없습니다!");
            plugin.getLogger().severe("강화 GUI 오류: ");
            e.printStackTrace();
        }
    }
}