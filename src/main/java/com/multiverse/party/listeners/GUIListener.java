package com.multiverse.party.listeners;

import com. multiverse.party. PartyCore;
import com. multiverse.party. gui.*;
import com.multiverse.party.gui.GUIManager. GUISession;
import com. multiverse.party. gui.GUIManager.GUIType;
import com.multiverse.party.gui.GUIManager.PartyGUIHolder;
import com. multiverse.party. models.Party;
import com.multiverse. party.models.PartySkill;
import com.multiverse.party.models.enums.LootVoteType;
import com.multiverse.party.models.enums.PartyRole;
import org.bukkit.entity.Player;
import org.bukkit. event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org. bukkit.event. inventory.ClickType;
import org.bukkit.event.inventory. InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit. event.inventory. InventoryDragEvent;
import org.bukkit. inventory. Inventory;

import java.util. UUID;

public class GUIListener implements Listener {

    private final PartyCore plugin;

    public GUIListener(PartyCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Inventory inventory = event. getInventory();
        if (! plugin.getGuiManager().isPluginGUI(inventory)) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        if (slot < 0 || slot >= inventory.getSize()) return;

        PartyGUIHolder holder = (PartyGUIHolder) inventory.getHolder();
        if (holder == null) return;

        GUIType guiType = holder.getGUIType();
        ClickType clickType = event.getClick();

        switch (guiType) {
            case PARTY_MENU:
            case NO_PARTY_MENU: 
                handlePartyMenuClick(player, holder, slot, clickType);
                break;
            case PARTY_FINDER:
                handlePartyFinderClick(player, holder, slot, clickType);
                break;
            case PARTY_SETTINGS:
                handlePartySettingsClick(player, holder, slot, clickType);
                break;
            case PARTY_MEMBERS:
                handlePartyMembersClick(player, holder, slot, clickType);
                break;
            case PARTY_BUFF:
                handlePartyBuffClick(player, holder, slot, clickType);
                break;
            case PARTY_SKILL:
                handlePartySkillClick(player, holder, slot, clickType);
                break;
            case LOOT_ROLL:
                handleLootRollClick(player, holder, slot, clickType);
                break;
            case PARTY_INVITE:
                handlePartyInviteClick(player, holder, slot, clickType);
                break;
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event. getInventory();
        if (plugin.getGuiManager().isPluginGUI(inventory)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
        Inventory inventory = event. getInventory();

        if (plugin. getGuiManager().isPluginGUI(inventory)) {
            plugin.getGuiManager().removeSession(player);
        }
    }

    // ==================== 파티 메뉴 클릭 ====================
    private void handlePartyMenuClick(Player player, PartyGUIHolder holder, int slot, ClickType clickType) {
        PartyMenuGUI gui = (PartyMenuGUI) holder;
        Party party = gui.getParty();

        if (party != null) {
            switch (slot) {
                case PartyMenuGUI. SLOT_MEMBERS:
                    plugin.getGuiManager().openPartyMembersGUI(player, party);
                    break;
                case PartyMenuGUI. SLOT_SETTINGS:
                    PartyRole role = plugin.getPartyRoleManager().getRole(party, player.getUniqueId());
                    if (role == PartyRole.LEADER || role == PartyRole.OFFICER) {
                        plugin.getGuiManager().openPartySettingsGUI(player, party);
                    } else {
                        player.sendMessage(plugin.getMessageUtil().getMessage("party.no-permission"));
                    }
                    break;
                case PartyMenuGUI. SLOT_BUFFS:
                    plugin.getGuiManager().openPartyBuffGUI(player, party);
                    break;
                case PartyMenuGUI.SLOT_SKILLS:
                    plugin.getGuiManager().openPartySkillGUI(player, party);
                    break;
                case PartyMenuGUI. SLOT_STATS:
                    player.closeInventory();
                    player.performCommand("party stats");
                    break;
                case PartyMenuGUI. SLOT_FINDER:
                    plugin.getGuiManager().openPartyFinderGUI(player);
                    break;
                case PartyMenuGUI. SLOT_LEAVE:
                    player.closeInventory();
                    player. performCommand("party leave");
                    break;
                case PartyMenuGUI.SLOT_CLOSE:
                    player.closeInventory();
                    break;
            }
        } else {
            switch (slot) {
                case PartyMenuGUI.SLOT_CREATE:
                    player.closeInventory();
                    player. performCommand("party create");
                    break;
                case PartyMenuGUI. SLOT_FIND:
                    plugin.getGuiManager().openPartyFinderGUI(player);
                    break;
                case PartyMenuGUI. SLOT_INVITES:
                    player.closeInventory();
                    int pendingCount = plugin.getPartyInviteManager().getPendingInviteCount(player);
                    if (pendingCount > 0) {
                        player.sendMessage(plugin.getMessageUtil().getMessage("invite.pending-count",
                                "%count%", String.valueOf(pendingCount)));
                    } else {
                        player.sendMessage(plugin.getMessageUtil().getMessage("invite.no-pending"));
                    }
                    break;
                case 22: 
                    player. closeInventory();
                    break;
            }
        }
    }

    // ==================== 파티 찾기 클릭 ====================
    private void handlePartyFinderClick(Player player, PartyGUIHolder holder, int slot, ClickType clickType) {
        PartyFinderGUI gui = (PartyFinderGUI) holder;

        switch (slot) {
            case PartyFinderGUI. SLOT_PREV_PAGE:
                if (gui.getPage() > 1) {
                    plugin.getGuiManager().openPartyFinderGUI(player, gui.getPage() - 1);
                }
                break;
            case PartyFinderGUI.SLOT_NEXT_PAGE: 
                if (gui.getPage() < gui.getTotalPages()) {
                    plugin.getGuiManager().openPartyFinderGUI(player, gui.getPage() + 1);
                }
                break;
            case PartyFinderGUI. SLOT_BACK:
                Party currentParty = plugin.getPartyManager().getPlayerParty(player);
                if (currentParty != null) {
                    plugin.getGuiManager().openPartyMenuGUI(player, currentParty);
                } else {
                    plugin.getGuiManager().openNoPartyMenuGUI(player);
                }
                break;
            case PartyFinderGUI. SLOT_REFRESH:
                plugin. getGuiManager().openPartyFinderGUI(player, gui.getPage());
                break;
            case PartyFinderGUI.SLOT_QUEUE: 
                player.closeInventory();
                if (plugin.getPartyFinder().isInQueue(player)) {
                    player.performCommand("party cancelqueue");
                } else {
                    player.performCommand("party queue");
                }
                break;
            default:
                if (gui.isPartySlot(slot)) {
                    Party selectedParty = gui.getPartyAtSlot(slot);
                    if (selectedParty != null) {
                        player.closeInventory();
                        player.performCommand("party join " + 
                                (selectedParty.getPartyName() != null ? 
                                        selectedParty.getPartyName().replace(" ", "_") : 
                                        selectedParty.getPartyId().toString()));
                    }
                }
        }
    }

    // ==================== 파티 설정 클릭 ====================
    private void handlePartySettingsClick(Player player, PartyGUIHolder holder, int slot, ClickType clickType) {
        PartySettingsGUI gui = (PartySettingsGUI) holder;

        switch (slot) {
            case PartySettingsGUI.SLOT_PRIVACY:
                gui.cyclePrivacy();
                break;
            case PartySettingsGUI. SLOT_ALLOW_INVITES:
                gui.toggleAllowInvites();
                break;
            case PartySettingsGUI.SLOT_LOOT_DISTRIBUTION:
                gui. cycleLootDistribution();
                break;
            case PartySettingsGUI. SLOT_EXP_DISTRIBUTION:
                gui. cycleExpDistribution();
                break;
            case PartySettingsGUI.SLOT_MAX_MEMBERS:
                gui. changeMaxMembers(clickType. isLeftClick());
                break;
            case PartySettingsGUI. SLOT_PARTY_NAME:
                player.closeInventory();
                plugin.getChatListener().promptPartyNameChange(player, gui.getParty());
                break;
            case PartySettingsGUI. SLOT_DISBAND:
                PartyRole role = plugin.getPartyRoleManager().getRole(gui.getParty(), player.getUniqueId());
                if (role == PartyRole. LEADER) {
                    player.closeInventory();
                    player.performCommand("party disband");
                }
                break;
            case PartySettingsGUI. SLOT_BACK:
                plugin.getGuiManager().openPartyMenuGUI(player, gui.getParty());
                break;
        }
    }

    // ==================== 파티 멤버 클릭 ====================
    private void handlePartyMembersClick(Player player, PartyGUIHolder holder, int slot, ClickType clickType) {
        PartyMembersGUI gui = (PartyMembersGUI) holder;

        switch (slot) {
            case PartyMembersGUI.SLOT_PREV_PAGE:
                if (gui.getPage() > 1) {
                    plugin.getGuiManager().openPartyMembersGUI(player, gui.getParty(), gui.getPage() - 1);
                }
                break;
            case PartyMembersGUI. SLOT_NEXT_PAGE:
                if (gui.getPage() < gui.getTotalPages()) {
                    plugin.getGuiManager().openPartyMembersGUI(player, gui.getParty(), gui.getPage() + 1);
                }
                break;
            case PartyMembersGUI.SLOT_BACK: 
                plugin.getGuiManager().openPartyMenuGUI(player, gui.getParty());
                break;
            case PartyMembersGUI.SLOT_INVITE:
                player.closeInventory();
                player.sendMessage(plugin.getMessageUtil().getMessage("party.invite-usage"));
                break;
            default:
                if (gui.isMemberSlot(slot)) {
                    UUID targetUUID = gui.getMemberAtSlot(slot);
                    if (targetUUID != null) {
                        gui.handleMemberClick(targetUUID, clickType. isLeftClick(), clickType.isShiftClick());
                    }
                }
        }
    }

    // ==================== 파티 버프 클릭 ====================
    private void handlePartyBuffClick(Player player, PartyGUIHolder holder, int slot, ClickType clickType) {
        PartyBuffGUI gui = (PartyBuffGUI) holder;

        if (slot == PartyBuffGUI.SLOT_BACK) {
            plugin.getGuiManager().openPartyMenuGUI(player, gui.getParty());
        }
    }

    // ==================== 파티 스킬 클릭 ====================
    private void handlePartySkillClick(Player player, PartyGUIHolder holder, int slot, ClickType clickType) {
        PartySkillGUI gui = (PartySkillGUI) holder;

        switch (slot) {
            case PartySkillGUI.SLOT_PREV_PAGE:
                if (gui.getPage() > 1) {
                    plugin.getGuiManager().openPartySkillGUI(player, gui.getParty(), gui.getPage() - 1);
                }
                break;
            case PartySkillGUI. SLOT_NEXT_PAGE:
                if (gui.getPage() < gui.getTotalPages()) {
                    plugin.getGuiManager().openPartySkillGUI(player, gui.getParty(), gui.getPage() + 1);
                }
                break;
            case PartySkillGUI.SLOT_BACK:
                plugin.getGuiManager().openPartyMenuGUI(player, gui.getParty());
                break;
            default:
                if (gui.isSkillSlot(slot)) {
                    PartySkill skill = gui.getSkillAtSlot(slot);
                    if (skill != null) {
                        gui.handleSkillClick(skill);
                    }
                }
        }
    }

    // ==================== 아이템 분배 투표 클릭 ====================
    private void handleLootRollClick(Player player, PartyGUIHolder holder, int slot, ClickType clickType) {
        LootRollGUI gui = (LootRollGUI) holder;

        switch (slot) {
            case LootRollGUI. SLOT_NEED: 
                gui.handleVote(LootVoteType.NEED);
                break;
            case LootRollGUI. SLOT_GREED:
                gui.handleVote(LootVoteType.GREED);
                break;
            case LootRollGUI. SLOT_PASS:
                gui. handleVote(LootVoteType. PASS);
                break;
        }
    }

    // ==================== 파티 초대 클릭 ====================
    private void handlePartyInviteClick(Player player, PartyGUIHolder holder, int slot, ClickType clickType) {
        PartyInviteGUI gui = (PartyInviteGUI) holder;

        switch (slot) {
            case PartyInviteGUI. SLOT_ACCEPT:
                gui. handleAccept();
                break;
            case PartyInviteGUI.SLOT_DECLINE:
                gui.handleDecline();
                break;
        }
    }
}