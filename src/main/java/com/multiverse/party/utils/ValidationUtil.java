package com.multiverse.party.utils;

/**
 * 유효성 검사 유틸리티
 */
public class ValidationUtil {

    /** 문자열이 비어있는지 체크 */
    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** 값이 음수/0인지 체크 */
    public static boolean isPositive(int value) {
        return value > 0;
    }
}