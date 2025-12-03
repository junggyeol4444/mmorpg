package com.multiverse.npcai.models;

import com.multiverse.npcai.models.enums.ActionType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * 대화 이벤트/액션(예: 상점 열기, 퀘스트 진행)
 */
public class DialogueAction {
    private ActionType type;
    private Map<String, Object> params = new HashMap<>();

    public DialogueAction(ActionType type) {
        this.type = type;
    }

    public DialogueAction(ActionType type, Map<String, Object> params) {
        this.type = type;
        this.params = params;
    }

    public ActionType getType() { return type; }
    public Map<String, Object> getParams() { return params; }

    public void setType(ActionType type) { this.type = type; }
    public void setParams(Map<String, Object> params) { this.params = params; }

    // --- YAML 직렬화/역직렬화 ---
    public static DialogueAction fromYAML(ConfigurationSection yml) {
        ActionType type = ActionType.valueOf(yml.getString("type"));
        Map<String, Object> params = new HashMap<>();
        if (yml.isConfigurationSection("params")) {
            ConfigurationSection paramSec = yml.getConfigurationSection("params");
            for (String key : paramSec.getKeys(false)) {
                params.put(key, paramSec.get(key));
            }
        }
        return new DialogueAction(type, params);
    }

    public void toYAML(ConfigurationSection yml) {
        yml.set("type", type.name());
        yml.createSection("params");
        ConfigurationSection paramSec = yml.getConfigurationSection("params");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            paramSec.set(entry.getKey(), entry.getValue());
        }
    }
}