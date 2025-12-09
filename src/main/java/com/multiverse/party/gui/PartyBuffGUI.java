package com.multiverse.party.gui;

import com. multiverse.party. PartyCore;
import com.multiverse.party.gui. GUIManager.GUIType;
import com.multiverse.party.gui.GUIManager. PartyGUIHolder;
import com. multiverse.party. models.Party;
import com.multiverse.party.models. PartyBuff;
import com.multiverse.party.models.enums.BuffType;
import com.multiverse.party.utils.ColorUtil;
import com. multiverse.party. utils.ItemUtil;
import org.bukkit.Bukkit;
import org. bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit. inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util. ArrayList;
import java.util.List;
import java.util.Map;
import java.util. UUID;

public class PartyBuffGUI implements PartyGUIHolder {

    private final PartyCore plugin;
    private final Player player;
    private final Party party;
    private Inventory inventory;

    private static final int[] ACTIVE_BUFF_SLOTS = {10, 11, 12, 13, 14, 15, 16};
    private static final int[] PASSIVE_BUFF_SLOTS = {28, 29, 30, 31, 32, 33, 34};

    public static final int SLOT_INFO = 4;
    public static final int SLOT_MEMBER_BUFF = 22;
    public static final int SLOT_LEVEL_BUFF = 24;
    public static final int SLOT_BACK = 40;

    public PartyBuffGUI(PartyCore plugin, Player player, Party party) {
        this.plugin = plugin;
        this.player = player;
        this. party = party;
    }

    public void open() {
        String title = ColorUtil.colorize(plugin.getConfigUtil().getGUIConfig()
                .getString("party-buff.title", "&8파티 버프"));
        int rows = plugin.getConfigUtil().getGUIConfig().getInt("party-buff.rows", 5);

        inventory = Bukkit.createInventory(this, rows * 9, title);

        fillBackground();
        setInfoItem();
        displayActiveBuffs();
        displayMemberCountBuffs();
        displayLevelBuffs();
        setBackItem();

        player.openInventory(inventory);
    }

    private void fillBackground() {
        ItemStack filler = ItemUtil. createItem(Material. GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inventory. getSize(); i++) {
            inventory. setItem(i, filler);
        }

        // 활성 버프 영역 표시
        ItemStack activeLabel = ItemUtil.createItem(Material. LIME_STAINED_GLASS_PANE, "&a활성 버프");
        inventory.setItem(9, activeLabel);
        inventory.setItem(17, activeLabel);

        // 패시브 버프 영역 표시
        ItemStack passiveLabel = ItemUtil.createItem(Material.YELLOW_STAINED_GLASS_PANE, "&e조건부 버프");
        inventory. setItem(27, passiveLabel);
        inventory.setItem(35, passiveLabel);
    }

    private void setInfoItem() {
        List<String> lore = new ArrayList<>();

        List<PartyBuff> activeBuffs = plugin. getPartyBuffManager().getActiveBuffs(party);
        int memberCount = party.getMembers().size();
        int partyLevel = plugin.getPartyLevelManager().getPartyLevel(party);

        lore. add("&7파티 레벨: &e" + partyLevel);
        lore.add("&7현재 인원: &f" + memberCount + "/" + party.getMaxMembers());
        lore.add("");
        lore.add("&7활성 버프: &a" + activeBuffs.size() + "개");
        lore.add("");

        // 총 보너스 표시
        double expBonus = plugin.getPartyBuffManager().getExpBonus(party);
        double damageBonus = plugin.getPartyBuffManager().getDamageBonus(party);
        double defenseBonus = plugin.getPartyBuffManager().getDefenseBonus(party);
        double critBonus = plugin.getPartyBuffManager().getCriticalBonus(party);
        double speedBonus = plugin. getPartyBuffManager().getSpeedBonus(party);
        double healthBonus = plugin. getPartyBuffManager().getHealthBonus(party);

        lore.add("&7총 보너스:");
        if (expBonus > 0) lore.add("&a  경험치: +" + String.format("%. 0f", expBonus * 100) + "%");
        if (damageBonus > 0) lore.add("&c  공격력: +" + String.format("%.0f", damageBonus * 100) + "%");
        if (defenseBonus > 0) lore.add("&b  방어력:  +" + String. format("%.0f", defenseBonus * 100) + "%");
        if (critBonus > 0) lore.add("&e  크리티컬:  +" + String. format("%.0f", critBonus * 100) + "%");
        if (speedBonus > 0) lore.add("&f  이동속도: +" + String.format("%. 0f", speedBonus * 100) + "%");
        if (healthBonus > 0) lore.add("&d  체력: +" + String.format("%. 0f", healthBonus * 100) + "%");

        inventory.setItem(SLOT_INFO, ItemUtil.createItem(Material.BEACON, "&6&l파티 버프 현황", lore));
    }

    private void displayActiveBuffs() {
        List<PartyBuff> activeBuffs = plugin.getPartyBuffManager().getActiveBuffs(party);

        for (int i = 0; i < ACTIVE_BUFF_SLOTS.length; i++) {
            if (i < activeBuffs. size()) {
                PartyBuff buff = activeBuffs. get(i);
                inventory.setItem(ACTIVE_BUFF_SLOTS[i], createBuffItem(buff, true));
            } else {
                inventory. setItem(ACTIVE_BUFF_SLOTS[i], createEmptyBuffSlot());
            }
        }
    }

    private void displayMemberCountBuffs() {
        List<String> lore = new ArrayList<>();
        int memberCount = party. getMembers().size();

        lore.add("&7인원수에 따른 자동 버프");
        lore. add("");

        // 2인 버프
        String prefix2 = memberCount >= 2 ? "&a✔ " : "&c✘ ";
        double bonus2 = plugin.getConfig().getDouble("buffs.member-count.2-members", 0.1);
        lore.add(prefix2 + "2명 이상:  &e경험치 +" + String.format("%.0f", bonus2 * 100) + "%");

        // 3인 버프
        String prefix3 = memberCount >= 3 ? "&a✔ " : "&c✘ ";
        double bonus3 = plugin.getConfig().getDouble("buffs.member-count.3-members", 0.2);
        lore.add(prefix3 + "3명 이상: &e경험치 +" + String. format("%.0f", bonus3 * 100) + "%");

        // 4인 버프
        String prefix4 = memberCount >= 4 ? "&a✔ " : "&c✘ ";
        double bonus4 = plugin.getConfig().getDouble("buffs. member-count.4-members", 0.3);
        lore.add(prefix4 + "4명 이상: &e경험치 +" + String.format("%. 0f", bonus4 * 100) + "%");

        // 5인 버프 (풀파티)
        String prefix5 = memberCount >= 5 ?  "&a✔ " : "&c✘ ";
        double bonus5 = plugin.getConfig().getDouble("buffs.member-count. 5-members", 0.5);
        lore.add(prefix5 + "5명 (풀파티): &e경험치 +" + String.format("%.0f", bonus5 * 100) + "%");

        lore.add("");
        lore.add("&7현재 인원:  &f" + memberCount + "명");

        Material material = memberCount >= 2 ? Material. GLOWSTONE_DUST : Material. GUNPOWDER;
        inventory.setItem(SLOT_MEMBER_BUFF, ItemUtil.createItem(material, "&b인원수 버프", lore));
    }

    private void displayLevelBuffs() {
        List<String> lore = new ArrayList<>();
        int partyLevel = plugin.getPartyLevelManager().getPartyLevel(party);

        lore. add("&7파티 레벨에 따른 자동 버프");
        lore.add("");

        // Lv. 5 버프
        String prefix5 = partyLevel >= 5 ? "&a✔ " : "&c✘ ";
        lore.add(prefix5 + "Lv.5: &c공격력 +5%");

        // Lv.10 버프
        String prefix10 = partyLevel >= 10 ? "&a✔ " :  "&c✘ ";
        lore.add(prefix10 + "Lv.10: &b방어력 +5%");

        // Lv.15 버프
        String prefix15 = partyLevel >= 15 ? "&a✔ " :  "&c✘ ";
        lore.add(prefix15 + "Lv.15: &e크리티컬 +5%");

        // Lv.20 버프
        String prefix20 = partyLevel >= 20 ? "&a✔ " : "&c✘ ";
        lore.add(prefix20 + "Lv. 20: &f이동속도 +10%");

        // Lv.25 버프
        String prefix25 = partyLevel >= 25 ? "&a✔ " :  "&c✘ ";
        lore.add(prefix25 + "Lv.25: &d체력 +10%");

        lore.add("");
        lore.add("&7현재 파티 레벨: &e" + partyLevel);

        Material material = partyLevel >= 5 ? Material. EXPERIENCE_BOTTLE : Material.GLASS_BOTTLE;
        inventory. setItem(SLOT_LEVEL_BUFF, ItemUtil. createItem(material, "&5레벨 버프", lore));
    }

    private ItemStack createBuffItem(PartyBuff buff, boolean isActive) {
        List<String> lore = new ArrayList<>();

        lore.add("&7타입: &f" + getBuffTypeDisplay(buff.getType()));
        lore. add("");
        lore. add("&7효과:");

        for (Map.Entry<String, Double> effect : buff.getEffects().entrySet()) {
            String effectName = getEffectDisplayName(effect.getKey());
            double value = effect.getValue();
            lore.add("&a  " + effectName + ": +" + String.format("%.0f", value * 100) + "%");
        }

        lore.add("");

        if (buff.getDuration() == -1) {
            lore.add("&7지속시간: &a영구");
        } else {
            long remaining = getRemainingTime(buff);
            if (remaining > 0) {
                lore.add("&7남은 시간: &e" + formatTime(remaining));
            } else {
                lore.add("&7남은 시간:  &c만료됨");
            }
        }

        if (buff.getRequiredMembers() > 0) {
            lore.add("&7필요 인원: &f" + buff.getRequiredMembers() + "명");
        }

        if (buff.getRequiredPartyLevel() > 0) {
            lore.add("&7필요 레벨: &f" + buff.getRequiredPartyLevel());
        }

        lore.add("&7범위: &f" + String.format("%.0f", buff.getRange()) + " 블럭");

        Material material = getBuffMaterial(buff.getType());
        return ItemUtil.createItem(material, "&a" + buff.getName(), lore);
    }

    private ItemStack createEmptyBuffSlot() {
        List<String> lore = new ArrayList<>();
        lore.add("&7빈 버프 슬롯");
        lore.add("");
        lore.add("&7버프 아이템을 사용하거나");
        lore. add("&7조건을 만족하면 활성화됩니다.");
        return ItemUtil. createItem(Material. LIGHT_GRAY_STAINED_GLASS_PANE, "&7빈 슬롯", lore);
    }

    private String getBuffTypeDisplay(BuffType type) {
        switch (type) {
            case MEMBER_COUNT:
                return "인원수 버프";
            case PARTY_LEVEL: 
                return "레벨 버프";
            case ITEM: 
                return "아이템 버프";
            case SKILL:
                return "스킬 버프";
            default:
                return type.name();
        }
    }

    private String getEffectDisplayName(String effectKey) {
        switch (effectKey. toLowerCase()) {
            case "exp_bonus":
                return "경험치";
            case "damage_bonus":
                return "공격력";
            case "defense_bonus":
                return "방어력";
            case "critical_bonus":
                return "크리티컬";
            case "speed_bonus": 
                return "이동속도";
            case "health_bonus":
                return "체력";
            default:
                return effectKey;
        }
    }

    private Material getBuffMaterial(BuffType type) {
        switch (type) {
            case MEMBER_COUNT: 
                return Material. GLOWSTONE_DUST;
            case PARTY_LEVEL:
                return Material.EXPERIENCE_BOTTLE;
            case ITEM: 
                return Material. POTION;
            case SKILL:
                return Material.ENCHANTED_BOOK;
            default: 
                return Material. NETHER_STAR;
        }
    }

    private long getRemainingTime(PartyBuff buff) {
        if (buff.getDuration() == -1) return -1;
        long elapsed = (System.currentTimeMillis() - buff.getStartTime()) / 1000;
        return Math.max(0, buff.getDuration() - elapsed);
    }

    private String formatTime(long seconds) {
        if (seconds >= 3600) {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            return hours + "시간 " + minutes + "분";
        } else if (seconds >= 60) {
            long minutes = seconds / 60;
            long secs = seconds % 60;
            return minutes + "분 " + secs + "초";
        } else {
            return seconds + "초";
        }
    }

    private void setBackItem() {
        List<String> lore = new ArrayList<>();
        lore.add("&7파티 메뉴로 돌아갑니다.");
        inventory.setItem(SLOT_BACK, ItemUtil.createItem(Material.ARROW, "&7뒤로 가기", lore));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public GUIType getGUIType() {
        return GUIType.PARTY_BUFF;
    }

    @Override
    public UUID getPartyId() {
        return party.getPartyId();
    }

    public Party getParty() {
        return party;
    }

    public Player getPlayer() {
        return player;
    }
}