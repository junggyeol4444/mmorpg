package com.  multiverse.item. managers;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit. NamespacedKey;
import org.bukkit. persistence.PersistentDataType;
import com.multiverse.item.ItemCore;
import com.  multiverse.item.data.CustomItem;
import com.multiverse. item.data.ItemOption;
import com.multiverse. item.data. Gem;
import com.multiverse. item.utils.NBTUtil;
import com.  multiverse.item.utils.LoreUtil;
import java.util.*;

public class ItemManager {
    
    private ItemCore plugin;
    private DataManager dataManager;
    private Map<String, CustomItem> itemCache;
    
    public ItemManager(ItemCore plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.itemCache = new HashMap<>();
    }
    
    /**
     * ID로 커스텀 아이템 조회
     */
    public CustomItem getItemById(String itemId) throws Exception {
        if (itemCache.containsKey(itemId)) {
            return itemCache.get(itemId);
        }
        
        CustomItem item = dataManager.loadItemTemplate(itemId);
        if (item != null) {
            itemCache.put(itemId, item);
        }
        return item;
    }
    
    /**
     * ItemStack을 CustomItem으로 변환
     */
    public CustomItem fromItemStack(ItemStack itemStack) throws Exception {
        if (itemStack == null || itemStack.getAmount() == 0) {
            return null;
        }
        
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return null;
        }
        
        // NBT 데이터에서 커스텀 아이템 ID 조회
        NamespacedKey key = new NamespacedKey(plugin, "item_id");
        String itemId = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        
        if (itemId == null) {
            return null;
        }
        
        // 템플릿 로드
        CustomItem customItem = getItemById(itemId);
        if (customItem == null) {
            return null;
        }
        
        // 인스턴스 복사 (원본 수정 방지)
        customItem = customItem.clone();
        
        // NBT 데이터 로드
        NamespacedKey enhanceKey = new NamespacedKey(plugin, "enhance_level");
        Integer enhanceLevel = meta.getPersistentDataContainer().get(enhanceKey, PersistentDataType.INTEGER);
        if (enhanceLevel != null) {
            customItem.setEnhanceLevel(enhanceLevel);
        }
        
        NamespacedKey durabilityKey = new NamespacedKey(plugin, "durability");
        Integer durability = meta. getPersistentDataContainer().get(durabilityKey, PersistentDataType.INTEGER);
        if (durability != null) {
            customItem.setDurability(durability);
        }
        
        NamespacedKey tradeCountKey = new NamespacedKey(plugin, "trade_count");
        Integer tradeCount = meta.getPersistentDataContainer(). get(tradeCountKey, PersistentDataType.INTEGER);
        if (tradeCount != null) {
            customItem. setTradeCount(tradeCount);
        }
        
        // 옵션 로드 (Lore에서)
        loadOptionsFromLore(customItem, meta);
        
        // 보석 로드
        loadGemsFromNBT(customItem, meta);
        
        return customItem;
    }
    
    /**
     * CustomItem을 ItemStack으로 변환
     */
    public ItemStack toItemStack(CustomItem customItem) throws Exception {
        ItemStack itemStack = new ItemStack(customItem.getMaterial(), 1);
        ItemMeta meta = itemStack.getItemMeta();
        
        if (meta == null) {
            throw new Exception("ItemMeta를 생성할 수 없습니다!");
        }
        
        // 아이템 이름 설정
        meta. setDisplayName(customItem.getRarity().getColor() + customItem.getName());
        
        // NBT 데이터 저장
        NamespacedKey itemIdKey = new NamespacedKey(plugin, "item_id");
        meta.getPersistentDataContainer().set(itemIdKey, PersistentDataType.STRING, customItem.getItemId());
        
        NamespacedKey enhanceKey = new NamespacedKey(plugin, "enhance_level");
        meta.getPersistentDataContainer().set(enhanceKey, PersistentDataType.INTEGER, customItem.getEnhanceLevel());
        
        NamespacedKey durabilityKey = new NamespacedKey(plugin, "durability");
        meta. getPersistentDataContainer().set(durabilityKey, PersistentDataType.INTEGER, customItem.getDurability());
        
        NamespacedKey tradeCountKey = new NamespacedKey(plugin, "trade_count");
        meta.getPersistentDataContainer().set(tradeCountKey, PersistentDataType.INTEGER, customItem. getTradeCount());
        
        NamespacedKey soulboundKey = new NamespacedKey(plugin, "soulbound");
        meta.getPersistentDataContainer().set(soulboundKey, PersistentDataType.BYTE, (byte) (customItem.isSoulbound() ? 1 : 0));
        
        // Lore 생성
        List<String> lore = new ArrayList<>();
        lore.addAll(LoreUtil.generateItemLore(customItem));
        meta.setLore(lore);
        
        // 속성 설정
        meta.setUnbreakable(customItem.isUnbreakable());
        
        itemStack.setItemMeta(meta);
        
        return itemStack;
    }
    
    /**
     * Lore에서 옵션 로드
     */
    private void loadOptionsFromLore(CustomItem item, ItemMeta meta) {
        List<String> lore = meta.getLore();
        if (lore == null) return;
        
        List<ItemOption> options = new ArrayList<>();
        
        for (String line : lore) {
            if (line.contains("옵션:")) {
                // 옵션 파싱 로직
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    try {
                        ItemOption option = new ItemOption();
                        // 옵션 파싱 (상세 구현은 생략)
                        options.add(option);
                    } catch (Exception e) {
                        plugin.getLogger().warning("옵션 파싱 오류: " + line);
                    }
                }
            }
        }
        
        if (!options.isEmpty()) {
            item.setOptions(options);
        }
    }
    
    /**
     * NBT 데이터에서 보석 로드
     */
    private void loadGemsFromNBT(CustomItem item, ItemMeta meta) {
        NamespacedKey gemsKey = new NamespacedKey(plugin, "gems");
        String gemsData = meta.getPersistentDataContainer().get(gemsKey, PersistentDataType.STRING);
        
        if (gemsData == null || gemsData.isEmpty()) {
            return;
        }
        
        try {
            // JSON 형태로 저장된 보석 데이터 파싱
            List<Gem> gems = new ArrayList<>();
            // 파싱 로직 (상세 구현은 생략)
            item.setGems(gems);
        } catch (Exception e) {
            plugin.getLogger().warning("보석 로드 오류: " + e.getMessage());
        }
    }
    
    /**
     * 캐시 초기화
     */
    public void clearCache() {
        itemCache.clear();
    }
    
    /**
     * 캐시 업데이트
     */
    public void updateCache(String itemId, CustomItem item) {
        itemCache.put(itemId, item);
    }
    
    /**
     * 캐시된 아이템 개수
     */
    public int getCachedItemCount() {
        return itemCache.size();
    }
}