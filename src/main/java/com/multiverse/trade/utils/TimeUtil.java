package com.multiverse. trade.utils;

import java.text.SimpleDateFormat;
import java.  util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("MM/dd HH:mm");

    public static String formatDate(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }

    public static String formatShortDate(long timestamp) {
        return SHORT_DATE_FORMAT.format(new Date(timestamp));
    }

    public static String formatDuration(long millis) {
        if (millis <= 0) {
            return "만료됨";
        }
        
        long days = TimeUnit. MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
        long minutes = TimeUnit.MILLISECONDS. toMinutes(millis) % 60;
        long seconds = TimeUnit. MILLISECONDS.toSeconds(millis) % 60;
        
        StringBuilder result = new StringBuilder();
        
        if (days > 0) {
            result.append(days).append("일 ");
        }
        if (hours > 0) {
            result.append(hours).append("시간 ");
        }
        if (minutes > 0) {
            result.append(minutes).append("분 ");
        }
        if (days == 0 && hours == 0 && seconds > 0) {
            result.append(seconds).append("초");
        }
        
        return result.toString().trim();
    }

    public static String formatRelative(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        
        if (diff < 0) {
            return formatDuration(-diff) + " 후";
        }
        
        long seconds = TimeUnit. MILLISECONDS.toSeconds(diff);
        long minutes = TimeUnit. MILLISECONDS.toMinutes(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        long days = TimeUnit. MILLISECONDS.toDays(diff);
        
        if (seconds < 60) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else if (days < 7) {
            return days + "일 전";
        } else if (days < 30) {
            return (days / 7) + "주 전";
        } else if (days < 365) {
            return (days / 30) + "개월 전";
        } else {
            return (days / 365) + "년 전";
        }
    }

    public static long parseDuration(String input) {
        if (input == null || input.isEmpty()) {
            return -1;
        }
        
        input = input.toLowerCase().trim();
        long multiplier = 1;
        
        if (input.endsWith("s") || input.endsWith("초")) {
            multiplier = 1000;
            input = input.replaceAll("[s초]", "");
        } else if (input.endsWith("m") || input.endsWith("분")) {
            multiplier = 60 * 1000;
            input = input. replaceAll("[m분]", "");
        } else if (input.endsWith("h") || input.endsWith("시간")) {
            multiplier = 60 * 60 * 1000;
            input = input.replaceAll("[h시간]", "");
        } else if (input. endsWith("d") || input.endsWith("일")) {
            multiplier = 24 * 60 * 60 * 1000;
            input = input.replaceAll("[d일]", "");
        }
        
        try {
            return Long.parseLong(input. trim()) * multiplier;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static boolean isExpired(long expiryTime) {
        return System.currentTimeMillis() >= expiryTime;
    }

    public static long getTimeUntil(long timestamp) {
        return timestamp - System.currentTimeMillis();
    }

    public static long getTimeSince(long timestamp) {
        return System.currentTimeMillis() - timestamp;
    }

    public static long hoursToMillis(int hours) {
        return hours * 60L * 60L * 1000L;
    }

    public static long daysToMillis(int days) {
        return days * 24L * 60L * 60L * 1000L;
    }

    public static long minutesToMillis(int minutes) {
        return minutes * 60L * 1000L;
    }
}