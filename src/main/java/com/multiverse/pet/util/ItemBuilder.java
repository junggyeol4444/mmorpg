package com.multiverse.pet.    util;

import org.bukkit.   Color;
import org. bukkit.   Material;
import org.bukkit.   NamespacedKey;
import org.bukkit.    enchantments.Enchantment;
import org.bukkit.    inventory.ItemFlag;
import org. bukkit.   inventory.ItemStack;
import org.bukkit.   inventory.meta.ItemMeta;
import org.bukkit.   inventory. meta.LeatherArmorMeta;
import org.bukkit.   inventory.meta.SkullMeta;
import org.bukkit.    persistence.PersistentDataContainer;
import org. bukkit.   persistence.PersistentDataType;
import org. bukkit.   plugin.Plugin;

import java.util.   ArrayList;
import java. util.   Arrays;
import java. util.   List;
import java.util.   UUID;

/**
 * 아이템 빌더
 * ItemStack 생성을 편리하게 해주는 빌더 클래스
 */
public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    /**
     * 생성자 (Material)
     */
    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    /**
     * 생성자 (ItemStack 복사)
     */
    public ItemBuilder(ItemStack item) {
        this. item = item. clone();
        this.meta = this.item.getItemMeta();
    }

    /**
     * 생성자 (Material + 수량)
     */
    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
        this.meta = item.getItemMeta();
    }

    // ===== 기본 속성 =====

    /**
     * 이름 설정
     */
    public ItemBuilder name(String name) {
        if (meta != null && name != null) {
            meta.setDisplayName(MessageUtil.colorize(name));
        }
        return this;
    }

    /**
     * 수량 설정
     */
    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    /**
     * 로어 설정 (리스트)
     */
    public ItemBuilder lore(List<String> lore) {
        if (meta != null && lore != null) {
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore. add(MessageUtil. colorize(line));
            }
            meta.setLore(coloredLore);
        }
        return this;
    }

    /**
     * 로어 설정 (가변 인자)
     */
    public ItemBuilder lore(String... lore) {
        return lore(Arrays. asList(lore));
    }

    /**
     * 로어 추가
     */
    public ItemBuilder addLore(String line) {
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(MessageUtil.colorize(line));
            meta.setLore(lore);
        }
        return this;
    }

    /**
     * 로어 추가 (여러 줄)
     */
    public ItemBuilder addLore(String... lines) {
        for (String line : lines) {
            addLore(line);
        }
        return this;
    }

    /**
     * 로어 삽입
     */
    public ItemBuilder insertLore(int index, String line) {
        if (meta != null) {
            List<String> lore = meta. getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(index, MessageUtil.colorize(line));
            meta.setLore(lore);
        }
        return this;
    }

    /**
     * 로어 제거
     */
    public ItemBuilder removeLore(int index) {
        if (meta != null && meta.getLore() != null) {
            List<String> lore = new ArrayList<>(meta. getLore());
            if (index >= 0 && index < lore.size()) {
                lore.remove(index);
                meta. setLore(lore);
            }
        }
        return this;
    }

    /**
     * 로어 초기화
     */
    public ItemBuilder clearLore() {
        if (meta != null) {
            meta.setLore(new ArrayList<>());
        }
        return this;
    }

    // ===== 인챈트 =====

    /**
     * 인챈트 추가
     */
    public ItemBuilder enchant(Enchantment enchantment, int level) {
        if (meta != null) {
            meta.addEnchant(enchantment, level, true);
        }
        return this;
    }

    /**
     * 인챈트 제거
     */
    public ItemBuilder removeEnchant(Enchantment enchantment) {
        if (meta != null) {
            meta.removeEnchant(enchantment);
        }
        return this;
    }

    /**
     * 모든 인챈트 제거
     */
    public ItemBuilder clearEnchants() {
        if (meta != null) {
            for (Enchantment enchant : meta.getEnchants().keySet()) {
                meta.removeEnchant(enchant);
            }
        }
        return this;
    }

    /**
     * 발광 효과 (인챈트 숨김)
     */
    public ItemBuilder glow(boolean glow) {
        if (meta != null) {
            if (glow) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag. HIDE_ENCHANTS);
            } else {
                meta. removeEnchant(Enchantment. DURABILITY);
                meta.removeItemFlags(ItemFlag. HIDE_ENCHANTS);
            }
        }
        return this;
    }

    // ===== 아이템 플래그 =====

    /**
     * 아이템 플래그 추가
     */
    public ItemBuilder addFlags(ItemFlag...  flags) {
        if (meta != null) {
            meta.addItemFlags(flags);
        }
        return this;
    }

    /**
     * 아이템 플래그 제거
     */
    public ItemBuilder removeFlags(ItemFlag... flags) {
        if (meta != null) {
            meta.removeItemFlags(flags);
        }
        return this;
    }

    /**
     * 모든 플래그 추가 (속성 숨김)
     */
    public ItemBuilder hideAllFlags() {
        return addFlags(ItemFlag.values());
    }

    /**
     * 파괴 불가 설정
     */
    public ItemBuilder unbreakable(boolean unbreakable) {
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
            if (unbreakable) {
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            }
        }
        return this;
    }

    // ===== 커스텀 모델 데이터 =====

    /**
     * 커스텀 모델 데이터 설정
     */
    public ItemBuilder customModelData(int data) {
        if (meta != null) {
            meta. setCustomModelData(data);
        }
        return this;
    }

    // ===== Persistent Data =====

    /**
     * Persistent Data 설정 (문자열)
     */
    public ItemBuilder setData(Plugin plugin, String key, String value) {
        if (meta != null) {
            NamespacedKey nsKey = new NamespacedKey(plugin, key);
            meta.getPersistentDataContainer().set(nsKey, PersistentDataType.STRING, value);
        }
        return this;
    }

    /**
     * Persistent Data 설정 (정수)
     */
    public ItemBuilder setData(Plugin plugin, String key, int value) {
        if (meta != null) {
            NamespacedKey nsKey = new NamespacedKey(plugin, key);
            meta.getPersistentDataContainer().set(nsKey, PersistentDataType.INTEGER, value);
        }
        return this;
    }

    /**
     * Persistent Data 설정 (실수)
     */
    public ItemBuilder setData(Plugin plugin, String key, double value) {
        if (meta != null) {
            NamespacedKey nsKey = new NamespacedKey(plugin, key);
            meta.getPersistentDataContainer().set(nsKey, PersistentDataType.DOUBLE, value);
        }
        return this;
    }

    /**
     * Persistent Data 설정 (바이트)
     */
    public ItemBuilder setData(Plugin plugin, String key, byte value) {
        if (meta != null) {
            NamespacedKey nsKey = new NamespacedKey(plugin, key);
            meta.getPersistentDataContainer().set(nsKey, PersistentDataType. BYTE, value);
        }
        return this;
    }

    // ===== 특수 아이템 =====

    /**
     * 플레이어 스컬 설정
     */
    public ItemBuilder skullOwner(String playerName) {
        if (meta instanceof SkullMeta) {
            ((SkullMeta) meta).setOwner(playerName);
        }
        return this;
    }

    /**
     * 가죽 방어구 색상 설정
     */
    public ItemBuilder leatherColor(Color color) {
        if (meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(color);
        }
        return this;
    }

    /**
     * 가죽 방어구 색상 설정 (RGB)
     */
    public ItemBuilder leatherColor(int red, int green, int blue) {
        return leatherColor(Color.fromRGB(red, green, blue));
    }

    // ===== 빌드 =====

    /**
     * ItemStack 빌드
     */
    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * ItemStack 복제 빌드
     */
    public ItemStack buildClone() {
        return build().clone();
    }

    // ===== 유틸리티 =====

    /**
     * 빈 아이템인지 확인
     */
    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    /**
     * 같은 아이템인지 확인 (메타 포함)
     */
    public static boolean isSimilar(ItemStack item1, ItemStack item2) {
        if (isEmpty(item1) && isEmpty(item2)) {
            return true;
        }
        if (isEmpty(item1) || isEmpty(item2)) {
            return false;
        }
        return item1.isSimilar(item2);
    }

    /**
     * Persistent Data 가져오기 (문자열)
     */
    public static String getData(ItemStack item, Plugin plugin, String key) {
        if (isEmpty(item) || !item.hasItemMeta()) {
            return null;
        }
        NamespacedKey nsKey = new NamespacedKey(plugin, key);
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(nsKey, PersistentDataType.STRING);
    }

    /**
     * Persistent Data 가져오기 (정수)
     */
    public static Integer getDataInt(ItemStack item, Plugin plugin, String key) {
        if (isEmpty(item) || !item.hasItemMeta()) {
            return null;
        }
        NamespacedKey nsKey = new NamespacedKey(plugin, key);
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(nsKey, PersistentDataType.INTEGER);
    }

    /**
     * Persistent Data 존재 확인
     */
    public static boolean hasData(ItemStack item, Plugin plugin, String key) {
        if (isEmpty(item) || !item.hasItemMeta()) {
            return false;
        }
        NamespacedKey nsKey = new NamespacedKey(plugin, key);
        return item.getItemMeta().getPersistentDataContainer().has(nsKey, PersistentDataType.STRING);
    }
}