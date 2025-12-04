package com.multiverse.quest.commands;

import com.multiverse.quest.models.*;
import com.multiverse.quest.managers.QuestDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit. Bukkit;
import java.util.*;

/**
 * 플레이어 명령어
 * 플레이어가 퀨스트 설정 및 개인 설정을 관리하는 명령어를 제공합니다.
 */
public class PlayerCommand implements CommandExecutor {
    private final QuestDataManager questDataManager;
    private final Map<UUID, Map<String, Object>> playerSettings;

    /**
     * 생성자
     * @param questDataManager 퀨스트 데이터 관리자
     */
    public PlayerCommand(QuestDataManager questDataManager) {
        this.questDataManager = questDataManager;
        this.playerSettings = new HashMap<>();
    }

    // ============ Command Execution ============

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            showHelpMessage(player);
            return true;
        }

        String subcommand = args[0]. toLowerCase();

        switch (subcommand) {
            case "settings":
                return handleSettings(player, args);
            case "notification":
                return handleNotification(player, args);
            case "tracker":
                return handleTracker(player, args);
            case "favorite":
                return handleFavorite(player, args);
            case "history":
                return handleHistory(player, args);
            case "daily":
                return handleDaily(player, args);
            case "weekly":
                return handleWeekly(player, args);
            default:
                player.sendMessage("§c알 수 없는 서브 명령어입니다: " + subcommand);
                showHelpMessage(player);
                return true;
        }
    }

    // ============ Subcommand Handlers ============

    /**
     * settings 서브명령어 처리 - 개인 설정 관리
     */
    private boolean handleSettings(Player player, String[] args) {
        UUID playerUUID = player.getUniqueId();
        Map<String, Object> settings = playerSettings.computeIfAbsent(playerUUID, k -> new HashMap<>());

        if (args.length < 2) {
            // 현재 설정 표시
            player.sendMessage("§6=== 개인 설정 ===§r");
            player.sendMessage("§7사운드: " + (settings.getOrDefault("sound", true) ? "§a활성화" : "§c비활성화"));
            player.sendMessage("§7타이틀: " + (settings. getOrDefault("title", true) ? "§a활성화" : "§c비활성화"));
            player.sendMessage("§7액션바: " + (settings.getOrDefault("actionbar", true) ? "§a활성화" : "§c비활성화"));
            player.sendMessage("§7채팅: " + (settings.getOrDefault("chat", true) ?  "§a활성화" : "§c비활성화"));
            return true;
        }

        String setting = args[1].toLowerCase();
        boolean value = args. length > 2 ?  Boolean.parseBoolean(args[2]) : !(boolean) settings.getOrDefault(setting, true);

        switch (setting) {
            case "sound":
                settings.put("sound", value);
                questDataManager.getNotificationManager().setSoundEnabled(value);
                break;
            case "title":
                settings.put("title", value);
                questDataManager.getNotificationManager().setTitleEnabled(value);
                break;
            case "actionbar":
                settings.put("actionbar", value);
                questDataManager.getNotificationManager().setActionbarEnabled(value);
                break;
            case "chat":
                settings.put("chat", value);
                questDataManager.getNotificationManager().setChatEnabled(value);
                break;
            default:
                player.sendMessage("§c알 수 없는 설정입니다: " + setting);
                return true;
        }

        player.sendMessage("§a설정이 변경되었습니다: " + setting + " = " + value);
        return true;
    }

    /**
     * notification 서브명령어 처리 - 알림 설정
     */
    private boolean handleNotification(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c사용법: /pquest notification <on|off|reset>");
            return true;
        }

        String action = args[1].toLowerCase();

        switch (action) {
            case "on":
                questDataManager. getNotificationManager().enableAllNotifications();
                player.sendMessage("§a모든 알림이 활성화되었습니다.");
                break;
            case "off":
                questDataManager.getNotificationManager().disableAllNotifications();
                player.sendMessage("§7모든 알림이 비활성화되었습니다.");
                break;
            case "reset":
                questDataManager.getNotificationManager().enableAllNotifications();
                player. sendMessage("§a알림 설정이 초기화되었습니다.");
                break;
            default:
                player.sendMessage("§c알 수 없는 액션입니다: " + action);
                return true;
        }

        return true;
    }

    /**
     * tracker 서브명령어 처리 - 추적 위치 설정
     */
    private boolean handleTracker(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c사용법: /pquest tracker <위치>");
            player.sendMessage("§7위치: TOP_LEFT, TOP_CENTER, TOP_RIGHT, CENTER, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT");
            return true;
        }

        String position = args[1].toUpperCase();

        try {
            TrackerPosition trackerPos = TrackerPosition.valueOf(position);
            questDataManager.getTrackerManager().setTrackerPosition(player, trackerPos);
            player. sendMessage("§a추적기 위치가 변경되었습니다: " + position);
            return true;
        } catch (IllegalArgumentException e) {
            player.sendMessage("§c알 수 없는 위치입니다: " + position);
            return true;
        }
    }

    /**
     * favorite 서브명령어 처리 - 즐겨찾기 관리
     */
    private boolean handleFavorite(Player player, String[] args) {
        UUID playerUUID = player.getUniqueId();

        if (args.length < 2) {
            // 즐겨찾기 목록 표시
            player.sendMessage("§6=== 즐겨찾기 ===§r");
            // 실제 구현에서는 저장된 즐겨찾기 표시
            return true;
        }

        String action = args[1].toLowerCase();
        String questId = args. length > 2 ? args[2] : null;

        switch (action) {
            case "add":
                if (questId == null) {
                    player. sendMessage("§c사용법: /pquest favorite add <퀨스트ID>");
                    return true;
                }
                player.sendMessage("§a" + questId + "을 즐겨찾기에 추가했습니다.");
                break;
            case "remove":
                if (questId == null) {
                    player.sendMessage("§c사용법: /pquest favorite remove <퀨스트ID>");
                    return true;
                }
                player.sendMessage("§7" + questId + "을 즐겨찾기에서 제거했습니다.");
                break;
            case "clear":
                player.sendMessage("§7모든 즐겨찾기가 제거되었습니다.");
                break;
            default:
                player.sendMessage("§c알 수 없는 액션입니다: " + action);
                return true;
        }

        return true;
    }

    /**
     * history 서브명령어 처리 - 완료 이력 조회
     */
    private boolean handleHistory(Player player, String[] args) {
        UUID playerUUID = player.getUniqueId();

        List<PlayerQuest> completedQuests = questDataManager. getPlayerCompletedQuests(playerUUID);

        if (completedQuests.isEmpty()) {
            player.sendMessage("§7완료한 퀨스트가 없습니다.");
            return true;
        }

        player.sendMessage("§6=== 완료 이력 ===§r");

        for (PlayerQuest pq : completedQuests) {
            Quest quest = questDataManager.getQuest(pq.getQuestId());
            if (quest != null) {
                player.sendMessage("§f✓ " + quest.getName());
            }
        }

        return true;
    }

    /**
     * daily 서브명령어 처리 - 일일 퀨스트 정보
     */
    private boolean handleDaily(Player player, String[] args) {
        UUID playerUUID = player.getUniqueId();

        player.sendMessage("§6=== 일일 퀨스트 ===§r");

        long timeUntilReset = questDataManager.getDailyWeeklyManager().getTimeUntilDailyReset();
        player.sendMessage("§7다음 리셋: " + formatTime(timeUntilReset));

        List<Quest> dailyQuests = questDataManager.getQuestManager().getQuestsByType(QuestType.DAILY);

        if (dailyQuests. isEmpty()) {
            player.sendMessage("§7일일 퀨스트가 없습니다.");
            return true;
        }

        for (Quest quest : dailyQuests) {
            if (questDataManager.canAcceptDailyQuest(playerUUID, quest. getQuestId())) {
                player.sendMessage("§a○ " + quest.getName() + " (수락 가능)");
            } else {
                player.sendMessage("§7● " + quest.getName() + " (대기 중)");
            }
        }

        return true;
    }

    /**
     * weekly 서브명령어 처리 - 주간 퀨스트 정보
     */
    private boolean handleWeekly(Player player, String[] args) {
        UUID playerUUID = player.getUniqueId();

        player.sendMessage("§6=== 주간 퀨스트 ===§r");

        long timeUntilReset = questDataManager.getDailyWeeklyManager().getTimeUntilWeeklyReset();
        player.sendMessage("§7다음 리셋: " + formatTime(timeUntilReset));

        List<Quest> weeklyQuests = questDataManager. getQuestManager().getQuestsByType(QuestType. WEEKLY);

        if (weeklyQuests.isEmpty()) {
            player.sendMessage("§7주간 퀨스트가 없습니다.");
            return true;
        }

        for (Quest quest : weeklyQuests) {
            if (questDataManager.canAcceptWeeklyQuest(playerUUID, quest.getQuestId())) {
                player.sendMessage("§a○ " + quest. getName() + " (수락 가능)");
            } else {
                player.sendMessage("§7● " + quest.getName() + " (대기 중)");
            }
        }

        return true;
    }

    // ============ Utility Methods ============

    /**
     * 도움말 메시지 표시
     */
    private void showHelpMessage(Player player) {
        player.sendMessage("§6=== 플레이어 명령어 ===§r");
        player.sendMessage("§f/pquest settings [설정] [값] §7- 개인 설정 관리");
        player.sendMessage("§f/pquest notification <on|off|reset> §7- 알림 설정");
        player.sendMessage("§f/pquest tracker <위치> §7- 추적기 위치 설정");
        player.sendMessage("§f/pquest favorite <add|remove|clear> [ID] §7- 즐겨찾기 관리");
        player.sendMessage("§f/pquest history §7- 완료 이력 조회");
        player.sendMessage("§f/pquest daily §7- 일일 퀨스트 정보");
        player.sendMessage("§f/pquest weekly §7- 주간 퀨스트 정보");
    }

    /**
     * 시간을 포맷된 문자열로 변환
     */
    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        return String.format("%d시간 %d분 %d초", hours, minutes, secs);
    }

    // ============ Getters ============

    /**
     * 데이터 관리자 반환
     */
    public QuestDataManager getQuestDataManager() {
        return questDataManager;
    }

    /**
     * 플레이어 설정 반환
     */
    public Map<String, Object> getPlayerSettings(UUID playerUUID) {
        return playerSettings.getOrDefault(playerUUID, new HashMap<>());
    }
}