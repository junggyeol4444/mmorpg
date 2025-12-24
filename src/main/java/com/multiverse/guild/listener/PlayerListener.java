package com.multiverse.guild.listener;

import com.multiverse.guild.GuildCore;
import com.multiverse.guild.model.Guild;
import com.multiverse.guild.model.GuildPermission;
import com.multiverse.guild.model.GuildWar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final GuildCore plugin;

    public PlayerListener(GuildCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        // 이름 갱신
        Guild g = plugin.getGuildManager().getPlayerGuild(e.getPlayer().getUniqueId());
        if (g != null && g.getMembers().containsKey(e.getPlayer().getUniqueId())) {
            g.getMembers().get(e.getPlayer().getUniqueId()).setPlayerName(e.getPlayer().getName());
            plugin.getGuildStorage().save(g);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        // 마지막 온라인 기록
        Guild g = plugin.getGuildManager().getPlayerGuild(e.getPlayer().getUniqueId());
        if (g != null && g.getMembers().containsKey(e.getPlayer().getUniqueId())) {
            g.getMembers().get(e.getPlayer().getUniqueId()).setLastOnline(System.currentTimeMillis());
            plugin.getGuildStorage().save(g);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        // 예: 접두어 표시
        Guild g = plugin.getGuildManager().getPlayerGuild(e.getPlayer().getUniqueId());
        if (g != null) {
            e.setFormat(g.getGuildTag() + " " + e.getFormat());
        }
    }
}