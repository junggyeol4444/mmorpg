package com.multiverse.core.data.storage;

import com.multiverse.core.models.PlayerDimensionData;
import com.multiverse.core.models.DimensionVisitData;
import com.multiverse.core.models.WarmupData;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerStorage {
    private final File file;

    public PlayerStorage(File file) {
        this.file = file;
    }

    public void save(PlayerDimensionData data) throws IOException {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("player.uuid", data.getUuid().toString());
        yaml.set("player.name", data.getName());

        for (Map.Entry<String, DimensionVisitData> entry : data.getDimensionData().entrySet()) {
            String dim = entry.getKey();
            DimensionVisitData dv = entry.getValue();
            yaml.set("dimensions." + dim + ".last-visit", dv.getLastVisit());
            yaml.set("dimensions." + dim + ".visit-count", dv.getVisitCount());
            yaml.set("dimensions." + dim + ".discovered-regions", dv.getDiscoveredRegions());
        }
        WarmupData warmup = data.getWarmup();
        if (warmup != null) {
            yaml.set("warmup.active", warmup.isActive());
            yaml.set("warmup.destination", warmup.getDestination());
            yaml.set("warmup.start-time", warmup.getStartTime());
        }
        yaml.save(file);
    }

    public PlayerDimensionData load(UUID uuid) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        String name = yaml.getString("player.name", "");
        PlayerDimensionData data = new PlayerDimensionData(uuid, name);

        if (yaml.contains("dimensions")) {
            for (String dim : yaml.getConfigurationSection("dimensions").getKeys(false)) {
                long lastVisit = yaml.getLong("dimensions." + dim + ".last-visit", 0L);
                int visitCount = yaml.getInt("dimensions." + dim + ".visit-count", 0);
                List<String> regions = yaml.getStringList("dimensions." + dim + ".discovered-regions");
                DimensionVisitData dv = new DimensionVisitData(lastVisit, visitCount, regions);
                data.getDimensionData().put(dim, dv);
            }
        }

        if (yaml.contains("warmup")) {
            boolean active = yaml.getBoolean("warmup.active", false);
            String dest = yaml.getString("warmup.destination", null);
            long startTime = yaml.getLong("warmup.start-time", 0L);
            WarmupData wd = new WarmupData(active, dest, startTime);
            data.setWarmup(wd);
        }
        return data;
    }
}