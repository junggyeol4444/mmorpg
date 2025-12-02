package com.multiverse.core.utils;

import java.util.HashMap;
import java.util.Map;

public class MessageUtil {
    private final Map<String, String> messages = new HashMap<>();

    public MessageUtil() {
        // 기본 메시지 등록
        messages.put("teleport.warmup-cancelled", "텔레포트가 취소되었습니다: 움직임 또는 피해를 받았습니다.");
        messages.put("portal.use.success", "포탈 이동에 성공했습니다!");
        messages.put("portal.use.fail", "포탈 이동에 실패했습니다.");
        messages.put("dimension.balance.changed", "해당 차원의 균형도가 변경되었습니다.");
        // ... 추가 메시지 등록
    }

    public String get(String key) {
        return messages.getOrDefault(key, "알 수 없는 메시지 (" + key + ")");
    }

    public void add(String key, String message) {
        messages.put(key, message);
    }

    public boolean contains(String key) {
        return messages.containsKey(key);
    }
}