package com.multiverse.item. commands. subcommands;

import org.bukkit. Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.multiverse.item.ItemCore;
import com.multiverse. item.data.CustomItem;
import com. multiverse.item.data.ItemTrade;
import com.multiverse.item.utils.MessageUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TradeCommand implements SubCommand {
    
    private ItemCore plugin;
    private static Map<UUID, ItemTrade> tradeRequests = new HashMap<>();
    private static final long TRADE_TIMEOUT = 300000; // 5분
    
    public TradeCommand(ItemCore plugin) {
        this. plugin = plugin;
    }
    
    @Override
    public void execute(Player player, String[] args) {
        
        if (args.length == 0) {
            showHelp(player);
            return;
        }
        
        String subCommand = args[0]. toLowerCase();
        
        switch (subCommand) {
            case "요청":
                if (args.length < 2) {
                    MessageUtil.sendMessage(player, "&c사용법: /item trade 요청 <플레이어>");
                    return;
                }
                requestTrade(player, args[1]);
                break;
                
            case "수락":
                acceptTrade(player);
                break;
                
            case "거절":
                declineTrade(player);
                break;
                
            case "취소":
                cancelTrade(player);
                break;
                
            default:
                MessageUtil.sendMessage(player, "&c알 수 없는 명령어입니다!");
                showHelp(player);
        }
    }
    
    private void requestTrade(Player player, String targetName) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        
        if (targetPlayer == null) {
            MessageUtil.sendMessage(player, "&c플레이어를 찾을 수 없습니다: " + targetName);
            return;
        }
        
        if (targetPlayer.getUniqueId(). equals(player.getUniqueId())) {
            MessageUtil.sendMessage(player, "&c자신과 거래할 수 없습니다!");
            return;
        }
        
        // 손에 들고 있는 아이템 확인
        ItemStack itemStack = player.getInventory(). getItemInMainHand();
        
        if (itemStack == null || itemStack.getAmount() == 0) {
            MessageUtil. sendMessage(player, "&c손에 들고 있는 아이템이 없습니다!");
            return;
        }
        
        // CustomItem으로 변환
        CustomItem customItem = null;
        try {
            customItem = plugin.getItemManager().fromItemStack(itemStack);
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "&c이 아이템은 거래할 수 없습니다!");
            return;
        }
        
        if (customItem == null) {
            MessageUtil. sendMessage(player, "&c이 아이템은 거래할 수 없습니다!");
            return;
        }
        
        // 귀속 아이템 확인
        if (customItem.isSoulbound()) {
            MessageUtil.sendMessage(player, "&c귀속 아이템은 거래할 수 없습니다!");
            return;
        }
        
        // 거래 횟수 확인
        if (customItem.getTradeCount() >= customItem.getMaxTrades()) {
            MessageUtil.sendMessage(player, "&c더 이상 거래할 수 없습니다!");
            MessageUtil.sendMessage(player, "&7거래 횟수: &f" + customItem.getTradeCount() + "/" + customItem.getMaxTrades());
            return;
        }
        
        // 거래 요청 생성
        ItemTrade trade = new ItemTrade();
        trade.setTradeId(UUID.randomUUID().toString());
        trade.setSenderId(player.getUniqueId());
        trade.setReceiverId(targetPlayer.getUniqueId());
        trade. setItem(customItem);
        trade. setCreatedTime(System.currentTimeMillis());
        trade.setTimeout(TRADE_TIMEOUT);
        
        // 요청 저장
        tradeRequests. put(targetPlayer.getUniqueId(), trade);
        
        // 메시지 전송
        MessageUtil.sendMessage(player, "&a거래 요청을 보냈습니다!");
        MessageUtil.sendMessage(player, "&7상대: &f" + targetPlayer.getName());
        MessageUtil.sendMessage(player, "&7아이템: &f" + customItem.getName());
        
        MessageUtil.sendMessage(targetPlayer, "&e" + player.getName() + "&a님이 거래를 요청했습니다!");
        MessageUtil.sendMessage(targetPlayer, "&7아이템: &f" + customItem.getName());
        MessageUtil.sendMessage(targetPlayer, "&7수락: &f/item trade 수락");
        MessageUtil.sendMessage(targetPlayer, "&7거절: &f/item trade 거절");
    }
    
    private void acceptTrade(Player player) {
        if (! tradeRequests.containsKey(player.getUniqueId())) {
            MessageUtil. sendMessage(player, "&c받은 거래 요청이 없습니다!");
            return;
        }
        
        ItemTrade trade = tradeRequests.get(player.getUniqueId());
        
        // 타임아웃 확인
        if (System.currentTimeMillis() - trade.getCreatedTime() > trade.getTimeout()) {
            MessageUtil. sendMessage(player, "&c거래 요청이 만료되었습니다!");
            tradeRequests.remove(player.getUniqueId());
            return;
        }
        
        Player senderPlayer = Bukkit.getPlayer(trade.getSenderId());
        if (senderPlayer == null) {
            MessageUtil. sendMessage(player, "&c거래 요청자가 오프라인 상태입니다!");
            tradeRequests.remove(player. getUniqueId());
            return;
        }
        
        try {
            // 거래 횟수 증가
            CustomItem item = trade.getItem();
            item. setTradeCount(item.getTradeCount() + 1);
            
            // ItemStack으로 변환
            ItemStack itemStack = plugin.getItemManager().toItemStack(item);
            
            // 플레이어에게 아이템 전달
            player.getInventory().addItem(itemStack);
            
            // 메시지 전송
            MessageUtil.sendMessage(player, "&a거래를 수락했습니다!");
            MessageUtil.sendMessage(player, "&7아이템: &f" + item.getName());
            
            MessageUtil.sendMessage(senderPlayer, "&a거래가 성공했습니다!");
            MessageUtil.sendMessage(senderPlayer, "&7받는 플레이어: &f" + player.getName());
            
            // 거래 기록 저장
            plugin.getDataManager().saveTradeLog(trade);
            
            // 요청 삭제
            tradeRequests.remove(player.getUniqueId());
            
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "&c거래 처리 중 오류가 발생했습니다!");
            plugin.getLogger().severe("거래 오류:");
            e.printStackTrace();
        }
    }
    
    private void declineTrade(Player player) {
        if (!tradeRequests.containsKey(player.getUniqueId())) {
            MessageUtil.sendMessage(player, "&c받은 거래 요청이 없습니다!");
            return;
        }
        
        ItemTrade trade = tradeRequests.get(player.getUniqueId());
        Player senderPlayer = Bukkit.getPlayer(trade.getSenderId());
        
        MessageUtil.sendMessage(player, "&a거래를 거절했습니다!");
        if (senderPlayer != null) {
            MessageUtil.sendMessage(senderPlayer, "&c" + player.getName() + "님이 거래를 거절했습니다!");
        }
        
        tradeRequests.remove(player.getUniqueId());
    }
    
    private void cancelTrade(Player player) {
        boolean found = false;
        
        for (UUID key : tradeRequests.keySet()) {
            ItemTrade trade = tradeRequests.get(key);
            if (trade.getSenderId().equals(player.getUniqueId())) {
                Player receiverPlayer = Bukkit.getPlayer(key);
                
                MessageUtil.sendMessage(player, "&a거래 요청을 취소했습니다!");
                if (receiverPlayer != null) {
                    MessageUtil.sendMessage(receiverPlayer, "&c거래 요청이 취소되었습니다!");
                }
                
                tradeRequests.remove(key);
                found = true;
                break;
            }
        }
        
        if (!found) {
            MessageUtil.sendMessage(player, "&c보낸 거래 요청이 없습니다!");
        }
    }
    
    private void showHelp(Player player) {
        MessageUtil.sendMessage(player, "&8═══════════════════════════════════");
        MessageUtil.sendMessage(player, "&e거래 명령어");
        MessageUtil.sendMessage(player, "&8═══════════════════════════════════");
        MessageUtil.sendMessage(player, "&7/item trade 요청 <플레이어> &f- 거래 요청");
        MessageUtil.sendMessage(player, "&7/item trade 수락 &f- 거래 수락");
        MessageUtil.sendMessage(player, "&7/item trade 거절 &f- 거래 거절");
        MessageUtil.sendMessage(player, "&7/item trade 취소 &f- 거래 취소");
        MessageUtil.sendMessage(player, "&8═══════════════════════════════════");
    }
}