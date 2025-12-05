package com. multiverse.item.commands.subcommands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.multiverse.item.ItemCore;
import com.multiverse.item.data.CustomItem;
import com. multiverse.item.gui.SocketGUI;
import com.multiverse.item.utils.MessageUtil;

public class SocketCommand implements SubCommand {
    
    private ItemCore plugin;
    
    public SocketCommand(ItemCore plugin) {
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
        
        // CustomItem으로 변환
        CustomItem customItem = null;
        try {
            customItem = plugin.getItemManager().fromItemStack(itemStack);
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "&c이 아이템은 소켓을 지원하지 않습니다!");
            return;
        }
        
        if (customItem == null) {
            MessageUtil.sendMessage(player, "&c이 아이템은 소켓을 지원하지 않습니다!");
            return;
        }
        
        // 소켓 확인
        if (customItem. getSockets() == 0) {
            MessageUtil.sendMessage(player, "&c이 아이템에는 소켓이 없습니다!");
            return;
        }
        
        // 모든 소켓이 채워졌는지 확인
        if (customItem.getGems(). size() >= customItem.getSockets()) {
            MessageUtil.sendMessage(player, "&c모든 소켓이 이미 채워져 있습니다!");
            MessageUtil.sendMessage(player, "&7소켓: &f" + customItem.getGems().size() + "/" + customItem.getSockets());
            return;
        }
        
        // 소켓 GUI 열기
        try {
            SocketGUI gui = new SocketGUI(plugin, player, customItem, itemStack);
            gui.openGUI(player);
        } catch (Exception e) {
            MessageUtil. sendMessage(player, "&cGUI를 열 수 없습니다!");
            plugin. getLogger().severe("소켓 GUI 오류:");
            e.printStackTrace();
        }
    }
}