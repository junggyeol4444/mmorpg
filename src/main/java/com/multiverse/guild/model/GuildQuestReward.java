package com.multiverse.guild.model;

import java.util.Map;

public class GuildQuestReward {
    private long experience;
    private Map<String, Double> money;
    private int fame;

    public GuildQuestReward(long experience, Map<String, Double> money, int fame) {
        this.experience = experience;
        this.money = money;
        this.fame = fame;
    }

    public long getExperience() { return experience; }
    public Map<String, Double> getMoney() { return money; }
    public int getFame() { return fame; }
}