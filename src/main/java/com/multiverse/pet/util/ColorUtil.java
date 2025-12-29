package com.multiverse.pet.         util;

import org.bukkit.         Color;
import org.bukkit.         DyeColor;

import java.util.          HashMap;
import java.  util.          Map;
import java.util.           Random;

/**
 * 색상 유틸리티
 * 색상 관련 유틸리티
 */
public class ColorUtil {

    private static final Random random = new Random();

    // 마인크래프트 기본 색상 코드
    private static final Map<Character, String> COLOR_CODES = new HashMap<>();
    private static final Map<String, String> COLOR_NAMES = new HashMap<>();

    static {
        // 색상 코드
        COLOR_CODES.put('0', "000000"); // 검정
        COLOR_CODES.put('1', "0000AA"); // 어두운 파랑
        COLOR_CODES.put('2', "00AA00"); // 어두운 초록
        COLOR_CODES.put('3', "00AAAA"); // 어두운 청록
        COLOR_CODES.put('4', "AA0000"); // 어두운 빨강
        COLOR_CODES.put('5', "AA00AA"); // 어두운 자주
        COLOR_CODES.put('6', "FFAA00"); // 금색
        COLOR_CODES.put('7', "AAAAAA"); // 회색
        COLOR_CODES.put('8', "555555"); // 어두운 회색
        COLOR_CODES.put('9', "5555FF"); // 파랑
        COLOR_CODES.put('a', "55FF55"); // 초록
        COLOR_CODES.put('b', "55FFFF"); // 청록
        COLOR_CODES.put('c', "FF5555"); // 빨강
        COLOR_CODES.put('d', "FF55FF"); // 분홍
        COLOR_CODES.put('e', "FFFF55"); // 노랑
        COLOR_CODES.put('f', "FFFFFF"); // 흰색

        // 색상 이름
        COLOR_NAMES.put("black", "§0");
        COLOR_NAMES.put("dark_blue", "§1");
        COLOR_NAMES.put("dark_green", "§2");
        COLOR_NAMES. put("dark_aqua", "§3");
        COLOR_NAMES.put("dark_red", "§4");
        COLOR_NAMES. put("dark_purple", "§5");
        COLOR_NAMES.put("gold", "§6");
        COLOR_NAMES.put("gray", "§7");
        COLOR_NAMES.put("dark_gray", "§8");
        COLOR_NAMES.put("blue", "§9");
        COLOR_NAMES.put("green", "§a");
        COLOR_NAMES.put("aqua", "§b");
        COLOR_NAMES.put("red", "§c");
        COLOR_NAMES.put("light_purple", "§d");
        COLOR_NAMES.put("yellow", "§e");
        COLOR_NAMES.put("white", "§f");
    }

    /**
     * HEX 문자열을 Color로 변환
     */
    public static Color hexToColor(String hex) {
        if (hex == null) return Color.WHITE;

        hex = hex.replace("#", "").replace("&", "");

        try {
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer. parseInt(hex.substring(4, 6), 16);
            return Color.fromRGB(r, g, b);
        } catch (Exception e) {
            return Color.WHITE;
        }
    }

    /**
     * Color를 HEX 문자열로 변환
     */
    public static String colorToHex(Color color) {
        return String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * RGB를 Color로 변환
     */
    public static Color fromRGB(int r, int g, int b) {
        r = Math.max(0, Math. min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math. max(0, Math.min(255, b));
        return Color.fromRGB(r, g, b);
    }

    /**
     * 마인크래프트 색상 코드를 HEX로 변환
     */
    public static String mcColorToHex(char colorCode) {
        return COLOR_CODES.getOrDefault(Character.toLowerCase(colorCode), "FFFFFF");
    }

    /**
     * 색상 이름을 색상 코드로 변환
     */
    public static String nameToCode(String colorName) {
        return COLOR_NAMES.getOrDefault(colorName.toLowerCase(), "§f");
    }

    /**
     * 두 색상 보간 (그라디언트)
     */
    public static Color interpolate(Color color1, Color color2, double ratio) {
        ratio = Math.max(0, Math. min(1, ratio));

        int r = (int) (color1.getRed() + ratio * (color2.getRed() - color1.getRed()));
        int g = (int) (color1.getGreen() + ratio * (color2.getGreen() - color1.getGreen()));
        int b = (int) (color1.getBlue() + ratio * (color2.getBlue() - color1.getBlue()));

        return fromRGB(r, g, b);
    }

    /**
     * 그라디언트 색상 배열 생성
     */
    public static Color[] createGradient(Color start, Color end, int steps) {
        Color[] gradient = new Color[steps];

        for (int i = 0; i < steps; i++) {
            double ratio = (double) i / (steps - 1);
            gradient[i] = interpolate(start, end, ratio);
        }

        return gradient;
    }

    /**
     * 무지개 색상 배열 생성
     */
    public static Color[] createRainbow(int steps) {
        Color[] rainbow = new Color[steps];

        for (int i = 0; i < steps; i++) {
            float hue = (float) i / steps;
            int rgb = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
            rainbow[i] = Color.fromRGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
        }

        return rainbow;
    }

    /**
     * 랜덤 색상
     */
    public static Color randomColor() {
        return Color.fromRGB(
            random.nextInt(256),
            random.nextInt(256),
            random. nextInt(256)
        );
    }

    /**
     * 랜덤 밝은 색상
     */
    public static Color randomBrightColor() {
        return Color.fromRGB(
            128 + random.nextInt(128),
            128 + random.nextInt(128),
            128 + random.nextInt(128)
        );
    }

    /**
     * 랜덤 어두운 색상
     */
    public static Color randomDarkColor() {
        return Color.fromRGB(
            random.nextInt(128),
            random.nextInt(128),
            random. nextInt(128)
        );
    }

    /**
     * 색상 밝기 조절
     */
    public static Color adjustBrightness(Color color, double factor) {
        int r = (int) Math.min(255, color.getRed() * factor);
        int g = (int) Math.min(255, color.getGreen() * factor);
        int b = (int) Math.min(255, color.getBlue() * factor);
        return fromRGB(r, g, b);
    }

    /**
     * 색상 반전
     */
    public static Color invert(Color color) {
        return Color.fromRGB(
            255 - color.getRed(),
            255 - color.getGreen(),
            255 - color.getBlue()
        );
    }

    /**
     * 색상 혼합
     */
    public static Color mix(Color color1, Color color2) {
        return interpolate(color1, color2, 0.5);
    }

    /**
     * 색상 혼합 (가중치)
     */
    public static Color mix(Color color1, Color color2, double weight1) {
        return interpolate(color2, color1, weight1);
    }

    /**
     * DyeColor를 Color로 변환
     */
    public static Color dyeToColor(DyeColor dyeColor) {
        return dyeColor.getColor();
    }

    /**
     * Color를 가장 가까운 DyeColor로 변환
     */
    public static DyeColor colorToDye(Color color) {
        DyeColor closest = DyeColor.WHITE;
        double closestDistance = Double. MAX_VALUE;

        for (DyeColor dye :  DyeColor. values()) {
            Color dyeColor = dye.getColor();
            double distance = colorDistance(color, dyeColor);

            if (distance < closestDistance) {
                closestDistance = distance;
                closest = dye;
            }
        }

        return closest;
    }

    /**
     * 두 색상 간의 거리 (유사도)
     */
    public static double colorDistance(Color c1, Color c2) {
        int dr = c1.getRed() - c2.getRed();
        int dg = c1.getGreen() - c2.getGreen();
        int db = c1.getBlue() - c2.getBlue();
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }

    /**
     * 채도 조절
     */
    public static Color adjustSaturation(Color color, double factor) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        double gray = 0.299 * r + 0.587 * g + 0.114 * b;

        r = (int) Math.min(255, gray + factor * (r - gray));
        g = (int) Math.min(255, gray + factor * (g - gray));
        b = (int) Math.min(255, gray + factor * (b - gray));

        return fromRGB(Math.max(0, r), Math.max(0, g), Math.max(0, b));
    }

    /**
     * 희귀도별 색상
     */
    public static Color getRarityColor(String rarity) {
        switch (rarity.toUpperCase()) {
            case "COMMON":
                return Color.WHITE;
            case "UNCOMMON":
                return Color.LIME;
            case "RARE":
                return Color.AQUA;
            case "EPIC": 
                return Color. PURPLE;
            case "LEGENDARY":
                return Color.ORANGE;
            case "MYTHIC": 
                return Color. RED;
            default:
                return Color. GRAY;
        }
    }

    /**
     * 희귀도별 색상 코드
     */
    public static String getRarityColorCode(String rarity) {
        switch (rarity.toUpperCase()) {
            case "COMMON":
                return "§f";
            case "UNCOMMON": 
                return "§a";
            case "RARE":
                return "§b";
            case "EPIC":
                return "§5";
            case "LEGENDARY":
                return "§6";
            case "MYTHIC":
                return "§c";
            default: 
                return "§7";
        }
    }
}