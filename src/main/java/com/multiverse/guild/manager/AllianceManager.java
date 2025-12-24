package com.multiverse.guild.manager;

import com.multiverse.guild.GuildCore;
import com.multiverse.guild.model.Guild;
import com.multiverse.guild.util.Message;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AllianceManager {

    private final GuildCore plugin;
    // key: pair sorted UUIDs as string "a:b" to store alliance
    private final Set<String> alliances = ConcurrentHashMap.newKeySet();
    private final Map<UUID, UUID> proposals = new ConcurrentHashMap<>(); // targetGuildId -> proposerGuildId

    public AllianceManager(GuildCore plugin) {
        this.plugin = plugin;
    }

    private String key(UUID g1, UUID g2) {
        List<UUID> list = new ArrayList<>(List.of(g1, g2));
        list.sort(Comparator.comparing(UUID::toString));
        return list.get(0) + ":" + list.get(1);
    }

    public void proposeAllianceCmd(Player actor, Guild guild, Guild other) {
        if (!plugin.getConfig().getBoolean("alliance.enabled", true)) {
            actor.sendMessage(Message.prefixed("&c동맹 기능이 비활성화되어 있습니다."));
            return;
        }
        if (guild.equals(other)) { actor.sendMessage(Message.prefixed("&c자기 자신과 동맹할 수 없습니다.")); return; }
        if (isAllied(guild, other)) { actor.sendMessage(Message.prefixed("&c이미 동맹 상태입니다.")); return; }
        proposals.put(other.getGuildId(), guild.getGuildId());
        actor.sendMessage(Message.prefixed("&a동맹 제안을 보냈습니다."));
        plugin.getGuildManager().broadcastToGuild(other, Message.prefixed("&e" + guild.getGuildName() + " 길드가 동맹을 제안했습니다. /guild allyaccept"));
    }

    public void acceptAlliance(Guild guild) {
        UUID proposer = proposals.remove(guild.getGuildId());
        if (proposer == null) return;
        Guild other = plugin.getGuildManager().getGuild(proposer);
        if (other == null) return;
        alliances.add(key(guild.getGuildId(), other.getGuildId()));
        plugin.getGuildManager().broadcastToGuild(guild, Message.prefixed("&a동맹이 체결되었습니다: " + other.getGuildName()));
        plugin.getGuildManager().broadcastToGuild(other, Message.prefixed("&a동맹이 체결되었습니다: " + guild.getGuildName()));
    }

    public void breakAlliance(Guild g1, Guild g2) {
        alliances.remove(key(g1.getGuildId(), g2.getGuildId()));
        plugin.getGuildManager().broadcastToGuild(g1, Message.prefixed("&c동맹이 해제되었습니다: " + g2.getGuildName()));
        plugin.getGuildManager().broadcastToGuild(g2, Message.prefixed("&c동맹이 해제되었습니다: " + g1.getGuildName()));
    }

    public boolean isAllied(Guild g1, Guild g2) {
        return alliances.contains(key(g1.getGuildId(), g2.getGuildId()));
    }

    public List<Guild> getAllies(Guild guild) {
        List<Guild> res = new ArrayList<>();
        alliances.forEach(k -> {
            String[] s = k.split(":");
            UUID a = UUID.fromString(s[0]);
            UUID b = UUID.fromString(s[1]);
            if (a.equals(guild.getGuildId())) {
                Guild other = plugin.getGuildManager().getGuild(b);
                if (other != null) res.add(other);
            } else if (b.equals(guild.getGuildId())) {
                Guild other = plugin.getGuildManager().getGuild(a);
                if (other != null) res.add(other);
            }
        });
        return res;
    }
}