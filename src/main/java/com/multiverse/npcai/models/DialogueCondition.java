package com.multiverse.npcai.models;

import com.multiverse.npcai.models.enums.ConditionType;
import java.util.*;

/**
 * 대화 조건 객체 (분기, 특정 상황, 퀘스트, 호감도 등)
 */
public class DialogueCondition {
    private ConditionType type;
    private String value;

    public DialogueCondition(ConditionType type, String value) {
        this.type = type;
        this.value = value;
    }

    public ConditionType getType() { return type; }
    public String getValue() { return value; }

    public void setType(ConditionType type) { this.type = type; }
    public void setValue(String value) { this.value = value; }

    // 매핑 기반 직렬화/역직렬화
    public static DialogueCondition fromMap(Map<?, ?> map) {
        ConditionType type = ConditionType.valueOf((String) map.get("type"));
        String value = (String) map.get("value");
        return new DialogueCondition(type, value);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type.name());
        map.put("value", value);
        return map;
    }
}