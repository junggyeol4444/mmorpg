package com.multiverse. pet.hook;

import com.multiverse.pet.PetCore;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org. bukkit.entity. Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit. persistence.PersistentDataType;
import org. bukkit. NamespacedKey;
import org.bukkit. plugin.Plugin;

import java. lang.reflect.Method;
import java. util.HashMap;
import java. util.Map;

/**
 * ItemCore 연동 훅
 * 커스텀 아이템 시스템 연동
 */
public class ItemCoreHook {

    private final PetCore plugin;
    private Plugin itemCore;
    private boolean enabled;

    // 리플렉션용 캐시
    private Object itemManager;
    private Method getItemMethod;
    private Method giveItemMethod;
    private Method hasItemMethod;
    private Method removeItemMethod;
    private Method getCustomItemIdMethod;

    // 네임스페이스 키
    private NamespacedKey customItemKey;

    public ItemCoreHook(PetCore plugin) {
        this.plugin = plugin;
        this. enabled = false;
        this.customItemKey = new NamespacedKey(plugin, "custom_item_id");
        initialize();
    }

    /**
     * 초기화
     */
    private void initialize() {
        itemCore = Bukkit.getPluginManager().getPlugin("ItemCore");

        if (itemCore == null || !itemCore.isEnabled()) {
            plugin.getLogger().info("ItemCore를 찾을 수 없습니다. 기본 아이템으로 동작합니다.");
            return;
        }

        try {
            // API 클래스 로드
            Class<?> apiClass = Class. forName("com. multiverse.itemcore.api.ItemCoreAPI");

            // Item Manager
            Method getItemManager = apiClass.getMethod("getItemManager");
            itemManager = getItemManager. invoke(null);

            if (itemManager != null) {
                Class<?> itemManagerClass = itemManager. getClass();
                getItemMethod = itemManagerClass.getMethod("getItem", String.class);
                giveItemMethod = itemManagerClass.getMethod("giveItem", Player.class, String.class, int.class);
                hasItemMethod = itemManagerClass.getMethod("hasItem", Player.class, String. class, int.class);
                removeItemMethod = itemManagerClass.getMethod("removeItem", Player.class, String.class, int.class);
                getCustomItemIdMethod = itemManagerClass.getMethod("getCustomItemId", ItemStack.class);
            }

            enabled = true;
            plugin.getLogger().info("ItemCore 연동 완료!");

        } catch (Exception e) {
            plugin.getLogger().warning("ItemCore 연동 실패: " + e.getMessage());
            enabled = false;
        }
    }

    /**
     * 연동 활성화 여부
     */
    public boolean isEnabled() {
        return enabled;
    }

    // ===== 아이템 조회 =====

    /**
     * 커스텀 아이템 가져오기
     */
    public ItemStack getItem(String itemId) {
        if (!enabled || itemManager == null) {
            return createFallbackItem(itemId);
        }

        try {
            Object result = getItemMethod.invoke(itemManager, itemId);
            return result != null ? (ItemStack) result : createFallbackItem(itemId);
        } catch (Exception e) {
            if (plugin.isDebugMode()) {
                plugin.getLogger().warning("[DEBUG] 아이템 조회 실패:  " + e.getMessage());
            }
            return createFallbackItem(itemId);
        }
    }

    /**
     * 커스텀 아이템 ID 가져오기
     */
    public String getCustomItemId(ItemStack item) {
        if (item == null || ! item.hasItemMeta()) {
            return null;
        }

        // ItemCore 사용
        if (enabled && itemManager != null) {
            try {
                Object result = getCustomItemIdMethod.invoke(itemManager, item);
                return result != null ? (String) result : null;
            } catch (Exception e) {
                // 폴백
            }
        }

        // 자체 확인
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container. get(customItemKey, PersistentDataType.STRING);
    }

    /**
     * 커스텀 아이템인지 확인
     */
    public boolean isCustomItem(ItemStack item) {
        return getCustomItemId(item) != null;
    }

    // ===== 아이템 지급/제거 =====

    /**
     * 아이템 지급
     */
    public boolean giveItem(Player player, String itemId, int amount) {
        if (!enabled || itemManager == null) {
            return giveItemFallback(player, itemId, amount);
        }

        try {
            Object result = giveItemMethod.invoke(itemManager, player, itemId, amount);
            return result != null && (boolean) result;
        } catch (Exception e) {
            if (plugin.isDebugMode()) {
                plugin.getLogger().warning("[DEBUG] 아이템 지급 실패: " + e.getMessage());
            }
            return giveItemFallback(player, itemId, amount);
        }
    }

    /**
     * 아이템 보유 확인
     */
    public boolean hasItem(Player player, String itemId, int amount) {
        if (!enabled || itemManager == null) {
            return hasItemFallback(player, itemId, amount);
        }

        try {
            Object result = hasItemMethod.invoke(itemManager, player, itemId, amount);
            return result != null && (boolean) result;
        } catch (Exception e) {
            if (plugin.isDebugMode()) {
                plugin.getLogger().warning("[DEBUG] 아이템 확인 실패:  " + e.getMessage());
            }
            return hasItemFallback(player, itemId, amount);
        }
    }

    /**
     * 아이템 제거
     */
    public boolean removeItem(Player player, String itemId, int amount) {
        if (!enabled || itemManager == null) {
            return removeItemFallback(player, itemId, amount);
        }

        try {
            Object result = removeItemMethod.invoke(itemManager, player, itemId, amount);
            return result != null && (boolean) result;
        } catch (Exception e) {
            if (plugin.isDebugMode()) {
                plugin.getLogger().warning("[DEBUG] 아이템 제거 실패: " + e. getMessage());
            }
            return removeItemFallback(player, itemId, amount);
        }
    }

    // ===== 폴백 메서드 =====

    /**
     * 폴백 아이템 생성
     */
    private ItemStack createFallbackItem(String itemId) {
        Material material = Material.getMaterial(itemId. toUpperCase());
        if (material == null) {
            material = Material.PAPER;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta. setDisplayName("§f" + itemId);
            meta.getPersistentDataContainer().set(customItemKey, PersistentDataType.STRING, itemId);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 폴백 아이템 지급
     */
    private boolean giveItemFallback(Player player, String itemId, int amount) {
        ItemStack item = createFallbackItem(itemId);
        item.setAmount(amount);

        Map<Integer, ItemStack> overflow = player.getInventory().addItem(item);

        if (! overflow.isEmpty()) {
            for (ItemStack leftover : overflow.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            }
        }

        return true;
    }

    /**
     * 폴백 아이템 확인
     */
    private boolean hasItemFallback(Player player, String itemId, int amount) {
        int count = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;

            String id = getCustomItemId(item);
            if (itemId.equals(id)) {
                count += item.getAmount();
            } else if (item.getType().name().equalsIgnoreCase(itemId)) {
                count += item. getAmount();
            }
        }

        return count >= amount;
    }

    /**
     * 폴백 아이템 제거
     */
    private boolean removeItemFallback(Player player, String itemId, int amount) {
        if (! hasItemFallback(player, itemId, amount)) {
            return false;
        }

        int remaining = amount;

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;

            String id = getCustomItemId(item);
            boolean matches = itemId.equals(id) || item.getType().name().equalsIgnoreCase(itemId);

            if (matches) {
                int itemAmount = item.getAmount();

                if (itemAmount <= remaining) {
                    player.getInventory().setItem(i, null);
                    remaining -= itemAmount;
                } else {
                    item.setAmount(itemAmount - remaining);
                    remaining = 0;
                }

                if (remaining <= 0) {
                    break;
                }
            }
        }

        return remaining <= 0;
    }

    // ===== 펫 관련 아이템 =====

    /**
     * 펫 알 아이템 지급
     */
    public boolean givePetEgg(Player player, String eggType) {
        String itemId = "pet_egg_" + eggType. toLowerCase();
        return giveItem(player, itemId, 1);
    }

    /**
     * 포획구 아이템 지급
     */
    public boolean giveCaptureBall(Player player, String ballType, int amount) {
        String itemId = "capture_ball_" + ballType. toLowerCase();
        return giveItem(player, itemId, amount);
    }

    /**
     * 펫 음식 아이템 지급
     */
    public boolean givePetFood(Player player, String foodType, int amount) {
        String itemId = "pet_food_" + foodType.toLowerCase();
        return giveItem(player, itemId, amount);
    }

    /**
     * 펫 장비 아이템 지급
     */
    public boolean givePetEquipment(Player player, String equipmentId) {
        String itemId = "pet_equip_" + equipmentId.toLowerCase();
        return giveItem(player, itemId, 1);
    }

    /**
     * 리로드
     */
    public void reload() {
        enabled = false;
        itemManager = null;
        initialize();
    }

    /**
     * 종료
     */
    public void shutdown() {
        enabled = false;
        itemManager = null;
    }
}