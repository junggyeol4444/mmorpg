package com.multiverse.guild.manager;

import com.multiverse.guild.GuildCore;
import com.multiverse.guild.model.Guild;
import com.multiverse.guild.model.RankingType;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GuildRanking {

    private final GuildCore plugin;

    public GuildRanking(GuildCore plugin) {
        this.plugin = plugin;
    }

    public List<Guild> getTopGuildsByLevel(int limit) {
        return plugin.getGuildManager().getAllGuilds().stream()
                .sorted(Comparator.comparingInt(Guild::getLevel).reversed()
                        .thenComparingLong(Guild::getExperience).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Guild> getTopGuildsByPower(int limit) {
        return plugin.getGuildManager().getAllGuilds().stream()
                .sorted(Comparator.comparingInt(g -> g.getStatistics().getWarsWon()).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Guild> getTopGuildsByWealth(int limit) {
        return plugin.getGuildManager().getAllGuilds().stream()
                .sorted(Comparator.comparingDouble(g -> g.getTreasury().getOrDefault("fantasy_gold", 0.0)).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public int getGuildRank(Guild guild, RankingType type) {
        List<Guild> list;
        switch (type) {
            case LEVEL -> list = getTopGuildsByLevel(Integer.MAX_VALUE);
            case POWER -> list = getTopGuildsByPower(Integer.MAX_VALUE);
            case WEALTH -> list = getTopGuildsByWealth(Integer.MAX_VALUE);
            case ACTIVITY -> list = plugin.getGuildManager().getAllGuilds().stream()
                    .sorted(Comparator.comparingLong(g -> g.getStatistics().getTotalContribution()).reversed())
                    .collect(Collectors.toList());
            default -> list = List.of();
        }
        for (int i = 0; i < list.size(); i++) if (list.get(i).getGuildId().equals(guild.getGuildId())) return i + 1;
        return -1;
    }

    public void updateRankings() {
        // For YAML storage, could persist cached rankings; skipped for brevity.
    }
}