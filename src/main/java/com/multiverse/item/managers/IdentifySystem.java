package com.multiverse.item. managers;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit. NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import com.multiverse.item.ItemCore;
import com.multiverse.item.data.CustomItem;
import com.multiverse.item. data.ItemRarity;
import com.multiverse.item.events.ItemIdentifyEvent;
import org.bukkit. Bukkit;
import java.util.*;

public class IdentifySystem {
    
    private ItemCore plugin;
    private ConfigManager configManager;
    private DataManager dataManager;
    
    public IdentifySystem(ItemCore plugin, ConfigManager configManager, DataManager dataManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.dataManager = dataManager;
    }
    
    /**
     * 미식별 아이템인지 확인
     */
    public boolean isUnidentified(ItemStack itemStack) {
        if (itemStack == null || itemStack.getAmount() == 0) {
            return false;
        }
        
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        NamespacedKey key = new NamespacedKey(plugin, "unidentified");
        Byte unidentified = meta.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
        
        return unidentified != null && unidentified == 1;
    }
    
    /**
     * 아이템을 미식별 상태로 설정
     */
    public ItemStack createUnidentifiedItem(CustomItem baseItem) throws Exception {
        ItemStack itemStack = new ItemStack(baseItem.getMaterial(), 1);
        ItemMeta meta = itemStack.getItemMeta();
        
        if (meta == null) {
            throw new Exception("ItemMeta를 생성할 수 없습니다!");
        }
        
        // 아이템 이름을 "미식별 아이템"으로 설정
        meta.setDisplayName("&7미식별 아이템");
        
        // 기본 정보만 저장
        NamespacedKey itemIdKey = new NamespacedKey(plugin, "item_id");
        meta.getPersistentDataContainer().set(itemIdKey, PersistentDataType.STRING, baseItem.getItemId());
        
        // 미식별 표시
        NamespacedKey unidentifiedKey = new NamespacedKey(plugin, "unidentified");
        meta.getPersistentDataContainer().set(unidentifiedKey, PersistentDataType. BYTE, (byte) 1);
        
        // 기본 롤 설정
        List<String> lore = new ArrayList<>();
        lore.add("&7아직 식별되지 않은 아이템입니다.");
        lore.add("&7식별 시스템을 이용하여 식별하세요.");
        meta.setLore(lore);
        
        itemStack.setItemMeta(meta);
        
        return itemStack;
    }
    
    /**
     * 아이템 식별
     */
    public ItemStack identifyItem(ItemStack unidentifiedStack) throws Exception {
        if (! isUnidentified(unidentifiedStack)) {
            throw new Exception("이 아이템은 미식별 아이템이 아닙니다!");
        }
        
        ItemMeta meta = unidentifiedStack.getItemMeta();
        if (meta == null) {
            throw new Exception("ItemMeta를 로드할 수 없습니다!");
        }
        
        // 원본 아이템 ID 가져오기
        NamespacedKey itemIdKey = new NamespacedKey(plugin, "item_id");
        String itemId = meta.getPersistentDataContainer().get(itemIdKey, PersistentDataType. STRING);
        
        if (itemId == null) {
            throw new Exception("아이템 ID를 찾을 수 없습니다!");
        }
        
        // 원본 아이템 템플릿 로드
        CustomItem baseItem = plugin.getItemManager().getItemById(itemId);
        if (baseItem == null) {
            throw new Exception("아이템 템플릿을 찾을 수 없습니다!");
        }
        
        // 식별 시 옵션 롤링
        CustomItem identifiedItem = baseItem.clone();
        plugin.getItemGenerator().generateOptions(identifiedItem);
        
        // 등급 결정 (랜덤)
        ItemRarity rarity = getRandomRarity();
        identifiedItem.setRarity(rarity);
        
        // 소켓 결정
        int sockets = getRandomSocketCount(rarity);
        identifiedItem. setSockets(sockets);
        
        // ItemStack으로 변환
        ItemStack identifiedStack = plugin.getItemManager().toItemStack(identifiedItem);
        
        // 미식별 표시 제거
        ItemMeta newMeta = identifiedStack.getItemMeta();
        if (newMeta != null) {
            NamespacedKey unidentifiedKey = new NamespacedKey(plugin, "unidentified");
            newMeta. getPersistentDataContainer().remove(unidentifiedKey);
            identifiedStack.setItemMeta(newMeta);
        }
        
        // 이벤트 발생
        ItemIdentifyEvent event = new ItemIdentifyEvent(identifiedItem);
        Bukkit.getPluginManager(). callEvent(event);
        
        return identifiedStack;
    }
    
    /**
     * 식별 성공 여부 (나중에 성공률 시스템 추가 가능)
     */
    public boolean canIdentify(ItemStack itemStack) {
        return isUnidentified(itemStack);
    }
    
    /**
     * 랜덤 등급 반환
     */
    private ItemRarity getRandomRarity() {
        double random = Math.random() * 100;
        
        if (random < 50) {
            return ItemRarity.COMMON;
        } else if (random < 75) {
            return ItemRarity.UNCOMMON;
        } else if (random < 90) {
            return ItemRarity.RARE;
        } else if (random < 97) {
            return ItemRarity.EPIC;
        } else if (random < 99. 5) {
            return ItemRarity.LEGENDARY;
        } else {
            return ItemRarity.MYTHIC;
        }
    }
    
    /**
     * 등급에 따른 소켓 개수
     */
    private int getRandomSocketCount(ItemRarity rarity) {
        switch (rarity) {
            case COMMON:
                return 0;
            case UNCOMMON:
                return new Random().nextInt(2); // 0 or 1
            case RARE:
                return new Random().nextInt(2) + 1; // 1 or 2
            case EPIC:
                return new Random().nextInt(2) + 2; // 2 or 3
            case LEGENDARY:
                return new Random().nextInt(2) + 3; // 3 or 4
            case MYTHIC:
                return new Random().nextInt(2) + 4; // 4 or 5
            default:
                return 0;
        }
    }
}