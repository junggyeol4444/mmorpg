package com. multiverse.item. api;

import com.multiverse.item.ItemCore;
import com.multiverse. item.data.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.Map;

public class ItemAPI {
    
    private static ItemAPI instance;
    private ItemCore plugin;
    
    /**
     * 싱글톤 인스턴스 가져오기
     */
    public static ItemAPI getInstance() {
        if (instance == null) {
            instance = new ItemAPI();
        }
        return instance;
    }
    
    /**
     * 플러그인 초기화
     */
    public void init(ItemCore plugin) {
        this. plugin = plugin;
    }
    
    /**
     * 커스텀 아이템 생성
     */
    public CustomItem createItem(String itemId, String name, String type) {
        CustomItem item = new CustomItem();
        item.setItemId(itemId);
        item.setName(name);
        return item;
    }
    
    /**
     * 커스텀 아이템 생성 (설정)
     */
    public CustomItem createItem(String itemId, String name, String type, Map<String, Double> baseStats) {
        CustomItem item = createItem(itemId, name, type);
        item.setBaseStats(baseStats);
        return item;
    }
    
    /**
     * 아이템 복제
     */
    public CustomItem duplicateItem(CustomItem original) {
        if (original == null) {
            return null;
        }
        
        CustomItem copy = new CustomItem();
        copy.setItemId(original.getItemId() + "_copy");
        copy. setName(original.getName());
        copy.setType(original.getType());
        copy. setRarity(original.getRarity());
        copy.setBaseStats(new java.util.HashMap<>(original. getBaseStats()));
        
        return copy;
    }
    
    /**
     * ItemStack에서 CustomItem 변환
     */
    public CustomItem fromItemStack(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        
        try {
            return plugin.getItemManager().fromItemStack(itemStack);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * CustomItem을 ItemStack으로 변환
     */
    public ItemStack toItemStack(CustomItem item) {
        if (item == null) {
            return null;
        }
        
        try {
            return plugin.getItemManager().toItemStack(item);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 플레이어에게 아이템 지급
     */
    public boolean giveItemToPlayer(Player player, CustomItem item, int amount) {
        if (player == null || item == null) {
            return false;
        }
        
        ItemStack itemStack = toItemStack(item);
        if (itemStack == null) {
            return false;
        }
        
        itemStack.setAmount(amount);
        player.getInventory().addItem(itemStack);
        return true;
    }
    
    /**
     * 아이템 기본 스탯 가져오기
     */
    public Map<String, Double> getBaseStats(CustomItem item) {
        if (item == null) {
            return new java.util.HashMap<>();
        }
        return new java.util.HashMap<>(item.getBaseStats());
    }
    
    /**
     * 아이템 기본 스탯 설정
     */
    public void setBaseStats(CustomItem item, Map<String, Double> stats) {
        if (item != null) {
            item.setBaseStats(stats);
        }
    }
    
    /**
     * 아이템 옵션 가져오기
     */
    public List<? > getOptions(CustomItem item) {
        if (item == null) {
            return new java.util.ArrayList<>();
        }
        return item.getOptions();
    }
    
    /**
     * 아이템 정보 출력
     */
    public String getItemInfo(CustomItem item) {
        if (item == null) {
            return "";
        }
        
        StringBuilder info = new StringBuilder();
        info.append("§b"). append(item.getName()).append("\n");
        info.append("§7타입: ").append(item. getType()).append("\n");
        info.append("§7희귀도: ").append(item.getRarity(). getKoreanName()).append("\n");
        info.append("§7강화: +").append(item.getEnhanceLevel()).append("\n");
        
        return info.toString();
    }
    
    /**
     * 아이템 유효성 확인
     */
    public boolean isValidItem(CustomItem item) {
        return item != null && item.getItemId() != null && item.getName() != null;
    }
    
    /**
     * 아이템 비교
     */
    public boolean isSameItem(CustomItem item1, CustomItem item2) {
        if (item1 == null || item2 == null) {
            return false;
        }
        return item1.getItemId().equals(item2.getItemId());
    }
}