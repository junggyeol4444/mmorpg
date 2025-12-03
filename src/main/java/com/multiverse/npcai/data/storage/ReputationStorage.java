package com.multiverse.npcai.data.storage;

import com.multiverse.npcai.models.Reputation;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 플레이어별 호감도(YAML) 개별 파일 저장
 */
public class ReputationStorage {

    private final File baseDir;

    public ReputationStorage(File baseDir) {
        this.baseDir = new File(baseDir, "players");
        if (!this.baseDir.exists()) this.baseDir.mkdirs();
    }

    public Reputation load(UUID playerUUID, int npcId) {
        File file = new File(baseDir, playerUUID + ".yml");
        if (!file.exists()) return null;
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        String path = "reputations." + npcId;
        if (!yml.contains(path)) return null;
        return Reputation.fromYAML(yml.getConfigurationSection(path));
    }

    public void save(Reputation rep) {
        File file = new File(baseDir, rep.getPlayerUUID() + ".yml");
        YamlConfiguration yml = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
        String path = "reputations." + rep.getNpcId();
        rep.toYAML(yml.createSection(path));
        try { yml.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public List<Reputation> loadAll(UUID playerUUID) {
        File file = new File(baseDir, playerUUID + ".yml");
        if (!file.exists()) return new ArrayList<>();
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        List<Reputation> result = new ArrayList<>();
        if (!yml.contains("reputations")) return result;
        for (String key : yml.getConfigurationSection("reputations").getKeys(false)) {
            result.add(Reputation.fromYAML(yml.getConfigurationSection("reputations." + key)));
        }
        return result;
    }
}