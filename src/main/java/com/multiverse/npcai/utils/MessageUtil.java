package com.multiverse.npcai.utils;

/**
 * 메시지 처리를 위한 유틸리티 클래스
 */
public class MessageUtil {

    public static String prefix = "§7[§bNPC§fAI§7]§r ";

    public static String format(String message) {
        // 플레이스홀더 적용 등 필요한 포맷이 있다면 여기에 추가
        return prefix + message;
    }

    public static void sendMessage(String message) {
        // 실제 게임/서버 환경에서는 플레이어 객체에 메시지 전달
        System.out.println(format(message));
    }
}