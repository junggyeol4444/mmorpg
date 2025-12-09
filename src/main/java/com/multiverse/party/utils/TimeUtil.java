package com.multiverse.party.utils;

/**
 * 시간 관련 유틸리티
 */
public class TimeUtil {

    /** ms → 초 */
    public static long millisToSeconds(long millis) {
        return millis / 1000;
    }

    /** 초 → ms */
    public static long secondsToMillis(long seconds) {
        return seconds * 1000;
    }

    /** 남은 시간 포맷: 1h 23m 10s */
    public static String formatDuration(long millis) {
        long sec = millis / 1000;
        long min = sec / 60;
        long hrs = min / 60;
        sec %= 60;
        min %= 60;
        StringBuilder sb = new StringBuilder();
        if (hrs > 0) sb.append(hrs).append("h ");
        if (min > 0) sb.append(min).append("m ");
        sb.append(sec).append("s");
        return sb.toString().trim();
    }
}