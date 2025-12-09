package com.multiverse.party.gui;

import com. multiverse.party. PartyCore;
import com.multiverse.party.gui. GUIManager.GUIType;
import com.multiverse. party.gui.GUIManager.PartyGUIHolder;
import com.multiverse.party.models. Party;
import com.multiverse.party.models.PartyInvite;
import com.multiverse.party.utils.ColorUtil;
import com.multiverse.party.utils. ItemUtil;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org.bukkit. OfflinePlayer;
import org.bukkit. entity.Player;
import org.bukkit.inventory.Inventory;
import org. bukkit.inventory. ItemStack;
import org.bukkit. inventory.meta. SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java. util.UUID;

public class PartyInviteGUI implements PartyGUIHolder {

    private final PartyCore plugin;
    private final Player player;
    private final Party party;
    private final UUID inviterId;
    private Inventory inventory;

    public static final int SLOT_PARTY_INFO = 4;
    public static final int SLOT_INVITER = 13;
    public static final int SLOT_ACCEPT = 29;
    public static final int SLOT_DECLINE = 33;
    public static final int SLOT_TIMER = 31;

    public PartyInviteGUI(PartyCore plugin, Player player, Party party, UUID inviterId) {
        this.plugin = plugin;
        this.player = player;
        this.party = party;
        this. inviterId = inviterId;
    }

    public void open() {
        String title = ColorUtil. colorize(plugin. getConfigUtil().getGUIConfig()
                .getString("party-invite. title", "&8파티 초대"));
        int rows = plugin.getConfigUtil().getGUIConfig().getInt("party-invite.rows", 5);

        inventory = Bukkit.createInventory(this, rows * 9, title);

        fillBackground();
        setPartyInfoItem();
        setInviterItem();
        setAcceptButton();
        setDeclineButton();
        setTimerItem();

        player.openInventory(inventory);
    }

    private void fillBackground() {
        ItemStack filler = ItemUtil.createItem(Material. GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, filler);
        }

        // 상단 장식
        ItemStack topBorder = ItemUtil. createItem(Material. LIME_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) {
            if (i != SLOT_PARTY_INFO) {
                inventory.setItem(i, topBorder);
            }
        }
    }

    private void setPartyInfoItem() {
        List<String> lore = new ArrayList<>();

        String partyName = party. getPartyName() != null ? party. getPartyName() : "이름 없음";
        int memberCount = party.getMembers().size();
        int maxMembers = party.getMaxMembers();
        int level = plugin.getPartyLevelManager().getPartyLevel(party);

        Player leader = Bukkit.getPlayer(party.getLeaderId());
        String leaderName = leader != null ? leader.getName() : 
                plugin.getPartyManager().getOfflinePlayerName(party.getLeaderId());

        lore.add("&7리더: &f" + leaderName);
        lore. add("&7인원: &f" + memberCount + "/" + maxMembers);
        lore. add("&7레벨: &e" + level);
        lore.add("");
        
        // 온라인 멤버 목록
        lore.add("&7온라인 멤버:");
        int onlineCount = 0;
        for (UUID memberUUID : party. getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null) {
                lore.add("&a  • " + member.getName());
                onlineCount++;
                if (onlineCount >= 5) {
                    int remaining = 0;
                    for (UUID uuid : party.getMembers()) {
                        if (Bukkit.getPlayer(uuid) != null) remaining++;
                    }
                    remaining -= 5;
                    if (remaining > 0) {
                        lore.add("&7  ...  그 외 " + remaining + "명");
                    }
                    break;
                }
            }
        }

        inventory.setItem(SLOT_PARTY_INFO, ItemUtil. createItem(Material. GOLDEN_APPLE, "&6" + partyName, lore));
    }

    private void setInviterItem() {
        List<String> lore = new ArrayList<>();

        Player inviter = Bukkit.getPlayer(inviterId);
        String inviterName = inviter != null ? inviter.getName() : 
                plugin.getPartyManager().getOfflinePlayerName(inviterId);

        lore.add("&7" + inviterName + "님이");
        lore. add("&7파티에 초대했습니다!");
        lore. add("");
        lore.add("&7아래 버튼을 클릭하여");
        lore. add("&7수락 또는 거절하세요.");

        ItemStack skull = new ItemStack(Material. PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        
        if (meta != null) {
            OfflinePlayer offlineInviter = Bukkit.getOfflinePlayer(inviterId);
            meta.setOwningPlayer(offlineInviter);
            meta.setDisplayName(ColorUtil.colorize("&e" + inviterName + "의 초대"));
            
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore. add(ColorUtil. colorize(line));
            }
            meta.setLore(coloredLore);
            
            skull.setItemMeta(meta);
        }

        inventory. setItem(SLOT_INVITER, skull);
    }

    private void setAcceptButton() {
        List<String> lore = new ArrayList<>();
        
        lore.add("&7초대를 수락하고");
        lore. add("&7파티에 참가합니다.");
        lore. add("");
        
        int memberCount = party.getMembers().size();
        int maxMembers = party.getMaxMembers();
        
        if (memberCount >= maxMembers) {
            lore.add("&c파티가 가득 찼습니다!");
            lore.add("&7참가할 수 없습니다.");
            inventory.setItem(SLOT_ACCEPT, ItemUtil.createItem(Material.BARRIER, "&c수락 불가", lore));
        } else {
            lore.add("&a클릭하여 수락");
            inventory.setItem(SLOT_ACCEPT, ItemUtil.createItem(Material.LIME_CONCRETE, "&a수락", lore));
        }
    }

    private void setDeclineButton() {
        List<String> lore = new ArrayList<>();
        
        lore.add("&7초대를 거절합니다.");
        lore.add("");
        lore. add("&c클릭하여 거절");

        inventory.setItem(SLOT_DECLINE, ItemUtil. createItem(Material. RED_CONCRETE, "&c거절", lore));
    }

    private void setTimerItem() {
        PartyInvite invite = plugin.getPartyInviteManager().getInviteByParty(player, party);
        
        List<String> lore = new ArrayList<>();
        
        if (invite != null) {
            long remainingTime = invite.getRemainingTime();
            
            lore.add("&7초대 만료까지:");
            lore. add("&e" + remainingTime + "초");
            lore.add("");
            lore.add("&7시간이 지나면");
            lore.add("&7자동으로 거절됩니다.");
            
            Material material = remainingTime > 10 ? Material. CLOCK : Material. REDSTONE;
            inventory.setItem(SLOT_TIMER, ItemUtil.createItem(material, "&e남은 시간", lore));
        } else {
            lore.add("&c초대가 만료되었습니다.");
            inventory.setItem(SLOT_TIMER, ItemUtil.createItem(Material.BARRIER, "&c만료됨", lore));
        }
    }

    public void handleAccept() {
        // 파티 인원 확인
        if (party.getMembers().size() >= party.getMaxMembers()) {
            player.sendMessage(plugin.getMessageUtil().getMessage("party.party-full"));
            player.closeInventory();
            return;
        }

        // 이미 파티에 있는지 확인
        if (plugin.getPartyManager().isInParty(player)) {
            player. sendMessage(plugin. getMessageUtil().getMessage("party.already-in-party"));
            player.closeInventory();
            return;
        }

        // 초대 확인
        PartyInvite invite = plugin.getPartyInviteManager().getInviteByParty(player, party);
        if (invite == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("invite.expired"));
            player. closeInventory();
            return;
        }

        // 파티 가입
        boolean success = plugin.getPartyManager().addMember(party, player);
        
        if (success) {
            // 초대 제거
            plugin.getPartyInviteManager().removeInvite(invite);
            plugin.getPartyInviteManager().removeAllInvites(player);
            
            player.sendMessage(plugin.getMessageUtil().getMessage("invite.accepted",
                    "%party%", party.getPartyName() != null ? party.getPartyName() : "파티"));
            
            // 파티원들에게 알림
            plugin. getPartyChatManager().notifyMemberJoin(party, player);
            
            player.closeInventory();
        } else {
            player.sendMessage(plugin.getMessageUtil().getMessage("invite.accept-failed"));
        }
    }

    public void handleDecline() {
        PartyInvite invite = plugin.getPartyInviteManager().getInviteByParty(player, party);
        
        if (invite != null) {
            // 초대자에게 알림
            Player inviter = Bukkit. getPlayer(invite.getInviterId());
            if (inviter != null) {
                inviter.sendMessage(plugin.getMessageUtil().getMessage("invite. declined",
                        "%player%", player.getName()));
            }
            
            plugin. getPartyInviteManager().removeInvite(invite);
        }
        
        player.sendMessage(plugin.getMessageUtil().getMessage("invite. you-declined"));
        player.closeInventory();
    }

    public void refresh() {
        setTimerItem();
        setAcceptButton();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public GUIType getGUIType() {
        return GUIType. PARTY_INVITE;
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

    public UUID getInviterId() {
        return inviterId;
    }
}