package com.multiverse.quest.listeners;

import com.multiverse. quest.models.*;
import com.multiverse.quest.managers.QuestDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event. EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event. player.*;
import org.bukkit.  Bukkit;
import java.util.*;

/**
 * 플레이어 리스너
 * 플레이어 접속/퇴장 및 기타 플레이어 이벤트를 처리합니다.
 */
public class PlayerListener implements Listener {
    private final QuestDataManager questDataManager;
    private final Map<UUID, Long> playerLoginTime;
    private final Map<UUID, Long> playerLastActivityTime;

    /**
     * 생성자
     * @param questDataManager 퀨스트 데이터 관리자
     */
    public PlayerListener(QuestDataManager questDataManager) {
        this.questDataManager = questDataManager;
        this.playerLoginTime = new HashMap<>();
        this. playerLastActivityTime = new HashMap<>();
    }

    // ============ Login/Logout Events ============

    /**
     * 플레이어 접속 이벤트 감지
     */
    @EventHandler(priority = EventPriority. NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event. getPlayer();
        UUID playerUUID = player.getUniqueId();

        // 접속 시간 기록
        playerLoginTime.put(playerUUID, System.currentTimeMillis());
        playerLastActivityTime.put(playerUUID, System.currentTimeMillis());

        // 플레이어 데이터 로드
        questDataManager.getProgressManager().loadPlayerQuests(playerUUID);
        questDataManager.getChainManager().getPlayerChains(playerUUID);

        // 환영 메시지
        player.sendMessage("§a환영합니다! " + player.getName());
        player.sendMessage("§7퀨스트 시스템에 접속했습니다.");

        // 진행 중인 퀨스트 표시
        List<PlayerQuest> inProgressQuests = questDataManager.getPlayerInProgressQuests(playerUUID);
        if (!inProgressQuests.isEmpty()) {
            player. sendMessage("§6진행 중인 퀨스트:");
            for (PlayerQuest pq : inProgressQuests) {
                player.sendMessage("  §f- " + questDataManager.getQuest(pq.getQuestId()).getName());
            }
        }

        Bukkit.getLogger().info(player.getName() + "이 접속했습니다.");
    }

    /**
     * 플레이어 퇴장 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event. getPlayer();
        UUID playerUUID = player.getUniqueId();

        // 모든 데이터 저장
        questDataManager.saveAll();

        // 접속 시간 정보 제거
        playerLoginTime.remove(playerUUID);
        playerLastActivityTime.remove(playerUUID);

        Bukkit.getLogger().info(player.getName() + "이 퇴장했습니다.");
    }

    /**
     * 플레이어 킥 이벤트 감지
     */
    @EventHandler(priority = EventPriority. NORMAL)
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // 모든 데이터 저장
        questDataManager.saveAll();

        // 접속 시간 정보 제거
        playerLoginTime.remove(playerUUID);
        playerLastActivityTime.remove(playerUUID);

        Bukkit.getLogger().info(player.getName() + "이 강제 퇴장되었습니다: " + event.getReason());
    }

    // ============ Activity Tracking ============

    /**
     * 플레이어 이동 이벤트 감지 (활동 추적)
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // 마지막 활동 시간 업데이트
        playerLastActivityTime.put(playerUUID, System.currentTimeMillis());
    }

    /**
     * 플레이어 채팅 이벤트 감지 (활동 추적)
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // 마지막 활동 시간 업데이트
        playerLastActivityTime.put(playerUUID, System.currentTimeMillis());
    }

    /**
     * 플레이어 명령어 이벤트 감지 (활동 추적)
     */
    @EventHandler(priority = EventPriority. NORMAL)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // 마지막 활동 시간 업데이트
        playerLastActivityTime.put(playerUUID, System.currentTimeMillis());
    }

    // ============ Level/Experience Events ============

    /**
     * 플레이어 경험치 변화 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
        Player player = event. getPlayer();
        int oldLevel = event.getOldLevel();
        int newLevel = event.getNewLevel();

        if (newLevel > oldLevel) {
            player.sendMessage("§a레벨 업!  " + newLevel + " 레벨이 되었습니다.");
        }
    }

    // ============ Respawn Events ============

    /**
     * 플레이어 부활 이벤트 감지
     */
    @EventHandler(priority = EventPriority. NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // 진행 중인 퀨스트 확인
        List<PlayerQuest> inProgressQuests = questDataManager.getPlayerInProgressQuests(playerUUID);

        if (!inProgressQuests. isEmpty()) {
            player.sendMessage("§7진행 중인 퀨스트:");
            for (PlayerQuest pq : inProgressQuests) {
                Quest quest = questDataManager.getQuest(pq.getQuestId());
                if (quest != null) {
                    player.sendMessage("  §f- " + quest.getName());
                }
            }
        }
    }

    // ============ Death Events ============

    /**
     * 플레이어 사망 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerUUID = player.getUniqueId();

        // 진행 중인 시간 제한 퀨스트 확인 (사망 시 실패 가능)
        List<PlayerQuest> inProgressQuests = questDataManager.getPlayerInProgressQuests(playerUUID);

        for (PlayerQuest pq : inProgressQuests) {
            Quest quest = questDataManager.getQuest(pq.getQuestId());
            if (quest != null && quest.isFailOnDeath()) {
                questDataManager.failQuest(player, pq.getQuestId(), "플레이어 사망");
            }
        }

        Bukkit.getLogger().info(player.getName() + "이 사망했습니다.");
    }

    // ============ Teleport Events ============

    /**
     * 플레이어 텔레포트 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // 마지막 활동 시간 업데이트
        playerLastActivityTime. put(playerUUID, System. currentTimeMillis());
    }

    // ============ Inventory Events ============

    /**
     * 플레이어 인벤토리 열기 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerOpenInventory(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // 마지막 활동 시간 업데이트
        playerLastActivityTime.put(playerUUID, System.currentTimeMillis());
    }

    // ============ Drop/Pickup Events ============

    /**
     * 플레이어 아이템 드롭 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event. getPlayer();
        UUID playerUUID = player.getUniqueId();

        // 마지막 활동 시간 업데이트
        playerLastActivityTime.put(playerUUID, System.currentTimeMillis());
    }

    // ============ Interaction Events ============

    /**
     * 플레이어 인터랙션 이벤트 감지
     */
    @EventHandler(priority = EventPriority. NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // 마지막 활동 시간 업데이트
        playerLastActivityTime.put(playerUUID, System.currentTimeMillis());
    }

    // ============ Tracking & Statistics ============

    /**
     * 플레이어 접속 시간 반환 (분)
     */
    public long getPlayerSessionTime(UUID playerUUID) {
        Long loginTime = playerLoginTime.get(playerUUID);
        if (loginTime == null) {
            return 0;
        }

        return (System.currentTimeMillis() - loginTime) / 60000; // 분 단위
    }

    /**
     * 플레이어 마지막 활동 이후 경과 시간 반환 (초)
     */
    public long getPlayerInactiveTime(UUID playerUUID) {
        Long lastActivityTime = playerLastActivityTime.get(playerUUID);
        if (lastActivityTime == null) {
            return 0;
        }

        return (System.currentTimeMillis() - lastActivityTime) / 1000; // 초 단위
    }

    /**
     * 플레이어가 활동 중인지 확인 (30초 이내)
     */
    public boolean isPlayerActive(UUID playerUUID) {
        return getPlayerInactiveTime(playerUUID) < 30;
    }

    /**
     * 모든 온라인 플레이어 통계
     */
    public Map<UUID, Map<String, Object>> getAllPlayerStatistics() {
        Map<UUID, Map<String, Object>> stats = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerUUID = player.getUniqueId();
            Map<String, Object> playerStats = new LinkedHashMap<>();

            playerStats.put("playerName", player.getName());
            playerStats.put("sessionTimeMinutes", getPlayerSessionTime(playerUUID));
            playerStats. put("inactiveTimeSeconds", getPlayerInactiveTime(playerUUID));
            playerStats.put("isActive", isPlayerActive(playerUUID));
            playerStats.put("questStats", questDataManager.getPlayerComprehensiveStatistics(playerUUID));

            stats.put(playerUUID, playerStats);
        }

        return stats;
    }

    /**
     * 플레이어 AFK 감지 및 경고 (10분 이상)
     */
    public void checkAFKPlayers() {
        for (Player player : Bukkit. getOnlinePlayers()) {
            UUID playerUUID = player. getUniqueId();

            if (getPlayerInactiveTime(playerUUID) > 600) { // 10분
                player.sendMessage("§c경고: 활동이 감지되지 않습니다.");
            }
        }
    }

    /**
     * 모든 활동 추적 데이터 초기화
     */
    public void clearActivityTracking() {
        playerLoginTime.clear();
        playerLastActivityTime.clear();
    }

    // ============ Status Information ============

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== 플레이어 리스너 상태 ===§r\n");
        sb.append("§7활동 중인 플레이어: §f"). append(playerLastActivityTime.size()).append("\n");

        int activeCount = (int) playerLastActivityTime.entrySet().stream()
            . filter(e -> isPlayerActive(e.getKey()))
            .count();
        sb.append("§7활동 중: §f").append(activeCount). append("\n");

        return sb.toString();
    }

    // ============ Getters ============

    /**
     * 데이터 관리자 반환
     */
    public QuestDataManager getQuestDataManager() {
        return questDataManager;
    }

    /**
     * 접속 시간 맵 반환
     */
    public Map<UUID, Long> getPlayerLoginTime() {
        return new HashMap<>(playerLoginTime);
    }

    /**
     * 마지막 활동 시간 맵 반환
     */
    public Map<UUID, Long> getPlayerLastActivityTime() {
        return new HashMap<>(playerLastActivityTime);
    }

    /**
     * 온라인 플레이어 수 반환
     */
    public int getOnlinePlayerCount() {
        return playerLastActivityTime.size();
    }
}