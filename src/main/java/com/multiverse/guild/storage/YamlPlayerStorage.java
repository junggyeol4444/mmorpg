package com.multiverse.guild.storage;

import com.multiverse.guild.GuildCore;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class YamlPlayerStorage {

    private final GuildCore plugin;
    private final File dir;

    // 예: 초대 대기, 개인 설정 등을 저장할 때 사용 가능 (간단 예시)
    private final Map<UUID, Long> lastSalaryPaid = new ConcurrentHashMap<>();

    public YamlPlayerStorage(GuildCore plugin) {
        this.plugin = plugin;
        this.dir = new File(plugin.getDataFolder(), "players");
        if (!dir.exists()) dir.mkdirs();
    }

    public long getLastSalaryPaid(UUID playerId) {
        if (lastSalaryPaid.containsKey(playerId)) return lastSalaryPaid.get(playerId);
        File f = new File(dir, playerId.toString() + ".yml");
        if (!f.exists()) return 0;
        YamlConfiguration yc = YamlConfiguration.loadConfiguration(f);
        long v = yc.getLong("player.last-salary-paid", 0);
        lastSalaryPaid.put(playerId, v);
        return v;
    }

    public void setLastSalaryPaid(UUID playerId, long time) {
        lastSalaryPaid.put(playerId, time);
        File f = new File(dir, playerId.toString() + ".yml");
        YamlConfiguration yc = new YamlConfiguration();
        yc.set("player.last-salary-paid", time);
        try { yc.save(f); } catch (IOException e) { e.printStackTrace(); }
    }
}