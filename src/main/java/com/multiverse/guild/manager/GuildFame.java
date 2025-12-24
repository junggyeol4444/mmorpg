package com.multiverse.guild.manager;

import com.multiverse.guild.GuildCore;
import com.multiverse.guild.model.Guild;

public class GuildFame {

    private final GuildCore plugin;

    public GuildFame(GuildCore plugin) {
        this.plugin = plugin;
    }

    public void addFame(Guild guild, int amount, String reason) {
        guild.setFame(guild.getFame() + amount);
        plugin.getGuildStorage().save(guild);
    }

    public int getFame(Guild guild) {
        return guild.getFame();
    }

    public int getRank(Guild guild) {
        return plugin.getGuildRanking().getGuildRank(guild, com.multiverse.guild.model.RankingType.LEVEL); // simple placeholder
    }

    public double getShopDiscount(Guild guild) {
        double max = plugin.getConfig().getDouble("fame.shop-discount.max-discount", 20.0);
        double discount = Math.min(max, guild.getFame() * 0.01);
        return discount;
    }

    public int getNPCReputation(Guild guild) {
        return guild.getFame();
    }
}