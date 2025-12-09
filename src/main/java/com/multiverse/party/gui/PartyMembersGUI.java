package com.multiverse.party. gui;

import com.multiverse.party.PartyCore;
import com. multiverse.party. gui.GUIManager.GUIType;
import com.multiverse.party.gui.GUIManager.PartyGUIHolder;
import com. multiverse.party. models.Party;
import com. multiverse.party. models.MemberStatistics;
import com.multiverse.party.models.enums.PartyPermission;
import com.multiverse.party.models.enums.PartyRole;
import com. multiverse.party. utils.ColorUtil;
import com.multiverse.party.utils.ItemUtil;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org. bukkit. OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory. Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta. SkullMeta;

import java.util. ArrayList;
import java. util.List;
import java.util. UUID;

public class PartyMembersGUI implements PartyGUIHolder {

    private final PartyCore plugin;
    private final Player player;
    private final Party party;
    private final int page;
    private Inventory inventory;
    private List<UUID> displayedMembers;

    private static final int ITEMS_PER_PAGE = 21;
    private static final int[] MEMBER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    public static final int SLOT_PREV_PAGE = 45;
    public static final int SLOT_INFO = 49;
    public static final int SLOT_NEXT_PAGE = 53;
    public static final int SLOT_BACK = 48;
    public static final int SLOT_INVITE = 50;

    public PartyMembersGUI(PartyCore plugin, Player player, Party party, int page) {
        this.plugin = plugin;
        this.player = player;
        this.party = party;
        this.page = Math.max(1, page);
        this.displayedMembers = new ArrayList<>();
    }

    public void open() {
        String title = ColorUtil.colorize(plugin.getConfigUtil().getGUIConfig()
                .getString("party-members. title", "&8멤버 관리"));
        int rows = plugin.getConfigUtil().getGUIConfig().getInt("party-members.rows", 6);

        inventory = Bukkit.createInventory(this, rows * 9, title);

        fillBackground();
        loadMembers();
        displayMembers();
        setNavigationItems();

        player.openInventory(inventory);
    }

    private void fillBackground() {
        ItemStack filler = ItemUtil.createItem(Material. GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, filler);
        }

        ItemStack border = ItemUtil.createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, border);
            inventory.setItem(inventory.getSize() - 9 + i, border);
        }
        for (int i = 0; i < inventory.getSize(); i += 9) {
            inventory.setItem(i, border);
            inventory.setItem(i + 8, border);
        }
    }

    private void loadMembers() {
        displayedMembers = new ArrayList<>(party.getMembers());

        // 리더를 맨 앞으로, 부리더 그 다음, 일반 멤버 순으로 정렬
        displayedMembers.sort((a, b) -> {
            PartyRole roleA = plugin.getPartyRoleManager().getRole(party, a);
            PartyRole roleB = plugin.getPartyRoleManager().getRole(party, b);
            return Integer.compare(roleA.ordinal(), roleB.ordinal());
        });
    }

    private void displayMembers() {
        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, displayedMembers.size());

        for (int i = 0; i < MEMBER_SLOTS.length; i++) {
            int memberIndex = startIndex + i;

            if (memberIndex < endIndex) {
                UUID memberUUID = displayedMembers. get(memberIndex);
                inventory.setItem(MEMBER_SLOTS[i], createMemberItem(memberUUID));
            } else {
                inventory.setItem(MEMBER_SLOTS[i], ItemUtil.createItem(Material.AIR, " "));
            }
        }
    }

    private ItemStack createMemberItem(UUID memberUUID) {
        List<String> lore = new ArrayList<>();

        Player memberPlayer = Bukkit.getPlayer(memberUUID);
        String memberName;
        boolean isOnline;

        if (memberPlayer != null) {
            memberName = memberPlayer.getName();
            isOnline = true;
        } else {
            memberName = plugin.getPartyManager().getOfflinePlayerName(memberUUID);
            isOnline = false;
        }

        PartyRole role = plugin.getPartyRoleManager().getRole(party, memberUUID);
        String roleDisplay = getRoleDisplay(role);
        String statusDisplay = isOnline ? "&a● 온라인" :  "&c● 오프라인";

        lore.add(statusDisplay);
        lore.add("&7역할: " + roleDisplay);
        lore.add("");

        // 멤버 통계
        MemberStatistics stats = plugin.getPartyStatisticsManager().getMemberStats(party, memberUUID);
        if (stats != null) {
            lore.add("&7데미지: &c" + String.format("%,d", stats.getDamageDealt()));
            lore. add("&7힐량: &a" + String.format("%,d", stats.getHealingDone()));
            lore.add("&7MVP: &e" + stats.getMvpCount() + "회");
            lore.add("");
        }

        // 현재 플레이어의 권한에 따른 액션 표시
        PartyRole playerRole = plugin.getPartyRoleManager().getRole(party, player.getUniqueId());
        boolean isSelf = memberUUID.equals(player.getUniqueId());

        if (! isSelf) {
            if (playerRole == PartyRole.LEADER) {
                if (role == PartyRole. OFFICER) {
                    lore.add("&e좌클릭:  리더 위임");
                    lore.add("&7우클릭: 일반 멤버로 강등");
                    lore.add("&cShift+클릭: 추방");
                } else if (role == PartyRole.MEMBER) {
                    lore.add("&e좌클릭: 부리더로 승격");
                    lore.add("&cShift+클릭: 추방");
                }
            } else if (playerRole == PartyRole.OFFICER) {
                if (role == PartyRole.MEMBER) {
                    lore.add("&cShift+클릭: 추방");
                }
            }
        } else {
            lore.add("&7(본인)");
        }

        ItemStack skull = new ItemStack(Material. PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(memberUUID);
            meta.setOwningPlayer(offlinePlayer);
            meta.setDisplayName(ColorUtil.colorize(roleDisplay + memberName));

            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore. add(ColorUtil. colorize(line));
            }
            meta.setLore(coloredLore);

            skull.setItemMeta(meta);
        }

        return skull;
    }

    private String getRoleDisplay(PartyRole role) {
        switch (role) {
            case LEADER: 
                return "&6[리더] &f";
            case OFFICER:
                return "&e[부리더] &f";
            case MEMBER: 
            default:
                return "&7[멤버] &f";
        }
    }

    private void setNavigationItems() {
        int totalPages = (int) Math.ceil((double) displayedMembers.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        // 이전 페이지
        if (page > 1) {
            List<String> lore = new ArrayList<>();
            lore. add("&7현재 페이지: " + page);
            lore.add("");
            lore. add("&e클릭하여 이전 페이지");
            inventory.setItem(SLOT_PREV_PAGE, ItemUtil. createItem(Material. ARROW, "&a이전 페이지", lore));
        } else {
            inventory.setItem(SLOT_PREV_PAGE, ItemUtil. createItem(Material. GRAY_STAINED_GLASS_PANE, " "));
        }

        // 다음 페이지
        if (page < totalPages) {
            List<String> lore = new ArrayList<>();
            lore. add("&7현재 페이지: " + page);
            lore. add("");
            lore.add("&e클릭하여 다음 페이지");
            inventory.setItem(SLOT_NEXT_PAGE, ItemUtil.createItem(Material.ARROW, "&a다음 페이지", lore));
        } else {
            inventory.setItem(SLOT_NEXT_PAGE, ItemUtil. createItem(Material. GRAY_STAINED_GLASS_PANE, " "));
        }

        // 정보
        List<String> infoLore = new ArrayList<>();
        infoLore.add("&7페이지:  &f" + page + "/" + totalPages);
        infoLore.add("&7멤버 수: &f" + party.getMembers().size() + "/" + party.getMaxMembers());

        int onlineCount = 0;
        for (UUID memberUUID :  party.getMembers()) {
            if (Bukkit.getPlayer(memberUUID) != null) {
                onlineCount++;
            }
        }
        infoLore.add("&7온라인:  &a" + onlineCount + "명");

        inventory.setItem(SLOT_INFO, ItemUtil.createItem(Material.BOOK, "&e멤버 정보", infoLore));

        // 뒤로 가기
        List<String> backLore = new ArrayList<>();
        backLore.add("&7파티 메뉴로 돌아갑니다.");
        inventory.setItem(SLOT_BACK, ItemUtil.createItem(Material. DARK_OAK_DOOR, "&c뒤로 가기", backLore));

        // 초대 버튼
        PartyRole playerRole = plugin.getPartyRoleManager().getRole(party, player.getUniqueId());
        boolean canInvite = plugin.getPartyRoleManager().hasPermission(party, player.getUniqueId(), PartyPermission.INVITE_MEMBERS);

        if (canInvite && party.getMembers().size() < party.getMaxMembers()) {
            List<String> inviteLore = new ArrayList<>();
            inviteLore.add("&7새로운 멤버를 초대합니다.");
            inviteLore.add("");
            inviteLore.add("&7남은 자리: &f" + (party.getMaxMembers() - party.getMembers().size()));
            inviteLore.add("");
            inviteLore.add("&e클릭하여 초대");
            inventory.setItem(SLOT_INVITE, ItemUtil.createItem(Material. WRITABLE_BOOK, "&a멤버 초대", inviteLore));
        } else {
            List<String> inviteLore = new ArrayList<>();
            if (!canInvite) {
                inviteLore.add("&c초대 권한이 없습니다.");
            } else {
                inviteLore.add("&c파티가 가득 찼습니다.");
            }
            inventory.setItem(SLOT_INVITE, ItemUtil.createItem(Material.BARRIER, "&c멤버 초대", inviteLore));
        }
    }

    public UUID getMemberAtSlot(int slot) {
        for (int i = 0; i < MEMBER_SLOTS.length; i++) {
            if (MEMBER_SLOTS[i] == slot) {
                int memberIndex = (page - 1) * ITEMS_PER_PAGE + i;
                if (memberIndex < displayedMembers.size()) {
                    return displayedMembers.get(memberIndex);
                }
            }
        }
        return null;
    }

    public boolean isMemberSlot(int slot) {
        for (int memberSlot : MEMBER_SLOTS) {
            if (memberSlot == slot) return true;
        }
        return false;
    }

    public void handleMemberClick(UUID targetUUID, boolean isLeftClick, boolean isShiftClick) {
        if (targetUUID == null) return;
        if (targetUUID.equals(player.getUniqueId())) return;

        PartyRole playerRole = plugin.getPartyRoleManager().getRole(party, player. getUniqueId());
        PartyRole targetRole = plugin.getPartyRoleManager().getRole(party, targetUUID);
        String targetName = plugin.getPartyManager().getOfflinePlayerName(targetUUID);

        if (isShiftClick) {
            // 추방
            if (plugin.getPartyRoleManager().canKick(party, player.getUniqueId(), targetUUID)) {
                plugin.getPartyManager().kickMember(party, player.getUniqueId(), targetUUID);
                player.sendMessage(plugin.getMessageUtil().getMessage("member.kicked", "%player%", targetName));

                Player targetPlayer = Bukkit.getPlayer(targetUUID);
                if (targetPlayer != null) {
                    targetPlayer. sendMessage(plugin. getMessageUtil().getMessage("member.you-were-kicked"));
                }

                open(); // GUI 새로고침
            } else {
                player.sendMessage(plugin.getMessageUtil().getMessage("party.no-kick-permission"));
            }
        } else if (isLeftClick) {
            if (playerRole == PartyRole.LEADER) {
                if (targetRole == PartyRole.OFFICER) {
                    // 리더 위임
                    plugin.getPartyRoleManager().transferLeadership(party, targetUUID);
                    player.sendMessage(plugin.getMessageUtil().getMessage("party.leadership-transferred", "%player%", targetName));

                    plugin.getPartyChatManager().sendNotification(party,
                            plugin. getMessageUtil().getMessage("party.leader-changed", "%player%", targetName));

                    open();
                } else if (targetRole == PartyRole.MEMBER) {
                    // 부리더 승격
                    plugin.getPartyRoleManager().promoteToOfficer(party, targetUUID);
                    player.sendMessage(plugin.getMessageUtil().getMessage("party.promoted", "%player%", targetName));

                    Player targetPlayer = Bukkit.getPlayer(targetUUID);
                    if (targetPlayer != null) {
                        targetPlayer.sendMessage(plugin.getMessageUtil().getMessage("party.you-were-promoted"));
                    }

                    open();
                }
            }
        } else {
            // 우클릭 - 강등
            if (playerRole == PartyRole.LEADER && targetRole == PartyRole.OFFICER) {
                plugin.getPartyRoleManager().demoteToMember(party, targetUUID);
                player. sendMessage(plugin. getMessageUtil().getMessage("party.demoted", "%player%", targetName));

                Player targetPlayer = Bukkit.getPlayer(targetUUID);
                if (targetPlayer != null) {
                    targetPlayer.sendMessage(plugin.getMessageUtil().getMessage("party.you-were-demoted"));
                }

                open();
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public GUIType getGUIType() {
        return GUIType.PARTY_MEMBERS;
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

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return Math.max(1, (int) Math.ceil((double) displayedMembers.size() / ITEMS_PER_PAGE));
    }
}