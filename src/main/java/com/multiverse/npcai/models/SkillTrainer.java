package com.multiverse.npcai.models;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

/**
 * NPC 스킬 트레이너 데이터 모델
 */
public class SkillTrainer {
    private int npcId;
    private List<SkillTrainingEntry> teachableSkills = new ArrayList<>();

    public SkillTrainer(int npcId) {
        this.npcId = npcId;
    }

    public int getNpcId() { return npcId; }
    public List<SkillTrainingEntry> getTeachableSkills() { return teachableSkills; }
    public void setTeachableSkills(List<SkillTrainingEntry> skills) { this.teachableSkills = skills; }

    // === YAML 직렬화/역직렬화 ===
    public static SkillTrainer fromYAML(YamlConfiguration yml) {
        int npcId = yml.getInt("npcId");
        SkillTrainer trainer = new SkillTrainer(npcId);
        List<Map<?, ?>> trainList = yml.getMapList("teachableSkills");
        List<SkillTrainingEntry> entries = new ArrayList<>();
        for (Map<?, ?> m : trainList) {
            entries.add(SkillTrainingEntry.fromMap(m));
        }
        trainer.setTeachableSkills(entries);
        return trainer;
    }

    public YamlConfiguration toYAML() {
        YamlConfiguration yml = new YamlConfiguration();
        yml.set("npcId", npcId);
        if (!teachableSkills.isEmpty()) {
            List<Map<String, Object>> entryList = new ArrayList<>();
            for (SkillTrainingEntry entry : teachableSkills) entryList.add(entry.toMap());
            yml.set("teachableSkills", entryList);
        }
        return yml;
    }
}