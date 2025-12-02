package com.multiverse.core.utils;

public class ValidationUtil {

    public static boolean isValidDimensionId(String id) {
        if (id == null || id.isEmpty()) return false;
        // ID는 영문자, 숫자, 대시(-), 언더스코어(_)만 허용
        return id.matches("^[a-zA-Z0-9_-]+$");
    }

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        // 이름은 2~32글자, 한글·영문·숫자 허용
        return name.matches("^[ㄱ-ㅎ가-힣a-zA-Z0-9 ]{2,32}$");
    }

    public static boolean isValidLocationString(String locStr) {
        if (locStr == null || locStr.isEmpty()) return false;
        // world;x;y;z;yaw;pitch 형태 체크
        String[] parts = locStr.split(";");
        return parts.length == 6;
    }
}