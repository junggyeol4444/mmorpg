package com. multiverse.item.commands.subcommands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import com.multiverse. item.ItemCore;
import com.multiverse.item.data. CustomItem;
import com.multiverse.item.gui.IdentifyGUI;
import com. multiverse.item.utils.MessageUtil;

public class IdentifyCommand implements SubCommand {
    
    private ItemCore plugin;
    
    public IdentifyCommand(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void execute(Player player, String[] args) {
        
        // 손에 들고 있는 아이템 확인
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        
        if (itemStack == null || itemStack.getAmount() == 0) {
            MessageUtil. sendMessage(player, "&c손에 들고 있는 아이템이 없습니다!");
            return;
        }
        
        // 미식별 아이템인지 확인
        if (! plugin.getIdentifySystem().isUnidentified(itemStack)) {
            MessageUtil.sendMessage(player, "&c이 아이템은 이미 식별되었거나 미식별 아이템이 아닙니다!");
            return;
        }
        
        // 식별 GUI 열기
        try {
            IdentifyGUI gui = new IdentifyGUI(plugin, player, itemStack);
            gui.openGUI(player);
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "&cGUI를 열 수 없습니다!");
            plugin.getLogger().severe("식별 GUI 오류:");
            e.printStackTrace();
        }
    }
}