package com.multiverse.quest.rewards;

import com.multiverse. quest.models.*;
import org.bukkit.entity.Player;
import org.bukkit.  Bukkit;
import java.util.*;

/**
 * 명령어 보상 핸들러
 * 플레이어에게 명령어를 실행하여 보상을 지급합니다.
 */
public class CommandReward implements RewardHandler {
    private QuestReward reward;
    private List<String> commands;                  // 실행할 명령어 목록
    private boolean replacePlayerName;              // 플레이어 이름 치환 여부
    private boolean broadcastExecution;             // 명령어 실행 공지 여부
    private Map<UUID, List<Long>> rewardHistory;   // 보상 지급 이력
    private Map<UUID, Integer> totalGiven;         // 플레이어별 총 실행 명령어 수
    private boolean enabled;

    /**
     * 생성자
     */
    public CommandReward() {
        this.commands = new ArrayList<>();
        this. rewardHistory = new HashMap<>();
        this.totalGiven = new HashMap<>();
        this.replacePlayerName = true;
        this.broadcastExecution = false;
        this.enabled = true;
    }

    // ============ Reward Distribution ============

    @Override
    public boolean giveReward(Player player, UUID playerUUID) {
        return giveReward(player, playerUUID, 1);
    }

    @Override
    public boolean giveReward(Player player, UUID playerUUID, int amount) {
        if (!enabled || ! canGiveReward(player, playerUUID)) {
            return false;
        }

        try {
            onBeforeGive(player, playerUUID);

            int commandsExecuted = 0;

            for (int i = 0; i < amount; i++) {
                for (String command : commands) {
                    if (command == null || command.isEmpty()) {
                        continue;
                    }

                    String finalCommand = command;

                    // 플레이어 이름 치환
                    if (replacePlayerName) {
                        finalCommand = finalCommand.replace("%player%", player.getName());
                        finalCommand = finalCommand.replace("%uuid%", playerUUID.toString());
                    }

                    try {
                        // 명령어 실행 (콘솔 권한)
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
                        commandsExecuted++;

                        if (broadcastExecution) {
                            Bukkit.broadcastMessage(
                                String.format("§7[보상] %s 플레이어에게 명령어 실행: §f%s",
                                    player.getName(), finalCommand)
                            );
                        }
                    } catch (Exception e) {
                        Bukkit.getLogger().warning("명령어 실행 실패: " + finalCommand + " (" + e.getMessage() + ")");
                    }
                }
            }

            if (commandsExecuted > 0) {
                // 이력 기록
                rewardHistory.computeIfAbsent(playerUUID, k -> new ArrayList<>())
                    .add(System. currentTimeMillis());
                totalGiven.put(playerUUID, totalGiven.getOrDefault(playerUUID, 0) + commandsExecuted);

                onAfterGive(player, playerUUID);
                return true;
            }

            return false;
        } catch (Exception e) {
            onGiveFailed(player, playerUUID, e. getMessage());
            return false;
        }
    }

    @Override
    public boolean previewReward(Player player, UUID playerUUID) {
        if (player == null) {
            return false;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("§6명령어 보상:\n");

        for (String command : commands) {
            if (command != null && !command.isEmpty()) {
                String displayCommand = command;
                if (replacePlayerName) {
                    displayCommand = displayCommand. replace("%player%", player.getName());
                }
                sb.append(String.format("§f- %s\n", displayCommand));
            }
        }

        player.sendMessage(sb.toString());
        return true;
    }

    // ============ Validation ============

    @Override
    public boolean canGiveReward(Player player, UUID playerUUID) {
        if (player == null || ! enabled) {
            return false;
        }

        return checkConditions(player);
    }

    @Override
    public boolean isValid() {
        return ! commands.isEmpty();
    }

    @Override
    public boolean validateRewardData() {
        if (reward == null || commands. isEmpty()) {
            return false;
        }

        for (String command : commands) {
            if (command == null || command.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    // ============ Information ============

    @Override
    public String getRewardType() {
        return "COMMAND";
    }

    @Override
    public String getDescription() {
        if (commands.isEmpty()) {
            return "명령어 없음";
        }

        if (commands.size() == 1) {
            return String.format("명령어 실행: %s", commands.get(0));
        } else {
            return String.format("명령어 %d개 실행", commands.size());
        }
    }

    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== 명령어 보상 ===§r\n");
        sb.append("§7보상 타입: §f명령어\n");
        sb.append("§7명령어 수: §f"). append(commands.size()).append("\n");

        for (int i = 0; i < commands.size(); i++) {
            String command = commands.get(i);
            if (command != null && !command.isEmpty()) {
                sb.append(String.format("§7  %d. §f%s\n", i + 1, command));
            }
        }

        sb.append("§7플레이어 이름 치환: ").append(replacePlayerName ?  "§a활성화" : "§c비활성화").append("\n");
        sb.append("§7공지: ").append(broadcastExecution ?  "§a표시" : "§c미표시").append("\n");
        sb.append("§7상태: ").append(enabled ?  "§a활성화" : "§c비활성화"). append("\n");
        return sb.toString();
    }

    @Override
    public double getRewardValue() {
        // 명령어 보상 가치는 명령어 개수 기반
        return commands.size() * 0.8;
    }

    // ============ Configuration ============

    @Override
    public void initialize(QuestReward reward) {
        this.reward = reward;
        // 명령어 목록은 QuestReward에서 추출 (커스텀 구현 필요)
        // this.commands = reward.getCommands();
    }

    @Override
    public void cleanup() {
        commands.clear();
        rewardHistory.clear();
        totalGiven.clear();
    }

    // ============ Events ============

    @Override
    public void onBeforeGive(Player player, UUID playerUUID) {
        if (player != null) {
            player.sendMessage(String.format("§a보상 명령어를 실행합니다: §f%d개", commands.size()));
        }
    }

    @Override
    public void onAfterGive(Player player, UUID playerUUID) {
        if (player != null) {
            player.sendMessage("§7보상 명령어 실행이 완료되었습니다.");
        }
    }

    @Override
    public void onGiveFailed(Player player, UUID playerUUID, String reason) {
        if (player != null) {
            player.sendMessage(String.format("§c명령어 실행 실패: %s", reason));
        }
    }

    // ============ Data Management ============

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("type", getRewardType());
        data. put("commandCount", commands.size());
        data.put("replacePlayerName", replacePlayerName);
        data.put("broadcastExecution", broadcastExecution);
        data.put("enabled", enabled);
        return data;
    }

    @Override
    public void deserialize(Map<String, Object> data) {
        if (data.containsKey("replacePlayerName")) {
            this.replacePlayerName = (Boolean) data.get("replacePlayerName");
        }
        if (data.containsKey("broadcastExecution")) {
            this.broadcastExecution = (Boolean) data.get("broadcastExecution");
        }
        if (data.containsKey("enabled")) {
            this.enabled = (Boolean) data.get("enabled");
        }
    }

    // ============ Conditions ============

    @Override
    public boolean checkConditions(Player player) {
        if (player == null) {
            return false;
        }

        return player.isOnline();
    }

    @Override
    public boolean checkCondition(Player player, String condition) {
        if (player == null || condition == null) {
            return false;
        }

        switch (condition.toLowerCase()) {
            case "online":
                return player.isOnline();
            case "has_commands":
                return ! commands.isEmpty();
            default:
                return true;
        }
    }

    // ============ Command Management ============

    /**
     * 명령어 추가
     */
    public void addCommand(String command) {
        if (command != null && !command.isEmpty()) {
            commands.add(command);
        }
    }

    /**
     * 명령어 제거
     */
    public void removeCommand(int index) {
        if (index >= 0 && index < commands.size()) {
            commands. remove(index);
        }
    }

    /**
     * 모든 명령어 반환
     */
    public List<String> getCommands() {
        return new ArrayList<>(commands);
    }

    /**
     * 명령어 목록 설정
     */
    public void setCommands(List<String> commands) {
        this.commands = commands != null ? new ArrayList<>(commands) : new ArrayList<>();
    }

    /**
     * 명령어 개수 반환
     */
    public int getCommandCount() {
        return commands.size();
    }

    /**
     * 특정 인덱스 명령어 반환
     */
    public String getCommand(int index) {
        if (index >= 0 && index < commands.size()) {
            return commands.get(index);
        }
        return null;
    }

    // ============ Statistics ============

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("type", getRewardType());
        stats.put("commandCount", commands.size());
        stats.put("totalCommandsExecuted", totalGiven.values().stream().mapToInt(Integer::intValue).sum());
        stats.put("playersRewarded", totalGiven.size());
        stats.put("replacePlayerName", replacePlayerName);
        stats.put("broadcastExecution", broadcastExecution);
        stats.put("enabled", enabled);
        return stats;
    }

    @Override
    public Map<String, Object> getPlayerStatistics(UUID playerUUID) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats. put("playerUUID", playerUUID);
        stats.put("totalCommandsExecuted", totalGiven.getOrDefault(playerUUID, 0));

        List<Long> history = rewardHistory.getOrDefault(playerUUID, new ArrayList<>());
        stats.put("timesRewarded", history.size());

        if (!history.isEmpty()) {
            stats.put("lastRewardTime", history.get(history.size() - 1));
        }

        return stats;
    }

    @Override
    public int getTotalRewardsGiven() {
        return totalGiven.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public List<Map<String, Object>> getRewardHistory(UUID playerUUID) {
        List<Map<String, Object>> history = new ArrayList<>();
        List<Long> times = rewardHistory.getOrDefault(playerUUID, new ArrayList<>());

        for (long time : times) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("timestamp", time);
            entry.put("commandCount", commands.size());
            entry.put("type", getRewardType());
            history.add(entry);
        }

        return history;
    }

    // ============ Getters & Setters ============

    /**
     * 플레이어 이름 치환 여부 설정
     */
    public void setReplacePlayerName(boolean replace) {
        this.replacePlayerName = replace;
    }

    /**
     * 플레이어 이름 치환 여부 조회
     */
    public boolean isReplacePlayerName() {
        return replacePlayerName;
    }

    /**
     * 명령어 실행 공지 여부 설정
     */
    public void setBroadcastExecution(boolean broadcast) {
        this.broadcastExecution = broadcast;
    }

    /**
     * 명령어 실행 공지 여부 조회
     */
    public boolean isBroadcastExecution() {
        return broadcastExecution;
    }

    /**
     * 활성화 여부 설정
     */
    public void setEnabled(boolean enabled) {
        this. enabled = enabled;
    }

    /**
     * 활성화 여부 조회
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 플레이어 총 실행 명령어 수 반환
     */
    public int getPlayerTotalGiven(UUID playerUUID) {
        return totalGiven.getOrDefault(playerUUID, 0);
    }

    /**
     * 모든 보상 이력 초기화
     */
    public void resetHistory() {
        rewardHistory.clear();
        totalGiven.clear();
    }
}