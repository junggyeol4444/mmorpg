package com.multiverse.quest.utils;

import org.bukkit. entity.Player;
import org.bukkit.  Bukkit;
import java.util.*;

/**
 * 메시지 유틸리티
 * 플레이어에게 다양한 형식의 메시지를 전송합니다.
 */
public class MessageUtil {
    private static final String PREFIX = "§6【 QuestCore 】§r";
    private static final String ERROR_PREFIX = "§c【 오류 】§r";
    private static final String SUCCESS_PREFIX = "§a【 성공 】§r";
    private static final String INFO_PREFIX = "§b【 정보 】§r";
    private static final String WARNING_PREFIX = "§e【 경고 】§r";

    // ============ Chat Messages ============

    /**
     * 일반 메시지 전송
     */
    public static void sendMessage(Player player, String message) {
        if (player != null) {
            player.sendMessage(PREFIX + " §f" + message);
        }
    }

    /**
     * 오류 메시지 전송
     */
    public static void sendError(Player player, String message) {
        if (player != null) {
            player.sendMessage(ERROR_PREFIX + " §f" + message);
        }
    }

    /**
     * 성공 메시지 전송
     */
    public static void sendSuccess(Player player, String message) {
        if (player != null) {
            player.sendMessage(SUCCESS_PREFIX + " §f" + message);
        }
    }

    /**
     * 정보 메시지 전송
     */
    public static void sendInfo(Player player, String message) {
        if (player != null) {
            player. sendMessage(INFO_PREFIX + " §f" + message);
        }
    }

    /**
     * 경고 메시지 전송
     */
    public static void sendWarning(Player player, String message) {
        if (player != null) {
            player.sendMessage(WARNING_PREFIX + " §f" + message);
        }
    }

    /**
     * 구분선 전송
     */
    public static void sendSeparator(Player player) {
        if (player != null) {
            player.sendMessage("§6════════════════════════════════════════════════════════════════§r");
        }
    }

    // ============ Action Bar Messages ============

    /**
     * 액션바 메시지 전송
     */
    public static void sendActionBar(Player player, String message) {
        if (player != null) {
            player.sendActionBar(message);
        }
    }

    /**
     * 진행도 바 액션바 전송
     */
    public static void sendProgressBar(Player player, int current, int total) {
        if (player != null) {
            double percentage = (current * 100.0) / total;
            int filledBars = (int) (percentage / 10);
            
            StringBuilder bar = new StringBuilder();
            bar.append("§7[");
            
            for (int i = 0; i < 10; i++) {
                if (i < filledBars) {
                    bar.append("§a█");
                } else {
                    bar.append("§7█");
                }
            }
            
            bar.append("§7] §f"). append(current).append("/").append(total);
            
            player.sendActionBar(bar.toString());
        }
    }

    // ============ Title Messages ============

    /**
     * 제목 메시지 전송
     */
    public static void sendTitle(Player player, String title, String subtitle) {
        if (player != null) {
            player.sendTitle(title, subtitle, 10, 60, 10);
        }
    }

    /**
     * 제목 메시지 전송 (시간 지정)
     */
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player != null) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    /**
     * 퀨스트 완료 제목 전송
     */
    public static void sendQuestCompleteTitle(Player player, String questName) {
        if (player != null) {
            sendTitle(player, "§a퀨스트 완료!", "§f" + questName, 10, 80, 10);
        }
    }

    /**
     * 퀨스트 실패 제목 전송
     */
    public static void sendQuestFailTitle(Player player, String questName) {
        if (player != null) {
            sendTitle(player, "§c퀨스트 실패", "§f" + questName, 10, 80, 10);
        }
    }

    // ============ Broadcast Messages ============

    /**
     * 모든 플레이어에게 메시지 전송
     */
    public static void broadcast(String message) {
        Bukkit.broadcastMessage(PREFIX + " §f" + message);
    }

    /**
     * 모든 플레이어에게 성공 메시지 전송
     */
    public static void broadcastSuccess(String message) {
        Bukkit.broadcastMessage(SUCCESS_PREFIX + " §f" + message);
    }

    /**
     * 모든 플레이어에게 오류 메시지 전송
     */
    public static void broadcastError(String message) {
        Bukkit.broadcastMessage(ERROR_PREFIX + " §f" + message);
    }

    /**
     * 특정 권한을 가진 플레이어에게만 메시지 전송
     */
    public static void broadcastToPermission(String permission, String message) {
        Bukkit.broadcast(PREFIX + " §f" + message, permission);
    }

    // ============ Format Messages ============

    /**
     * 퀨스트 정보 포맷
     */
    public static String formatQuestInfo(String questName, String description, String difficulty, int requiredLevel) {
        return "§6【 " + questName + " 】§r\n" +
               "§7설명: §f" + description + "\n" +
               "§7난이도: " + formatDifficulty(difficulty) + "\n" +
               "§7필요 레벨: §f" + requiredLevel;
    }

    /**
     * 난이도 포맷
     */
    public static String formatDifficulty(String difficulty) {
        if (difficulty == null) return "§7보통";

        return switch (difficulty. toLowerCase()) {
            case "easy" -> "§a쉬움";
            case "normal" -> "§7보통";
            case "hard" -> "§c어려움";
            case "very_hard" -> "§4매우 어려움";
            default -> "§7" + difficulty;
        };
    }

    /**
     * 보상 정보 포맷
     */
    public static String formatReward(String type, long amount, String itemName) {
        return switch (type.toLowerCase()) {
            case "experience" -> "경험치 §f" + amount;
            case "money" -> "$§f" + amount;
            case "item" -> itemName + " §f×" + amount;
            case "command" -> "명령어 실행";
            default -> type;
        };
    }

    /**
     * 시간 포맷 (초를 시간:분:초로)
     */
    public static String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) {
            return String.format("%d시간 %d분 %d초", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%d분 %d초", minutes, secs);
        } else {
            return String.format("%d초", secs);
        }
    }

    /**
     * 시간 포맷 (일:시간:분:초)
     */
    public static String formatLongTime(long seconds) {
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (days > 0) {
            return String.format("%d일 %d시간 %d분", days, hours, minutes);
        } else if (hours > 0) {
            return String.format("%d시간 %d분 %d초", hours, minutes, secs);
        } else {
            return String.format("%d분 %d초", minutes, secs);
        }
    }

    /**
     * 색상 코드 변환
     */
    public static String colorize(String message) {
        return message.replace("&", "§");
    }

    /**
     * 색상 코드 제거
     */
    public static String decolorize(String message) {
        return message.replaceAll("§[0-9a-fA-F]", "");
    }

    // ============ List Messages ============

    /**
     * 리스트 메시지 전송
     */
    public static void sendList(Player player, String title, List<String> items) {
        if (player == null) return;

        sendSeparator(player);
        player.sendMessage("§6" + title);
        sendSeparator(player);

        for (String item : items) {
            player.sendMessage("§f- " + item);
        }

        sendSeparator(player);
    }

    /**
     * 페이지 정보 포맷
     */
    public static String formatPageInfo(int currentPage, int totalPages) {
        return "§7[§f" + currentPage + "§7/§f" + totalPages + "§7]";
    }

    // ============ Status Messages ============

    /**
     * 온라인 플레이어 수 반환
     */
    public static int getOnlinePlayerCount() {
        return Bukkit.getOnlinePlayers().size();
    }

    /**
     * 서버 상태 메시지
     */
    public static String getServerStatus() {
        int onlineCount = getOnlinePlayerCount();
        int maxPlayers = Bukkit.getMaxPlayers();

        return "§7현재 플레이어: §f" + onlineCount + "§7/§f" + maxPlayers;
    }

    // ============ Getters ============

    /**
     * 프리픽스 반환
     */
    public static String getPrefix() {
        return PREFIX;
    }

    /**
     * 오류 프리픽스 반환
     */
    public static String getErrorPrefix() {
        return ERROR_PREFIX;
    }

    /**
     * 성공 프리픽스 반환
     */
    public static String getSuccessPrefix() {
        return SUCCESS_PREFIX;
    }
}