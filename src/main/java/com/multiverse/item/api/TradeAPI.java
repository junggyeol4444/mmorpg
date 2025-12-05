package com.multiverse.item.api;

import com.multiverse. item.ItemCore;
import com.multiverse.  item.data.CustomItem;
import org.bukkit.entity.Player;

public class TradeAPI {
    
    private static TradeAPI instance;
    private ItemCore plugin;
    
    /**
     * 싱글톤 인스턴스 가져오기
     */
    public static TradeAPI getInstance() {
        if (instance == null) {
            instance = new TradeAPI();
        }
        return instance;
    }
    
    /**
     * 플러그인 초기화
     */
    public void init(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 거래 가능 여부 확인
     */
    public boolean canTrade(CustomItem item) {
        if (item == null) {
            return false;
        }
        
        // 소울바운드 아이템은 거래 불가
        return ! item.isSoulbound();
    }
    
    /**
     * 거래 시작
     */
    public boolean startTrade(Player buyer, Player seller, CustomItem item, int price) {
        if (buyer == null || seller == null || item == null) {
            return false;
        }
        
        if (!canTrade(item)) {
            return false;
        }
        
        return plugin.getTradeManager().startTrade(buyer, seller, item, price);
    }
    
    /**
     * 거래 취소
     */
    public boolean cancelTrade(Player player) {
        if (player == null) {
            return false;
        }
        
        return plugin.getTradeManager().cancelTrade(player);
    }
    
    /**
     * 거래 확정
     */
    public boolean confirmTrade(Player player) {
        if (player == null) {
            return false;
        }
        
        return plugin.getTradeManager().confirmTrade(player);
    }
    
    /**
     * 거래 수수료 계산
     */
    public int calculateTradeFee(int price) {
        double fee = price * 0.1; // 기본 10%
        int finalFee = (int) fee;
        return Math.max(100, finalFee); // 최소 100 골드
    }
    
    /**
     * 판매자 수익 계산
     */
    public int calculateSellerProceeds(int price) {
        return price - calculateTradeFee(price);
    }
    
    /**
     * 거래 가능한 가격 범위 확인
     */
    public boolean isValidPrice(int price) {
        return price >= 100 && price <= 1000000;
    }
    
    /**
     * 아이템 거래 횟수
     */
    public int getTradeCount(CustomItem item) {
        if (item == null) {
            return 0;
        }
        return item.getTradeCount();
    }
    
    /**
     * 거래 정보 조회
     */
    public String getTradeInfo(CustomItem item, int price) {
        if (item == null) {
            return "";
        }
        
        int fee = calculateTradeFee(price);
        int proceeds = calculateSellerProceeds(price);
        
        return "§b거래 정보\n" +
               "§7가격: §a" + price + " Gold\n" +
               "§7수수료: §c" + fee + " Gold\n" +
               "§7수익: §a" + proceeds + " Gold";
    }
    
    /**
     * 플레이어 거래 제한 확인
     */
    public boolean hasTradeLimit(Player player) {
        if (player == null) {
            return false;
        }
        
        return plugin. getTradeManager().hasTradeLimit(player);
    }
    
    /**
     * 바인딩 온 트레이드 활성화
     */
    public void bindItemOnTrade(CustomItem item) {
        if (item != null) {
            item.setSoulbound(true);
        }
    }
}