package com.multiverse.quest.commands;

import com.multiverse. quest.models.*;
import com.multiverse.quest.managers.QuestDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit. Bukkit;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 문제 보고 명령어
 * 플레이어가 퀨스트 시스템의 문제나 버그를 보고합니다.
 */
public class ReportCommand implements CommandExecutor {
    private final QuestDataManager questDataManager;
    private final List<BugReport> bugReports;
    private final DateTimeFormatter dateFormatter;

    /**
     * 생성자
     * @param questDataManager 퀨스트 데이터 관리자
     */
    public ReportCommand(QuestDataManager questDataManager) {
        this.questDataManager = questDataManager;
        this. bugReports = new ArrayList<>();
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "bug":
                return handleBugReport(player, args);
            case "suggestion":
                return handleSuggestion(player, args);
            case "issue":
                return handleIssue(player, args);
            case "check":
                return handleCheck(player, args);
            case "list":
                return handleList(player, args);
            default:
                player.sendMessage("§c알 수 없는 서브 명령어입니다: " + subcommand);
                showHelpMessage(player);
                return true;
        }
    }

    // ============ Subcommand Handlers ============

    /**
     * bug 서브명령어 처리 - 버그 보고
     */
    private boolean handleBugReport(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c사용법: /report bug <버그 설명>");
            return true;
        }

        StringBuilder description = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            description.append(args[i]).append(" ");
        }

        BugReport report = new BugReport(
            UUID.randomUUID(),
            player.getUniqueId(),
            player.getName(),
            "BUG",
            description.toString(). trim(),
            LocalDateTime.now()
        );

        bugReports.add(report);
        player.sendMessage("§a버그 보고가 접수되었습니다.");
        player.sendMessage("§7보고 ID: §f" + report.getId());

        // 관리자에게 알림
        notifyAdmins("§c[버그] " + player.getName() + ": " + description.toString(). trim());

        Bukkit.getLogger().warning("[BUG REPORT] " + player.getName() + ": " + description.toString().trim());

        return true;
    }

    /**
     * suggestion 서브명령어 처리 - 건의사항 제출
     */
    private boolean handleSuggestion(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c사용법: /report suggestion <건의사항>");
            return true;
        }

        StringBuilder description = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            description.append(args[i]).append(" ");
        }

        BugReport report = new BugReport(
            UUID.randomUUID(),
            player.getUniqueId(),
            player.getName(),
            "SUGGESTION",
            description.toString().trim(),
            LocalDateTime.now()
        );

        bugReports.add(report);
        player.sendMessage("§a건의사항이 접수되었습니다.");
        player.sendMessage("§7보고 ID: §f" + report.getId());

        // 관리자에게 알림
        notifyAdmins("§6[건의] " + player.getName() + ": " + description.toString().trim());

        return true;
    }

    /**
     * issue 서브명령어 처리 - 문제 보고
     */
    private boolean handleIssue(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c사용법: /report issue <문제 설명>");
            return true;
        }

        StringBuilder description = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            description.append(args[i]).append(" ");
        }

        BugReport report = new BugReport(
            UUID.randomUUID(),
            player.getUniqueId(),
            player.getName(),
            "ISSUE",
            description.toString().trim(),
            LocalDateTime.now()
        );

        bugReports.add(report);
        player.sendMessage("§a문제가 접수되었습니다.");
        player.sendMessage("§7보고 ID: §f" + report.getId());

        // 관리자에게 알림
        notifyAdmins("§e[문제] " + player. getName() + ": " + description.toString().trim());

        return true;
    }

    /**
     * check 서브명령어 처리 - 보고 상태 확인
     */
    private boolean handleCheck(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c사용법: /report check <보고ID>");
            return true;
        }

        String reportId = args[1];
        UUID reportUUID = null;

        try {
            reportUUID = UUID.fromString(reportId);
        } catch (IllegalArgumentException e) {
            player.sendMessage("§c잘못된 보고 ID입니다.");
            return true;
        }

        BugReport report = findReport(reportUUID);
        if (report == null) {
            player.sendMessage("§c해당 보고를 찾을 수 없습니다.");
            return true;
        }

        player. sendMessage("§6=== 보고 상태 ===§r");
        player.sendMessage("§7ID: §f" + report.getId());
        player.sendMessage("§7타입: §f" + report.getType());
        player.sendMessage("§7상태: §f" + (report.isResolved() ? "§a해결됨" : "§c접수됨"));
        player. sendMessage("§7작성일: §f" + report.getCreatedAt(). format(dateFormatter));
        player.sendMessage("§7설명: §f" + report. getDescription());

        return true;
    }

    /**
     * list 서브명령어 처리 - 내 보고 목록
     */
    private boolean handleList(Player player, String[] args) {
        UUID playerUUID = player.getUniqueId();

        List<BugReport> playerReports = new ArrayList<>();
        for (BugReport report : bugReports) {
            if (report.getPlayerUUID().equals(playerUUID)) {
                playerReports. add(report);
            }
        }

        if (playerReports.isEmpty()) {
            player.sendMessage("§7제출한 보고가 없습니다.");
            return true;
        }

        player.sendMessage("§6=== 내 보고 목록 ===§r");

        for (BugReport report : playerReports) {
            String status = report.isResolved() ?  "§a[해결됨]" : "§c[접수됨]";
            player.sendMessage(status + " §f" + report.getType() + " - " + report.getId());
            player.sendMessage("  §7" + report.getDescription());
        }

        return true;
    }

    // ============ Utility Methods ============

    /**
     * 관리자들에게 알림
     */
    private void notifyAdmins(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player. hasPermission("questcore.admin")) {
                player.sendMessage(message);
            }
        }
    }

    /**
     * 보고 찾기
     */
    private BugReport findReport(UUID reportId) {
        for (BugReport report : bugReports) {
            if (report.getId().equals(reportId)) {
                return report;
            }
        }
        return null;
    }

    /**
     * 도움말 메시지 표시
     */
    private void showHelpMessage(Player player) {
        player.sendMessage("§6=== 보고 명령어 ===§r");
        player.sendMessage("§f/report bug <설명> §7- 버그 보고");
        player.sendMessage("§f/report suggestion <설명> §7- 건의사항 제출");
        player.sendMessage("§f/report issue <설명> §7- 문제 보고");
        player.sendMessage("§f/report check <ID> §7- 보고 상태 확인");
        player.sendMessage("§f/report list §7- 내 보고 목록");
    }

    // ============ Getters & Statistics ============

    /**
     * 데이터 관리자 반환
     */
    public QuestDataManager getQuestDataManager() {
        return questDataManager;
    }

    /**
     * 전체 보고 수
     */
    public int getTotalReports() {
        return bugReports.size();
    }

    /**
     * 해결되지 않은 보고 수
     */
    public int getUnresolvedReports() {
        return (int) bugReports.stream(). filter(r -> !r.isResolved()).count();
    }

    /**
     * 타입별 보고 통계
     */
    public Map<String, Integer> getReportStatistics() {
        Map<String, Integer> stats = new HashMap<>();

        for (BugReport report : bugReports) {
            String type = report.getType();
            stats.put(type, stats.getOrDefault(type, 0) + 1);
        }

        return stats;
    }

    /**
     * 모든 보고 반환
     */
    public List<BugReport> getAllReports() {
        return new ArrayList<>(bugReports);
    }

    /**
     * 보고 삭제
     */
    public void deleteReport(UUID reportId) {
        bugReports.removeIf(r -> r.getId().equals(reportId));
    }

    /**
     * 모든 보고 삭제
     */
    public void clearAllReports() {
        bugReports.clear();
    }

    // ============ Inner Class ============

    /**
     * 버그 보고 클래스
     */
    public static class BugReport {
        private final UUID id;
        private final UUID playerUUID;
        private final String playerName;
        private final String type;
        private final String description;
        private final LocalDateTime createdAt;
        private boolean resolved;

        public BugReport(UUID id, UUID playerUUID, String playerName, String type, String description, LocalDateTime createdAt) {
            this.id = id;
            this.playerUUID = playerUUID;
            this.playerName = playerName;
            this.type = type;
            this.description = description;
            this.createdAt = createdAt;
            this.resolved = false;
        }

        // Getters
        public UUID getId() { return id; }
        public UUID getPlayerUUID() { return playerUUID; }
        public String getPlayerName() { return playerName; }
        public String getType() { return type; }
        public String getDescription() { return description; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public boolean isResolved() { return resolved; }

        // Setters
        public void setResolved(boolean resolved) { this.resolved = resolved; }
    }
}