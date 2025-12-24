package com.multiverse. pvp.utils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java. time. Instant;
import java.time.LocalDateTime;
import java. time.ZoneId;
import java. time.format.DateTimeFormatter;
import java.util. Date;
import java.util. concurrent.TimeUnit;

public class TimeUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH: mm:ss");
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 밀리초를 읽기 쉬운 형식으로 변환
     */
    public static String formatDuration(long millis) {
        if (millis < 0) {
            return "0초";
        }

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        StringBuilder sb = new StringBuilder();

        if (days > 0) {
            sb.append(days).append("일 ");
        }
        if (hours > 0) {
            sb.append(hours).append("시간 ");
        }
        if (minutes > 0) {
            sb. append(minutes).append("분 ");
        }
        if (seconds > 0 || sb.length() == 0) {
            sb.append(seconds).append("초");
        }

        return sb.toString().trim();
    }

    /**
     * 초를 읽기 쉬운 형식으로 변환
     */
    public static String formatSeconds(long seconds) {
        return formatDuration(seconds * 1000);
    }

    /**
     * 짧은 형식으로 시간 변환 (00:00:00)
     */
    public static String formatDurationShort(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds %= 60;
        minutes %= 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    /**
     * 초를 짧은 형식으로 변환
     */
    public static String formatSecondsShort(long seconds) {
        return formatDurationShort(seconds * 1000);
    }

    /**
     * 타임스탬프를 날짜 문자열로 변환
     */
    public static String formatTimestamp(long timestamp) {
        LocalDateTime dateTime = LocalDateTime. ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault()
        );
        return dateTime. format(DATETIME_FORMATTER);
    }

    /**
     * 타임스탬프를 날짜만 변환
     */
    public static String formatDate(long timestamp) {
        LocalDateTime dateTime = LocalDateTime. ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault()
        );
        return dateTime.format(DATE_FORMATTER);
    }

    /**
     * 상대적 시간 표시 (방금, 1분 전, 1시간 전 등)
     */
    public static String getRelativeTime(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < 0) {
            return "방금";
        }

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;
        long months = days / 30;
        long years = days / 365;

        if (seconds < 60) {
            return "방금";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else if (days < 7) {
            return days + "일 전";
        } else if (weeks < 4) {
            return weeks + "주 전";
        } else if (months < 12) {
            return months + "개월 전";
        } else {
            return years + "년 전";
        }
    }

    /**
     * 남은 시간 표시
     */
    public static String getRemainingTime(long endTimestamp) {
        long now = System. currentTimeMillis();
        long remaining = endTimestamp - now;

        if (remaining <= 0) {
            return "종료됨";
        }

        return formatDuration(remaining);
    }

    /**
     * 남은 시간 표시 (짧은 형식)
     */
    public static String getRemainingTimeShort(long endTimestamp) {
        long now = System.currentTimeMillis();
        long remaining = endTimestamp - now;

        if (remaining <= 0) {
            return "00:00";
        }

        return formatDurationShort(remaining);
    }

    /**
     * 문자열 시간을 밀리초로 변환 (1d, 2h, 30m, 45s 형식)
     */
    public static long parseTimeString(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return 0;
        }

        timeString = timeString. toLowerCase().trim();

        long totalMillis = 0;
        StringBuilder number = new StringBuilder();

        for (char c : timeString. toCharArray()) {
            if (Character.isDigit(c)) {
                number.append(c);
            } else if (number.length() > 0) {
                long value = Long.parseLong(number.toString());
                number = new StringBuilder();

                switch (c) {
                    case 'd':
                        totalMillis += TimeUnit.DAYS. toMillis(value);
                        break;
                    case 'h': 
                        totalMillis += TimeUnit. HOURS.toMillis(value);
                        break;
                    case 'm': 
                        totalMillis += TimeUnit. MINUTES.toMillis(value);
                        break;
                    case 's':
                        totalMillis += TimeUnit.SECONDS.toMillis(value);
                        break;
                }
            }
        }

        return totalMillis;
    }

    /**
     * 문자열 시간을 초로 변환
     */
    public static long parseTimeStringToSeconds(String timeString) {
        return parseTimeString(timeString) / 1000;
    }

    /**
     * 현재 시간 타임스탬프
     */
    public static long now() {
        return System.currentTimeMillis();
    }

    /**
     * 오늘 자정 타임스탬프
     */
    public static long getTodayMidnight() {
        LocalDateTime midnight = LocalDateTime.now()
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        return midnight.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 이번 주 시작 타임스탬프
     */
    public static long getWeekStart() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekStart = now
                .minusDays(now.getDayOfWeek().getValue() - 1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        return weekStart.atZone(ZoneId. systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 이번 달 시작 타임스탬프
     */
    public static long getMonthStart() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        return monthStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 시간이 지났는지 확인
     */
    public static boolean hasPassed(long timestamp) {
        return System.currentTimeMillis() > timestamp;
    }

    /**
     * 두 타임스탬프 사이의 시간 차이 (밀리초)
     */
    public static long getDifference(long timestamp1, long timestamp2) {
        return Math.abs(timestamp1 - timestamp2);
    }

    /**
     * 쿨다운 체크
     */
    public static boolean isOnCooldown(long lastUsed, long cooldownMillis) {
        return System.currentTimeMillis() - lastUsed < cooldownMillis;
    }

    /**
     * 쿨다운 남은 시간
     */
    public static long getCooldownRemaining(long lastUsed, long cooldownMillis) {
        long elapsed = System.currentTimeMillis() - lastUsed;
        return Math.max(0, cooldownMillis - elapsed);
    }

    /**
     * 쿨다운 남은 시간 (포맷팅)
     */
    public static String getCooldownRemainingFormatted(long lastUsed, long cooldownMillis) {
        long remaining = getCooldownRemaining(lastUsed, cooldownMillis);
        return formatDuration(remaining);
    }

    /**
     * 플레이 시간 포맷팅 (시간: 분)
     */
    public static String formatPlayTime(long millis) {
        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis / (1000 * 60)) % 60;

        return hours + "시간 " + minutes + "분";
    }
}