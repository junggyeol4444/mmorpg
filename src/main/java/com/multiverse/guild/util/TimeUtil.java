package com.multiverse.guild.util;

import java.time.Duration;

public final class TimeUtil {
    private TimeUtil() {}

    public static String formatDurationSeconds(long seconds) {
        Duration d = Duration.ofSeconds(seconds);
        long h = d.toHours();
        long m = d.toMinutesPart();
        long s = d.toSecondsPart();
        if (h > 0) return String.format("%dh %dm %ds", h, m, s);
        if (m > 0) return String.format("%dm %ds", m, s);
        return String.format("%ds", s);
    }
}