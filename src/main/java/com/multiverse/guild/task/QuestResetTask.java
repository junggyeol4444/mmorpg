package com.multiverse.guild.task;

import com.multiverse.guild.GuildCore;

public class QuestResetTask implements Runnable {

    private final GuildCore plugin;

    public QuestResetTask(GuildCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getGuildQuestManager().tickReset();
    }
}