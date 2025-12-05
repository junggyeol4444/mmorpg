package com.multiverse.skill.data. storage;

import com.multiverse.skill. SkillCore;
import com.multiverse. skill.data.models.*;
import com.multiverse.skill.data. enums.SkillTreeType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

/**
 * 스킬 트리 데이터 로더
 */
public class SkillTreeLoader {

    private final SkillCore plugin;
    private final DataStorage storage;
    private final Map<String, SkillTree> treeCache;

    public SkillTreeLoader(SkillCore plugin, DataStorage storage) {
        this. plugin = plugin;
        this. storage = storage;
        this. treeCache = new HashMap<>();
    }

    /**
     * 모든 스킬 트리 로드
     */
    public List<SkillTree> loadAllTrees() {
        List<SkillTree> trees = new ArrayList<>();
        File treesFolder = new File(plugin. getDataFolder(), "trees");

        if (! treesFolder.exists()) {
            plugin.getLogger().warning("⚠️ 스킬 트리 폴더가 없습니다: " + treesFolder.getPath());
            return trees;
        }

        File[] treeFiles = treesFolder.listFiles((d, name) -> name.endsWith(".yml"));
        if (treeFiles == null) {
            return trees;
        }

        for (File treeFile : treeFiles) {
            try {
                SkillTree tree = loadTreeFromFile(treeFile);
                if (tree != null) {
                    trees.add(tree);
                    treeCache. put(tree.getTreeId(), tree);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("스킬 트리 로드 실패: " + treeFile.getName());
                e.printStackTrace();
            }
        }

        return trees;
    }

    /**
     * 파일에서 스킬 트리 로드
     */
    private SkillTree loadTreeFromFile(File file) {
        FileConfiguration config = YamlConfiguration. loadConfiguration(file);

        SkillTree tree = new SkillTree();
        tree.setTreeId(config.getString("id", file.getName(). replace(".yml", "")));
        tree.setName(config.getString("name", "Unknown"));
        tree.setDescription(config.getString("description", ""));
        tree.setLore(config.getStringList("lore"));

        // 타입
        String typeString = config.getString("type", "SPECIAL");
        try {
            tree.setType(SkillTreeType.valueOf(typeString));
        } catch (IllegalArgumentException e) {
            tree.setType(SkillTreeType.SPECIAL);
        }

        tree.setMaxPoints(config.getInt("max-points", 100));
        tree.setRequiredLevel(config.getInt("required-level", 1));
        tree.setRequiredClass(config.getString("required-class", ""));

        // 노드 로드
        if (config.contains("nodes")) {
            for (String nodeKey : config.getConfigurationSection("nodes").getKeys(false)) {
                String nodePath = "nodes." + nodeKey;
                SkillNode node = new SkillNode();

                node.setNodeId(nodeKey);
                node.setSkillId(config.getString(nodePath + ".skill-id", ""));
                node.setTier(config.getInt(nodePath + ".tier", 0));
                node.setPosition(config.getInt(nodePath + ".position", 0));
                node.setRequiredPoints(config.getInt(nodePath + ".required-points", 1));
                node.setMaxLevel(config.getInt(nodePath + ".max-level", 10));

                // 선행 조건
                List<String> prerequisites = config.getStringList(nodePath + ".prerequisites");
                node.setPrerequisites(prerequisites);

                tree.addNode(node);
            }
        }

        return tree;
    }

    /**
     * 트리 조회
     */
    public SkillTree getTree(String treeId) {
        return treeCache.getOrDefault(treeId, null);
    }

    /**
     * 캐시 초기화
     */
    public void clearCache() {
        treeCache.clear();
    }
}