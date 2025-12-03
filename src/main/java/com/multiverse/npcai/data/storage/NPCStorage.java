package com.multiverse.npcai.data.storage;

import com.multiverse.npcai.models.NPCData;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * NPC 단일/일괄 파일 처리 및 로딩
 * YAMLDataManager에서 사용
 */
public class NPCStorage {

    private final File baseDir;

    public NPCStorage(File baseDir) {
        this.baseDir = new File(baseDir, "npcs");
        if (!this.baseDir.exists()) this.baseDir.mkdirs();
    }

    public NPCData load(int npcId) {
        File file = new File(baseDir, npcId + ".yml");
        if (!file.exists()) return null;
        return NPCData.fromYAML(YamlConfiguration.loadConfiguration(file));
    }

    public void save(NPCData npc) {
        File file = new File(baseDir, npc.getNpcId() + ".yml");
        YamlConfiguration yml = npc.toYAML();
        try { yml.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public void delete(int npcId) {
        File file = new File(baseDir, npcId + ".yml");
        if (file.exists()) file.delete();
    }

    public List<NPCData> loadAll() {
        List<NPCData> result = new ArrayList<>();
        for (File f : Objects.requireNonNull(baseDir.listFiles())) {
            result.add(NPCData.fromYAML(YamlConfiguration.loadConfiguration(f)));
        }
        return result;
    }
}