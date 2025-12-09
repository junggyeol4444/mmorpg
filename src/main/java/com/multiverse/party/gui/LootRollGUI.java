package com.multiverse.party. gui;

import com.multiverse.party.PartyCore;
import com. multiverse.party. gui.GUIManager. GUIType;
import com.multiverse.party.gui. GUIManager.PartyGUIHolder;
import com. multiverse.party. models.Party;
import com. multiverse.party. models.LootSession;
import com. multiverse.party. models.enums.LootVoteType;
import com.multiverse.party.utils.ColorUtil;
import com.multiverse.party.utils.ItemUtil;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org. bukkit.entity.Player;
import org.bukkit. inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org. bukkit.inventory. meta.ItemMeta;

import java.util.ArrayList;
import java. util.List;
import java.util. Map;
import java.util. UUID;

public class LootRollGUI implements PartyGUIHolder {

    private final PartyCore plugin;
    private final Player player;
    private final Party party;
    private final UUID sessionId;
    private Inventory inventory;

    public static final int SLOT_ITEM = 13;
    public static final int SLOT_NEED = 29;
    public static final int SLOT_GREED = 31;
    public static final int SLOT_PASS = 33;
    public static final int SLOT_TIMER = 4;
    public static final int SLOT_VOTES = 22;

    public LootRollGUI(PartyCore plugin, Player player, Party party, UUID sessionId) {
        this.plugin = plugin;
        this.player = player;
        this. party = party;
        this.sessionId = sessionId;
    }

    public void open() {
        LootSession session = plugin.getLootManager().getSession(sessionId);
        if (session == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("loot. session-expired"));
            return;
        }

        String title = ColorUtil.colorize(plugin.getConfigUtil().getGUIConfig()
                .getString("loot-roll.title", "&8아이템 분배"));
        int rows = plugin.getConfigUtil().getGUIConfig().getInt("loot-roll.rows", 5);

        inventory = Bukkit.createInventory(this, rows * 9, title);

        fillBackground();
        setItemDisplay(session);
        setTimerItem(session);
        setVoteButtons(session);
        setVoteStatusItem(session);

        player.openInventory(inventory);
    }

    private void fillBackground() {
        ItemStack filler = ItemUtil.createItem(Material. GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, filler);
        }

        // 상단 장식
        ItemStack topBorder = ItemUtil.createItem(Material. YELLOW_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, topBorder);
        }

        // 하단 장식
        ItemStack bottomBorder = ItemUtil.createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 36; i < 45; i++) {
            inventory.setItem(i, bottomBorder);
        }
    }

    private void setItemDisplay(LootSession session) {
        ItemStack lootItem = session.getItem();
        if (lootItem == null) {
            inventory.setItem(SLOT_ITEM, ItemUtil.createItem(Material.BARRIER, "&c아이템 없음", null));
            return;
        }

        // 아이템 복사본 생성 (추가 정보 표시)
        ItemStack displayItem = lootItem.clone();
        ItemMeta meta = displayItem.getItemMeta();
        
        if (meta != null) {
            List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            lore.add("");
            lore.add(ColorUtil.colorize("&7━━━━━━━━━━━━━━━━━━"));
            lore.add(ColorUtil.colorize("&e분배 대상 아이템"));
            lore.add(ColorUtil.colorize("&7아래 버튼을 클릭하세요"));
            meta.setLore(lore);
            displayItem.setItemMeta(meta);
        }

        inventory.setItem(SLOT_ITEM, displayItem);
    }

    private void setTimerItem(LootSession session) {
        List<String> lore = new ArrayList<>();
        
        long remainingTime = session. getRemainingTime();
        int totalTime = session.getDuration();
        
        // 프로그레스 바 생성
        int progressBars = 20;
        int filledBars = (int) ((double) remainingTime / totalTime * progressBars);
        StringBuilder progressBar = new StringBuilder();
        
        for (int i = 0; i < progressBars; i++) {
            if (i < filledBars) {
                progressBar.append("&a|");
            } else {
                progressBar. append("&7|");
            }
        }
        
        lore.add("&7" + progressBar);
        lore. add("");
        lore. add("&7남은 시간:  &e" + remainingTime + "초");
        lore.add("");
        lore.add("&7시간이 지나면 자동으로");
        lore. add("&7'패스'로 처리됩니다.");

        Material material = remainingTime > 5 ? Material. CLOCK : Material.REDSTONE;
        inventory.setItem(SLOT_TIMER, ItemUtil.createItem(material, "&e남은 시간", lore));
    }

    private void setVoteButtons(LootSession session) {
        LootVoteType currentVote = session. getVote(player.getUniqueId());
        boolean hasVoted = currentVote != null;

        // NEED 버튼
        List<String> needLore = new ArrayList<>();
        needLore.add("&7이 아이템이 필요합니다!");
        needLore.add("");
        needLore.add("&7필요(NEED)를 선택하면");
        needLore.add("&7우선적으로 분배받습니다.");
        needLore. add("");
        
        if (hasVoted && currentVote == LootVoteType. NEED) {
            needLore. add("&a✔ 선택됨");
        } else if (hasVoted) {
            needLore.add("&7이미 다른 선택을 했습니다.");
        } else {
            needLore.add("&e클릭하여 선택");
        }

        Material needMaterial = (hasVoted && currentVote == LootVoteType.NEED) ? 
                Material.LIME_CONCRETE : Material.GREEN_CONCRETE;
        inventory.setItem(SLOT_NEED, ItemUtil.createItem(needMaterial, "&a필요 (NEED)", needLore));

        // GREED 버튼
        List<String> greedLore = new ArrayList<>();
        greedLore. add("&7있으면 좋겠습니다.");
        greedLore.add("");
        greedLore.add("&7탐욕(GREED)을 선택하면");
        greedLore.add("&7NEED가 없을 때 분배받습니다.");
        greedLore.add("");
        
        if (hasVoted && currentVote == LootVoteType. GREED) {
            greedLore.add("&e✔ 선택됨");
        } else if (hasVoted) {
            greedLore.add("&7이미 다른 선택을 했습니다.");
        } else {
            greedLore.add("&e클릭하여 선택");
        }

        Material greedMaterial = (hasVoted && currentVote == LootVoteType.GREED) ? 
                Material.YELLOW_CONCRETE : Material.GOLD_BLOCK;
        inventory. setItem(SLOT_GREED, ItemUtil.createItem(greedMaterial, "&e탐욕 (GREED)", greedLore));

        // PASS 버튼
        List<String> passLore = new ArrayList<>();
        passLore.add("&7이 아이템이 필요없습니다.");
        passLore.add("");
        passLore.add("&7패스(PASS)를 선택하면");
        passLore.add("&7분배 대상에서 제외됩니다.");
        passLore.add("");
        
        if (hasVoted && currentVote == LootVoteType.PASS) {
            passLore.add("&c✔ 선택됨");
        } else if (hasVoted) {
            passLore.add("&7이미 다른 선택을 했습니다.");
        } else {
            passLore.add("&e클릭하여 선택");
        }

        Material passMaterial = (hasVoted && currentVote == LootVoteType.PASS) ? 
                Material.RED_CONCRETE : Material.REDSTONE_BLOCK;
        inventory.setItem(SLOT_PASS, ItemUtil.createItem(passMaterial, "&c패스 (PASS)", passLore));
    }

    private void setVoteStatusItem(LootSession session) {
        List<String> lore = new ArrayList<>();
        
        Map<UUID, LootVoteType> votes = session.getVotes();
        List<UUID> eligiblePlayers = session.getEligiblePlayers();
        
        int needCount = 0;
        int greedCount = 0;
        int passCount = 0;
        int pendingCount = 0;

        for (UUID playerUUID : eligiblePlayers) {
            LootVoteType vote = votes. get(playerUUID);
            if (vote == null) {
                pendingCount++;
            } else {
                switch (vote) {
                    case NEED:  needCount++; break;
                    case GREED: greedCount++; break;
                    case PASS: passCount++; break;
                }
            }
        }

        lore.add("&7투표 현황:");
        lore.add("");
        lore. add("&a필요(NEED): " + needCount + "명");
        lore.add("&e탐욕(GREED): " + greedCount + "명");
        lore.add("&c패스(PASS): " + passCount + "명");
        lore.add("&7대기중:  " + pendingCount + "명");
        lore.add("");
        lore.add("&7총 " + eligiblePlayers.size() + "명 중 " + votes.size() + "명 투표");

        inventory.setItem(SLOT_VOTES, ItemUtil.createItem(Material.PAPER, "&b투표 현황", lore));
    }

    public void handleVote(LootVoteType voteType) {
        LootSession session = plugin.getLootManager().getSession(sessionId);
        if (session == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("loot.session-expired"));
            player.closeInventory();
            return;
        }

        // 이미 투표했는지 확인
        if (session.hasVoted(player. getUniqueId())) {
            player.sendMessage(plugin.getMessageUtil().getMessage("loot.already-voted"));
            return;
        }

        // 투표 처리
        boolean success = plugin.getLootManager().vote(sessionId, player.getUniqueId(), voteType);
        
        if (success) {
            String voteTypeName;
            switch (voteType) {
                case NEED:  voteTypeName = "필요(NEED)"; break;
                case GREED: voteTypeName = "탐욕(GREED)"; break;
                case PASS: voteTypeName = "패스(PASS)"; break;
                default: voteTypeName = voteType.name();
            }
            
            player.sendMessage(plugin.getMessageUtil().getMessage("loot.vote-cast",
                    "%vote%", voteTypeName));
            
            // GUI 새로고침
            open();
            
            // 모든 투표가 완료되었으면 결과 처리
            if (session.isAllVoted()) {
                plugin.getLootManager().finishSession(sessionId);
            }
        } else {
            player. sendMessage(plugin. getMessageUtil().getMessage("loot.vote-failed"));
        }
    }

    public void refresh() {
        LootSession session = plugin.getLootManager().getSession(sessionId);
        if (session == null || session.isExpired()) {
            player.closeInventory();
            return;
        }
        
        setTimerItem(session);
        setVoteButtons(session);
        setVoteStatusItem(session);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public GUIType getGUIType() {
        return GUIType.LOOT_ROLL;
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

    public UUID getSessionId() {
        return sessionId;
    }
}