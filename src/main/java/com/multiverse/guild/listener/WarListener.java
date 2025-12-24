package com.multiverse.guild.listener;

import com.multiverse.guild.GuildCore;
import com.multiverse.guild.model.Guild;
import com.multiverse.guild.model.GuildWar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;

public class WarListener implements Listener {

    private final GuildCore plugin;

    public WarListener(GuildCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) return;
        Guild vg = plugin.getGuildManager().getPlayerGuild(victim.getUniqueId());
        Guild kg = plugin.getGuildManager().getPlayerGuild(killer.getUniqueId());
        if (vg == null || kg == null) return;
        GuildWar war = plugin.getGuildWarManager().getActiveWar(vg);
        if (war == null) return;
        // 전쟁 중 처치 처리
        plugin.getGuildWarManager().onPlayerKill(war, killer, victim);
    }
}