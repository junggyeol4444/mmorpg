package com.multiverse.guild.manager;

import com.multiverse.guild.GuildCore;
import com.multiverse.guild.model.Guild;
import com.multiverse.guild.model.GuildLevel;
import com.multiverse.guild.util.Message;

public class GuildLevelManager {

    private final GuildCore plugin;

    public GuildLevelManager(GuildCore plugin) {
        this.plugin = plugin;
    }

    public void addExp(Guild guild, long amount, String source) {
        if (!plugin.getConfig().getBoolean("guild-level.enabled", true)) return;
        guild.setExperience(guild.getExperience() + amount);
        long needed = expToNext(guild.getLevel());
        while (guild.getExperience() >= needed && guild.getLevel() < plugin.getConfig().getInt("guild-level.max-level", 100)) {
            guild.setExperience(guild.getExperience() - needed);
            levelUp(guild);
            needed = expToNext(guild.getLevel());
        }
        plugin.getGuildStorage().save(guild);
    }

    public long getExp(Guild guild) {
        return guild.getExperience();
    }

    public void levelUp(Guild guild) {
        guild.setLevel(guild.getLevel() + 1);
        int maxMembersBonus = plugin.getConfig().getInt("guild-level.bonuses-per-level.max-members", 5);
        guild.setMaxMembers(guild.getMaxMembers() + maxMembersBonus);
        int sp = plugin.getConfig().getInt("guild-level.bonuses-per-level.skill-points", 1);
        guild.setSkillPoints(guild.getSkillPoints() + sp);
        plugin.getGuildStorage().save(guild);
        guild.getOnlinePlayers().forEach(p -> p.sendMessage(Message.prefixed("&a&l길드 레벨 업! &eLv." + guild.getLevel())));
    }

    public int getLevel(Guild guild) {
        return guild.getLevel();
    }

    public double getBonus(Guild guild, String bonusKey) {
        // simple hook: bonuses stored inside GuildLevel.bonuses map (not persisted separately in this simplified impl)
        GuildLevel lv = guild.getLevelData();
        if (lv == null) return 0;
        return lv.getBonuses().getOrDefault(bonusKey, 0.0);
    }

    public void applyLevelBonuses(Guild guild) {
        // placeholder: recompute derived stats if needed
    }

    private long expToNext(int level) {
        // simple curve: 1000 * level^1.3
        return (long) (1000 * Math.pow(level, 1.3));
    }
}