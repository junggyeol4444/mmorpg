package com.multiverse.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {

    public static String formatUnixTime(long unixTimeMillis, String pattern) {
        Date date = new Date(unixTimeMillis);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return sdf.format(date);
    }

    public static long currentUnixTimeMillis() {
        return System.currentTimeMillis();
    }

    public static String timeAgo(long unixTimeMillis) {
        long diffMillis = System.currentTimeMillis() - unixTimeMillis;
        long seconds = diffMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + "일 전";
        if (hours > 0) return hours + "시간 전";
        if (minutes > 0) return minutes + "분 전";
        return seconds + "초 전";
    }
}