package com.multiverse.item.commands. subcommands;

import org. bukkit. Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.multiverse.item.ItemCore;
import com.multiverse. item.data.CustomItem;
import com.multiverse. item.data.ItemRarity;
import com.multiverse.item.utils.MessageUtil;

public class GiveCommand implements SubCommand {
    
    private ItemCore plugin;
    
    public GiveCommand(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void execute(Player player, String[] args) {
        
        // 권한 확인
        if (! player.hasPermission("item. admin")) {
            MessageUtil.sendMessage(player, "&c이 명령어는 관리자만 사용 가능합니다!");
            return;
        }
        
        // 인자 확인: /item give <플레이어> <아이템ID> [등급] [강화]
        if (args.length < 2) {
            MessageUtil.sendMessage(player, "&c사용법: /item give <플레이어> <아이템ID> [등급] [강화]");
            return;
        }
        
        // 플레이어 확인
        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            MessageUtil.sendMessage(player, "&c플레이어를 찾을 수 없습니다: " + args[0]);
            return;
        }
        
        // 아이템 ID 확인
        String itemId = args[1];
        CustomItem customItem = null;
        
        try {
            customItem = plugin. getItemManager().getItemById(itemId);
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "&c아이템을 찾을 수 없습니다: " + itemId);
            plugin.getLogger().severe("아이템 로드 오류: " + itemId);
            return;
        }
        
        if (customItem == null) {
            MessageUtil.sendMessage(player, "&c아이템을 찾을 수 없습니다: " + itemId);
            return;
        }
        
        // 등급 처리 (선택사항)
        if (args.length >= 3) {
            try {
                ItemRarity rarity = ItemRarity.valueOf(args[2].toUpperCase());
                customItem.setRarity(rarity);
            } catch (IllegalArgumentException e) {
                MessageUtil.sendMessage(player, "&c잘못된 등급입니다: " + args[2]);
                return;
            }
        }
        
        // 강화 레벨 처리 (선택사항)
        if (args.length >= 4) {
            try {
                int enhanceLevel = Integer.parseInt(args[3]);
                if (enhanceLevel < 0 || enhanceLevel > customItem.getMaxEnhance()) {
                    MessageUtil. sendMessage(player, "&c강화 레벨은 0~" + customItem.getMaxEnhance() + " 사이여야 합니다!");
                    return;
                }
                customItem.setEnhanceLevel(enhanceLevel);
            } catch (NumberFormatException e) {
                MessageUtil.sendMessage(player, "&c강화 레벨은 숫자여야 합니다!");
                return;
            }
        }
        
        // ItemStack으로 변환
        ItemStack itemStack = null;
        try {
            itemStack = plugin.getItemManager().toItemStack(customItem);
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "&cItemStack 변환 중 오류 발생!");
            plugin.getLogger().severe("ItemStack 변환 오류: " + itemId);
            e.printStackTrace();
            return;
        }
        
        if (itemStack == null) {
            MessageUtil.sendMessage(player, "&c아이템 변환 실패!");
            return;
        }
        
        // 플레이어에게 지급
        targetPlayer.getInventory().addItem(itemStack);
        
        // 메시지 출력
        MessageUtil.sendMessage(player, "&a아이템을 지급했습니다!");
        MessageUtil.sendMessage(player, "&7플레이어: &f" + targetPlayer.getName());
        MessageUtil.sendMessage(player, "&7아이템: &f" + customItem.getName());
        MessageUtil.sendMessage(player, "&7등급: &f" + customItem.getRarity().name());
        MessageUtil.sendMessage(player, "&7강화: &f+" + customItem.getEnhanceLevel());
        
        MessageUtil.sendMessage(targetPlayer, "&a관리자가 아이템을 지급했습니다!");
        MessageUtil.sendMessage(targetPlayer, "&7아이템: &f" + customItem.getName() + " &7(+" + customItem.getEnhanceLevel() + ")");
    }
}