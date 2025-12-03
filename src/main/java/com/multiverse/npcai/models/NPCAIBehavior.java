package com.multiverse.npcai.models;

import java.util.*;

/**
 * NPC의 반응(리액션) 정보를 담는 클래스
 */
public class NPCReaction {
    private String reactionId;
    private String description;
    private ReactionTrigger trigger;
    private ActionType actionType;
    private Map<String, Object> parameters;

    public NPCReaction(String reactionId, String description, ReactionTrigger trigger, ActionType actionType, Map<String, Object> parameters) {
        this.reactionId = reactionId;
        this.description = description;
        this.trigger = trigger;
        this.actionType = actionType;
        this.parameters = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
    }

    public String getReactionId() { return reactionId; }
    public String getDescription() { return description; }
    public ReactionTrigger getTrigger() { return trigger; }
    public ActionType getActionType() { return actionType; }
    public Map<String, Object> getParameters() { return parameters; }

    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    public Object getParameter(String key) {
        return this.parameters.get(key);
    }

    // Map 직렬화/역직렬화
    public static NPCReaction fromMap(Map<?, ?> map) {
        String reactionId = (String) map.get("reactionId");
        String description = (String) map.get("description");
        ReactionTrigger trigger = ReactionTrigger.valueOf((String) map.get("trigger"));
        ActionType actionType = ActionType.valueOf((String) map.get("actionType"));
        Map<String, Object> parameters = (Map<String, Object>) map.getOrDefault("parameters", new HashMap<>());
        return new NPCReaction(reactionId, description, trigger, actionType, parameters);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("reactionId", reactionId);
        map.put("description", description);
        map.put("trigger", trigger.name());
        map.put("actionType", actionType.name());
        map.put("parameters", parameters);
        return map;
    }
}