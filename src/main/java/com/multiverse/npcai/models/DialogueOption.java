package com.multiverse.npcai.models;

import java.util.*;

/**
 * 대화 선택지
 */
public class DialogueOption {
    private String text;
    private String nextNodeId;
    private List<DialogueCondition> conditions = new ArrayList<>();

    public DialogueOption(String text, String nextNodeId) {
        this.text = text;
        this.nextNodeId = nextNodeId;
    }

    public String getText() { return text; }
    public String getNextNodeId() { return nextNodeId; }
    public List<DialogueCondition> getConditions() { return conditions; }

    public void setConditions(List<DialogueCondition> conditions) { this.conditions = conditions; }

    // 직렬화/역직렬화 Map 기반
    public static DialogueOption fromMap(Map<?, ?> map) {
        String text = (String) map.get("text");
        String nextNodeId = (String) map.get("nextNodeId");
        DialogueOption option = new DialogueOption(text, nextNodeId);
        if (map.containsKey("conditions")) {
            List<Map<?, ?>> condsRaw = (List<Map<?, ?>>) map.get("conditions");
            List<DialogueCondition> conds = new ArrayList<>();
            for (Map<?, ?> m : condsRaw) {
                conds.add(DialogueCondition.fromMap(m));
            }
            option.setConditions(conds);
        }
        return option;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("text", text);
        map.put("nextNodeId", nextNodeId);
        if (!conditions.isEmpty()) {
            List<Map<String, Object>> condsList = new ArrayList<>();
            for (DialogueCondition c : conditions) condsList.add(c.toMap());
            map.put("conditions", condsList);
        }
        return map;
    }
}