package com. multiverse.item. commands. subcommands;

import org.bukkit. Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.multiverse.item.ItemCore;
import com.multiverse. item.data.CustomItem;
import com.multiverse.item.data.ItemRarity;
import com.multiverse.item.utils.MessageUtil;

public class AdminCommand implements SubCommand {
    
    private ItemCore plugin;
    
    public AdminCommand(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void execute(Player player, String[] args) {
        
        // 권한 확인
        if (!player.hasPermission("item.admin")) {
            MessageUtil.sendMessage(player, "&c이 명령어는 관리자만 사용 가능합니다!");
            return;
        }
        
        if (args.length == 0) {
            showHelp(player);
            return;
        }
        
        String subCommand = args[0]. toLowerCase();
        
        switch (subCommand) {
            case "give":
                adminGive(player, args);
                break;
                
            case "create":
                adminCreate(player, args);
                break;
                
            case "reload":
                adminReload(player);
                break;
                
            case "info":
                adminInfo(player, args);
                break;
                
            case "delete":
                adminDelete(player, args);
                break;
                
            default:
                MessageUtil.sendMessage(player, "&c알 수 없는 명령어입니다!");
                showHelp(player);
        }
    }
    
    private void adminGive(Player player, String[] args) {
        // /item admin give <플레이어> <아이템ID> [등급] [강화]
        if (args.length < 3) {
            MessageUtil.sendMessage(player, "&c사용법: /item admin give <플레이어> <아이템ID> [등급] [강화]");
            return;
        }
        
        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            MessageUtil.sendMessage(player, "&c플레이어를 찾을 수 없습니다: " + args[1]);
            return;
        }
        
        String itemId = args[2];
        CustomItem customItem = null;
        
        try {
            customItem = plugin.getItemManager().getItemById(itemId);
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "&c아이템을 찾을 수 없습니다: " + itemId);
            return;
        }
        
        if (customItem == null) {
            MessageUtil.sendMessage(player, "&c아이템을 찾을 수 없습니다: " + itemId);
            return;
        }
        
        // 등급 처리
        if (args.length >= 4) {
            try {
                ItemRarity rarity = ItemRarity.valueOf(args[3].toUpperCase());
                customItem.setRarity(rarity);
            } catch (IllegalArgumentException e) {
                MessageUtil.sendMessage(player, "&c잘못된 등급입니다: " + args[3]);
                return;
            }
        }
        
        // 강화 처리
        if (args.length >= 5) {
            try {
                int enhance = Integer.parseInt(args[4]);
                if (enhance < 0 || enhance > customItem.getMaxEnhance()) {
                    MessageUtil.sendMessage(player, "&c강화는 0~" + customItem.getMaxEnhance() + " 사이여야 합니다!");
                    return;
                }
                customItem.setEnhanceLevel(enhance);
            } catch (NumberFormatException e) {
                MessageUtil.sendMessage(player, "&c강화는 숫자여야 합니다!");
                return;
            }
        }
        
        try {
            ItemStack itemStack = plugin.getItemManager().toItemStack(customItem);
            targetPlayer.getInventory().addItem(itemStack);
            
            MessageUtil.sendMessage(player, "&a아이템을 지급했습니다!");
            MessageUtil.sendMessage(targetPlayer, "&a관리자가 아이템을 지급했습니다: &f" + customItem.getName());
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "&c오류 발생!");
            e.printStackTrace();
        }
    }
    
    private void adminCreate(Player player, String[] args) {
        // /item admin create <아이템ID>
        if (args.length < 2) {
            MessageUtil.sendMessage(player, "&c사용법: /item admin create <아이템ID>");
            return;
        }
        
        String itemId = args[1];
        MessageUtil.sendMessage(player, "&e아이템 생성 기능은 준비 중입니다!");
        MessageUtil.sendMessage(player, "&7ItemID: &f" + itemId);
    }
    
    private void adminReload(Player player) {
        try {
            plugin.reloadConfig();
            plugin.getConfigManager().reload();
            plugin.getDataManager().loadAllData();
            
            MessageUtil.sendMessage(player, "&a설정을 다시 로드했습니다!");
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "&c설정 로드 중 오류 발생!");
            e.printStackTrace();
        }
    }
    
    private void adminInfo(Player player, String[] args) {
        // /item admin info <아이템ID>
        if (args.length < 2) {
            MessageUtil.sendMessage(player, "&c사용법: /item admin info <아이템ID>");
            return;
        }
        
        String itemId = args[1];
        CustomItem customItem = null;
        
        try {
            customItem = plugin.getItemManager().getItemById(itemId);
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "&c아이템을 찾을 수 없습니다: " + itemId);
            return;
        }
        
        if (customItem == null) {
            MessageUtil.sendMessage(player, "&c아이템을 찾을 수 없습니다: " + itemId);
            return;
        }
        
        MessageUtil.sendMessage(player, "&8═══════════════════════════════════");
        MessageUtil.sendMessage(player, "&e아이템 정보: &f" + itemId);
        MessageUtil.sendMessage(player, "&8═══════════════════════════════════");
        MessageUtil.sendMessage(player, "&7이름: &f" + customItem. getName());
        MessageUtil.sendMessage(player, "&7등급: &f" + customItem. getRarity().name());
        MessageUtil.sendMessage(player, "&7타입: &f" + customItem.getType().name());
        MessageUtil.sendMessage(player, "&7최대 강화: &f" + customItem.getMaxEnhance());
        MessageUtil.sendMessage(player, "&7소켓: &f" + customItem.getSockets());
        MessageUtil.sendMessage(player, "&7옵션 개수: &f" + (customItem.getOptions() != null ? customItem.getOptions().size() : 0));
        MessageUtil.sendMessage(player, "&8═══════════════════════════════════");
    }
    
    private void adminDelete(Player player, String[] args) {
        // /item admin delete <아이템ID>
        if (args.length < 2) {
            MessageUtil.sendMessage(player, "&c사용법: /item admin delete <아이템ID>");
            return;
        }
        
        String itemId = args[1];
        MessageUtil.sendMessage(player, "&e아이템 삭제 기능은 준비 중입니다!");
        MessageUtil.sendMessage(player, "&7ItemID: &f" + itemId);
    }
    
    private void showHelp(Player player) {
        MessageUtil.sendMessage(player, "&8═══════════════════════════════════");
        MessageUtil. sendMessage(player, "&e[관리자 커맨드]");
        MessageUtil.sendMessage(player, "&8═══════════════════════════════════");
        MessageUtil.sendMessage(player, "&7/item admin give <플레이어> <ID> [등급] [강화]");
        MessageUtil.sendMessage(player, "&7/item admin create <아이템ID>");
        MessageUtil.sendMessage(player, "&7/item admin reload");
        MessageUtil.sendMessage(player, "&7/item admin info <아이템ID>");
        MessageUtil.sendMessage(player, "&7/item admin delete <아이템ID>");
        MessageUtil.sendMessage(player, "&8═══════════════════════════════════");
    }
}