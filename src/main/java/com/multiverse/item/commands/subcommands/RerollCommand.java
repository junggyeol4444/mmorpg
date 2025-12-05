package com.multiverse.item.commands.subcommands;

import org.bukkit.entity. Player;
import org.bukkit.inventory.ItemStack;
import com.multiverse.item.ItemCore;
import com.multiverse.item.data.CustomItem;
import com.multiverse.item.gui.RerollGUI;
import com.multiverse.item.utils.MessageUtil;

public class RerollCommand implements SubCommand {
    
    private ItemCore plugin;
    
    public RerollCommand(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void execute(Player player, String[] args) {
        
        // 손에 들고 있는 아이템 확인
        ItemStack itemStack = player.getInventory(). getItemInMainHand();
        
        if (itemStack == null || itemStack.getAmount() == 0) {
            MessageUtil.sendMessage(player, "&c손에 들고 있는 아이템이 없습니다!");
            return;
        }
        
        // CustomItem으로 변환
        CustomItem customItem = null;
        try {
            customItem = plugin.getItemManager().fromItemStack(itemStack);
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "&c이 아이템은 리롤할 수 없습니다!");
            return;
        }
        
        if (customItem == null) {
            MessageUtil.sendMessage(player, "&c이 아이템은 리롤할 수 없습니다!");
            return;
        }
        
        // 옵션 확인
        if (customItem. getOptions() == null || customItem.getOptions().isEmpty()) {
            MessageUtil.sendMessage(player, "&c이 아이템에는 옵션이 없습니다!");
            return;
        }
        
        // 리롤 비용 확인
        int rerollCost = plugin.getConfigManager().getRerollCost();
        
        // 나중에 경제 시스템과 연동될 예정
        // 현재는 메시지만 출력
        MessageUtil.sendMessage(player, "&e리롤 비용: &f" + rerollCost + " Gold");
        
        // 리롤 GUI 열기
        try {
            RerollGUI gui = new RerollGUI(plugin, player, customItem, itemStack);
            gui.openGUI(player);
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "&cGUI를 열 수 없습니다!");
            plugin.getLogger().severe("리롤 GUI 오류:");
            e.printStackTrace();
        }
    }
}