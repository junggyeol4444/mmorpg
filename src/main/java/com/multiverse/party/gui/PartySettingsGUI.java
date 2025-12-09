package com.multiverse.party.gui;

import com.multiverse.party.PartyCore;
import com.multiverse. party.gui.GUIManager.GUIType;
import com.multiverse.party.gui.GUIManager.PartyGUIHolder;
import com.multiverse. party.models.Party;
import com. multiverse.party. models.enums.*;
import com.multiverse.party.utils.ColorUtil;
import com.multiverse.party.utils. ItemUtil;
import org.bukkit. Bukkit;
import org.bukkit.Material;
import org. bukkit.entity. Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util. UUID;

public class PartySettingsGUI implements PartyGUIHolder {

    private final PartyCore plugin;
    private final Player player;
    private final Party party;
    private Inventory inventory;

    public static final int SLOT_PRIVACY = 10;
    public static final int SLOT_ALLOW_INVITES = 12;
    public static final int SLOT_LOOT_DISTRIBUTION = 14;
    public static final int SLOT_EXP_DISTRIBUTION = 16;
    public static final int SLOT_MAX_MEMBERS = 28;
    public static final int SLOT_PARTY_NAME = 30;
    public static final int SLOT_DISBAND = 34;
    public static final int SLOT_BACK = 40;

    public PartySettingsGUI(PartyCore plugin, Player player, Party party) {
        this. plugin = plugin;
        this.player = player;
        this.party = party;
    }

    public void open() {
        String title = ColorUtil.colorize(plugin.getConfigUtil().getGUIConfig()
                .getString("party-settings.title", "&8파티 설정"));
        int rows = plugin.getConfigUtil().getGUIConfig().getInt("party-settings.rows", 5);
        
        inventory = Bukkit.createInventory(this, rows * 9, title);

        fillBackground();
        setPrivacyItem();
        setAllowInvitesItem();
        setLootDistributionItem();
        setExpDistributionItem();
        setMaxMembersItem();
        setPartyNameItem();
        setDisbandItem();
        setBackItem();

        player.openInventory(inventory);
    }

    private void fillBackground() {
        ItemStack filler = ItemUtil. createItem(Material. GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inventory. getSize(); i++) {
            inventory. setItem(i, filler);
        }
    }

    private void setPrivacyItem() {
        List<String> lore = new ArrayList<>();
        PartyPrivacy currentPrivacy = party. getPrivacy();
        
        lore.add("&7현재 설정: &f" + getPrivacyDisplayName(currentPrivacy));
        lore.add("");
        
        for (PartyPrivacy privacy : PartyPrivacy.values()) {
            String prefix = privacy == currentPrivacy ? "&a▶ " : "&7  ";
            lore.add(prefix + getPrivacyDisplayName(privacy));
        }
        
        lore.add("");
        lore.add("&e클릭하여 변경");

        Material material = getPrivacyMaterial(currentPrivacy);
        inventory.setItem(SLOT_PRIVACY, ItemUtil.createItem(material, "&e공개 설정", lore));
    }

    private String getPrivacyDisplayName(PartyPrivacy privacy) {
        switch (privacy) {
            case PUBLIC: return "공개";
            case INVITE_ONLY: return "초대 전용";
            case PRIVATE: return "비공개";
            default: return privacy.name();
        }
    }

    private Material getPrivacyMaterial(PartyPrivacy privacy) {
        switch (privacy) {
            case PUBLIC: return Material.OAK_DOOR;
            case INVITE_ONLY:  return Material.IRON_DOOR;
            case PRIVATE:  return Material.IRON_TRAPDOOR;
            default: return Material. OAK_DOOR;
        }
    }

    private void setAllowInvitesItem() {
        List<String> lore = new ArrayList<>();
        boolean allowInvites = party.isAllowInvites();
        
        lore.add("&7현재 설정: " + (allowInvites ? "&a허용" : "&c비허용"));
        lore. add("");
        lore.add("&7이 설정이 활성화되면");
        lore. add("&7부리더도 멤버를 초대할 수 있습니다.");
        lore.add("");
        lore.add("&e클릭하여 전환");

        Material material = allowInvites ? Material.LIME_DYE : Material. GRAY_DYE;
        inventory.setItem(SLOT_ALLOW_INVITES, ItemUtil.createItem(material, "&b초대 허용", lore));
    }

    private void setLootDistributionItem() {
        List<String> lore = new ArrayList<>();
        LootDistribution currentLoot = party.getLootDistribution();
        
        lore. add("&7현재 설정:  &f" + getLootDisplayName(currentLoot));
        lore.add("");
        
        for (LootDistribution loot : LootDistribution.values()) {
            String prefix = loot == currentLoot ? "&a▶ " :  "&7  ";
            lore.add(prefix + getLootDisplayName(loot));
        }
        
        lore.add("");
        lore.add("&e클릭하여 변경");

        inventory.setItem(SLOT_LOOT_DISTRIBUTION, ItemUtil.createItem(Material.CHEST, "&6아이템 분배", lore));
    }

    private String getLootDisplayName(LootDistribution loot) {
        switch (loot) {
            case FREE_FOR_ALL: return "자유 획득";
            case ROUND_ROBIN: return "순차 분배";
            case NEED_BEFORE_GREED: return "필요 우선";
            case MASTER_LOOT: return "리더 분배";
            default: return loot.name();
        }
    }

    private void setExpDistributionItem() {
        List<String> lore = new ArrayList<>();
        ExpDistribution currentExp = party. getExpDistribution();
        
        lore.add("&7현재 설정: &f" + getExpDisplayName(currentExp));
        lore.add("");
        
        for (ExpDistribution exp : ExpDistribution.values()) {
            String prefix = exp == currentExp ? "&a▶ " : "&7  ";
            lore. add(prefix + getExpDisplayName(exp));
        }
        
        lore.add("");
        lore.add("&e클릭하여 변경");

        inventory. setItem(SLOT_EXP_DISTRIBUTION, ItemUtil.createItem(Material.EXPERIENCE_BOTTLE, "&a경험치 분배", lore));
    }

    private String getExpDisplayName(ExpDistribution exp) {
        switch (exp) {
            case EQUAL:  return "균등 분배";
            case LEVEL_BASED: return "레벨 기반";
            case CONTRIBUTION:  return "기여도 기반";
            default:  return exp.name();
        }
    }

    private void setMaxMembersItem() {
        List<String> lore = new ArrayList<>();
        int currentMax = party.getMaxMembers();
        int currentMembers = party. getMembers().size();
        
        lore.add("&7현재 설정: &f" + currentMax + "명");
        lore. add("&7현재 인원: &f" + currentMembers + "명");
        lore.add("");
        
        int smallMax = plugin.getConfig().getInt("party.size.small", 5);
        int mediumMax = plugin. getConfig().getInt("party.size. medium", 10);
        int largeMax = plugin.getConfig().getInt("party.size.large", 20);
        
        String smallPrefix = currentMax == smallMax ? "&a▶ " :  "&7  ";
        String mediumPrefix = currentMax == mediumMax ? "&a▶ " :  "&7  ";
        String largePrefix = currentMax == largeMax ? "&a▶ " :  "&7  ";
        
        lore.add(smallPrefix + "소규모:  " + smallMax + "명");
        lore.add(mediumPrefix + "중규모: " + mediumMax + "명");
        lore.add(largePrefix + "대규모: " + largeMax + "명");
        
        lore.add("");
        lore. add("&7좌클릭:  증가");
        lore. add("&7우클릭: 감소");

        inventory.setItem(SLOT_MAX_MEMBERS, ItemUtil. createItem(Material. PLAYER_HEAD, "&d최대 인원", lore));
    }

    private void setPartyNameItem() {
        List<String> lore = new ArrayList<>();
        String currentName = party. getPartyName();
        
        if (currentName != null && !currentName.isEmpty()) {
            lore.add("&7현재 이름: &f" + currentName);
        } else {
            lore.add("&7현재 이름: &7없음");
        }
        
        lore.add("");
        lore. add("&7채팅으로 새 이름을 입력합니다.");
        lore.add("");
        lore.add("&e클릭하여 이름 변경");

        inventory.setItem(SLOT_PARTY_NAME, ItemUtil. createItem(Material. NAME_TAG, "&e파티 이름", lore));
    }

    private void setDisbandItem() {
        List<String> lore = new ArrayList<>();
        
        PartyRole role = plugin.getPartyRoleManager().getRole(party, player.getUniqueId());
        
        if (role == PartyRole.LEADER) {
            lore.add("&c파티를 해체합니다.");
            lore.add("&c이 작업은 되돌릴 수 없습니다!");
            lore.add("");
            lore. add("&c클릭하여 파티 해체");
            inventory.setItem(SLOT_DISBAND, ItemUtil.createItem(Material.TNT, "&c&l파티 해체", lore));
        } else {
            lore.add("&7리더만 파티를 해체할 수 있습니다.");
            inventory.setItem(SLOT_DISBAND, ItemUtil.createItem(Material. BARRIER, "&c파티 해체", lore));
        }
    }

    private void setBackItem() {
        List<String> lore = new ArrayList<>();
        lore.add("&7파티 메뉴로 돌아갑니다.");
        inventory.setItem(SLOT_BACK, ItemUtil.createItem(Material.ARROW, "&7뒤로 가기", lore));
    }

    public void cyclePrivacy() {
        PartyPrivacy current = party.getPrivacy();
        PartyPrivacy[] values = PartyPrivacy.values();
        int nextIndex = (current. ordinal() + 1) % values.length;
        party.setPrivacy(values[nextIndex]);
        
        plugin.getDataManager().saveParty(party);
        open();
    }

    public void toggleAllowInvites() {
        party.setAllowInvites(!party. isAllowInvites());
        
        plugin.getDataManager().saveParty(party);
        open();
    }

    public void cycleLootDistribution() {
        LootDistribution current = party.getLootDistribution();
        LootDistribution[] values = LootDistribution. values();
        int nextIndex = (current.ordinal() + 1) % values.length;
        party.setLootDistribution(values[nextIndex]);
        
        plugin.getDataManager().saveParty(party);
        plugin.getPartyChatManager().sendNotification(party,
                plugin.getMessageUtil().getMessage("settings.loot-changed-notify",
                        "%player%", player.getName(),
                        "%loot%", getLootDisplayName(values[nextIndex])));
        open();
    }

    public void cycleExpDistribution() {
        ExpDistribution current = party. getExpDistribution();
        ExpDistribution[] values = ExpDistribution.values();
        int nextIndex = (current.ordinal() + 1) % values.length;
        party.setExpDistribution(values[nextIndex]);
        
        plugin. getDataManager().saveParty(party);
        open();
    }

    public void changeMaxMembers(boolean increase) {
        int currentMax = party. getMaxMembers();
        int currentMembers = party.getMembers().size();
        
        int smallMax = plugin.getConfig().getInt("party.size.small", 5);
        int mediumMax = plugin.getConfig().getInt("party.size.medium", 10);
        int largeMax = plugin.getConfig().getInt("party. size.large", 20);
        
        int newMax;
        if (increase) {
            if (currentMax < mediumMax) {
                newMax = mediumMax;
            } else if (currentMax < largeMax) {
                newMax = largeMax;
            } else {
                newMax = currentMax;
            }
        } else {
            if (currentMax > mediumMax) {
                newMax = mediumMax;
            } else if (currentMax > smallMax) {
                newMax = smallMax;
            } else {
                newMax = currentMax;
            }
        }
        
        if (newMax < currentMembers) {
            player.sendMessage(plugin.getMessageUtil().getMessage("settings.cannot-reduce-below-members"));
            return;
        }
        
        party.setMaxMembers(newMax);
        plugin.getDataManager().saveParty(party);
        open();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public GUIType getGUIType() {
        return GUIType.PARTY_SETTINGS;
    }

    @Override
    public UUID getPartyId() {
        return party. getPartyId();
    }

    public Party getParty() {
        return party;
    }

    public Player getPlayer() {
        return player;
    }
}