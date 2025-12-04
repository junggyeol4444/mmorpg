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
 * 관리자 명령어
 * 관리자가 퀨스트 시스템을 관리하기 위한 명령어를 제공합니다.
 */
public class AdminCommand implements CommandExecutor {
    private final QuestDataManager questDataManager;

    /**
     * 생성자
     * @param questDataManager 퀨스트 데이터 관리자
     */
    public AdminCommand(QuestDataManager questDataManager) {
        this. questDataManager = questDataManager;
    }

    // ============ Command Execution ============

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 관리자 권한 확인
        if (! sender.hasPermission("questcore.admin")) {
            sender. sendMessage("§c관리자 권한이 없습니다.");
            return true;
        }

        if (args.length == 0) {
            showHelpMessage(sender);
            return true;
        }

        String subcommand = args[0]. toLowerCase();

        switch (subcommand) {
            case "reload":
                return handleReload(sender, args);
            case "create":
                return handleCreate(sender, args);
            case "delete":
                return handleDelete(sender, args);
            case "edit":
                return handleEdit(sender, args);
            case "list":
                return handleList(sender, args);
            case "player":
                return handlePlayer(sender, args);
            case "reset":
                return handleReset(sender, args);
            case "give":
                return handleGive(sender, args);
            case "stats":
                return handleStats(sender, args);
            case "status":
                return handleStatus(sender, args);
            default:
                sender.sendMessage("§c알 수 없는 서브 명령어입니다: " + subcommand);
                showHelpMessage(sender);
                return true;
        }
    }

    // ============ Subcommand Handlers ============

    /**
     * reload 서브명령어 처리 - 모든 데이터 다시 로드
     */
    private boolean handleReload(CommandSender sender, String[] args) {
        sender.sendMessage("§7퀨스트 데이터를 다시 로드 중입니다.. .");

        if (questDataManager.reloadAll()) {
            sender.sendMessage("§a퀨스트 데이터가 성공적으로 로드되었습니다.");
            Bukkit.getLogger().info("퀨스트 데이터 리로드 완료");
            return true;
        } else {
            sender.sendMessage("§c퀨스트 데이터 로드에 실패했습니다.");
            return true;
        }
    }

    /**
     * create 서브명령어 처리 - 새 퀨스트 생성
     */
    private boolean handleCreate(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§c사용법: /questadmin create <퀨스트ID> <퀨스트이름>");
            return true;
        }

        String questId = args[1];
        String questName = args[2];

        // 기본 퀨스트 생성
        Quest quest = new Quest();
        quest.setQuestId(questId);
        quest.setName(questName);
        quest. setEnabled(true);

        if (questDataManager.getQuestManager().createQuest(quest)) {
            sender.sendMessage("§a퀨스트가 생성되었습니다: " + questId);
            return true;
        } else {
            sender.sendMessage("§c퀨스트 생성에 실패했습니다.");
            return true;
        }
    }

    /**
     * delete 서브명령어 처리 - 퀨스트 삭제
     */
    private boolean handleDelete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c사용법: /questadmin delete <퀨스트ID>");
            return true;
        }

        String questId = args[1];

        if (questDataManager.getQuestManager().deleteQuest(questId)) {
            sender.sendMessage("§a퀨스트가 삭제되었습니다: " + questId);
            return true;
        } else {
            sender.sendMessage("§c퀨스트 삭제에 실패했습니다.");
            return true;
        }
    }

    /**
     * edit 서브명령어 처리 - 퀨스트 수정
     */
    private boolean handleEdit(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§c사용법: /questadmin edit <퀨스트ID> <설정> <값>");
            sender.sendMessage("§7예: /questadmin edit quest1 name 새이름");
            return true;
        }

        String questId = args[1];
        String setting = args[2]. toLowerCase();
        String value = args[3];

        Quest quest = questDataManager.getQuest(questId);
        if (quest == null) {
            sender.sendMessage("§c퀨스트를 찾을 수 없습니다: " + questId);
            return true;
        }

        switch (setting) {
            case "name":
                quest.setName(value);
                sender.sendMessage("§a퀨스트 이름이 변경되었습니다.");
                break;
            case "description":
                quest.setDescription(value);
                sender.sendMessage("§a퀨스트 설명이 변경되었습니다.");
                break;
            case "enabled":
                boolean enabled = value.equalsIgnoreCase("true");
                quest.setEnabled(enabled);
                sender.sendMessage("§a퀨스트 활성화 상태가 변경되었습니다: " + enabled);
                break;
            default:
                sender.sendMessage("§c알 수 없는 설정입니다: " + setting);
                return true;
        }

        questDataManager.getQuestManager().updateQuest(quest);
        return true;
    }

    /**
     * list 서브명령어 처리 - 모든 퀨스트 목록
     */
    private boolean handleList(CommandSender sender, String[] args) {
        List<Quest> allQuests = questDataManager.getQuestManager().getAllQuests();

        sender.sendMessage("§6=== 모든 퀨스트 ===§r");
        sender.sendMessage("§7총 " + allQuests.size() + "개의 퀨스트");

        for (Quest quest : allQuests) {
            String status = quest.isEnabled() ? "§a활성" : "§c비활성";
            sender.sendMessage(status + " §f" + quest.getQuestId() + " - " + quest.getName());
        }

        return true;
    }

    /**
     * player 서브명령어 처리 - 플레이어 퀨스트 관리
     */
    private boolean handlePlayer(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§c사용법: /questadmin player <플레이어이름> <action>");
            sender.sendMessage("§7액션: info, reset, complete");
            return true;
        }

        String playerName = args[1];
        String action = args[2].toLowerCase();

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage("§c플레이어를 찾을 수 없습니다: " + playerName);
            return true;
        }

        UUID playerUUID = player.getUniqueId();

        switch (action) {
            case "info":
                Map<String, Object> stats = questDataManager.getPlayerComprehensiveStatistics(playerUUID);
                sender.sendMessage("§6=== " + playerName + "의 퀨스트 정보 ===§r");
                sender.sendMessage("§7통계: " + stats.toString());
                break;

            case "reset":
                if (questDataManager.resetPlayerData(playerUUID)) {
                    sender.sendMessage("§a" + playerName + "의 퀨스트 데이터가 초기화되었습니다.");
                    player.sendMessage("§c관리자가 당신의 퀨스트 데이터를 초기화했습니다.");
                } else {
                    sender. sendMessage("§c데이터 초기화에 실패했습니다.");
                }
                break;

            case "complete":
                if (args.length < 4) {
                    sender.sendMessage("§c사용법: /questadmin player <플레이어이름> complete <퀨스트ID>");
                    return true;
                }
                
                String questId = args[3];
                if (questDataManager.completeQuest(player, questId)) {
                    sender.sendMessage("§a퀨스트가 완료되었습니다.");
                    player.sendMessage("§c관리자가 퀨스트를 완료 처리했습니다.");
                } else {
                    sender. sendMessage("§c퀨스트 완료 처리에 실패했습니다.");
                }
                break;

            default:
                sender.sendMessage("§c알 수 없는 액션입니다: " + action);
                return true;
        }

        return true;
    }

    /**
     * reset 서브명령어 처리 - 전체 데이터 초기화
     */
    private boolean handleReset(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c사용법: /questadmin reset <타입>");
            sender.sendMessage("§7타입: all, daily, weekly");
            return true;
        }

        String resetType = args[1].toLowerCase();

        switch (resetType) {
            case "all":
                questDataManager.getDailyWeeklyManager().resetAllDailyQuests();
                questDataManager.getDailyWeeklyManager().resetAllWeeklyQuests();
                sender.sendMessage("§a모든 퀨스트가 초기화되었습니다.");
                break;

            case "daily":
                int dailyCount = questDataManager.getDailyWeeklyManager().resetAllDailyQuests();
                sender.sendMessage("§a" + dailyCount + "명의 플레이어 일일 퀨스트가 초기화되었습니다.");
                break;

            case "weekly":
                int weeklyCount = questDataManager.getDailyWeeklyManager().resetAllWeeklyQuests();
                sender.sendMessage("§a" + weeklyCount + "명의 플레이어 주간 퀨스트가 초기화되었습니다.");
                break;

            default:
                sender.sendMessage("§c알 수 없는 초기화 타입입니다: " + resetType);
                return true;
        }

        return true;
    }

    /**
     * give 서브명령어 처리 - 플레이어에게 퀨스트 지급
     */
    private boolean handleGive(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§c사용법: /questadmin give <플레이어이름> <퀨스트ID>");
            return true;
        }

        String playerName = args[1];
        String questId = args[2];

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage("§c플레이어를 찾을 수 없습니다: " + playerName);
            return true;
        }

        if (questDataManager.acceptQuest(player, questId)) {
            sender.sendMessage("§a" + playerName + "에게 퀨스트가 지급되었습니다.");
            player.sendMessage("§c관리자가 퀨스트를 지급했습니다.");
            return true;
        } else {
            sender.sendMessage("§c퀨스트 지급에 실패했습니다.");
            return true;
        }
    }

    /**
     * stats 서브명령어 처리 - 전체 통계 표시
     */
    private boolean handleStats(CommandSender sender, String[] args) {
        Map<String, Object> globalStats = questDataManager.getComprehensiveStatistics();

        sender.sendMessage("§6=== 전체 퀨스트 통계 ===§r");

        if (globalStats.containsKey("quests")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> questStats = (Map<String, Object>) globalStats.get("quests");
            sender.sendMessage("§7퀨스트:");
            for (Map.Entry<String, Object> entry : questStats.entrySet()) {
                sender.sendMessage("  §f" + entry.getKey() + ": " + entry.getValue());
            }
        }

        if (globalStats.containsKey("rewards")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> rewardStats = (Map<String, Object>) globalStats.get("rewards");
            sender.sendMessage("§7보상:");
            sender.sendMessage("  §f지급된 보상: " + rewardStats.get("totalRewardsDistributed"));
            sender.sendMessage("  §f플레이어 수: " + rewardStats.get("playersRewarded"));
        }

        return true;
    }

    /**
     * status 서브명령어 처리 - 시스템 상태 표시
     */
    private boolean handleStatus(CommandSender sender, String[] args) {
        sender.sendMessage(questDataManager.getStatusInfo());
        return true;
    }

    // ============ Utility Methods ============

    /**
     * 도움말 메시지 표시
     */
    private void showHelpMessage(CommandSender sender) {
        sender.sendMessage("§6=== 관리자 명령어 ===§r");
        sender.sendMessage("§f/questadmin reload §7- 데이터 다시 로드");
        sender.sendMessage("§f/questadmin create <ID> <이름> §7- 퀨스트 생성");
        sender.sendMessage("§f/questadmin delete <ID> §7- 퀨스트 삭제");
        sender.sendMessage("§f/questadmin edit <ID> <설정> <값> §7- 퀨스트 수정");
        sender.sendMessage("§f/questadmin list §7- 퀨스트 목록");
        sender.sendMessage("§f/questadmin player <이름> <액션> §7- 플레이어 관리");
        sender.sendMessage("§f/questadmin reset <타입> §7- 데이터 초기화");
        sender.sendMessage("§f/questadmin give <이름> <ID> §7- 퀨스트 지급");
        sender.sendMessage("§f/questadmin stats §7- 전체 통계");
        sender.sendMessage("§f/questadmin status §7- 시스템 상태");
    }

    // ============ Getters ============

    /**
     * 데이터 관리자 반환
     */
    public QuestDataManager getQuestDataManager() {
        return questDataManager;
    }
}