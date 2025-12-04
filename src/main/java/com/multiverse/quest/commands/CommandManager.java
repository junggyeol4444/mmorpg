package com.multiverse.quest.commands;

import com.multiverse. quest.managers.QuestDataManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command. PluginCommand;
import org. bukkit.  Bukkit;
import java.util.*;

/**
 * 명령어 관리자
 * 모든 퀨스트 명령어를 등록하고 관리합니다.
 */
public class CommandManager {
    private final JavaPlugin plugin;
    private final QuestDataManager questDataManager;
    private final Map<String, CommandExecutor> commandExecutors;

    /**
     * 생성자
     * @param plugin 플러그인 인스턴스
     * @param questDataManager 퀨스트 데이터 관리자
     */
    public CommandManager(JavaPlugin plugin, QuestDataManager questDataManager) {
        this.plugin = plugin;
        this.questDataManager = questDataManager;
        this.commandExecutors = new HashMap<>();
    }

    // ============ Command Registration ============

    /**
     * 모든 명령어 등록
     */
    public void registerAllCommands() {
        try {
            registerQuestCommand();
            registerAdminCommand();
            registerPlayerCommand();
            registerReportCommand();
            registerStatisticsCommand();
            registerHelpCommand();

            Bukkit.getLogger().info("모든 명령어가 등록되었습니다.");
        } catch (Exception e) {
            Bukkit.getLogger().warning("명령어 등록 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 퀨스트 명령어 등록
     */
    private void registerQuestCommand() {
        QuestCommand questCommand = new QuestCommand(questDataManager);
        commandExecutors.put("quest", questCommand);

        PluginCommand command = plugin.getCommand("quest");
        if (command != null) {
            command.setExecutor(questCommand);
            Bukkit.getLogger().info("✓ /quest 명령어 등록 완료");
        } else {
            Bukkit.getLogger(). warning("✗ /quest 명령어를 plugin.yml에 등록하세요.");
        }
    }

    /**
     * 관리자 명령어 등록
     */
    private void registerAdminCommand() {
        AdminCommand adminCommand = new AdminCommand(questDataManager);
        commandExecutors.put("questadmin", adminCommand);

        PluginCommand command = plugin.getCommand("questadmin");
        if (command != null) {
            command.setExecutor(adminCommand);
            Bukkit. getLogger().info("✓ /questadmin 명령어 등록 완료");
        } else {
            Bukkit.getLogger().warning("✗ /questadmin 명령어를 plugin.yml에 등록하세요.");
        }
    }

    /**
     * 플레이어 명령어 등록
     */
    private void registerPlayerCommand() {
        PlayerCommand playerCommand = new PlayerCommand(questDataManager);
        commandExecutors. put("pquest", playerCommand);

        PluginCommand command = plugin.getCommand("pquest");
        if (command != null) {
            command.setExecutor(playerCommand);
            Bukkit.getLogger().info("✓ /pquest 명령어 등록 완료");
        } else {
            Bukkit.getLogger().warning("✗ /pquest 명령어를 plugin.yml에 등록하세요.");
        }
    }

    /**
     * 보고 명령어 등록
     */
    private void registerReportCommand() {
        ReportCommand reportCommand = new ReportCommand(questDataManager);
        commandExecutors.put("report", reportCommand);

        PluginCommand command = plugin.getCommand("report");
        if (command != null) {
            command.setExecutor(reportCommand);
            Bukkit.getLogger().info("✓ /report 명령어 등록 완료");
        } else {
            Bukkit.getLogger().warning("✗ /report 명령어를 plugin.yml에 등록하세요.");
        }
    }

    /**
     * 통계 명령어 등록
     */
    private void registerStatisticsCommand() {
        StatisticsCommand statsCommand = new StatisticsCommand(questDataManager);
        commandExecutors.put("stats", statsCommand);

        PluginCommand command = plugin.getCommand("stats");
        if (command != null) {
            command. setExecutor(statsCommand);
            Bukkit.getLogger(). info("✓ /stats 명령어 등록 완료");
        } else {
            Bukkit.getLogger(). warning("✗ /stats 명령어를 plugin.yml에 등록하세요.");
        }
    }

    /**
     * 도움말 명령어 등록
     */
    private void registerHelpCommand() {
        HelpCommand helpCommand = new HelpCommand(questDataManager);
        commandExecutors.put("help", helpCommand);

        PluginCommand command = plugin.getCommand("help");
        if (command != null) {
            command.setExecutor(helpCommand);
            Bukkit.getLogger().info("✓ /help 명령어 등록 완료");
        } else {
            Bukkit.getLogger().warning("✗ /help 명령어를 plugin.yml에 등록하세요.");
        }
    }

    // ============ Command Management ============

    /**
     * 명령어 실행기 조회
     */
    public org.bukkit.command.CommandExecutor getCommandExecutor(String commandName) {
        return commandExecutors.getOrDefault(commandName, null);
    }

    /**
     * 모든 등록된 명령어 조회
     */
    public Set<String> getRegisteredCommands() {
        return new HashSet<>(commandExecutors.keySet());
    }

    /**
     * 명령어 등록 여부 확인
     */
    public boolean isCommandRegistered(String commandName) {
        return commandExecutors.containsKey(commandName);
    }

    /**
     * 등록된 명령어 수
     */
    public int getRegisteredCommandCount() {
        return commandExecutors.size();
    }

    // ============ Status Information ============

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== 명령어 관리자 상태 ===§r\n");
        sb.append("§7등록된 명령어: §f"). append(getRegisteredCommandCount()).append("\n");

        for (String command : getRegisteredCommands()) {
            sb.append("§7  ✓ §f/").append(command).append("\n");
        }

        return sb.toString();
    }

    // ============ Getters ============

    /**
     * 플러그인 반환
     */
    public JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * 데이터 관리자 반환
     */
    public QuestDataManager getQuestDataManager() {
        return questDataManager;
    }

    /**
     * 모든 명령어 실행기 반환
     */
    public Map<String, org.bukkit.command.CommandExecutor> getAllCommandExecutors() {
        return new HashMap<>(commandExecutors);
    }
}