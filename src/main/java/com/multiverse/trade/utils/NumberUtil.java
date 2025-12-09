package com.multiverse. trade.utils;

import java.text. DecimalFormat;
import java.text.NumberFormat;
import java.util. Locale;

public class NumberUtil {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.##");
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0");
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#0.0%");

    public static String format(double value) {
        return CURRENCY_FORMAT.format(value);
    }

    public static String formatDecimal(double value) {
        return DECIMAL_FORMAT.format(value);
    }

    public static String formatPercent(double value) {
        return PERCENT_FORMAT.format(value / 100.0);
    }

    public static String formatCompact(double value) {
        if (value < 1000) {
            return format(value);
        } else if (value < 1000000) {
            return DECIMAL_FORMAT.format(value / 1000) + "K";
        } else if (value < 1000000000) {
            return DECIMAL_FORMAT.format(value / 1000000) + "M";
        } else {
            return DECIMAL_FORMAT.format(value / 1000000000) + "B";
        }
    }

    public static double parseDouble(String input, double defaultValue) {
        if (input == null || input.isEmpty()) {
            return defaultValue;
        }
        
        try {
            String cleaned = input.replaceAll("[^0-9.\\-]", "");
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static int parseInt(String input, int defaultValue) {
        if (input == null || input. isEmpty()) {
            return defaultValue;
        }
        
        try {
            String cleaned = input.replaceAll("[^0-9\\-]", "");
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static long parseLong(String input, long defaultValue) {
        if (input == null || input.isEmpty()) {
            return defaultValue;
        }
        
        try {
            String cleaned = input.replaceAll("[^0-9\\-]", "");
            return Long. parseLong(cleaned);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean isNumeric(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        
        try {
            Double. parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInteger(String input) {
        if (input == null || input. isEmpty()) {
            return false;
        }
        
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double round(double value, int decimals) {
        double multiplier = Math. pow(10, decimals);
        return Math.round(value * multiplier) / multiplier;
    }

    public static String formatWithSign(double value) {
        if (value > 0) {
            return "+" + format(value);
        } else if (value < 0) {
            return format(value);
        } else {
            return "0";
        }
    }

    public static String formatChange(double oldValue, double newValue) {
        double change = newValue - oldValue;
        double percent = oldValue != 0 ? (change / oldValue) * 100 :  0;
        
        String changeStr = formatWithSign(change);
        String percentStr = formatWithSign(round(percent, 1)) + "%";
        
        return changeStr + " (" + percentStr + ")";
    }
}