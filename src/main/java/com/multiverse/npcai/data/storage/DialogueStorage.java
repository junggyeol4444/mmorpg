package com.multiverse.npcai.data.storage;

import com.multiverse.npcai.models.DialogueNode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * NPC 대화 트리 YAML 저장/로드
 */
public class DialogueStorage {

    private final File baseDir;

    public DialogueStorage(File baseDir) {
        this.baseDir = new File(baseDir, "dialogues");
        if (!this.baseDir.exists()) this.baseDir.mkdirs();
    }

    public Map<String, DialogueNode> load(int npcId) {
        File file = new File(baseDir, npcId + ".yml");
        if (!file.exists()) return new HashMap<>();
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.getConfigurationSection("dialogue-tree.nodes");
        if (section == null) return new HashMap<>();
        return DialogueNode.fromYAMLTree(section);
    }

    public void save(int npcId, Map<String, DialogueNode> nodes, String startNodeId) {
        File file = new File(baseDir, npcId + ".yml");
        YamlConfiguration yml = new YamlConfiguration();
        yml.set("dialogue-tree.start-node", startNodeId);
        yml.createSection("dialogue-tree.nodes");
        for (Map.Entry<String, DialogueNode> entry : nodes.entrySet()) {
            yml.createSection("dialogue-tree.nodes." + entry.getKey());
            entry.getValue().toYAML(yml.getConfigurationSection("dialogue-tree.nodes." + entry.getKey()));
        }
        try { yml.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
}