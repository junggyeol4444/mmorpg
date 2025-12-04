package com.multiverse.dungeon.gui;

import com. multiverse.dungeon.DungeonCore;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 파티 관리 GUI
 */
public class PartyManageGUI extends AbstractDungeonGUI {

    private final DungeonCore plugin;
    private int page = 0;
    private static final int ITEMS_PER_PAGE = 21;

    /**
     * 생성자
     */
    public PartyManageGUI(Player player, DungeonCore plugin) {
        super(player, "§6=== 파티 관리 ===");
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        fillBorder();
        
        var party = plugin.getPartyManager().getPlayerParty(player);
        if (party == null) {
            close();
            return;
        }
        
        var members = party.getMembers();
        
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, members. size());
        
        int slot = 10;
        for (int i = startIndex; i < endIndex; i++) {
            if (slot >= 44) break;
            
            var memberId = members.get(i);
            var memberPlayer = org.bukkit.  Bukkit.getPlayer(memberId);
            var item = createMemberItem(party, memberId, memberPlayer);
            
            setItem(slot, item);
            
            if ((slot - 10 + 1) % 7 == 0) {
                slot += 3;
            } else {
                slot++;
            }
        }
        
        int maxPage = (members.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
        addPreviousButton(page);
        addCloseButton();
        addNextButton(page, maxPage - 1);
    }

    /**
     * 파티원 아이템 생성
     */
    private ItemStack createMemberItem(com.multiverse.dungeon.data.model.Party party, java.util.UUID memberId, Player memberPlayer) {
        var item = new ItemStack(org.bukkit. Material.PLAYER_HEAD);
        var meta = item.getItemMeta();
        
        if (meta != null) {
            String memberName = memberPlayer != null ? memberPlayer.getName() : "Unknown";
            
            if (party.isLeader(memberId)) {
                meta.setDisplayName("§b" + memberName + " §6[리더]");
            } else {
                meta.setDisplayName("§b" + memberName);
            }
            
            java.util.List<String> lore = new java.util.ArrayList<>();
            
            if (memberPlayer != null && memberPlayer.isOnline()) {
                lore.add("§a온라인");
            } else {
                lore.add("§c오프라인");
            }
            
            if (party.isLeader(player.getUniqueId())) {
                lore.add("§7");
                if (! party.isLeader(memberId)) {
                    lore. add("§e우클릭: 추방");
                    lore.add("§e쉬프트 클릭: 리더 위임");
                }
            }
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event. setCancelled(true);
        
        int slot = event.getSlot();
        
        if (slot == 48) {
            if (page > 0) {
                page--;
                open();
            }
        } else if (slot == 50) {
            page++;
            open();
        } else if (slot == 49) {
            close();
        }
    }
}