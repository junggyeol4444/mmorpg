package com.multiverse.party.gui;

import com.multiverse.party.PartyCore;
import com.multiverse. party.gui.GUIManager.GUIType;
import com.multiverse.party.gui.GUIManager.PartyGUIHolder;
import com.multiverse. party.models.Party;
import com. multiverse.party. models.PartyListing;
import com. multiverse.party. utils.ColorUtil;
import com.multiverse.party.utils. ItemUtil;
import org.bukkit. Bukkit;
import org.bukkit.Material;
import org.bukkit. entity.Player;
import org.bukkit.inventory.Inventory;
import org. bukkit.inventory. ItemStack;

import java.util. ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyFinderGUI implements PartyGUIHolder {

    private final PartyCore plugin;
    private final Player player;
    private final int page;
    private Inventory inventory;
    private List<Party> displayedParties;

    private static final int ITEMS_PER_PAGE = 28;
    private static final int[] PARTY_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    public static final int SLOT_PREV_PAGE = 45;
    public static final int SLOT_INFO = 49;
    public static final int SLOT_NEXT_PAGE = 53;
    public static final int SLOT_BACK = 48;
    public static final int SLOT_REFRESH = 50;
    public static final int SLOT_QUEUE = 51;

    public PartyFinderGUI(PartyCore plugin, Player player, int page) {
        this.plugin = plugin;
        this. player = player;
        this.page = Math.max(1, page);
        this.displayedParties = new ArrayList<>();
    }

    public void open() {
        String title = ColorUtil. colorize(plugin. getConfigUtil().getGUIConfig()
                .getString("party-finder.title", "&8파티 찾기"));
        int rows = plugin.getConfigUtil().getGUIConfig().getInt("party-finder.rows", 6);
        
        inventory = Bukkit.createInventory(this, rows * 9, title);

        fillBackground();
        loadParties();
        displayParties();
        setNavigationItems();

        player.openInventory(inventory);
    }

    private void fillBackground() {
        ItemStack filler = ItemUtil. createItem(Material. GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inventory. getSize(); i++) {
            inventory. setItem(i, filler);
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

    private void loadParties() {
        displayedParties = new ArrayList<>();
        
        List<Party> publicParties = plugin. getPartyFinder().getPublicParties();
        
        for (Party party : publicParties) {
            if (party.getMembers().size() < party.getMaxMembers()) {
                displayedParties.add(party);
            }
        }
        
        displayedParties.sort((p1, p2) -> {
            int level1 = p1.getPartyLevel() != null ? p1.getPartyLevel().getLevel() : 1;
            int level2 = p2.getPartyLevel() != null ? p2.getPartyLevel().getLevel() : 1;
            return Integer.compare(level2, level1);
        });
    }

    private void displayParties() {
        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, displayedParties.size());

        for (int i = 0; i < PARTY_SLOTS.length; i++) {
            int partyIndex = startIndex + i;
            
            if (partyIndex < endIndex) {
                Party party = displayedParties.get(partyIndex);
                inventory.setItem(PARTY_SLOTS[i], createPartyItem(party));
            } else {
                inventory.setItem(PARTY_SLOTS[i], ItemUtil.createItem(Material.AIR, " "));
            }
        }
    }

    private ItemStack createPartyItem(Party party) {
        List<String> lore = new ArrayList<>();
        
        String partyName = party.getPartyName() != null ? party.getPartyName() : "이름 없음";
        int memberCount = party.getMembers().size();
        int maxMembers = party.getMaxMembers();
        int level = party.getPartyLevel() != null ? party.getPartyLevel().getLevel() : 1;
        
        Player leader = Bukkit.getPlayer(party.getLeaderId());
        String leaderName = leader != null ? leader.getName() : 
                plugin.getPartyManager().getOfflinePlayerName(party. getLeaderId());

        lore. add("&7리더: &f" + leaderName);
        lore. add("&7인원: &f" + memberCount + "/" + maxMembers);
        lore. add("&7레벨: &e" + level);
        lore.add("");
        
        PartyListing listing = plugin. getDataManager().loadListing(party.getPartyId());
        if (listing != null && listing.getDescription() != null) {
            lore. add("&7설명:");
            String description = listing.getDescription();
            if (description.length() > 30) {
                description = description.substring(0, 30) + "...";
            }
            lore.add("&f" + description);
            lore.add("");
        }
        
        int onlineCount = 0;
        for (UUID memberUUID : party.getMembers()) {
            if (Bukkit.getPlayer(memberUUID) != null) {
                onlineCount++;
            }
        }
        lore.add("&7온라인: &a" + onlineCount + "명");
        lore.add("");
        lore.add("&a클릭하여 참가 요청");

        Material material = getMaterialByLevel(level);
        return ItemUtil.createItem(material, "&6" + partyName, lore);
    }

    private Material getMaterialByLevel(int level) {
        if (level >= 40) return Material.NETHERITE_HELMET;
        if (level >= 30) return Material.DIAMOND_HELMET;
        if (level >= 20) return Material.GOLDEN_HELMET;
        if (level >= 10) return Material.IRON_HELMET;
        return Material. LEATHER_HELMET;
    }

    private void setNavigationItems() {
        int totalPages = (int) Math.ceil((double) displayedParties.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        if (page > 1) {
            List<String> lore = new ArrayList<>();
            lore. add("&7현재 페이지: " + page);
            lore.add("");
            lore. add("&e클릭하여 이전 페이지");
            inventory.setItem(SLOT_PREV_PAGE, ItemUtil. createItem(Material. ARROW, "&a이전 페이지", lore));
        } else {
            inventory.setItem(SLOT_PREV_PAGE, ItemUtil. createItem(Material. GRAY_STAINED_GLASS_PANE, " "));
        }

        if (page < totalPages) {
            List<String> lore = new ArrayList<>();
            lore. add("&7현재 페이지: " + page);
            lore. add("");
            lore.add("&e클릭하여 다음 페이지");
            inventory.setItem(SLOT_NEXT_PAGE, ItemUtil. createItem(Material. ARROW, "&a다음 페이지", lore));
        } else {
            inventory.setItem(SLOT_NEXT_PAGE, ItemUtil.createItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }

        List<String> infoLore = new ArrayList<>();
        infoLore.add("&7페이지:  &f" + page + "/" + totalPages);
        infoLore.add("&7검색된 파티: &f" + displayedParties.size() + "개");
        inventory.setItem(SLOT_INFO, ItemUtil.createItem(Material.BOOK, "&e파티 목록 정보", infoLore));

        List<String> backLore = new ArrayList<>();
        backLore.add("&7메인 메뉴로 돌아갑니다.");
        inventory.setItem(SLOT_BACK, ItemUtil. createItem(Material. DARK_OAK_DOOR, "&c뒤로 가기", backLore));

        List<String> refreshLore = new ArrayList<>();
        refreshLore.add("&7파티 목록을 새로고침합니다.");
        inventory.setItem(SLOT_REFRESH, ItemUtil. createItem(Material. SUNFLOWER, "&a새로고침", refreshLore));

        List<String> queueLore = new ArrayList<>();
        boolean isInQueue = plugin.getPartyFinder().isInQueue(player);
        if (isInQueue) {
            queueLore. add("&c현재 매칭 대기 중입니다.");
            queueLore.add("");
            queueLore.add("&e클릭하여 대기 취소");
            inventory.setItem(SLOT_QUEUE, ItemUtil.createItem(Material.REDSTONE, "&c매칭 대기 취소", queueLore));
        } else {
            queueLore.add("&7자동으로 파티를 찾아줍니다.");
            queueLore.add("");
            queueLore. add("&e클릭하여 자동 매칭");
            inventory. setItem(SLOT_QUEUE, ItemUtil.createItem(Material. ENDER_EYE, "&b자동 매칭", queueLore));
        }
    }

    public Party getPartyAtSlot(int slot) {
        for (int i = 0; i < PARTY_SLOTS.length; i++) {
            if (PARTY_SLOTS[i] == slot) {
                int partyIndex = (page - 1) * ITEMS_PER_PAGE + i;
                if (partyIndex < displayedParties.size()) {
                    return displayedParties.get(partyIndex);
                }
            }
        }
        return null;
    }

    public boolean isPartySlot(int slot) {
        for (int partySlot :  PARTY_SLOTS) {
            if (partySlot == slot) return true;
        }
        return false;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public GUIType getGUIType() {
        return GUIType.PARTY_FINDER;
    }

    @Override
    public UUID getPartyId() {
        return null;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return Math.max(1, (int) Math.ceil((double) displayedParties.size() / ITEMS_PER_PAGE));
    }

    public Player getPlayer() {
        return player;
    }
}