package com.multiverse.npcai.models;

import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * NPC 대화 트리의 하나의 노드 (문/이벤트/분기)
 */
public class DialogueNode {
    private String nodeId;
    private String text;
    private List<DialogueOption> options = new ArrayList<>();
    private List<DialogueCondition> conditions = new ArrayList<>();
    private DialogueAction action;

    public DialogueNode(String nodeId, String text) {
        this.nodeId = nodeId;
        this.text = text;
    }

    public String getNodeId() { return nodeId; }
    public String getText() { return text; }
    public List<DialogueOption> getOptions() { return options; }
    public List<DialogueCondition> getConditions() { return conditions; }
    public DialogueAction getAction() { return action; }

    public void setOptions(List<DialogueOption> options) { this.options = options; }
    public void setConditions(List<DialogueCondition> conditions) { this.conditions = conditions; }
    public void setAction(DialogueAction action) { this.action = action; }

    // === YAML 직렬화/역직렬화 ===
    public static DialogueNode fromYAML(ConfigurationSection yml) {
        DialogueNode node = new DialogueNode(
            yml.getName(),
            yml.getString("text", "")
        );
        // options
        if (yml.isList("options")) {
            List<Map<?, ?>> optsRaw = yml.getMapList("options");
            List<DialogueOption> opts = new ArrayList<>();
            for (Map<?, ?> m : optsRaw) {
                opts.add(DialogueOption.fromMap(m));
            }
            node.setOptions(opts);
        }
        // conditions
        if (yml.isList("conditions")) {
            List<Map<?, ?>> condsRaw = yml.getMapList("conditions");
            List<DialogueCondition> conds = new ArrayList<>();
            for (Map<?, ?> m : condsRaw) {
                conds.add(DialogueCondition.fromMap(m));
            }
            node.setConditions(conds);
        }
        // action
        if (yml.contains("action")) {
            node.setAction(DialogueAction.fromYAML(yml.getConfigurationSection("action")));
        }
        return node;
    }

    public void toYAML(ConfigurationSection yml) {
        yml.set("text", text);
        if (!options.isEmpty()) {
            List<Map<String, Object>> opts = new ArrayList<>();
            for (DialogueOption o : options) opts.add(o.toMap());
            yml.set("options", opts);
        }
        if (!conditions.isEmpty()) {
            List<Map<String, Object>> conds = new ArrayList<>();
            for (DialogueCondition c : conditions) conds.add(c.toMap());
            yml.set("conditions", conds);
        }
        if (action != null) {
            yml.createSection("action");
            action.toYAML(yml.getConfigurationSection("action"));
        }
    }

    public static Map<String, DialogueNode> fromYAMLTree(ConfigurationSection section) {
        Map<String, DialogueNode> tree = new HashMap<>();
        if (section == null) return tree;
        for (String key : section.getKeys(false)) {
            tree.put(key, fromYAML(section.getConfigurationSection(key)));
        }
        return tree;
    }
}