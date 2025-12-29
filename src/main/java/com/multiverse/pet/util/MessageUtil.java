package com. multiverse.  pet. util;

import net.md_5.bungee.api.ChatMessageType;
import net. md_5.bungee.api. chat.TextComponent;
import org.bukkit. Bukkit;
import org.bukkit.   command.CommandSender;
import org.  bukkit.entity.  Player;

import java.util.List;
import java.util. regex. Matcher;
import java.util.regex. Pattern;

/**
 * 메시지 유틸리티
 * 메시지 포맷팅 및 전송
 */
public class MessageUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<gradient:([A-Fa-f0-9]{6}):([A-Fa-f0-9]{6})>(.*? )</gradient>");

    private static String prefix = "§6[Pet] §f";

    /**
     * 프리픽스 설정
     */
    public static void setPrefix(String newPrefix) {
        prefix = colorize(newPrefix) + " ";
    }

    /**
     * 프리픽스 가져오기
     */
    public static String getPrefix() {
        return prefix;
    }

    // ===== 메시지 전송 =====

    /**
     * 메시지 전송 (프리픽스 포함)
     */
    public static void sendMessage(CommandSender sender, String message) {
        if (sender == null || message == null || message.isEmpty()) {
            return;
        }
        sender.sendMessage(prefix + colorize(message));
    }

    /**
     * 메시지 전송 (프리픽스 없음)
     */
    public static void sendRawMessage(CommandSender sender, String message) {
        if (sender == null || message == null || message. isEmpty()) {
            return;
        }
        sender.sendMessage(colorize(message));
    }

    /**
     * 여러 줄 메시지 전송
     */
    public static void sendMessages(CommandSender sender, List<String> messages) {
        if (sender == null || messages == null) {
            return;
        }
        for (String message :  messages) {
            sendRawMessage(sender, message);
        }
    }

    /**
     * 여러 줄 메시지 전송 (배열)
     */
    public static void sendMessages(CommandSender sender, String...  messages) {
        if (sender == null || messages == null) {
            return;
        }
        for (String message :  messages) {
            sendRawMessage(sender, message);
        }
    }

    /**
     * 액션바 메시지 전송
     */
    public static void sendActionBar(Player player, String message) {
        if (player == null || message == null) {
            return;
        }
        player.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                new TextComponent(colorize(message))
        );
    }

    /**
     * 타이틀 전송
     */
    public static void sendTitle(Player player, String title, String subtitle) {
        sendTitle(player, title, subtitle, 10, 40, 10);
    }

    /**
     * 타이틀 전송 (시간 지정)
     */
    public static void sendTitle(Player player, String title, String subtitle, 
                                  int fadeIn, int stay, int fadeOut) {
        if (player == null) {
            return;
        }
        player.sendTitle(
                title != null ? colorize(title) : "",
                subtitle != null ?   colorize(subtitle) : "",
                fadeIn, stay, fadeOut
        );
    }

    /**
     * 전체 브로드캐스트
     */
    public static void broadcast(String message) {
        Bukkit.broadcastMessage(prefix + colorize(message));
    }

    /**
     * 전체 브로드캐스트 (프리픽스 없음)
     */
    public static void broadcastRaw(String message) {
        Bukkit. broadcastMessage(colorize(message));
    }

    /**
     * 권한 기반 브로드캐스트
     */
    public static void broadcastPermission(String message, String permission) {
        String formatted = prefix + colorize(message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission)) {
                player. sendMessage(formatted);
            }
        }
    }

    // ===== 색상 처리 =====

    /**
     * 색상 코드 변환
     */
    public static String colorize(String message) {
        if (message == null) {
            return "";
        }

        // HEX 색상 처리
        message = translateHexColors(message);

        // 그라디언트 처리
        message = translateGradient(message);

        // 기본 색상 코드 처리
        message = message.replace("&", "§");

        return message;
    }

    /**
     * HEX 색상 변환 (&#RRGGBB)
     */
    private static String translateHexColors(String message) {
        Matcher matcher = HEX_PATTERN. matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher. find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append("§").append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * 그라디언트 변환
     */
    private static String translateGradient(String message) {
        Matcher matcher = GRADIENT_PATTERN. matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher. find()) {
            String startHex = matcher.group(1);
            String endHex = matcher.group(2);
            String text = matcher.group(3);

            String gradientText = applyGradient(text, startHex, endHex);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(gradientText));
        }

        matcher.appendTail(buffer);
        return buffer. toString();
    }

    /**
     * 그라디언트 적용
     */
    private static String applyGradient(String text, String startHex, String endHex) {
        int length = text.length();
        if (length == 0) {
            return text;
        }

        int[] startRGB = hexToRGB(startHex);
        int[] endRGB = hexToRGB(endHex);

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            double ratio = (double) i / (length - 1);

            int r = (int) (startRGB[0] + ratio * (endRGB[0] - startRGB[0]));
            int g = (int) (startRGB[1] + ratio * (endRGB[1] - startRGB[1]));
            int b = (int) (startRGB[2] + ratio * (endRGB[2] - startRGB[2]));

            String hex = String.format("%02x%02x%02x", r, g, b);
            result.append("&#").append(hex).append(text.charAt(i));
        }

        return translateHexColors(result.toString());
    }

    /**
     * HEX to RGB
     */
    private static int[] hexToRGB(String hex) {
        return new int[]{
                Integer.parseInt(hex.substring(0, 2), 16),
                Integer.parseInt(hex.substring(2, 4), 16),
                Integer. parseInt(hex.substring(4, 6), 16)
        };
    }

    /**
     * 색상 코드 제거
     */
    public static String stripColor(String message) {
        if (message == null) {
            return "";
        }
        return message.replaceAll("§[0-9a-fk-orA-FK-OR]", "")
                .replaceAll("&#[A-Fa-f0-9]{6}", "")
                .replaceAll("§x(§[A-Fa-f0-9]){6}", "");
    }

    // ===== 포맷팅 =====

    /**
     * 진행률 바 생성
     */
    public static String createProgressBar(double percent, int length, 
                                            String filledChar, String emptyChar,
                                            String filledColor, String emptyColor) {
        int filled = (int) (percent / 100 * length);
        StringBuilder bar = new StringBuilder();

        bar.append(filledColor);
        for (int i = 0; i < filled; i++) {
            bar.append(filledChar);
        }

        bar.append(emptyColor);
        for (int i = filled; i < length; i++) {
            bar.append(emptyChar);
        }

        return bar.toString();
    }

    /**
     * 기본 진행률 바
     */
    public static String createProgressBar(double percent) {
        return createProgressBar(percent, 10, "█", "░", "§a", "§7");
    }

    /**
     * 시간 포맷팅
     */
    public static String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return String.format("%d일 %d시간", days, hours % 24);
        } else if (hours > 0) {
            return String.format("%d시간 %d분", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d분 %d초", minutes, seconds % 60);
        } else {
            return String.format("%d초", seconds);
        }
    }

    /**
     * 숫자 포맷팅 (천 단위 구분)
     */
    public static String formatNumber(double number) {
        if (number >= 1000000000) {
            return String.  format("%.1fB", number / 1000000000);
        } else if (number >= 1000000) {
            return String.format("%.1fM", number / 1000000);
        } else if (number >= 1000) {
            return String.  format("%.1fK", number / 1000);
        } else {
            return String.format("%.0f", number);
        }
    }

    /**
     * 중앙 정렬
     */
    public static String centerMessage(String message, int lineLength) {
        String stripped = stripColor(message);
        int spaces = (lineLength - stripped.length()) / 2;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            builder. append(" ");
        }
        builder.  append(message);
        return builder.  toString();
    }
}