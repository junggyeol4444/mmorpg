package com.multiverse.guild.manager;

import com.multiverse.guild.GuildCore;
import com.multiverse.guild.model.*;
import com.multiverse.guild.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GuildManager {

    private final GuildCore plugin;
    private final Map<UUID, Guild> guilds = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> invites = new ConcurrentHashMap<>(); // playerId -> guildId

    public GuildManager(GuildCore plugin) {
        this.plugin = plugin;
        // load guilds from storage
        guilds.putAll(plugin.getGuildStorage().loadAll());
    }

    public void createGuild(Player player, String name, String tag) {
        if (isInGuild(player.getUniqueId())) {
            player.sendMessage(Message.prefixed("&c이미 길드에 속해 있습니다."));
            return;
        }
        if (plugin.getEconomy() == null) {
            player.sendMessage(Message.prefixed("&c경제 플러그인이 필요합니다."));
            return;
        }
        double cost = plugin.getConfig().getDouble("guild-creation.cost", 100000.0);
        if (!plugin.getEconomy().has(player, cost)) {
            player.sendMessage(Message.prefixed("&c길드 생성 비용이 부족합니다. 필요: " + cost));
            return;
        }
        if (name.length() < plugin.getConfig().getInt("guild-creation.name.min-length", 3)
                || name.length() > plugin.getConfig().getInt("guild-creation.name.max-length", 16)) {
            player.sendMessage(Message.prefixed("&c길드 이름 길이가 올바르지 않습니다."));
            return;
        }
        if (tag.length() < plugin.getConfig().getInt("guild-creation.tag.min-length", 2)
                || tag.length() > plugin.getConfig().getInt("guild-creation.tag.max-length", 4)) {
            player.sendMessage(Message.prefixed("&c태그 길이가 올바르지 않습니다."));
            return;
        }
        if (getGuildByName(name) != null) {
            player.sendMessage(Message.prefixed("&c이미 존재하는 길드 이름입니다."));
            return;
        }
        plugin.getEconomy().withdrawPlayer(player, cost);
        Guild guild = new Guild(UUID.randomUUID(), name, "[" + tag + "]", player.getUniqueId());
        guild.getMembers().put(player.getUniqueId(), new GuildMember(player.getUniqueId(), player.getName(), "Guild Master"));
        guilds.put(guild.getGuildId(), guild);
        plugin.getGuildStorage().save(guild);
        player.sendMessage(Message.prefixed("&a길드를 생성했습니다! " + guild.getGuildName()));
    }

    public void disbandGuildCmd(Player player) {
        Guild guild = getPlayerGuild(player.getUniqueId());
        if (guild == null) { player.sendMessage(Message.prefixed("&c길드가 없습니다.")); return; }
        if (!guild.getMasterId().equals(player.getUniqueId())) {
            player.sendMessage(Message.prefixed("&c길드장만 해체할 수 있습니다."));
            return;
        }
        disbandGuild(guild.getGuildId());
        player.sendMessage(Message.prefixed("&c길드가 해체되었습니다."));
    }

    public void disbandGuild(UUID guildId) {
        Guild g = guilds.remove(guildId);
        if (g != null) {
            plugin.getGuildStorage().delete(guildId);
        }
    }

    public Guild getGuild(UUID guildId) { return guilds.get(guildId); }
    public Guild getGuildByName(String name) {
        return guilds.values().stream()
                .filter(g -> g.getGuildName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }
    public Guild getPlayerGuild(UUID playerId) {
        return guilds.values().stream()
                .filter(g -> g.getMembers().containsKey(playerId))
                .findFirst().orElse(null);
    }
    public boolean isInGuild(UUID playerId) { return getPlayerGuild(playerId) != null; }

    public void inviteMemberCmd(Player inviter, Player target) {
        if (target == null) { inviter.sendMessage(Message.prefixed("&c플레이어를 찾을 수 없습니다.")); return; }
        Guild guild = getPlayerGuild(inviter.getUniqueId());
        if (guild == null) { inviter.sendMessage(Message.prefixed("&c길드가 없습니다.")); return; }
        if (!hasPermission(guild, inviter.getUniqueId(), GuildPermission.INVITE_MEMBERS)) {
            inviter.sendMessage(Message.prefixed("&c초대 권한이 없습니다."));
            return;
        }
        invites.put(target.getUniqueId(), guild.getGuildId());
        inviter.sendMessage(Message.prefixed("&a초대 보냈습니다."));
        target.sendMessage(Message.prefixed("&e" + guild.getGuildName() + " 길드 초대가 도착했습니다. /guild accept"));
    }

    public void acceptInviteCmd(Player player) {
        UUID gid = invites.remove(player.getUniqueId());
        if (gid == null) { player.sendMessage(Message.prefixed("&c초대가 없습니다.")); return; }
        Guild guild = guilds.get(gid);
        if (guild == null) { player.sendMessage(Message.prefixed("&c길드를 찾을 수 없습니다.")); return; }
        int max = guild.getMaxMembers();
        if (guild.getMembers().size() >= max) {
            player.sendMessage(Message.prefixed("&c길드 정원이 가득 찼습니다."));
            return;
        }
        guild.getMembers().put(player.getUniqueId(), new GuildMember(player.getUniqueId(), player.getName(), "Member"));
        plugin.getGuildStorage().save(guild);
        broadcastGuild(guild, Message.prefixed("&a" + player.getName() + "님이 길드에 가입했습니다."));
    }

    public void kickMemberCmd(Player actor, String targetName) {
        Guild guild = getPlayerGuild(actor.getUniqueId());
        if (guild == null) { actor.sendMessage(Message.prefixed("&c길드가 없습니다.")); return; }
        if (!hasPermission(guild, actor.getUniqueId(), GuildPermission.KICK_MEMBERS)) {
            actor.sendMessage(Message.prefixed("&c추방 권한이 없습니다.")); return;
        }
        UUID targetId = findMemberIdByName(guild, targetName);
        if (targetId == null) { actor.sendMessage(Message.prefixed("&c해당 멤버가 없습니다.")); return; }
        if (guild.getMasterId().equals(targetId)) { actor.sendMessage(Message.prefixed("&c길드장은 추방할 수 없습니다.")); return; }

        guild.getMembers().remove(targetId);
        plugin.getGuildStorage().save(guild);
        broadcastGuild(guild, Message.prefixed("&c" + targetName + "님이 길드에서 추방되었습니다."));
    }

    public void leaveMember(Player player) {
        Guild guild = getPlayerGuild(player.getUniqueId());
        if (guild == null) { player.sendMessage(Message.prefixed("&c길드가 없습니다.")); return; }
        if (guild.getMasterId().equals(player.getUniqueId())) {
            // transfer master to highest priority member if exists
            Optional<UUID> next = guild.getMembers().entrySet().stream()
                    .filter(e -> !e.getKey().equals(player.getUniqueId()))
                    .sorted(Comparator.comparingInt(e -> getRankPriority(guild, e.getValue().getRankName())))
                    .map(Map.Entry::getKey)
                    .findFirst();
            if (next.isEmpty()) {
                // last member -> disband
                disbandGuild(guild.getGuildId());
                player.sendMessage(Message.prefixed("&c마지막 인원이 떠나 길드가 해체되었습니다."));
                return;
            } else {
                guild.setMasterId(next.get());
                guild.getMembers().remove(player.getUniqueId());
            }
        } else {
            guild.getMembers().remove(player.getUniqueId());
        }
        plugin.getGuildStorage().save(guild);
        broadcastGuild(guild, Message.prefixed("&c" + player.getName() + "님이 길드를 탈퇴했습니다."));
    }

    public void setRankCmd(Player actor, String targetName, String rankName, boolean promote) {
        Guild guild = getPlayerGuild(actor.getUniqueId());
        if (guild == null) { actor.sendMessage(Message.prefixed("&c길드가 없습니다.")); return; }
        if (!hasPermission(guild, actor.getUniqueId(), promote ? GuildPermission.PROMOTE_MEMBERS : GuildPermission.DEMOTE_MEMBERS)) {
            actor.sendMessage(Message.prefixed("&c권한이 없습니다.")); return;
        }
        UUID targetId = findMemberIdByName(guild, targetName);
        if (targetId == null) { actor.sendMessage(Message.prefixed("&c해당 멤버가 없습니다.")); return; }
        guild.getMembers().get(targetId).setRankName(rankName);
        plugin.getGuildStorage().save(guild);
        broadcastGuild(guild, Message.prefixed("&e" + targetName + "님의 계급이 " + rankName + " 로 변경되었습니다."));
    }

    public void demoteCmd(Player actor, String targetName) {
        setRankCmd(actor, targetName, "Member", false);
    }

    public void showInfo(Player viewer, Guild guild) {
        if (guild == null) { viewer.sendMessage(Message.prefixed("&c길드를 찾을 수 없습니다.")); return; }
        viewer.sendMessage(Message.prefixed("&6길드: " + guild.getGuildName() + " " + guild.getGuildTag()));
        viewer.sendMessage(Message.prefixed("&e길드장: " + nameOf(guild.getMasterId())));
        viewer.sendMessage(Message.prefixed("&e인원: " + guild.getMembers().size() + "/" + guild.getMaxMembers()));
        viewer.sendMessage(Message.prefixed("&e레벨: " + guild.getLevel() + " 경험치: " + guild.getExperience()));
        viewer.sendMessage(Message.prefixed("&e설명: " + guild.getDescription()));
    }

    public void listGuilds(Player player) {
        player.sendMessage(Message.prefixed("&6길드 목록:"));
        guilds.values().stream()
                .sorted(Comparator.comparingInt(Guild::getLevel).reversed())
                .limit(20)
                .forEach(g -> player.sendMessage(Message.prefixed("&e" + g.getGuildName() + " [" + g.getMembers().size() + "/" + g.getMaxMembers() + "] Lv." + g.getLevel())));
    }

    public void guildChat(Player sender, String msg) {
        Guild guild = getPlayerGuild(sender.getUniqueId());
        if (guild == null) { sender.sendMessage(Message.prefixed("&c길드가 없습니다.")); return; }
        broadcastGuild(guild, "&a[Guild] " + sender.getName() + ": &f" + msg);
    }

    public void handleTerritorySub(Player player, String[] args) {
        if (args.length < 2) { player.sendMessage(Message.prefixed("&c/guild territory claim|unclaim")); return; }
        Guild guild = getPlayerGuild(player.getUniqueId());
        if (guild == null) { player.sendMessage(Message.prefixed("&c길드가 없습니다.")); return; }
        switch (args[1].toLowerCase()) {
            case "claim" -> plugin.getTerritoryManager().claimTerritoryCmd(player, guild);
            case "unclaim" -> plugin.getTerritoryManager().unclaimTerritoryCmd(player, guild);
            default -> player.sendMessage(Message.prefixed("&c/guild territory claim|unclaim"));
        }
    }

    public void handleWarSub(Player player, String[] args) {
        if (args.length < 2) { player.sendMessage(Message.prefixed("&c/guild war declare <길드>|accept|decline")); return; }
        Guild guild = getPlayerGuild(player.getUniqueId());
        if (guild == null) { player.sendMessage(Message.prefixed("&c길드가 없습니다.")); return; }
        switch (args[1].toLowerCase()) {
            case "declare" -> {
                if (args.length < 3) { player.sendMessage(Message.prefixed("&c/guild war declare <길드>")); return; }
                plugin.getGuildWarManager().declareWarCmd(player, guild, args[2]);
            }
            case "accept" -> plugin.getGuildWarManager().acceptWarCmd(player, guild);
            case "decline" -> plugin.getGuildWarManager().declineWarCmd(player, guild);
            default -> player.sendMessage(Message.prefixed("&c/guild war declare|accept|decline"));
        }
    }

    public void handleAllySub(Player player, String targetGuildName) {
        Guild guild = getPlayerGuild(player.getUniqueId());
        if (guild == null) { player.sendMessage(Message.prefixed("&c길드가 없습니다.")); return; }
        Guild other = getGuildByName(targetGuildName);
        if (other == null) { player.sendMessage(Message.prefixed("&c대상 길드를 찾을 수 없습니다.")); return; }
        plugin.getAllianceManager().proposeAllianceCmd(player, guild, other);
    }

    private void broadcastGuild(Guild guild, String msg) {
        String formatted = Message.prefixed(msg);
        guild.getMembers().keySet().forEach(uuid -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) p.sendMessage(formatted);
        });
    }

    public boolean hasPermission(Guild guild, UUID playerId, GuildPermission permission) {
        GuildMember gm = guild.getMembers().get(playerId);
        if (gm == null) return false;
        String rank = gm.getRankName();
        GuildRank gr = guild.getRanks().getOrDefault(rank, GuildRank.defaultMember());
        return gr.getPermissions().contains(permission);
    }

    private UUID findMemberIdByName(Guild guild, String name) {
        return guild.getMembers().values().stream()
                .filter(m -> m.getPlayerName().equalsIgnoreCase(name))
                .map(GuildMember::getPlayerId)
                .findFirst().orElse(null);
    }

    private int getRankPriority(Guild guild, String rankName) {
        return guild.getRanks().getOrDefault(rankName, GuildRank.defaultMember()).getPriority();
    }

    private String nameOf(UUID uuid) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        return op != null ? op.getName() : "Unknown";
    }

    public List<String> getGuildNames() {
        return guilds.values().stream().map(Guild::getGuildName).collect(Collectors.toList());
    }
}