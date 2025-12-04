package com.multiverse.dungeon.utils;

import org.bukkit.entity.Player;
import org.bukkit. Bukkit;

/**
 * 메시지 유틸리티
 */
public class MessageUtils {

    /**
     * 플레이어에게 메시지 전송
     *
     * @param player 플레이어
     * @param message 메시지
     */
    public static void sendMessage(Player player, String message) {
        if (player != null && player.isOnline()) {
            player.sendMessage(message);
        }
    }

    /**
     * 플레이어에게 여러 메시지 전송
     *
     * @param player 플레이어
     * @param messages 메시지 배열
     */
    public static void sendMessages(Player player, String...  messages) {
        if (player == null || !  player.isOnline()) {
            return;
        }

        for (String message : messages) {
            player.sendMessage(message);
        }
    }

    /**
     * 플레이어에게 타이틀 전송
     *
     * @param player 플레이어
     * @param title 타이틀
     * @param subtitle 서브타이틀
     * @param fadeIn 페이드인 (틱)
     * @param stay 지속시간 (틱)
     * @param fadeOut 페이드아웃 (틱)
     */
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player == null || ! player.isOnline()) {
            return;
        }

        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    /**
     * 플레이어에게 액션바 메시지 전송
     *
     * @param player 플레이어
     * @param message 메시지
     */
    public static void sendActionBar(Player player, String message) {
        if (player == null || ! player.isOnline()) {
            return;
        }

        player.sendActionBar(message);
    }

    /**
     * 전체 플레이어에게 브로드캐스트
     *
     * @param message 메시지
     */
    public static void broadcast(String message) {
        Bukkit.broadcastMessage(message);
    }

    /**
     * 특정 권한을 가진 플레이어에게만 브로드캐스트
     *
     * @param message 메시지
     * @param permission 권한
     */
    public static void broadcastWithPermission(String message, String permission) {
        Bukkit. broadcast(message, permission);
    }

    /**
     * 콘솔에 메시지 출력
     *
     * @param message 메시지
     */
    public static void console(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    /**
     * 색상 코드 적용
     *
     * @param message 메시지
     * @return 색상이 적용된 메시지
     */
    public static String colorize(String message) {
        if (message == null) {
            return "";
        }
        return message.replace("&", "§");
    }

    /**
     * 여러 메시지에 색상 코드 적용
     *
     * @param messages 메시지 배열
     * @return 색상이 적용된 메시지 배열
     */
    public static String[] colorize(String...  messages) {
        String[] result = new String[messages. length];
        for (int i = 0; i < messages. length; i++) {
            result[i] = colorize(messages[i]);
        }
        return result;
    }

    /**
     * 플레이어에게 성공 메시지 전송
     *
     * @param player 플레이어
     * @param message 메시지
     */
    public static void sendSuccess(Player player, String message) {
        sendMessage(player, "§a✓ " + message);
    }

    /**
     * 플레이어에게 오류 메시지 전송
     *
     * @param player 플레이어
     * @param message 메시지
     */
    public static void sendError(Player player, String message) {
        sendMessage(player, "§c✗ " + message);
    }

    /**
     * 플레이어에게 경고 메시지 전송
     *
     * @param player 플레이어
     * @param message 메시지
     */
    public static void sendWarning(Player player, String message) {
        sendMessage(player, "§e⚠ " + message);
    }

    /**
     * 플레이어에게 정보 메시지 전송
     *
     * @param player 플레이어
     * @param message 메시지
     */
    public static void sendInfo(Player player, String message) {
        sendMessage(player, "§bℹ " + message);
    }

    /**
     * 분할선 전송
     *
     * @param player 플레이어
     */
    public static void sendDivider(Player player) {
        sendMessage(player, "§7" + "=".repeat(50));
    }

    /**
     * 빈 줄 전송
     *
     * @param player 플레이어
     */
    public static void sendBlank(Player player) {
        sendMessage(player, "");
    }

    /**
     * 시간을 MM:SS 형식으로 포맷
     *
     * @param seconds 시간 (초)
     * @return 포맷된 시간
     */
    public static String formatTime(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String. format("%02d:%02d", minutes, remainingSeconds);
    }

    /**
     * 시간을 HH:MM:SS 형식으로 포맷
     *
     * @param seconds 시간 (초)
     * @return 포맷된 시간
     */
    public static String formatTimeHMS(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;
        return String. format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    /**
     * 숫자를 보기 좋게 포맷 (천 단위 구분)
     *
     * @param number 숫자
     * @return 포맷된 숫자
     */
    public static String formatNumber(long number) {
        return String.format("%,d", number);
    }

    /**
     * 실수를 소수점 n자리로 포맷
     *
     * @param number 실수
     * @param decimal 소수점 자리
     * @return 포맷된 실수
     */
    public static String formatDecimal(double number, int decimal) {
        return String.format("%." + decimal + "f", number);
    }

    /**
     * 진행률을 진행바로 표시
     *
     * @param current 현재값
     * @param max 최대값
     * @param barLength 진행바 길이
     * @return 진행바 문자열
     */
    public static String progressBar(int current, int max, int barLength) {
        if (max == 0) {
            return "§8" + "[" + "□".repeat(barLength) + "] 0%";
        }

        double percent = (double) current / max;
        int filled = (int) (barLength * percent);
        int empty = barLength - filled;

        String color;
        if (percent >= 0.75) {
            color = "§a";
        } else if (percent >= 0.5) {
            color = "§e";
        } else if (percent >= 0.25) {
            color = "§6";
        } else {
            color = "§c";
        }

        String bar = color + "■". repeat(filled) + "§8" + "□".repeat(empty);
        int percentValue = (int) (percent * 100);

        return "[" + bar + "§r] " + percentValue + "%";
    }

    /**
     * 간단한 진행바
     *
     * @param current 현재값
     * @param max 최대값
     * @return 진행바 문자열
     */
    public static String simpleProgressBar(int current, int max) {
        return progressBar(current, max, 20);
    }

    /**
     * 리스트를 문자열로 합치기
     *
     * @param list 리스트
     * @param separator 구분자
     * @return 합쳐진 문자열
     */
    public static String joinList(java.util.List<String> list, String separator) {
        return String.join(separator, list);
    }

    /**
     * 배열을 문자열로 합치기
     *
     * @param array 배열
     * @param separator 구분자
     * @return 합쳐진 문자열
     */
    public static String joinArray(String[] array, String separator) {
        return String. join(separator, array);
    }

    /**
     * 문자열을 여러 줄로 감싸기
     *
     * @param text 텍스트
     * @param maxLength 최대 길이
     * @return 줄바꿈된 메시지 배열
     */
    public static String[] wrapText(String text, int maxLength) {
        if (text == null || text.isEmpty()) {
            return new String[]{};
        }

        java.util.List<String> lines = new java.util.ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.  length() + word.length() + 1 <= maxLength) {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                if (currentLine.length() > 0) {
                    lines. add(currentLine.toString());
                }
                currentLine = new StringBuilder(word);
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines.toArray(new String[0]);
    }

    /**
     * 플레이어의 좌표 포맷
     *
     * @param player 플레이어
     * @return 좌표 문자열
     */
    public static String formatPlayerLocation(Player player) {
        if (player == null) {
            return "Unknown";
        }

        var loc = player.getLocation();
        return String.format("X: %. 1f, Y: %.1f, Z: %.1f", loc. getX(), loc.getY(), loc.getZ());
    }
}