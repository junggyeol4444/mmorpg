package com.multiverse.quest.managers;

import com.multiverse.quest.models.*;
import org.bukkit.entity. Player;
import org.bukkit.  Bukkit;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatMessageType;
import java.util.*;

/**
 * 알림 관리자
 * 플레이어에게 퀘스트 관련 알림을 전송합니다.
 */
public class NotificationManager {
    private final Map<UUID, Queue<Notification>> playerNotifications;
    private boolean initialized;
    private boolean soundEnabled;
    private boolean titleEnabled;
    private boolean chatEnabled;
    private boolean actionbarEnabled;

    /**
     * 생성자
     */
    public NotificationManager() {
        this.playerNotifications = new HashMap<>();
        this.initialized = false;
        this.soundEnabled = true;
        this.titleEnabled = true;
        this.chatEnabled = true;
        this.actionbarEnabled = true;
    }

    // ============ Initialization ============

    /**
     * 알림 관리자 초기화
     */
    public boolean initialize() {
        try {
            initialized = true;
            Bukkit.getLogger().info("알림 관리자 초기화 완료");
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().warning("알림 관리자 초기화 실패: " + e.getMessage());
            initialized = false;
            return false;
        }
    }

    /**
     * 초기화 여부 확인
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * 종료
     */
    public boolean shutdown() {
        try {
            playerNotifications.clear();
            initialized = false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Notification Sending ============

    /**
     * 플레이어에게 일반 메시지 전송
     */
    public void sendMessage(Player player, String message) {
        if (player == null || message == null || ! chatEnabled) {
            return;
        }

        player.sendMessage(message);
    }

    /**
     * 플레이어에게 액션바 메시지 전송
     */
    public void sendActionbar(Player player, String message) {
        if (player == null || message == null || !actionbarEnabled) {
            return;
        }

        try {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        } catch (Exception e) {
            Bukkit.getLogger().warning("액션바 메시지 전송 실패: " + e.getMessage());
        }
    }

    /**
     * 플레이어에게 타이틀 전송
     */
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player == null || ! titleEnabled) {
            return;
        }

        try {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        } catch (Exception e) {
            Bukkit.getLogger().warning("타이틀 전송 실패: " + e. getMessage());
        }
    }

    /**
     * 플레이어에게 기본 타이틀 전송
     */
    public void sendTitle(Player player, String title, String subtitle) {
        sendTitle(player, title, subtitle, 10, 70, 20);
    }

    /**
     * 플레이어에게 음소거 전송
     */
    public void playSound(Player player, String sound, float volume, float pitch) {
        if (player == null || sound == null || !soundEnabled) {
            return;
        }

        try {
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (Exception e) {
            Bukkit.getLogger().warning("음소거 재생 실패: " + e.getMessage());
        }
    }

    // ============ Quest Notifications ============

    /**
     * 퀨스트 수락 알림
     */
    public void notifyQuestAccepted(Player player, String questName) {
        if (player == null || questName == null) {
            return;
        }

        sendTitle(player, "§a퀨스트 수락", "§f" + questName);
        sendMessage(player, "§a퀨스트를 수락했습니다: §f" + questName);
        sendActionbar(player, "§a◆ 퀨스트: §f" + questName);
        playSound(player, "block. note_block. pling", 1.0f, 1.0f);
    }

    /**
     * 퀨스트 완료 알림
     */
    public void notifyQuestCompleted(Player player, String questName) {
        if (player == null || questName == null) {
            return;
        }

        sendTitle(player, "§6퀨스트 완료!", "§f" + questName);
        sendMessage(player, "§6✓ 퀨스트를 완료했습니다: §f" + questName);
        sendActionbar(player, "§6◆ 완료: §f" + questName);
        playSound(player, "ui.toast.challenge_complete", 1.0f, 1.0f);
    }

    /**
     * 퀨스트 진행도 알림
     */
    public void notifyQuestProgress(Player player, String questName, int current, int required) {
        if (player == null || questName == null) {
            return;
        }

        String progressBar = createProgressBar(current, required);
        sendActionbar(player, "§7[" + progressBar + "§7] §f" + current + "§7/§f" + required);
        sendMessage(player, "§7진행도: §f" + questName + " §7[" + current + "/" + required + "]");
    }

    /**
     * 퀨스트 포기 알림
     */
    public void notifyQuestAbandoned(Player player, String questName) {
        if (player == null || questName == null) {
            return;
        }

        sendTitle(player, "§c퀨스트 포기", "§f" + questName);
        sendMessage(player, "§c퀨스트를 포기했습니다: §f" + questName);
        playSound(player, "block.note_block.bass", 1.0f, 0.5f);
    }

    /**
     * 퀨스트 실패 알림
     */
    public void notifyQuestFailed(Player player, String questName, String reason) {
        if (player == null || questName == null) {
            return;
        }

        sendTitle(player, "§c퀨스트 실패", "§f" + questName);
        sendMessage(player, "§c퀨스트 실패: §f" + questName);
        if (reason != null && ! reason.isEmpty()) {
            sendMessage(player, "§7사유: §f" + reason);
        }
        playSound(player, "entity.player.hurt", 1.0f, 0.5f);
    }

    // ============ Objective Notifications ============

    /**
     * 목표 시작 알림
     */
    public void notifyObjectiveStarted(Player player, String objectiveName) {
        if (player == null || objectiveName == null) {
            return;
        }

        sendMessage(player, "§7목표: §f" + objectiveName);
    }

    /**
     * 목표 완료 알림
     */
    public void notifyObjectiveCompleted(Player player, String objectiveName) {
        if (player == null || objectiveName == null) {
            return;
        }

        sendMessage(player, "§a✓ 목표 완료: §f" + objectiveName);
        playSound(player, "block.note_block.chime", 1.0f, 1.2f);
    }

    // ============ Reward Notifications ============

    /**
     * 보상 획득 알림
     */
    public void notifyRewardReceived(Player player, String rewardDescription) {
        if (player == null || rewardDescription == null) {
            return;
        }

        sendTitle(player, "§6보상 획득!", "§f" + rewardDescription);
        sendMessage(player, "§6★ 보상을 받았습니다: §f" + rewardDescription);
        sendActionbar(player, "§6★ " + rewardDescription);
        playSound(player, "block.note_block.bell", 1.0f, 1.5f);
    }

    /**
     * 여러 보상 획득 알림
     */
    public void notifyRewardsReceived(Player player, List<String> rewards) {
        if (player == null || rewards == null || rewards.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("§6보상:\n");
        for (String reward : rewards) {
            sb. append("  §f- ").append(reward).append("\n");
        }

        sendMessage(player, sb. toString());
        playSound(player, "block.note_block. bell", 1.0f, 1.5f);
    }

    // ============ Chain Notifications ============

    /**
     * 체인 시작 알림
     */
    public void notifyChainStarted(Player player, String chainName) {
        if (player == null || chainName == null) {
            return;
        }

        sendTitle(player, "§a체인 시작", "§f" + chainName);
        sendMessage(player, "§a퀨스트 체인을 시작했습니다: §f" + chainName);
        playSound(player, "block.note_block. pling", 1.0f, 1.2f);
    }

    /**
     * 체인 완료 알림
     */
    public void notifyChainCompleted(Player player, String chainName) {
        if (player == null || chainName == null) {
            return;
        }

        sendTitle(player, "§6체인 완료!", "§f" + chainName);
        sendMessage(player, "§6✓ 퀨스트 체인을 완료했습니다: §f" + chainName);
        playSound(player, "ui.toast.challenge_complete", 1.0f, 1.0f);
    }

    // ============ Notification Queue ============

    /**
     * 알림 대기열에 추가
     */
    public void queueNotification(UUID playerUUID, Notification notification) {
        if (playerUUID == null || notification == null) {
            return;
        }

        Queue<Notification> queue = playerNotifications
            .computeIfAbsent(playerUUID, k -> new LinkedList<>());
        queue. offer(notification);
    }

    /**
     * 대기 중인 알림 처리
     */
    public void processNotifications(Player player) {
        if (player == null) {
            return;
        }

        UUID playerUUID = player.getUniqueId();
        Queue<Notification> queue = playerNotifications.get(playerUUID);

        if (queue == null || queue.isEmpty()) {
            return;
        }

        Notification notification = queue.poll();
        if (notification != null) {
            notification.send(player);
        }
    }

    /**
     * 모든 온라인 플레이어에게 알림 처리
     */
    public void processAllNotifications() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            processNotifications(player);
        }
    }

    /**
     * 플레이어의 알림 대기열 비우기
     */
    public void clearNotifications(UUID playerUUID) {
        if (playerUUID != null) {
            Queue<Notification> queue = playerNotifications.get(playerUUID);
            if (queue != null) {
                queue.clear();
            }
        }
    }

    // ============ Utility Methods ============

    /**
     * 진행도 바 생성
     */
    private String createProgressBar(int current, int max) {
        if (max <= 0) {
            return "§c████";
        }

        int percentage = (current * 100) / max;
        int filled = (percentage / 10);
        int empty = 10 - filled;

        StringBuilder sb = new StringBuilder();

        // 채워진 부분
        sb.append("§a");
        for (int i = 0; i < filled; i++) {
            sb.append("█");
        }

        // 빈 부분
        sb. append("§7");
        for (int i = 0; i < empty; i++) {
            sb.append("█");
        }

        return sb.toString();
    }

    /**
     * 백분율 표시
     */
    public String formatPercentage(int current, int max) {
        if (max <= 0) {
            return "0%";
        }

        return ((current * 100) / max) + "%";
    }

    // ============ Settings Management ============

    /**
     * 음성 알림 활성화/비활성화
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    /**
     * 타이틀 알림 활성화/비활성화
     */
    public void setTitleEnabled(boolean enabled) {
        this.titleEnabled = enabled;
    }

    /**
     * 채팅 알림 활성화/비활성화
     */
    public void setChatEnabled(boolean enabled) {
        this.chatEnabled = enabled;
    }

    /**
     * 액션바 알림 활성화/비활성화
     */
    public void setActionbarEnabled(boolean enabled) {
        this.actionbarEnabled = enabled;
    }

    /**
     * 모든 알림 활성화
     */
    public void enableAllNotifications() {
        soundEnabled = true;
        titleEnabled = true;
        chatEnabled = true;
        actionbarEnabled = true;
    }

    /**
     * 모든 알림 비활성화
     */
    public void disableAllNotifications() {
        soundEnabled = false;
        titleEnabled = false;
        chatEnabled = false;
        actionbarEnabled = false;
    }

    // ============ Statistics ============

    /**
     * 대기 중인 알림 통계
     */
    public Map<String, Object> getNotificationStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();

        int totalQueued = playerNotifications.values().stream()
            .mapToInt(Queue::size)
            .sum();

        stats.put("playersWithNotifications", playerNotifications.size());
        stats.put("totalQueuedNotifications", totalQueued);
        stats.put("soundEnabled", soundEnabled);
        stats.put("titleEnabled", titleEnabled);
        stats.put("chatEnabled", chatEnabled);
        stats.put("actionbarEnabled", actionbarEnabled);

        return stats;
    }

    // ============ Status Information ============

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== 알림 관리자 상태 ===§r\n");
        sb.append("§7초기화: ").append(initialized ? "§a완료" : "§c미완료").append("\n");
        sb.append("§7사운드: ").append(soundEnabled ? "§a활성화" : "§c비활성화").append("\n");
        sb.append("§7타이틀: ").append(titleEnabled ? "§a활성화" : "§c비활성화").append("\n");
        sb.append("§7채팅: ").append(chatEnabled ? "§a활성화" : "§c비활성화").append("\n");
        sb.append("§7액션바: ").append(actionbarEnabled ? "§a활성화" : "§c비활성화").append("\n");
        sb.append("§7대기 중인 알림: §f").append(playerNotifications. values().stream()
            .mapToInt(Queue::size). sum()). append("\n");

        return sb.toString();
    }

    // ============ Getters & Setters ============

    /**
     * 캐시 정보 반환
     */
    public Map<String, Object> getCacheInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("playersWithNotifications", playerNotifications.size());

        int totalQueued = playerNotifications. values().stream()
            .mapToInt(Queue::size)
            .sum();
        info. put("totalQueuedNotifications", totalQueued);

        return info;
    }

    /**
     * 캐시 초기화
     */
    public void clearCache() {
        playerNotifications.clear();
    }
}

/**
 * 알림 인터페이스
 */
public interface Notification {
    /**
     * 플레이어에게 알림 전송
     */
    void send(Player player);
}