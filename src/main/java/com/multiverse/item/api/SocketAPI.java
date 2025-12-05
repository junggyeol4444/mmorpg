package com.multiverse. item.api;

import com. multiverse.item.ItemCore;
import com.multiverse.  item.data.CustomItem;
import com.multiverse.item.data. Gem;
import org.bukkit.entity.Player;
import java.util.List;

public class SocketAPI {
    
    private static SocketAPI instance;
    private ItemCore plugin;
    
    /**
     * 싱글톤 인스턴스 가져오기
     */
    public static SocketAPI getInstance() {
        if (instance == null) {
            instance = new SocketAPI();
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
     * 보석 끼우기
     */
    public boolean insertGem(Player player, CustomItem item, int socketIndex, Gem gem) {
        if (player == null || item == null || gem == null) {
            return false;
        }
        
        return plugin.getGemManager().insertGem(player, item, socketIndex, gem);
    }
    
    /**
     * 보석 빼기
     */
    public boolean removeGem(Player player, CustomItem item, int socketIndex) {
        if (player == null || item == null) {
            return false;
        }
        
        return plugin.getGemManager().removeGem(player, item, socketIndex);
    }
    
    /**
     * 아이템의 소켓 개수
     */
    public int getSocketCount(CustomItem item) {
        if (item == null || item.getSockets() == null) {
            return 0;
        }
        return item.getSockets().size();
    }
    
    /**
     * 아이템의 소켓이 가득 찼는지 확인
     */
    public boolean isSocketFull(CustomItem item) {
        if (item == null) {
            return true;
        }
        return getSocketCount(item) >= 3;
    }
    
    /**
     * 보석 끼우기 비용 계산
     */
    public int calculateInsertCost(int socketIndex) {
        return 500 + (socketIndex * 100);
    }
    
    /**
     * 보석 빼기 비용 계산
     */
    public int calculateRemoveCost(int socketIndex) {
        return 300 + (socketIndex * 50);
    }
    
    /**
     * 소켓에 끼워진 보석 조회
     */
    public Gem getSocket(CustomItem item, int socketIndex) {
        if (item == null || item.getSockets() == null) {
            return null;
        }
        
        if (socketIndex < 0 || socketIndex >= item.getSockets(). size()) {
            return null;
        }
        
        return item.getSockets().get(socketIndex);
    }
    
    /**
     * 모든 소켓 조회
     */
    public List<Gem> getAllSockets(CustomItem item) {
        if (item == null || item.getSockets() == null) {
            return new java.util.ArrayList<>();
        }
        return new java.util.ArrayList<>(item.getSockets());
    }
    
    /**
     * 소켓 정보 조회
     */
    public String getSocketInfo(CustomItem item) {
        if (item == null) {
            return "";
        }
        
        int socketCount = getSocketCount(item);
        return "§b소켓: " + socketCount + "/3";
    }
    
    /**
     * 색상 호환성 확인
     */
    public boolean isColorCompatible(Gem gem, String socketColor) {
        if (gem == null || socketColor == null) {
            return false;
        }
        
        return gem.getColor(). equalsIgnoreCase(socketColor);
    }
    
    /**
     * 최대 소켓 개수
     */
    public int getMaxSockets() {
        return 3;
    }
}