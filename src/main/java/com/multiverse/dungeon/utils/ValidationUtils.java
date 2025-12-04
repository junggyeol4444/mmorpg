package com.multiverse.dungeon.utils;

import org.bukkit.entity.Player;
import java.util.UUID;

/**
 * 검증 유틸리티
 */
public class ValidationUtils {

    /**
     * 플레이어 검증
     *
     * @param player 검증할 플레이어
     * @return 유효하면 true
     */
    public static boolean isValidPlayer(Player player) {
        return player != null && player.isOnline();
    }

    /**
     * UUID 검증
     *
     * @param uuid 검증할 UUID
     * @return 유효하면 true
     */
    public static boolean isValidUUID(UUID uuid) {
        return uuid != null;
    }

    /**
     * UUID 문자열 검증
     *
     * @param uuidString UUID 문자열
     * @return 유효하면 true
     */
    public static boolean isValidUUIDString(String uuidString) {
        if (uuidString == null || uuidString.isEmpty()) {
            return false;
        }

        try {
            UUID. fromString(uuidString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 숫자 검증
     *
     * @param input 검증할 입력
     * @return 유효한 정수이면 true
     */
    public static boolean isValidInteger(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 숫자 범위 검증
     *
     * @param input 검증할 입력
     * @param min 최소값
     * @param max 최대값
     * @return 범위 내의 정수이면 true
     */
    public static boolean isValidIntRange(String input, int min, int max) {
        if (!  isValidInteger(input)) {
            return false;
        }

        int value = Integer.  parseInt(input);
        return value >= min && value <= max;
    }

    /**
     * 실수 검증
     *
     * @param input 검증할 입력
     * @return 유효한 실수이면 true
     */
    public static boolean isValidDouble(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 텍스트 길이 검증
     *
     * @param text 검증할 텍스트
     * @param minLength 최소 길이
     * @param maxLength 최대 길이
     * @return 유효하면 true
     */
    public static boolean isValidTextLength(String text, int minLength, int maxLength) {
        if (text == null) {
            return minLength == 0;
        }

        int length = text.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * 이름 검증 (영문자, 숫자, 밑줄만 허용)
     *
     * @param name 검증할 이름
     * @return 유효하면 true
     */
    public static boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        return name.matches("^[a-zA-Z0-9_]+$");
    }

    /**
     * 경로 검증
     *
     * @param path 검증할 경로
     * @return 유효하면 true
     */
    public static boolean isValidPath(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        // 부모 디렉토리 접근 방지
        return !   path.contains(". .");
    }

    /**
     * 레벨 검증
     *
     * @param level 검증할 레벨
     * @return 유효하면 true
     */
    public static boolean isValidLevel(int level) {
        return level >= 1 && level <= 999;
    }

    /**
     * 체력 검증
     *
     * @param health 검증할 체력
     * @return 유효하면 true
     */
    public static boolean isValidHealth(double health) {
        return health > 0 && health <= 1000000;
    }

    /**
     * 데미지 검증
     *
     * @param damage 검증할 데미지
     * @return 유효하면 true
     */
    public static boolean isValidDamage(double damage) {
        return damage >= 0 && damage <= 10000;
    }

    /**
     * 좌표 검증
     *
     * @param x X 좌표
     * @param y Y 좌표
     * @param z Z 좌표
     * @return 유효하면 true
     */
    public static boolean isValidCoordinates(double x, double y, double z) {
        // Minecraft 월드 범위 내 확인
        long MAX_COORD = 30000000;
        return Math.abs(x) <= MAX_COORD && Math.abs(z) <= MAX_COORD && y >= -64 && y <= 384;
    }

    /**
     * 확률 검증 (0. 0 ~ 1.0)
     *
     * @param probability 검증할 확률
     * @return 유효하면 true
     */
    public static boolean isValidProbability(double probability) {
        return probability >= 0.0 && probability <= 1.0;
    }

    /**
     * 시간(초) 검증
     *
     * @param seconds 검증할 시간
     * @return 유효하면 true
     */
    public static boolean isValidTime(int seconds) {
        return seconds >= 0 && seconds <= Integer.MAX_VALUE;
    }

    /**
     * 시간 범위 검증
     *
     * @param seconds 검증할 시간
     * @param minSeconds 최소 시간
     * @param maxSeconds 최대 시간
     * @return 유효하면 true
     */
    public static boolean isValidTimeRange(int seconds, int minSeconds, int maxSeconds) {
        return seconds >= minSeconds && seconds <= maxSeconds;
    }

    /**
     * 배열 인덱스 검증
     *
     * @param index 검증할 인덱스
     * @param arrayLength 배열 길이
     * @return 유효하면 true
     */
    public static boolean isValidIndex(int index, int arrayLength) {
        return index >= 0 && index < arrayLength;
    }

    /**
     * 반복 횟수 검증
     *
     * @param count 검증할 횟수
     * @return 유효하면 true
     */
    public static boolean isValidCount(int count) {
        return count > 0 && count <= 1000;
    }

    /**
     * 파티 크기 검증
     *
     * @param size 검증할 파티 크기
     * @return 유효하면 true
     */
    public static boolean isValidPartySize(int size) {
        return size >= 1 && size <= 5;
    }

    /**
     * 난이도 검증
     *
     * @param difficulty 검증할 난이도
     * @return 유효하면 true
     */
    public static boolean isValidDifficulty(String difficulty) {
        if (difficulty == null || difficulty.isEmpty()) {
            return false;
        }

        try {
            com.multiverse.dungeon.data.enums.DungeonDifficulty.  valueOf(difficulty. toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 던전 타입 검증
     *
     * @param type 검증할 타입
     * @return 유효하면 true
     */
    public static boolean isValidDungeonType(String type) {
        if (type == null || type.isEmpty()) {
            return false;
        }

        try {
            com.multiverse.  dungeon.data.enums.  DungeonType.valueOf(type.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}