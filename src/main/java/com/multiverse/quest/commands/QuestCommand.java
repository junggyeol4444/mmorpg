package com.multiverse.quest.commands;

import com.multiverse.  quest.models.*;
import com.multiverse.quest.managers.QuestDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.  CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.  Bukkit;
import java.util.*;

/**
 * 퀨스트 명령어
 * 플레이어가 퀨스트와 상호작용하는 기본 명령어를 제공합니다.
 */
public class QuestCommand implements CommandExecutor {
    private final QuestDataManager questDataManager;

    /**
     * 생성자
     * @param questDataManager 퀨스트 데이터 관리자
     */
    public QuestCommand(QuestDataManager questDataManager) {
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
            showHelpMessage(player);
            return true;
        }

        String subcommand = args[0]. toLowerCase();

        switch (subcommand) {
            case "list":
                return handleList(player, args);
            case "info":
                return handleInfo(player, args);
            case "accept":
                return handleAccept(player, args);
            case "complete":
                return handleComplete(player, args);
            case "abandon":
                return handleAbandon(player, args);
            case "progress":
                return handleProgress(player, args);
            case "track":
                return handleTrack(player, args);
            case "untrack":
                return handleUntrack(player, args);
            case "status":
                return handleStatus(player, args);
            case "reward":
                return handleReward(player, args);
            case "reset":
                return handleReset(player, args);
            default:
                player.sendMessage("§c알 수 없는 서브 명령어입니다: " + subcommand);
                showHelpMessage(player);
                return true;
        }
    }

    // ============ Subcommand Handlers ============

    /**
     * list 서브명령어 처리 - 퀨스트 목록 표시
     */
    private boolean handleList(Player player, String[] args) {
        UUID playerUUID = player.getUniqueId();

        player.sendMessage("§6=== 이용 가능한 퀨스트 ===§r");

        List<Quest> availableQuests = questDataManager.getQuestManager().getEnabledQuests();

        if (availableQuests. isEmpty()) {
            player.sendMessage("§7이용 가능한 퀨스트가 없습니다.");
            return true;
        }

        for (Quest quest : availableQuests) {
            if (questDataManager.canAcceptQuest(player, quest.getQuestId())) {
                String difficulty = "§7[ " + formatDifficulty(quest.getDifficulty()) + " §7]";
                player.sendMessage(difficulty + " §f" + quest.getName());
                player.  sendMessage("   §7" + quest.getDescription());
            }
        }

        return true;
    }

    /**
     * info 서브명령어 처리 - 퀨스트 정보 표시
     */
    private boolean handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player. sendMessage("§c사용법: /quest info <퀨스트ID>");
            return true;
        }

        String questId = args[1];
        Quest quest = questDataManager. getQuest(questId);

        if (quest == null) {
            player.sendMessage("§c퀨스트를 찾을 수 없습니다: " + questId);
            return true;
        }

        player.sendMessage("§6=== 퀨스트 정보: " + quest.getName() + " ===§r");
        player.sendMessage("§7설명: §f" + quest.getDescription());
        player.sendMessage("§7난이도: §f" + quest.getDifficulty());
        player.sendMessage("§7필요 레벨: §f" + quest.getRequiredLevel());
        player.sendMessage("§7유형: §f" + quest.getType(). name());

        if (quest.getObjectives() != null && !quest.getObjectives().isEmpty()) {
            player.sendMessage("§7목표:");
            for (QuestObjective obj : quest.getObjectives()) {
                player.sendMessage("   §f- " + obj.getDescription());
            }
        }

        if (quest.getRewards() != null && !quest.getRewards().isEmpty()) {
            player.sendMessage("§7보상:");
            questDataManager.previewRewards(player, quest.getRewards());
        }

        return true;
    }

    /**
     * accept 서브명령어 처리 - 퀨스트 수락
     */
    private boolean handleAccept(Player player, String[] args) {
        if (args.length < 2) {
            player. sendMessage("§c사용법: /quest accept <퀨스트ID>");
            return true;
        }

        String questId = args[1];

        if (! questDataManager.canAcceptQuest(player, questId)) {
            player. sendMessage("§c이 퀨스트를 수락할 수 없습니다.");
            return true;
        }

        if (questDataManager.acceptQuest(player, questId)) {
            player.sendMessage("§a퀨스트를 수락했습니다!");
            return true;
        } else {
            player.sendMessage("§c퀨스트 수락에 실패했습니다.");
            return true;
        }
    }

    /**
     * complete 서브명령어 처리 - 퀨스트 완료
     */
    private boolean handleComplete(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c사용법: /quest complete <퀨스트ID>");
            return true;
        }

        String questId = args[1];
        UUID playerUUID = player.getUniqueId();

        if (! questDataManager.getProgressManager().isQuestCompleted(playerUUID, questId)) {
            player. sendMessage("§c완료할 수 있는 퀨스트가 없습니다.");
            return true;
        }

        if (questDataManager.completeQuest(player, questId)) {
            player.sendMessage("§a퀨스트를 완료했습니다!");
            return true;
        } else {
            player.sendMessage("§c퀨스트 완료에 실패했습니다.");
            return true;
        }
    }

    /**
     * abandon 서브명령어 처리 - 퀨스트 포기
     */
    private boolean handleAbandon(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c사용법: /quest abandon <퀨스트ID>");
            return true;
        }

        String questId = args[1];

        if (questDataManager.abandonQuest(player, questId)) {
            player.sendMessage("§7퀨스트를 포기했습니다.");
            return true;
        } else {
            player.sendMessage("§c퀨스트 포기에 실패했습니다.");
            return true;
        }
    }

    /**
     * progress 서브명령어 처리 - 퀨스트 진행도 표시
     */
    private boolean handleProgress(Player player, String[] args) {
        UUID playerUUID = player.getUniqueId();

        List<PlayerQuest> inProgressQuests = questDataManager.getPlayerInProgressQuests(playerUUID);

        if (inProgressQuests.isEmpty()) {
            player. sendMessage("§7진행 중인 퀨스트가 없습니다.");
            return true;
        }

        player.sendMessage("§6=== 진행 중인 퀨스트 ===§r");

        for (PlayerQuest pq : inProgressQuests) {
            Quest quest = questDataManager.getQuest(pq.getQuestId());
            if (quest != null) {
                player.sendMessage("§f" + quest.getName());
                
                // 목표별 진행도 표시
                for (QuestObjective obj : quest.getObjectives()) {
                    player.sendMessage("  §7- " + obj.getDescription());
                }
            }
        }

        return true;
    }

    /**
     * track 서브명령어 처리 - 퀨스트 추적 시작
     */
    private boolean handleTrack(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c사용법: /quest track <퀨스트ID>");
            return true;
        }

        String questId = args[1];
        UUID playerUUID = player. getUniqueId();

        if (questDataManager.getTrackerManager().startTracking(player, questId)) {
            player.sendMessage("§a퀨스트 추적을 시작했습니다: " + questId);
            return true;
        } else {
            player.sendMessage("§c퀨스트 추적 시작에 실패했습니다.");
            return true;
        }
    }

    /**
     * untrack 서브명령어 처리 - 퀨스트 추적 중지
     */
    private boolean handleUntrack(Player player, String[] args) {
        UUID playerUUID = player.getUniqueId();

        if (questDataManager.getTrackerManager().stopTracking(player)) {
            player.sendMessage("§7퀨스트 추적을 중지했습니다.");
            return true;
        } else {
            player.sendMessage("§c퀨스트 추적 중지에 실패했습니다.");
            return true;
        }
    }

    /**
     * status 서브명령어 처리 - 퀨스트 통계 표시
     */
    private boolean handleStatus(Player player, String[] args) {
        UUID playerUUID = player.getUniqueId();

        Map<String, Object> stats = questDataManager.getPlayerComprehensiveStatistics(playerUUID);

        player.sendMessage("§6=== 퀨스트 통계 ===§r");
        
        if (stats. containsKey("quests")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> questStats = (Map<String, Object>) stats.get("quests");
            player.sendMessage("§7총 퀨스트: §f" + questStats.get("totalQuests"));
            player.sendMessage("§7완료: §f" + questStats.get("completedQuests"));
            player.sendMessage("§7진행 중: §f" + questStats.get("inProgressQuests"));
        }

        if (stats.containsKey("chains")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> chainStats = (Map<String, Object>) stats.get("chains");
            player.sendMessage("§7체인: §f" + chainStats.get("totalChains"));
        }

        if (stats.containsKey("rewards")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> rewardStats = (Map<String, Object>) stats.get("rewards");
            player.sendMessage("§7보상 획득: §f" + rewardStats.get("totalRewardsReceived"));
        }

        return true;
    }

    /**
     * reward 서브명령어 처리 - 보상 미리보기
     */
    private boolean handleReward(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c사용법: /quest reward <퀨스트ID>");
            return true;
        }

        String questId = args[1];
        Quest quest = questDataManager.getQuest(questId);

        if (quest == null) {
            player.sendMessage("§c퀨스트를 찾을 수 없습니다.");
            return true;
        }

        if (quest.getRewards() == null || quest.getRewards().isEmpty()) {
            player.sendMessage("§7이 퀨스트의 보상이 없습니다.");
            return true;
        }

        player.sendMessage("§6=== " + quest.getName() + "의 보상 ===§r");
        questDataManager.previewRewards(player, quest. getRewards());

        return true;
    }

    /**
     * reset 서브명령어 처리 - 퀨스트 진행도 초기화
     */
    private boolean handleReset(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c사용법: /quest reset <퀨스트ID>");
            return true;
        }

        String questId = args[1];
        UUID playerUUID = player.getUniqueId();

        // 관리자 권한 확인
        if (!player.hasPermission("questcore.admin")) {
            player.sendMessage("§c권한이 없습니다.");
            return true;
        }

        questDataManager.getProgressManager().resetProgress(playerUUID);
        player.sendMessage("§7퀨스트 진행도를 초기화했습니다.");

        return true;
    }

    // ============ Utility Methods ============

    /**
     * 도움말 메시지 표시
     */
    private void showHelpMessage(Player player) {
        player.sendMessage("§6=== 퀨스트 명령어 ===§r");
        player.sendMessage("§f/quest list §7- 이용 가능한 퀨스트 목록");
        player.sendMessage("§f/quest info <ID> §7- 퀨스트 정보 조회");
        player.sendMessage("§f/quest accept <ID> §7- 퀨스트 수락");
        player.sendMessage("§f/quest complete <ID> §7- 퀨스트 완료");
        player.sendMessage("§f/quest abandon <ID> §7- 퀨스트 포기");
        player.sendMessage("§f/quest progress §7- 진행 중인 퀨스트 표시");
        player.sendMessage("§f/quest track <ID> §7- 퀨스트 추적 시작");
        player.sendMessage("§f/quest untrack §7- 퀨스트 추적 중지");
        player.sendMessage("§f/quest status §7- 퀨스트 통계");
        player.sendMessage("§f/quest reward <ID> §7- 보상 미리보기");
    }

    /**
     * 난이도를 색상과 함께 포맷
     */
    private String formatDifficulty(String difficulty) {
        if (difficulty == null) {
            return "§7보통";
        }

        return switch (difficulty. toLowerCase()) {
            case "easy" -> "§a쉬움";
            case "normal" -> "§7보통";
            case "hard" -> "§c어려움";
            case "very_hard" -> "§4매우 어려움";
            default -> "§7" + difficulty;
        };
    }

    /**
     * 퀨스트 타입을 읽기 좋은 형태로 포맷
     */
    private String formatQuestType(QuestType type) {
        if (type == null) {
            return "일반";
        }

        return switch (type) {
            case MAIN -> "메인";
            case SUB -> "보조";
            case DAILY -> "일일";
            case WEEKLY -> "주간";
            case EVENT -> "이벤트";
            default -> type.name();
        };
    }

    // ============ Status Information ============

    /**
     * 명령어 사용 통계
     */
    private final Map<String, Integer> commandUsageStats = new HashMap<>();

    /**
     * 명령어 사용 기록
     */
    public void recordCommandUsage(String subcommand) {
        commandUsageStats.put(subcommand, commandUsageStats.getOrDefault(subcommand, 0) + 1);
    }

    /**
     * 명령어 사용 통계 조회
     */
    public Map<String, Integer> getCommandUsageStats() {
        return new HashMap<>(commandUsageStats);
    }

    /**
     * 명령어 사용 통계 초기화
     */
    public void clearCommandUsageStats() {
        commandUsageStats.clear();
    }

    // ============ Getters ============

    /**
     * 데이터 관리자 반환
     */
    public QuestDataManager getQuestDataManager() {
        return questDataManager;
    }
}