package com.multiverse.item. commands. subcommands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.multiverse.item.ItemCore;
import com.multiverse. item.data.CustomItem;
import com. multiverse.item.gui.DisassembleGUI;
import com.multiverse.item.utils.MessageUtil;

public class DisassembleCommand implements SubCommand {
    
    private ItemCore plugin;
    
    public DisassembleCommand(ItemCore plugin) {
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
            customItem = plugin.getItemManager().fromItemStack(itemStack);
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "&c이 아이템은 분해할 수 없습니다!");
            return;
        }
        
        if (customItem == null) {
            MessageUtil.sendMessage(player, "&c이 아이템은 분해할 수 없습니다!");
            return;
        }
        
        // 분해 가능 여부 확인
        if (! plugin.getDisassembleSystem().canDisassemble(customItem)) {
            MessageUtil.sendMessage(player, "&c이 아이템은 분해할 수 없습니다!");
            MessageUtil.sendMessage(player, "&7이유: 세트 아이템이거나 귀속 아이템입니다.");
            return;
        }
        
        // 분해 GUI 열기
        try {
            DisassembleGUI gui = new DisassembleGUI(plugin, player, customItem, itemStack);
            gui.openGUI(player);
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "&cGUI를 열 수 없습니다!");
            plugin.getLogger().severe("분해 GUI 오류:");
            e.printStackTrace();
        }
    }
}