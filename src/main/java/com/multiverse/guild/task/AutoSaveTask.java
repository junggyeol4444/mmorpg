package com.multiverse.guild.task;

import com.multiverse.guild.GuildCore;

public class AutoSaveTask implements Runnable {

    private final GuildCore plugin;

    public AutoSaveTask(GuildCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // 길드 저장
        plugin.getGuildManager().getAllGuilds().forEach(plugin.getGuildStorage()::save);
        // 영지 저장
        plugin.getTerritoryManager().getAllTerritories().forEach(plugin.getTerritoryStorage()::save);
        // 전쟁 저장
        plugin.getGuildWarManager().getActiveWars().forEach(plugin.getWarStorage()::saveActive);
    }
}