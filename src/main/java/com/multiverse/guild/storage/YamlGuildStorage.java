package com.multiverse.guild.storage;

import com.multiverse.guild.GuildCore;
import com.multiverse.guild.model.*;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class YamlGuildStorage {

    private final GuildCore plugin;
    private final File dir;

    public YamlGuildStorage(GuildCore plugin) {
        this.plugin = plugin;
        this.dir = new File(plugin.getDataFolder(), "guilds");
        if (!dir.exists()) dir.mkdirs();
    }

    public Map<UUID, Guild> loadAll() {
        Map<UUID, Guild> map = new ConcurrentHashMap<>();
        File[] files = dir.listFiles((d, name) -> name.endsWith(".yml"));
        if (files == null) return map;
        for (File f : files) {
            YamlConfiguration yc = YamlConfiguration.loadConfiguration(f);
            Guild g = fromConfig(yc);
            if (g != null) map.put(g.getGuildId(), g);
        }
        return map;
    }

    public void save(Guild guild) {
        File f = new File(dir, guild.getGuildId().toString() + ".yml");
        YamlConfiguration yc = new YamlConfiguration();
        toConfig(guild, yc);
        try { yc.save(f); } catch (IOException e) { e.printStackTrace(); }
    }

    public void delete(UUID guildId) {
        File f = new File(dir, guildId.toString() + ".yml");
        if (f.exists()) f.delete();
    }

    /* ==== MAPPING ==== */
    private Guild fromConfig(YamlConfiguration c) {
        String idStr = c.getString("guild.guild-id");
        if (idStr == null) return null;
        UUID gid = UUID.fromString(idStr);
        String name = c.getString("guild.guild-name", "Unnamed");
        String tag = c.getString("guild.guild-tag", "");
        UUID masterId = UUID.fromString(c.getString("guild.master-id", gid.toString()));
        Guild g = new Guild(gid, name, tag, masterId);
        g.setDescription(c.getString("guild.description", ""));
        g.setAnnouncement(c.getString("guild.announcement", ""));
        g.setLevel(c.getInt("guild.level.level", 1));
        g.setExperience(c.getLong("guild.level.experience", 0L));
        g.setSkillPoints(c.getInt("guild.level.skill-points", 0));
        g.setUsedSkillPoints(c.getInt("guild.level.used-skill-points", 0));
        g.setMaxMembers(c.getInt("guild.max-members", 10));
        g.setFame(c.getInt("guild.fame", 0));
        g.setCreatedTime(c.getLong("guild.created-time", System.currentTimeMillis()));

        // members
        for (String key : c.getConfigurationSection("guild.members").getKeys(false)) {
            String path = "guild.members." + key;
            UUID pid = UUID.fromString(c.getString(path + ".player-id"));
            String pname = c.getString(path + ".player-name", "unknown");
            String rank = c.getString(path + ".rank-name", "Member");
            GuildMember m = new GuildMember(pid, pname, rank);
            m.setContribution(c.getLong(path + ".contribution", 0));
            m.setWeeklyContribution(c.getLong(path + ".weekly-contribution", 0));
            m.setJoinTime(c.getLong(path + ".join-time", System.currentTimeMillis()));
            m.setLastOnline(c.getLong(path + ".last-online", System.currentTimeMillis()));
            g.getMembers().put(pid, m);
        }

        // ranks
        if (c.isConfigurationSection("guild.ranks")) {
            for (String r : c.getConfigurationSection("guild.ranks").getKeys(false)) {
                String path = "guild.ranks." + r;
                int priority = c.getInt(path + ".priority", 99);
                double salary = c.getDouble(path + ".daily-salary", 0.0);
                Set<GuildPermission> perms = EnumSet.noneOf(GuildPermission.class);
                for (String p : c.getStringList(path + ".permissions")) {
                    try { perms.add(GuildPermission.valueOf(p)); } catch (Exception ignored) {}
                }
                g.getRanks().put(r, new GuildRank(r, priority, perms, salary));
            }
        }

        // treasury
        if (c.isConfigurationSection("guild.treasury")) {
            for (String cur : c.getConfigurationSection("guild.treasury").getKeys(false)) {
                g.getTreasury().put(cur, c.getDouble("guild.treasury." + cur, 0.0));
            }
        }

        // territory
        String tid = c.getString("guild.territory.territory-id", null);
        if (tid != null && !tid.isEmpty()) g.setTerritoryId(UUID.fromString(tid));

        // skills
        g.getSkills().addAll(c.getStringList("guild.skills"));

        // allies / enemies
        for (String a : c.getStringList("guild.allies")) g.getAllies().add(UUID.fromString(a));
        for (String e : c.getStringList("guild.enemies")) g.getEnemies().add(UUID.fromString(e));

        // statistics
        GuildStatistics st = g.getStatistics();
        st.setTotalWars(c.getInt("guild.statistics.total-wars", 0));
        st.setWarsWon(c.getInt("guild.statistics.wars-won", 0));
        st.setWarsLost(c.getInt("guild.statistics.wars-lost", 0));
        st.setTotalKills(c.getInt("guild.statistics.total-kills", 0));
        st.setTotalDeaths(c.getInt("guild.statistics.total-deaths", 0));
        st.setTotalContribution(c.getLong("guild.statistics.total-contribution", 0));

        // settings
        GuildSettings set = g.getSettings();
        set.setPublicJoin(c.getBoolean("guild.settings.public-join", false));
        set.setAutoKickInactive(c.getBoolean("guild.settings.auto-kick-inactive", false));
        set.setInactiveDays(c.getInt("guild.settings.inactive-days", 30));
        set.setTaxRate(c.getDouble("guild.settings.tax-rate", 5.0));

        return g;
    }

    private void toConfig(Guild g, YamlConfiguration c) {
        c.set("guild.guild-id", g.getGuildId().toString());
        c.set("guild.guild-name", g.getGuildName());
        c.set("guild.guild-tag", g.getGuildTag());
        c.set("guild.master-id", g.getMasterId().toString());
        c.set("guild.description", g.getDescription());
        c.set("guild.announcement", g.getAnnouncement());
        c.set("guild.max-members", g.getMaxMembers());
        c.set("guild.level.level", g.getLevel());
        c.set("guild.level.experience", g.getExperience());
        c.set("guild.level.skill-points", g.getSkillPoints());
        c.set("guild.level.used-skill-points", g.getUsedSkillPoints());
        c.set("guild.fame", g.getFame());
        c.set("guild.created-time", g.getCreatedTime());

        // members
        for (Map.Entry<UUID, GuildMember> e : g.getMembers().entrySet()) {
            String path = "guild.members." + e.getKey();
            GuildMember m = e.getValue();
            c.set(path + ".player-id", m.getPlayerId().toString());
            c.set(path + ".player-name", m.getPlayerName());
            c.set(path + ".rank-name", m.getRankName());
            c.set(path + ".contribution", m.getContribution());
            c.set(path + ".weekly-contribution", m.getWeeklyContribution());
            c.set(path + ".join-time", m.getJoinTime());
            c.set(path + ".last-online", m.getLastOnline());
        }

        // ranks
        for (GuildRank r : g.getRanks().values()) {
            String path = "guild.ranks." + r.getRankName();
            c.set(path + ".rank-name", r.getRankName());
            c.set(path + ".priority", r.getPriority());
            c.set(path + ".permissions", r.getPermissions().stream().map(Enum::name).toList());
            c.set(path + ".daily-salary", r.getDailySalary());
        }

        // treasury
        for (Map.Entry<String, Double> e : g.getTreasury().entrySet()) {
            c.set("guild.treasury." + e.getKey(), e.getValue());
        }

        // territory
        c.set("guild.territory.territory-id", g.getTerritoryId() == null ? null : g.getTerritoryId().toString());

        // skills
        c.set("guild.skills", new ArrayList<>(g.getSkills()));

        // allies/enemies
        c.set("guild.allies", g.getAllies().stream().map(UUID::toString).toList());
        c.set("guild.enemies", g.getEnemies().stream().map(UUID::toString).toList());

        // statistics
        GuildStatistics st = g.getStatistics();
        c.set("guild.statistics.total-wars", st.getTotalWars());
        c.set("guild.statistics.wars-won", st.getWarsWon());
        c.set("guild.statistics.wars-lost", st.getWarsLost());
        c.set("guild.statistics.total-kills", st.getTotalKills());
        c.set("guild.statistics.total-deaths", st.getTotalDeaths());
        c.set("guild.statistics.total-contribution", st.getTotalContribution());

        // settings
        GuildSettings set = g.getSettings();
        c.set("guild.settings.public-join", set.isPublicJoin());
        c.set("guild.settings.auto-kick-inactive", set.isAutoKickInactive());
        c.set("guild.settings.inactive-days", set.getInactiveDays());
        c.set("guild.settings.tax-rate", set.getTaxRate());
    }
}