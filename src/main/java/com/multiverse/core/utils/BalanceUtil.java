package com.multiverse.core.utils;

public class BalanceUtil {

    public static int calculateDelta(int oldValue, int newValue) {
        return newValue - oldValue;
    }

    public static boolean isBalanced(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static int clampBalance(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double calculateBalancePercent(int value, int min, int max) {
        if (max == min) return 0;
        return ((double) (value - min)) / (max - min) * 100.0;
    }
}