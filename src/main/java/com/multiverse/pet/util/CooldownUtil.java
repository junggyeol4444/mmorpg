package com.multiverse.pet.       util;

import java.util.       Map;
import java. util.       UUID;
import java. util.       concurrent. ConcurrentHashMap;

/**
 * 쿨다운 유틸리티
 * 다양한 쿨다운 관리
 */
public class CooldownUtil {

    // 쿨다운 저장소
    private static final Map<String, Map<UUID, Long>> cooldowns = new ConcurrentHashMap<>();

    /**
     * 쿨다운 설정
     */
    public static void setCooldown(String type, UUID id, long durationMillis) {
        cooldowns.computeIfAbsent(type, k -> new ConcurrentHashMap<>())
                . put(id, System.currentTimeMillis() + durationMillis);
    }

    /**
     * 쿨다운 설정 (초 단위)
     */
    public static void setCooldownSeconds(String type, UUID id, int seconds) {
        setCooldown(type, id, seconds * 1000L);
    }

    /**
     * 쿨다운 설정 (분 단위)
     */
    public static void setCooldownMinutes(String type, UUID id, int minutes) {
        setCooldown(type, id, minutes * 60 * 1000L);
    }

    /**
     * 쿨다운 중인지 확인
     */
    public static boolean isOnCooldown(String type, UUID id) {
        Map<UUID, Long> typeCooldowns = cooldowns.get(type);
        if (typeCooldowns == null) return false;

        Long endTime = typeCooldowns.get(id);
        if (endTime == null) return false;

        if (System.currentTimeMillis() >= endTime) {
            typeCooldowns.remove(id);
            return false;
        }

        return true;
    }

    /**
     * 남은 쿨다운 시간 (밀리초)
     */
    public static long getRemainingCooldown(String type, UUID id) {
        Map<UUID, Long> typeCooldowns = cooldowns.get(type);
        if (typeCooldowns == null) return 0;

        Long endTime = typeCooldowns.get(id);
        if (endTime == null) return 0;

        long remaining = endTime - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    /**
     * 남은 쿨다운 시간 (초)
     */
    public static int getRemainingCooldownSeconds(String type, UUID id) {
        return (int) (getRemainingCooldown(type, id) / 1000);
    }

    /**
     * 쿨다운 제거
     */
    public static void removeCooldown(String type, UUID id) {
        Map<UUID, Long> typeCooldowns = cooldowns.get(type);
        if (typeCooldowns != null) {
            typeCooldowns. remove(id);
        }
    }

    /**
     * 특정 타입의 모든 쿨다운 제거
     */
    public static void clearCooldownType(String type) {
        cooldowns. remove(type);
    }

    /**
     * 특정 ID의 모든 쿨다운 제거
     */
    public static void clearAllCooldowns(UUID id) {
        for (Map<UUID, Long> typeCooldowns : cooldowns.values()) {
            typeCooldowns.remove(id);
        }
    }

    /**
     * 모든 쿨다운 제거
     */
    public static void clearAll() {
        cooldowns.clear();
    }

    /**
     * 만료된 쿨다운 정리
     */
    public static void cleanupExpired() {
        long now = System.currentTimeMillis();

        for (Map<UUID, Long> typeCooldowns : cooldowns.values()) {
            typeCooldowns.entrySet().removeIf(entry -> entry.getValue() < now);
        }
    }

    /**
     * 쿨다운 연장
     */
    public static void extendCooldown(String type, UUID id, long additionalMillis) {
        Map<UUID, Long> typeCooldowns = cooldowns.get(type);
        if (typeCooldowns == null) return;

        Long endTime = typeCooldowns.get(id);
        if (endTime != null) {
            typeCooldowns.put(id, endTime + additionalMillis);
        }
    }

    /**
     * 쿨다운 단축
     */
    public static void reduceCooldown(String type, UUID id, long reduceMillis) {
        Map<UUID, Long> typeCooldowns = cooldowns.get(type);
        if (typeCooldowns == null) return;

        Long endTime = typeCooldowns.get(id);
        if (endTime != null) {
            long newEndTime = endTime - reduceMillis;
            if (newEndTime <= System.currentTimeMillis()) {
                typeCooldowns.remove(id);
            } else {
                typeCooldowns. put(id, newEndTime);
            }
        }
    }

    /**
     * 쿨다운 진행률 (0.0 ~ 1.0)
     */
    public static double getCooldownProgress(String type, UUID id, long totalDuration) {
        long remaining = getRemainingCooldown(type, id);
        if (remaining <= 0) return 1.0;
        if (totalDuration <= 0) return 0.0;

        return 1.0 - ((double) remaining / totalDuration);
    }

    /**
     * 포맷된 남은 시간
     */
    public static String getFormattedRemaining(String type, UUID id) {
        long remaining = getRemainingCooldown(type, id);
        return formatDuration(remaining);
    }

    /**
     * 시간 포맷팅
     */
    public static String formatDuration(long millis) {
        if (millis <= 0) return "0초";

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return String. format("%d일 %d시간", days, hours % 24);
        } else if (hours > 0) {
            return String.format("%d시간 %d분", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d분 %d초", minutes, seconds % 60);
        } else {
            return String.format("%d초", seconds);
        }
    }

    // ===== 펫 관련 쿨다운 헬퍼 =====

    /**
     * 스킬 쿨다운 키
     */
    public static String getSkillCooldownKey(UUID petId, String skillId) {
        return "skill:" + petId. toString() + ":" + skillId;
    }

    /**
     * 스킬 쿨다운 설정
     */
    public static void setSkillCooldown(UUID petId, String skillId, int cooldownSeconds) {
        String key = getSkillCooldownKey(petId, skillId);
        setCooldownSeconds(key, petId, cooldownSeconds);
    }

    /**
     * 스킬 쿨다운 확인
     */
    public static boolean isSkillOnCooldown(UUID petId, String skillId) {
        String key = getSkillCooldownKey(petId, skillId);
        return isOnCooldown(key, petId);
    }

    /**
     * 스킬 남은 쿨다운 (초)
     */
    public static int getSkillRemainingCooldown(UUID petId, String skillId) {
        String key = getSkillCooldownKey(petId, skillId);
        return getRemainingCooldownSeconds(key, petId);
    }

    /**
     * 소환 쿨다운 설정
     */
    public static void setSummonCooldown(UUID playerId, int cooldownSeconds) {
        setCooldownSeconds("summon", playerId, cooldownSeconds);
    }

    /**
     * 소환 쿨다운 확인
     */
    public static boolean isSummonOnCooldown(UUID playerId) {
        return isOnCooldown("summon", playerId);
    }

    /**
     * 교배 쿨다운 설정
     */
    public static void setBreedingCooldown(UUID petId, int cooldownMinutes) {
        setCooldownMinutes("breeding", petId, cooldownMinutes);
    }

    /**
     * 교배 쿨다운 확인
     */
    public static boolean isBreedingOnCooldown(UUID petId) {
        return isOnCooldown("breeding", petId);
    }

    /**
     * 교배 남은 쿨다운 (분)
     */
    public static int getBreedingRemainingMinutes(UUID petId) {
        return (int) (getRemainingCooldown("breeding", petId) / 60000);
    }

    /**
     * 배틀 쿨다운 설정
     */
    public static void setBattleCooldown(UUID playerId, int cooldownSeconds) {
        setCooldownSeconds("battle", playerId, cooldownSeconds);
    }

    /**
     * 배틀 쿨다운 확인
     */
    public static boolean isBattleOnCooldown(UUID playerId) {
        return isOnCooldown("battle", playerId);
    }
}