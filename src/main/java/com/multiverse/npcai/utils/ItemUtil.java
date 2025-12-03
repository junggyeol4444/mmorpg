package com.multiverse.npcai.utils;

/**
 * 아이템 관련 유틸리티 기능을 제공하는 클래스
 */
public class ItemUtil {

    public static boolean isValidItem(String itemId) {
        // 실제 환경에서는 아이템 데이터베이스 또는 플러그인 연동 필요
        return itemId != null && !itemId.isBlank();
    }

    public static String getDisplayName(String itemId) {
        // 실제로는 아이템의 이름을 데이터에서 조회해야 함
        return "아이템(" + itemId + ")";
    }
}