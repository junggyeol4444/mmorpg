package com.multiverse.party.gui;

import com.multiverse. party.PartyCore;
import com. multiverse.party. models.Party;
import com.multiverse.party.models. PartyListing;
import com. multiverse.party. utils.ColorUtil;
import org. bukkit. Bukkit;
import org.bukkit. entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory. InventoryHolder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GUIManager {

    private final PartyCore plugin;
    private final Map<UUID, GUISession> activeSessions;
    private final Map<UUID, Long> guiCooldowns;

    public GUIManager(PartyCore plugin) {
        this.plugin = plugin;
        this.activeSessions = new ConcurrentHashMap<>();
        this.guiCooldowns = new ConcurrentHashMap<>();
    }

    /**
     * 파티 메뉴 GUI 열기
     */
    public void openPartyMenuGUI(Player player, Party party) {
        if (! canOpenGUI(player)) return;

        PartyMenuGUI gui = new PartyMenuGUI(plugin, player, party);
        gui.open();

        registerSession(player, new GUISession(GUIType.PARTY_MENU, party.getPartyId()));
    }

    /**
     * 파티가 없는 플레이어용 메뉴 GUI 열기
     */
    public void openNoPartyMenuGUI(Player player) {
        if (!canOpenGUI(player)) return;

        PartyMenuGUI gui = new PartyMenuGUI(plugin, player, null);
        gui.openNoPartyMenu();

        registerSession(player, new GUISession(GUIType.NO_PARTY_MENU, null));
    }

    /**
     * 파티 찾기 GUI 열기
     */
    public void openPartyFinderGUI(Player player) {
        openPartyFinderGUI(player, 1);
    }

    /**
     * 파티 찾기 GUI 열기 (페이지 지정)
     */
    public void openPartyFinderGUI(Player player, int page) {
        if (!canOpenGUI(player)) return;

        PartyFinderGUI gui = new PartyFinderGUI(plugin, player, page);
        gui.open();

        registerSession(player, new GUISession(GUIType.PARTY_FINDER, null, page));
    }

    /**
     * 파티 설정 GUI 열기
     */
    public void openPartySettingsGUI(Player player, Party party) {
        if (!canOpenGUI(player)) return;

        PartySettingsGUI gui = new PartySettingsGUI(plugin, player, party);
        gui.open();

        registerSession(player, new GUISession(GUIType.PARTY_SETTINGS, party. getPartyId()));
    }

    /**
     * 파티 멤버 관리 GUI 열기
     */
    public void openPartyMembersGUI(Player player, Party party) {
        openPartyMembersGUI(player, party, 1);
    }

    /**
     * 파티 멤버 관리 GUI 열기 (페이지 지정)
     */
    public void openPartyMembersGUI(Player player, Party party, int page) {
        if (!canOpenGUI(player)) return;

        PartyMembersGUI gui = new PartyMembersGUI(plugin, player, party, page);
        gui.open();

        registerSession(player, new GUISession(GUIType.PARTY_MEMBERS, party. getPartyId(), page));
    }

    /**
     * 파티 버프 GUI 열기
     */
    public void openPartyBuffGUI(Player player, Party party) {
        if (!canOpenGUI(player)) return;

        PartyBuffGUI gui = new PartyBuffGUI(plugin, player, party);
        gui.open();

        registerSession(player, new GUISession(GUIType. PARTY_BUFF, party.getPartyId()));
    }

    /**
     * 파티 스킬 GUI 열기
     */
    public void openPartySkillGUI(Player player, Party party) {
        openPartySkillGUI(player, party, 1);
    }

    /**
     * 파티 스킬 GUI 열기 (페이지 지정)
     */
    public void openPartySkillGUI(Player player, Party party, int page) {
        if (!canOpenGUI(player)) return;

        PartySkillGUI gui = new PartySkillGUI(plugin, player, party, page);
        gui.open();

        registerSession(player, new GUISession(GUIType. PARTY_SKILL, party.getPartyId(), page));
    }

    /**
     * 아이템 분배 투표 GUI 열기
     */
    public void openLootRollGUI(Player player, Party party, UUID sessionId) {
        if (!canOpenGUI(player)) return;

        LootRollGUI gui = new LootRollGUI(plugin, player, party, sessionId);
        gui.open();

        GUISession session = new GUISession(GUIType.LOOT_ROLL, party.getPartyId());
        session.setExtraData("sessionId", sessionId);
        registerSession(player, session);
    }

    /**
     * 파티 초대 확인 GUI 열기
     */
    public void openPartyInviteGUI(Player player, Party party, UUID inviterId) {
        if (!canOpenGUI(player)) return;

        PartyInviteGUI gui = new PartyInviteGUI(plugin, player, party, inviterId);
        gui.open();

        GUISession session = new GUISession(GUIType.PARTY_INVITE, party. getPartyId());
        session.setExtraData("inviterId", inviterId);
        registerSession(player, session);
    }

    /**
     * GUI 열기 가능 여부 확인
     */
    public boolean canOpenGUI(Player player) {
        if (player == null || ! player.isOnline()) return false;

        // 쿨다운 확인
        long cooldown = plugin.getConfig().getLong("gui.cooldown", 500);
        Long lastOpen = guiCooldowns.get(player.getUniqueId());
        
        if (lastOpen != null && System.currentTimeMillis() - lastOpen < cooldown) {
            return false;
        }

        guiCooldowns. put(player.getUniqueId(), System.currentTimeMillis());
        return true;
    }

    /**
     * GUI 세션 등록
     */
    public void registerSession(Player player, GUISession session) {
        activeSessions.put(player. getUniqueId(), session);
    }

    /**
     * GUI 세션 제거
     */
    public void removeSession(Player player) {
        activeSessions.remove(player.getUniqueId());
    }

    /**
     * GUI 세션 반환
     */
    public GUISession getSession(Player player) {
        return activeSessions.get(player.getUniqueId());
    }

    /**
     * 활성 세션 여부 확인
     */
    public boolean hasActiveSession(Player player) {
        return activeSessions.containsKey(player. getUniqueId());
    }

    /**
     * 특정 GUI 타입의 세션인지 확인
     */
    public boolean isInGUI(Player player, GUIType type) {
        GUISession session = activeSessions.get(player.getUniqueId());
        return session != null && session.getType() == type;
    }

    /**
     * 플레이어의 모든 GUI 닫기
     */
    public void closeGUI(Player player) {
        if (player != null && player.isOnline()) {
            player.closeInventory();
        }
        removeSession(player);
    }

    /**
     * 모든 플레이어의 GUI 닫기
     */
    public void closeAllGUIs() {
        for (UUID playerUUID : new HashSet<>(activeSessions.keySet())) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null && player.isOnline()) {
                player.closeInventory();
            }
        }
        activeSessions.clear();
    }

    /**
     * 특정 파티의 모든 GUI 닫기
     */
    public void closeAllGUIsForParty(UUID partyId) {
        for (Map.Entry<UUID, GUISession> entry : new HashMap<>(activeSessions).entrySet()) {
            if (partyId.equals(entry.getValue().getPartyId())) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null && player. isOnline()) {
                    player. closeInventory();
                }
                activeSessions.remove(entry.getKey());
            }
        }
    }

    /**
     * GUI 새로고침
     */
    public void refreshGUI(Player player) {
        GUISession session = getSession(player);
        if (session == null) return;

        Party party = null;
        if (session.getPartyId() != null) {
            party = plugin.getPartyManager().getParty(session.getPartyId());
        }

        switch (session.getType()) {
            case PARTY_MENU:
                if (party != null) {
                    openPartyMenuGUI(player, party);
                } else {
                    openNoPartyMenuGUI(player);
                }
                break;
            case PARTY_FINDER:
                openPartyFinderGUI(player, session.getPage());
                break;
            case PARTY_SETTINGS:
                if (party != null) {
                    openPartySettingsGUI(player, party);
                }
                break;
            case PARTY_MEMBERS:
                if (party != null) {
                    openPartyMembersGUI(player, party, session.getPage());
                }
                break;
            case PARTY_BUFF:
                if (party != null) {
                    openPartyBuffGUI(player, party);
                }
                break;
            case PARTY_SKILL:
                if (party != null) {
                    openPartySkillGUI(player, party, session.getPage());
                }
                break;
            default:
                break;
        }
    }

    /**
     * 인벤토리가 플러그인 GUI인지 확인
     */
    public boolean isPluginGUI(Inventory inventory) {
        if (inventory == null) return false;
        InventoryHolder holder = inventory.getHolder();
        return holder instanceof PartyGUIHolder;
    }

    /**
     * 인벤토리 타이틀 생성
     */
    public String createTitle(String titleKey) {
        String title = plugin.getConfigUtil().getGUIConfig().getString("titles." + titleKey, titleKey);
        return ColorUtil.colorize(title);
    }

    /**
     * GUI 타입 열거형
     */
    public enum GUIType {
        PARTY_MENU,
        NO_PARTY_MENU,
        PARTY_FINDER,
        PARTY_SETTINGS,
        PARTY_MEMBERS,
        PARTY_BUFF,
        PARTY_SKILL,
        LOOT_ROLL,
        PARTY_INVITE
    }

    /**
     * GUI 세션 클래스
     */
    public static class GUISession {
        private final GUIType type;
        private final UUID partyId;
        private int page;
        private final Map<String, Object> extraData;

        public GUISession(GUIType type, UUID partyId) {
            this(type, partyId, 1);
        }

        public GUISession(GUIType type, UUID partyId, int page) {
            this.type = type;
            this. partyId = partyId;
            this.page = page;
            this. extraData = new HashMap<>();
        }

        public GUIType getType() {
            return type;
        }

        public UUID getPartyId() {
            return partyId;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public void setExtraData(String key, Object value) {
            extraData.put(key, value);
        }

        public Object getExtraData(String key) {
            return extraData. get(key);
        }

        @SuppressWarnings("unchecked")
        public <T> T getExtraData(String key, Class<T> clazz) {
            Object value = extraData.get(key);
            if (clazz.isInstance(value)) {
                return (T) value;
            }
            return null;
        }
    }

    /**
     * 파티 GUI 홀더 인터페이스
     */
    public interface PartyGUIHolder extends InventoryHolder {
        GUIType getGUIType();
        UUID getPartyId();
    }
}