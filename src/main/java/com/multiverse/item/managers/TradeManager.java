package com. multiverse.item.managers;

import org.bukkit. Bukkit;
import org.bukkit.entity.Player;
import com.multiverse.item.ItemCore;
import com.multiverse. item.data.CustomItem;
import com.multiverse.item. data.ItemTrade;
import com.multiverse.item.events.ItemTradeEvent;
import java.util.*;

public class TradeManager {
    
    private ItemCore plugin;
    private ConfigManager configManager;
    private DataManager dataManager;
    private Map<UUID, ItemTrade> activeTrades;
    
    private static final long TRADE_TIMEOUT = 300000; // 5분
    private static final int MAX_TRADE_COUNT = 3; // 최대 거래 가능 횟수
    
    public TradeManager(ItemCore plugin, ConfigManager configManager, DataManager dataManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this. dataManager = dataManager;
        this.activeTrades = new HashMap<>();
    }
    
    /**
     * 거래 가능 여부 확인
     */
    public boolean canTrade(CustomItem item, Player sender, Player receiver) {
        // 귀속 아이템은 거래 불가
        if (item.isSoulbound()) {
            return false;
        }
        
        // 거래 횟수 초과 확인
        if (item.getTradeCount() >= MAX_TRADE_COUNT) {
            return false;
        }
        
        // 송신자와 수신자가 같지 않은지 확인
        if (sender.getUniqueId(). equals(receiver.getUniqueId())) {
            return false;
        }
        
        // 송신자가 오프라인 확인
        if (sender.isOffline()) {
            return false;
        }
        
        // 수신자가 오프라인 확인
        if (receiver.isOffline()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 거래 생성
     */
    public ItemTrade createTrade(CustomItem item, Player sender, Player receiver) throws Exception {
        if (!canTrade(item, sender, receiver)) {
            throw new Exception("이 아이템은 거래할 수 없습니다!");
        }
        
        ItemTrade trade = new ItemTrade();
        trade.setTradeId(UUID.randomUUID().toString());
        trade.setSenderId(sender.getUniqueId());
        trade.setReceiverId(receiver.getUniqueId());
        trade.setItem(item);
        trade. setCreatedTime(System.currentTimeMillis());
        trade.setTimeout(TRADE_TIMEOUT);
        trade.setStatus("PENDING");
        
        return trade;
    }
    
    /**
     * 거래 수락
     */
    public boolean acceptTrade(ItemTrade trade, CustomItem item) throws Exception {
        // 타임아웃 확인
        if (System.currentTimeMillis() - trade.getCreatedTime() > trade.getTimeout()) {
            throw new Exception("거래 요청이 만료되었습니다!");
        }
        
        // 거래 횟수 증가
        item.setTradeCount(item. getTradeCount() + 1);
        
        // 거래 상태 업데이트
        trade.setStatus("ACCEPTED");
        trade.setAcceptedTime(System.currentTimeMillis());
        
        Player sender = Bukkit.getPlayer(trade. getSenderId());
        Player receiver = Bukkit.getPlayer(trade.getReceiverId());
        
        if (sender == null || receiver == null) {
            throw new Exception("한 명 이상의 플레이어가 오프라인 상태입니다!");
        }
        
        // 이벤트 발생
        ItemTradeEvent event = new ItemTradeEvent(sender, receiver, item, true);
        Bukkit.getPluginManager().callEvent(event);
        
        // 거래 기록 저장
        dataManager.saveTradeLog(trade);
        
        return true;
    }
    
    /**
     * 거래 거절
     */
    public boolean declineTrade(ItemTrade trade) {
        trade.setStatus("DECLINED");
        trade.setDeclinedTime(System.currentTimeMillis());
        
        return true;
    }
    
    /**
     * 거래 취소
     */
    public boolean cancelTrade(ItemTrade trade) {
        trade.setStatus("CANCELLED");
        trade. setCancelledTime(System.currentTimeMillis());
        
        return true;
    }
    
    /**
     * 거래 중인 아이템 정보
     */
    public ItemTrade getTrade(String tradeId) {
        for (ItemTrade trade : activeTrades.values()) {
            if (trade.getTradeId(). equals(tradeId)) {
                return trade;
            }
        }
        return null;
    }
    
    /**
     * 플레이어의 활성 거래 조회
     */
    public List<ItemTrade> getActiveTrades(UUID playerId) {
        List<ItemTrade> result = new ArrayList<>();
        
        for (ItemTrade trade : activeTrades.values()) {
            if (trade.getSenderId(). equals(playerId) || trade.getReceiverId(). equals(playerId)) {
                if ("PENDING".equals(trade.getStatus())) {
                    result.add(trade);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 거래 기록 조회
     */
    public List<ItemTrade> getTradeHistory(UUID playerId, int limit) throws Exception {
        return dataManager.loadTradeHistory(playerId, limit);
    }
    
    /**
     * 거래 가능 횟수 남은 개수
     */
    public int getRemainingTradeCount(CustomItem item) {
        return MAX_TRADE_COUNT - item. getTradeCount();
    }
    
    /**
     * 거래 기한 만료 확인 및 정리
     */
    public void cleanupExpiredTrades() {
        long currentTime = System.currentTimeMillis();
        List<String> expiredTradeIds = new ArrayList<>();
        
        for (UUID key : activeTrades.keySet()) {
            ItemTrade trade = activeTrades.get(key);
            if (currentTime - trade.getCreatedTime() > trade.getTimeout()) {
                expiredTradeIds.add(trade.getTradeId());
                trade.setStatus("EXPIRED");
            }
        }
        
        for (String tradeId : expiredTradeIds) {
            activeTrades.values().removeIf(t -> t.getTradeId(). equals(tradeId));
        }
    }
    
    /**
     * 거래 통계
     */
    public Map<String, Integer> getTradeStatistics(UUID playerId) throws Exception {
        Map<String, Integer> stats = new HashMap<>();
        
        List<ItemTrade> trades = getTradeHistory(playerId, 100);
        
        int sent = 0, received = 0, accepted = 0, declined = 0;
        
        for (ItemTrade trade : trades) {
            if (trade.getSenderId().equals(playerId)) {
                sent++;
            } else {
                received++;
            }
            
            if ("ACCEPTED".equals(trade.getStatus())) {
                accepted++;
            } else if ("DECLINED".equals(trade.getStatus())) {
                declined++;
            }
        }
        
        stats.put("sent", sent);
        stats.put("received", received);
        stats.put("accepted", accepted);
        stats.put("declined", declined);
        
        return stats;
    }
}