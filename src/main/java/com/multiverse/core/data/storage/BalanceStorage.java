package com.multiverse.core.data.storage;

import com.multiverse.core.models.BalanceLog;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BalanceStorage {
    private final File file;

    public BalanceStorage(File file) {
        this.file = file;
    }

    public void save(List<BalanceLog> logs) throws IOException {
        YamlConfiguration yaml = new YamlConfiguration();
        int idx = 0;
        for (BalanceLog log : logs) {
            yaml.set("logs." + idx + ".dimension", log.getDimension());
            yaml.set("logs." + idx + ".old-value", log.getOldValue());
            yaml.set("logs." + idx + ".new-value", log.getNewValue());
            yaml.set("logs." + idx + ".delta", log.getDelta());
            yaml.set("logs." + idx + ".reason", log.getReason());
            yaml.set("logs." + idx + ".changed-by", log.getChangedBy());
            yaml.set("logs." + idx + ".timestamp", log.getTimestamp());
            idx++;
        }
        yaml.save(file);
    }

    public List<BalanceLog> load(int limit) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<BalanceLog> logs = new ArrayList<>();
        if (yaml.contains("logs")) {
            for (String key : yaml.getConfigurationSection("logs").getKeys(false)) {
                String dim = yaml.getString("logs." + key + ".dimension");
                int oldVal = yaml.getInt("logs." + key + ".old-value");
                int newVal = yaml.getInt("logs." + key + ".new-value");
                int delta = yaml.getInt("logs." + key + ".delta");
                String reason = yaml.getString("logs." + key + ".reason");
                String changer = yaml.getString("logs." + key + ".changed-by");
                long ts = yaml.getLong("logs." + key + ".timestamp");
                logs.add(new BalanceLog(dim, oldVal, newVal, delta, reason, changer, ts));
            }
        }
        return logs.size() > limit ? logs.subList(logs.size() - limit, logs.size()) : logs;
    }
}