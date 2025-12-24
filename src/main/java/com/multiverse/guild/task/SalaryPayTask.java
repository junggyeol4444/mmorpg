package com.multiverse.guild.task;

import com.multiverse.guild.GuildCore;
import com.multiverse.guild.model.Guild;

public class SalaryPayTask implements Runnable {

    private final GuildCore plugin;

    public SalaryPayTask(GuildCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Guild g : plugin.getGuildManager().getAllGuilds()) {
            plugin.getGuildTreasury().paySalaries(g);
        }
    }
}