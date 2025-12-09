package com.multiverse.party. gui;

import com.multiverse.party.PartyCore;
import com. multiverse.party. gui.GUIManager.GUIType;
import com.multiverse.party.gui.GUIManager.PartyGUIHolder;
import com.multiverse.party.models.Party;
import com.multiverse. party.models.PartyBuff;
import com.multiverse.party.models.enums.PartyRole;
import com. multiverse.party. utils.ColorUtil;
import com.multiverse.party.utils. ItemUtil;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org. bukkit.entity.Player;
import org.bukkit. inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit. inventory.meta. SkullMeta;

import java.util. ArrayList;
import java. util.List;
import java.util. UUID;

public class PartyMenuGUI implements PartyGUIHolder {

    private final PartyCore plugin;
    private final Player player;
    private final Party party;
    private Inventory inventory;

    // 슬롯 상수
    public static final int SLOT_PARTY_INFO = 4;
    public static final int SLOT_MEMBERS = 10;
    public static final int SLOT_SETTINGS = 12;
    public static final int SLOT_BUFFS = 14;
    public static final int SLOT_SKILLS = 16;
    public static final int SLOT_STATS = 19;
    public static final int SLOT_FINDER = 21;
    public static final int SLOT_CHAT = 23;
    public static final int SLOT_LEAVE = 25;
    public static final int SLOT_CLOSE = 22;

    // 파티 없는 메뉴 슬롯
    public static final int SLOT_CREATE = 11;
    public static final int SLOT_FIND = 13;
    public static final int SLOT_INVITES = 15;

    public PartyMenuGUI(PartyCore plugin, Player player, Party party) {
        this.plugin = plugin;
        this.player = player;
        this. party = party;
    }

    public void open() {
        if (party == null) {
            openNoPartyMenu();
            return;
        }

        String title = ColorUtil.colorize(plugin.getConfigUtil().getGUIConfig()
                .getString("party-menu.title", "&8파티 관리"));
        int rows = plugin.getConfigUtil().getGUIConfig().getInt("party-menu.rows", 3);
        
        inventory = Bukkit.createInventory(this, rows * 9, title);

        fillBackground();
        setPartyInfoItem();
        setMembersItem();
        setSettingsItem();
        setBuffsItem();
        setSkillsItem();
        setStatsItem();
        setFinderItem();
        setChatItem();
        setLeaveItem();
        setCloseItem();

        player.openInventory(inventory);
    }

    public void openNoPartyMenu() {
        String title = ColorUtil. colorize(plugin. getConfigUtil().getGUIConfig()
                .getString("no-party-menu. title", "&8파티 메뉴"));
        int rows = plugin.getConfigUtil().getGUIConfig().getInt("no-party-menu.rows", 3);
        
        inventory = Bukkit.createInventory(this, rows * 9, title);

        fillBackground();
        setCreatePartyItem();
        setFindPartyItem();
        setInvitesItem();
        setCloseItemCenter();

        player.openInventory(inventory);
    }

    private void fillBackground() {
        ItemStack filler = ItemUtil.createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, filler);
        }
    }

    private void setPartyInfoItem() {
        List<String> lore = new ArrayList<>();
        
        String partyName = party.getPartyName() != null ? party. getPartyName() : "이름 없음";
        int memberCount = party.getMembers().size();
        int maxMembers = party. getMaxMembers();
        int level = plugin.getPartyLevelManager().getPartyLevel(party);
        long exp = plugin.getPartyLevelManager().getPartyExp(party);
        long expToNext = plugin.getPartyLevelManager().getExpToNextLevel(party);
        
        Player leader = Bukkit.getPlayer(party.getLeaderId());
        String leaderName = leader != null ? leader.getName() : 
                plugin.getPartyManager().getOfflinePlayerName(party.getLeaderId());

        lore.add("&7리더: &f" + leaderName);
        lore. add("&7멤버: &f" + memberCount + "/" + maxMembers);
        lore. add("&7레벨: &e" + level);
        lore.add("&7경험치: &b" + exp + "/" + expToNext);
        lore.add("");
        lore. add("&7공개 설정: &f" + party.getPrivacy().name());
        lore.add("&7아이템 분배: &f" + party.getLootDistribution().name());

        ItemStack item = ItemUtil.createItem(Material. GOLDEN_APPLE, "&6&l" + partyName, lore);
        inventory.setItem(SLOT_PARTY_INFO, item);
    }

    private void setMembersItem() {
        List<String> lore = new ArrayList<>();
        int memberCount = party. getMembers().size();
        int maxMembers = party. getMaxMembers();
        
        lore.add("&7현재 인원: &f" + memberCount + "/" + maxMembers);
        lore. add("");
        
        int count = 0;
        for (UUID memberUUID : party. getMembers()) {
            if (count >= 5) {
                lore.add("&7...  그 외 " + (memberCount - 5) + "명");
                break;
            }
            
            Player member = Bukkit.getPlayer(memberUUID);
            String memberName = member != null ? member.getName() : 
                    plugin.getPartyManager().getOfflinePlayerName(memberUUID);
            PartyRole role = plugin.getPartyRoleManager().getRole(party, memberUUID);
            String rolePrefix = getRolePrefix(role);
            String status = member != null ? "&a●" : "&c●";
            
            lore.add(status + " " + rolePrefix + memberName);
            count++;
        }
        
        lore.add("");
        lore.add("&e클릭하여 멤버 관리");

        ItemStack item = ItemUtil.createItem(Material.PLAYER_HEAD, "&b멤버 관리", lore);
        inventory.setItem(SLOT_MEMBERS, item);
    }

    private String getRolePrefix(PartyRole role) {
        switch (role) {
            case LEADER: return "&6[리더] &f";
            case OFFICER: return "&e[부리더] &f";
            default: return "&7";
        }
    }

    private void setSettingsItem() {
        List<String> lore = new ArrayList<>();
        
        lore.add("&7공개 설정:  &f" + party.getPrivacy().name());
        lore.add("&7초대 허용: &f" + (party.isAllowInvites() ? "예" : "아니오"));
        lore.add("&7아이템 분배: &f" + party.getLootDistribution().name());
        lore.add("&7경험치 분배: &f" + party.getExpDistribution().name());
        lore. add("");

        PartyRole playerRole = plugin.getPartyRoleManager().getRole(party, player.getUniqueId());
        if (playerRole == PartyRole.LEADER || playerRole == PartyRole.OFFICER) {
            lore.add("&e클릭하여 설정 변경");
        } else {
            lore.add("&c설정을 변경할 권한이 없습니다.");
        }

        ItemStack item = ItemUtil.createItem(Material.COMPARATOR, "&e파티 설정", lore);
        inventory.setItem(SLOT_SETTINGS, item);
    }

    private void setBuffsItem() {
        List<String> lore = new ArrayList<>();
        List<PartyBuff> activeBuffs = plugin.getPartyBuffManager().getActiveBuffs(party);
        
        if (activeBuffs. isEmpty()) {
            lore.add("&7활성화된 버프가 없습니다.");
        } else {
            lore.add("&7활성 버프:");
            for (PartyBuff buff : activeBuffs) {
                lore.add("&a• " + buff.getName());
            }
        }
        
        lore.add("");
        double expBonus = plugin.getPartyBuffManager().getExpBonus(party);
        double damageBonus = plugin. getPartyBuffManager().getDamageBonus(party);
        
        lore.add("&7경험치 보너스: &a+" + String.format("%.0f", expBonus * 100) + "%");
        lore.add("&7공격력 보너스:  &a+" + String.format("%.0f", damageBonus * 100) + "%");
        lore.add("");
        lore.add("&e클릭하여 버프 상세 정보");

        ItemStack item = ItemUtil.createItem(Material.BEACON, "&d파티 버프", lore);
        inventory.setItem(SLOT_BUFFS, item);
    }

    private void setSkillsItem() {
        List<String> lore = new ArrayList<>();
        int skillPoints = plugin.getPartyLevelManager().getAvailableSkillPoints(party);
        List<String> learnedSkills = party.getPartyLevel() != null ? 
                party.getPartyLevel().getLearnedSkills() : new ArrayList<>();
        
        lore.add("&7사용 가능 포인트: &e" + skillPoints);
        lore. add("&7습득한 스킬: &f" + learnedSkills.size() + "개");
        lore.add("");
        
        if (!learnedSkills.isEmpty()) {
            lore. add("&7습득 스킬:");
            int count = 0;
            for (String skill : learnedSkills) {
                if (count >= 3) {
                    lore.add("&7... 그 외 " + (learnedSkills. size() - 3) + "개");
                    break;
                }
                lore.add("&a• " + skill);
                count++;
            }
        }
        
        lore.add("");
        lore.add("&e클릭하여 스킬 관리");

        ItemStack item = ItemUtil. createItem(Material. ENCHANTED_BOOK, "&5파티 스킬", lore);
        inventory.setItem(SLOT_SKILLS, item);
    }

    private void setStatsItem() {
        List<String> lore = new ArrayList<>();
        var stats = plugin.getPartyStatisticsManager().getStatistics(party);
        
        lore.add("&7몬스터 처치: &f" + stats.getMonstersKilled());
        lore.add("&7보스 처치: &f" + stats. getBossesKilled());
        lore.add("&7던전 클리어: &f" + stats.getDungeonsCompleted());
        lore.add("&7퀘스트 완료: &f" + stats.getQuestsCompleted());
        lore.add("");
        lore. add("&7총 데미지: &c" + String.format("%.0f", stats.getTotalDamage()));
        lore. add("&7총 힐량: &a" + String.format("%.0f", stats.getTotalHealing()));
        lore.add("");
        lore. add("&e클릭하여 상세 통계");

        ItemStack item = ItemUtil.createItem(Material. BOOK, "&a파티 통계", lore);
        inventory.setItem(SLOT_STATS, item);
    }

    private void setFinderItem() {
        List<String> lore = new ArrayList<>();
        lore.add("&7공개 파티를 검색하거나");
        lore. add("&7자동 매칭에 참가합니다.");
        lore. add("");
        lore. add("&e클릭하여 파티 찾기");

        ItemStack item = ItemUtil.createItem(Material.COMPASS, "&b파티 찾기", lore);
        inventory.setItem(SLOT_FINDER, item);
    }

    private void setChatItem() {
        List<String> lore = new ArrayList<>();
        lore.add("&7파티 채팅을 사용합니다.");
        lore.add("");
        lore. add("&e/p <메시지>");
        lore. add("&7또는");
        lore. add("&e/party chat <메시지>");

        ItemStack item = ItemUtil.createItem(Material.WRITABLE_BOOK, "&a파티 채팅", lore);
        inventory. setItem(SLOT_CHAT, item);
    }

    private void setLeaveItem() {
        List<String> lore = new ArrayList<>();
        
        PartyRole role = plugin.getPartyRoleManager().getRole(party, player.getUniqueId());
        
        if (role == PartyRole. LEADER) {
            if (party.getMembers().size() > 1) {
                lore.add("&c리더가 떠나면 권한이");
                lore.add("&c다른 멤버에게 위임됩니다.");
            } else {
                lore.add("&c파티가 해체됩니다.");
            }
        } else {
            lore.add("&7파티를 떠납니다.");
        }
        
        lore.add("");
        lore. add("&c클릭하여 파티 떠나기");

        ItemStack item = ItemUtil.createItem(Material.BARRIER, "&c파티 떠나기", lore);
        inventory. setItem(SLOT_LEAVE, item);
    }

    private void setCloseItem() {
        ItemStack item = ItemUtil.createItem(Material.ARROW, "&7닫기", null);
        inventory.setItem(SLOT_CLOSE, item);
    }

    private void setCloseItemCenter() {
        ItemStack item = ItemUtil.createItem(Material. ARROW, "&7닫기", null);
        inventory.setItem(22, item);
    }

    private void setCreatePartyItem() {
        List<String> lore = new ArrayList<>();
        lore.add("&7새로운 파티를 생성합니다.");
        lore.add("");
        
        int cost = plugin.getConfig().getInt("party. creation.cost", 0);
        if (cost > 0) {
            lore.add("&7생성 비용:  &e" + cost);
        }
        
        lore. add("");
        lore. add("&a클릭하여 파티 생성");

        ItemStack item = ItemUtil.createItem(Material. NETHER_STAR, "&a파티 생성", lore);
        inventory.setItem(SLOT_CREATE, item);
    }

    private void setFindPartyItem() {
        List<String> lore = new ArrayList<>();
        
        int publicPartyCount = plugin. getPartyFinder().getPublicParties().size();
        
        lore.add("&7공개 파티 수: &f" + publicPartyCount);
        lore. add("");
        lore.add("&7공개 파티에 참가하거나");
        lore.add("&7자동 매칭에 참가합니다.");
        lore.add("");
        lore.add("&e클릭하여 파티 찾기");

        ItemStack item = ItemUtil. createItem(Material. COMPASS, "&b파티 찾기", lore);
        inventory.setItem(SLOT_FIND, item);
    }

    private void setInvitesItem() {
        List<String> lore = new ArrayList<>();
        
        int pendingInvites = plugin.getPartyInviteManager().getPendingInviteCount(player);
        
        if (pendingInvites > 0) {
            lore.add("&e대기 중인 초대:  " + pendingInvites + "개");
        } else {
            lore.add("&7대기 중인 초대가 없습니다.");
        }
        
        lore.add("");
        lore.add("&e클릭하여 초대 확인");

        Material material = pendingInvites > 0 ? Material.PAPER : Material.MAP;
        ItemStack item = ItemUtil. createItem(material, "&e초대 확인", lore);
        inventory.setItem(SLOT_INVITES, item);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public GUIType getGUIType() {
        return party != null ? GUIType.PARTY_MENU : GUIType.NO_PARTY_MENU;
    }

    @Override
    public UUID getPartyId() {
        return party != null ? party. getPartyId() : null;
    }

    public Party getParty() {
        return party;
    }

    public Player getPlayer() {
        return player;
    }
}