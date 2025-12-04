package com.multiverse.quest.commands;

import com.multiverse. quest.models.*;
import com.multiverse.quest.managers.QuestDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.  Bukkit;
import java.util.*;

/**
 * 통계 명령어
 * 플레이어 및 서버의 퀨스트 통계를 표시합니다.
 */
public class StatisticsCommand implements CommandExecutor {
    private final QuestDataManager questDataManager;

    /**
     * 생성자
     * @param questDataManager 퀨스트 데이터 관리자
     */
    public StatisticsCommand(QuestDataManager questDataManager) {
        this.questDataManager = questDataManager;
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
            return handlePersonal(player, args);
        }

        String subcommand = args[0]. toLowerCase();

        switch (subcommand) {
            case "personal":
                return handlePersonal(player, args);
            case "quest":
                return handleQuest(player, args);
            case "chain":
                return handleChain(player, args);
            case "reward":
                return handleReward(player, args);
            case "daily":
                return handleDaily(player, args);
            case "weekly":
                return handleWeekly(player, args);
            case "global":
                return handleGlobal(sender, args);
            case "top":
                return handleTop(sender, args);
            default:
                player.sendMessage("§c알 수 없는 서브 명령어입니다: " + subcommand);
                showHelpMessage(player);
                return true;
        }
    }

    // ============ Subcommand Handlers ============

    /**
     * personal 서브명령어 처리 - 개인 통계
     */
    private boolean handlePersonal(Player player, String[] args) {
        UUID playerUUID = player.getUniqueId();
        Map<String, Object> stats = questDataManager.getPlayerComprehensiveStatistics(playerUUID);

        player.sendMessage("§6=== 개인 통계 ===§r");

        if (stats. containsKey("quests")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> questStats = (Map<String, Object>) stats.get("quests");
            player.sendMessage("§7퀨스트:");
            player.sendMessage("  §f총: " + questStats.get("totalQuests"));
            player.sendMessage("  §a완료: " + questStats.get("completedQuests"));
            player.sendMessage("  §e진행 중: " + questStats.get("inProgressQuests"));
            if (questStats.containsKey("completionRate")) {
                player.sendMessage("  §6완료율: " + questStats.get("completionRate"));
            }
        }

        if (stats.containsKey("chains")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> chainStats = (Map<String, Object>) stats.get("chains");
            player.sendMessage("§7체인:");
            player.sendMessage("  §f총: " + chainStats.get("totalChains"));
            player.sendMessage("  §e진행 중: " + chainStats.get("chainsInProgress"));
        }

        if (stats.containsKey("rewards")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> rewardStats = (Map<String, Object>) stats.get("rewards");
            player.sendMessage("§7보상:");
            player. sendMessage("  §f획득: " + rewardStats. get("totalRewardsReceived"));
        }

        return true;
    }

    /**
     * quest 서브명령어 처리 - 퀨스트 통계
     */
    private boolean handleQuest(Player player, String[] args) {
        Map<String, Object> globalStats = questDataManager.getComprehensiveStatistics();

        player.sendMessage("§6=== 퀨스트 통계 ===§r");

        if (globalStats.containsKey("quests")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> questStats = (Map<String, Object>) globalStats.get("quests");
            
            player.sendMessage("§7전체 퀨스트:");
            for (Map.Entry<String, Object> entry : questStats.entrySet()) {
                player.sendMessage("  §f" + entry.getKey() + ": " + entry.getValue());
            }
        }

        return true;
    }

    /**
     * chain 서브명령어 처리 - 체인 통계
     */
    private boolean handleChain(Player player, String[] args) {
        Map<String, Object> globalStats = questDataManager.getComprehensiveStatistics();

        player.sendMessage("§6=== 체인 통계 ===§r");

        if (globalStats.containsKey("chains")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> chainStats = (Map<String, Object>) globalStats.get("chains");
            
            player.sendMessage("§7전체 체인:");
            for (Map.Entry<String, Object> entry : chainStats.entrySet()) {
                player.sendMessage("  §f" + entry.getKey() + ": " + entry.getValue());
            }
        }

        return true;
    }

    /**
     * reward 서브명령어 처리 - 보상 통계
     */
    private boolean handleReward(Player player, String[] args) {
        Map<String, Object> globalStats = questDataManager.getComprehensiveStatistics();

        player.sendMessage("§6=== 보상 통계 ===§r");

        if (globalStats.containsKey("rewards")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> rewardStats = (Map<String, Object>) globalStats.get("rewards");
            
            player.sendMessage("§7보상 정보:");
            player.sendMessage("  §f지급된 보상: " + rewardStats.get("totalRewardsDistributed"));
            player.sendMessage("  §f플레이어 수: " + rewardStats.get("playersRewarded"));

            if (rewardStats.containsKey("rewardsByType")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> byType = (Map<String, Object>) rewardStats.get("rewardsByType");
                player. sendMessage("§7타입별:");
                for (Map.Entry<String, Object> entry : byType.entrySet()) {
                    player.sendMessage("  §f" + entry.getKey() + ": " + entry.getValue());
                }
            }
        }

        return true;
    }

    /**
     * daily 서브명령어 처리 - 일일 퀨스트 통계
     */
    private boolean handleDaily(Player player, String[] args) {
        UUID playerUUID = player.getUniqueId();

        player.sendMessage("§6=== 일일 퀨스트 통계 ===§r");

        long timeUntilReset = questDataManager.getDailyWeeklyManager().getTimeUntilDailyReset();
        player.sendMessage("§7다음 리셋까지: " + formatTime(timeUntilReset));

        List<Quest> dailyQuests = questDataManager. getQuestManager().getQuestsByType(QuestType.DAILY);
        int available = 0;
        int completed = 0;

        for (Quest quest : dailyQuests) {
            if (questDataManager.canAcceptDailyQuest(playerUUID, quest. getQuestId())) {
                available++;
            } else {
                completed++;
            }
        }

        player.sendMessage("§7수락 가능: §a" + available);
        player.sendMessage("§7완료/대기: §e" + completed);
        player.sendMessage("§7총: §f" + dailyQuests.size());

        return true;
    }

    /**
     * weekly 서브명령어 처리 - 주간 퀨스트 통계
     */
    private boolean handleWeekly(Player player, String[] args) {
        UUID playerUUID = player.getUniqueId();

        player.sendMessage("§6=== 주간 퀨스트 통계 ===§r");

        long timeUntilReset = questDataManager.getDailyWeeklyManager().getTimeUntilWeeklyReset();
        player.sendMessage("§7다음 리셋까지: " + formatTime(timeUntilReset));

        List<Quest> weeklyQuests = questDataManager.getQuestManager().getQuestsByType(QuestType.WEEKLY);
        int available = 0;
        int completed = 0;

        for (Quest quest : weeklyQuests) {
            if (questDataManager.canAcceptWeeklyQuest(playerUUID, quest. getQuestId())) {
                available++;
            } else {
                completed++;
            }
        }

        player.sendMessage("§7수락 가능: §a" + available);
        player.sendMessage("§7완료/대기: §e" + completed);
        player. sendMessage("§7총: §f" + weeklyQuests. size());

        return true;
    }

    /**
     * global 서브명령어 처리 - 전역 통계 (관리자)
     */
    private boolean handleGlobal(CommandSender sender, String[] args) {
        if (! sender.hasPermission("questcore.admin")) {
            sender.sendMessage("§c관리자 권한이 없습니다.");
            return true;
        }

        Map<String, Object> globalStats = questDataManager.getComprehensiveStatistics();

        sender.sendMessage("§6=== 전역 통계 ===§r");

        if (globalStats.containsKey("quests")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> questStats = (Map<String, Object>) globalStats. get("quests");
            sender.sendMessage("§7퀨스트: " + questStats);
        }

        if (globalStats.containsKey("chains")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> chainStats = (Map<String, Object>) globalStats.get("chains");
            sender.sendMessage("§7체인: " + chainStats);
        }

        if (globalStats.containsKey("rewards")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> rewardStats = (Map<String, Object>) globalStats.get("rewards");
            sender.sendMessage("§7보상: " + rewardStats);
        }

        if (globalStats.containsKey("dailyWeekly")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> dwStats = (Map<String, Object>) globalStats.get("dailyWeekly");
            sender. sendMessage("§7일일/주간: " + dwStats);
        }

        return true;
    }

    /**
     * top 서브명령어 처리 - 순위 표시
     */
    private boolean handleTop(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c사용법: /stats top <타입>");
            sender.sendMessage("§7타입: completed, rewards, level");
            return true;
        }

        String type = args[1]. toLowerCase();

        sender.sendMessage("§6=== 순위 ===§r");

        switch (type) {
            case "completed":
                sender.sendMessage("§7완료 순위 (구현 필요)");
                break;
            case "rewards":
                sender.sendMessage("§7보상 순위 (구현 필요)");
                break;
            case "level":
                sender.sendMessage("§7레벨 순위 (구현 필요)");
                break;
            default:
                sender.sendMessage("§c알 수 없는 타입입니다: " + type);
                return true;
        }

        return true;
    }

    // ============ Utility Methods ============

    /**
     * 도움말 메시지 표시
     */
    private void showHelpMessage(Player player) {
        player.sendMessage("§6=== 통계 명령어 ===§r");
        player.sendMessage("§f/stats §7- 개인 통계");
        player.sendMessage("§f/stats personal §7- 개인 상세 통계");
        player.sendMessage("§f/stats quest §7- 퀨스트 통계");
        player.sendMessage("§f/stats chain §7- 체인 통계");
        player. sendMessage("§f/stats reward §7- 보상 통계");
        player.sendMessage("§f/stats daily §7- 일일 퀨스트 통계");
        player.sendMessage("§f/stats weekly §7- 주간 퀨스트 통계");
        player.sendMessage("§f/stats global §7- 전역 통계 (관리자)");
        player.sendMessage("§f/stats top <타입> §7- 순위 표시");
    }

    /**
     * 시간을 포맷된 문자열로 변환
     */
    private String formatTime(long seconds) {
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (days > 0) {
            return String.format("%d일 %d시간", days, hours);
        } else if (hours > 0) {
            return String.format("%d시간 %d분", hours, minutes);
        } else {
            return String.format("%d분 %d초", minutes, secs);
        }
    }

    // ============ Getters ============

    /**
     * 데이터 관리자 반환
     */
    public QuestDataManager getQuestDataManager() {
        return questDataManager;
    }
}