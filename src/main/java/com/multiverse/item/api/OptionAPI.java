package com.multiverse.item.api;

import com.multiverse.item.ItemCore;
import com. multiverse.item.data.CustomItem;
import com.multiverse.item.data.ItemOption;
import org.bukkit.entity.Player;
import java.util.List;

public class OptionAPI {
    
    private static OptionAPI instance;
    private ItemCore plugin;
    
    /**
     * 싱글톤 인스턴스 가져오기
     */
    public static OptionAPI getInstance() {
        if (instance == null) {
            instance = new OptionAPI();
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
     * 옵션 생성
     */
    public ItemOption createOption(String name, String type, double value, String trigger) {
        ItemOption option = new ItemOption();
        option.setName(name);
        option.setValue(value);
        return option;
    }
    
    /**
     * 아이템에 옵션 추가
     */
    public void addOptionToItem(CustomItem item, ItemOption option) {
        if (item != null && option != null) {
            item.getOptions().add(option);
        }
    }
    
    /**
     * 아이템의 옵션 제거
     */
    public void removeOptionFromItem(CustomItem item, ItemOption option) {
        if (item != null && option != null) {
            item.getOptions().remove(option);
        }
    }
    
    /**
     * 아이템의 옵션 개수
     */
    public int getOptionCount(CustomItem item) {
        if (item == null || item.getOptions() == null) {
            return 0;
        }
        return item.getOptions().size();
    }
    
    /**
     * 아이템의 모든 옵션 조회
     */
    public List<ItemOption> getOptions(CustomItem item) {
        if (item == null || item. getOptions() == null) {
            return new java.util.ArrayList<>();
        }
        return new java.util.ArrayList<>(item.getOptions());
    }
    
    /**
     * 옵션 리롤 실행
     */
    public boolean rerollOptions(Player player, CustomItem item) {
        if (player == null || item == null) {
            return false;
        }
        
        return plugin.getItemOptionManager().rerollOptions(player, item);
    }
    
    /**
     * 리롤 비용 계산
     */
    public int calculateRerollCost(int rerollCount) {
        return 1000 + (rerollCount * 500);
    }
    
    /**
     * 리롤 가능 여부 확인
     */
    public boolean canReroll(CustomItem item) {
        if (item == null) {
            return false;
        }
        
        return getOptionCount(item) > 0;
    }
    
    /**
     * 옵션 정보 조회
     */
    public String getOptionInfo(ItemOption option) {
        if (option == null) {
            return "";
        }
        
        return "§b" + option.getName() + "\n" +
               "§7값: " + String.format("%.1f", option.getValue());
    }
    
    /**
     * 옵션 발동 확률 계산
     */
    public double getTriggerChance(ItemOption option) {
        if (option == null) {
            return 0;
        }
        return option.getValue();
    }
    
    /**
     * 옵션 유효성 확인
     */
    public boolean isValidOption(ItemOption option) {
        return option != null && option.getName() != null && option.getValue() > 0;
    }
    
    /**
     * 최대 옵션 개수
     */
    public int getMaxOptions() {
        return 5;
    }
    
    /**
     * 최소 옵션 개수
     */
    public int getMinOptions() {
        return 1;
    }
}