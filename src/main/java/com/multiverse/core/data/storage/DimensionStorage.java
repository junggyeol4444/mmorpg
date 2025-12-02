package com.multiverse.core.data.storage;

import com.multiverse.core.models.Dimension;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DimensionStorage {
    private final File file;

    public DimensionStorage(File file) {
        this.file = file;
    }

    public void save(List<Dimension> dimensions) throws IOException {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Dimension dim : dimensions) {
            String key = dim.getId();
            yaml.set("dimensions." + key + ".name", dim.getName());
            yaml.set("dimensions." + key + ".world-name", dim.getWorldName());
            yaml.set("dimensions." + key + ".type", dim.getType().toString());
            yaml.set("dimensions." + key + ".balance", dim.getBalanceValue());
            yaml.set("dimensions." + key + ".time-multiplier", dim.getTimeMultiplier());
            yaml.set("dimensions." + key + ".active", dim.isActive());
            yaml.set("dimensions." + key + ".level-requirement", dim.getLevelRequirement());
            yaml.set("dimensions." + key + ".quest-requirement", dim.getQuestRequirement());
            yaml.set("dimensions." + key + ".connected-dimensions", dim.getConnectedDimensions());
        }
        yaml.save(file);
    }

    public List<Dimension> load() {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<Dimension> list = new ArrayList<>();
        if (yaml.contains("dimensions")) {
            for (String key : yaml.getConfigurationSection("dimensions").getKeys(false)) {
                String name = yaml.getString("dimensions." + key + ".name");
                String worldName = yaml.getString("dimensions." + key + ".world-name");
                String typeStr = yaml.getString("dimensions." + key + ".type", "MAIN");
                int balance = yaml.getInt("dimensions." + key + ".balance", 50);
                double multiplier = yaml.getDouble("dimensions." + key + ".time-multiplier", 1.0);
                boolean active = yaml.getBoolean("dimensions." + key + ".active", true);
                int levelReq = yaml.getInt("dimensions." + key + ".level-requirement", 0);
                String questReq = yaml.getString("dimensions." + key + ".quest-requirement");
                List<String> connects = yaml.getStringList("dimensions." + key + ".connected-dimensions");
                Dimension dim = new Dimension(key, name, worldName,
                        com.multiverse.core.models.enums.DimensionType.valueOf(typeStr),
                        balance, multiplier, active, levelReq, questReq, connects);
                list.add(dim);
            }
        }
        return list;
    }
}