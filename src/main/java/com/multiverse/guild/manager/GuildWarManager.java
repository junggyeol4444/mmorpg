package com.multiverse.guild.manager;

import com.multiverse.guild.GuildCore;
import com.multiverse.guild.model.Guild;
import com.multiverse.guild.model.GuildWar;
import com.multiverse.guild.model.WarStatus;
import com.multiverse.guild.storage.YamlWarStorage;
import com.multiverse.guild.util.Message;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GuildWarManager {

    private final GuildCore plugin;
    private final Map<UUID, GuildWar> activeWars = new ConcurrentHashMap<>();
    private final Map<UUID, GuildWar> pendingWars = new ConcurrentHashMap<>(); // defenderGuildId -> war

    public GuildWarManager(GuildCore plugin) {
        this.plugin = plugin;
        activeWars.putAll(plugin.getWarStorage().loadActive());
    }

    public void declareWarCmd(Player actor, Guild attacker, String defenderName) {
        Guild defender = plugin.getGuildManager().getGuildByName(defenderName);
        if (defender == null) { actor.sendMessage(Message.prefixed("&c대상 길드를 찾을 수 없습니다.")); return; }
        if (!plugin.getGuildManager().hasPermission(attacker, actor.getUniqueId(), com.multiverse.guild.model.GuildPermission.DECLARE_WAR)) {
            actor.sendMessage(Message.prefixed("&c전쟁 선포 권한이 없습니다.")); return;
        }
        double cost = plugin.getConfig().getDouble("guild-war.declaration.cost", 50000.0);
        if (!plugin.getEconomy().has(actor, cost)) {
            actor.sendMessage(Message.prefixed("&c전쟁 선포 비용이 부족합니다.")); return;
        }
        plugin.getEconomy().withdrawPlayer(actor, cost);

        GuildWar war = new GuildWar(UUID.randomUUID(), attacker.getGuildId(), defender.getGuildId(),
                0, 0, plugin.getConfig().getInt("guild-war.target-score", 100),
                System.currentTimeMillis(), 0L,
                plugin.getConfig().getInt("guild-war.duration", 3600),
                WarStatus.DECLARED, new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
        pendingWars.put(defender.getGuildId(), war);
        plugin.getWarStorage().savePending(war);

        plugin.getGuildManager().broadcastToGuild(defender, Message.prefixed("&c" + attacker.getGuildName() + " 길드가 전쟁을 선포했습니다. /guild war accept|decline"));
        actor.sendMessage(Message.prefixed("&a전쟁을 선포했습니다. 상대 길드의 수락을 기다립니다."));
    }

    public void acceptWarCmd(Player actor, Guild defender) {
        GuildWar war = pendingWars.remove(defender.getGuildId());
        if (war == null) { actor.sendMessage(Message.prefixed("&c수락할 전쟁이 없습니다.")); return; }
        war.setStatus(WarStatus.ACCEPTED);
        war.setStartTime(System.currentTimeMillis());
        war.setEndTime(war.getStartTime() + war.getDuration() * 1000L);
        activeWars.put(war.getWarId(), war);
        plugin.getWarStorage().deletePending(war.getWarId());
        plugin.getWarStorage().saveActive(war);

        Guild attacker = plugin.getGuildManager().getGuild(war.getAttackerGuildId());
        plugin.getGuildManager().broadcastToGuild(attacker, Message.prefixed("&c전쟁이 수락되어 시작되었습니다!"));
        plugin.getGuildManager().broadcastToGuild(defender, Message.prefixed("&c전쟁이 시작되었습니다!"));
    }

    public void declineWarCmd(Player actor, Guild defender) {
        GuildWar war = pendingWars.remove(defender.getGuildId());
        if (war == null) { actor.sendMessage(Message.prefixed("&c거절할 전쟁이 없습니다.")); return; }
        plugin.getWarStorage().deletePending(war.getWarId());
        actor.sendMessage(Message.prefixed("&a전쟁을 거절했습니다."));
    }

    public void onPlayerKill(GuildWar war, Player killer, Player victim) {
        UUID killerGuildId = plugin.getGuildManager().getPlayerGuild(killer.getUniqueId()).getGuildId();
        if (war.getAttackerGuildId().equals(killerGuildId)) {
            war.setAttackerScore(war.getAttackerScore() + 1);
        } else if (war.getDefenderGuildId().equals(killerGuildId)) {
            war.setDefenderScore(war.getDefenderScore() + 1);
        }
        war.getKills().merge(killer.getUniqueId(), 1, Integer::sum);
        war.getDeaths().merge(victim.getUniqueId(), 1, Integer::sum);
        checkEnd(war);
        plugin.getWarStorage().saveActive(war);
    }

    private void checkEnd(GuildWar war) {
        if (System.currentTimeMillis() >= war.getEndTime()
                || war.getAttackerScore() >= war.getTargetScore()
                || war.getDefenderScore() >= war.getTargetScore()) {
            UUID winner = war.getAttackerScore() == war.getDefenderScore() ? null
                    : (war.getAttackerScore() > war.getDefenderScore() ? war.getAttackerGuildId() : war.getDefenderGuildId());
            endWar(war, winner);
        }
    }

    public void endWar(GuildWar war, UUID winnerId) {
        activeWars.remove(war.getWarId());
        plugin.getWarStorage().deleteActive(war.getWarId());
        war.setStatus(WarStatus.ENDED);
        plugin.getWarStorage().appendHistory(war);

        Guild attacker = plugin.getGuildManager().getGuild(war.getAttackerGuildId());
        Guild defender = plugin.getGuildManager().getGuild(war.getDefenderGuildId());

        if (winnerId != null) {
            Guild winner = plugin.getGuildManager().getGuild(winnerId);
            plugin.getGuildLevelManager().addExp(winner, plugin.getConfig().getInt("guild-war.rewards.winner-exp", 10000), "war-win");
            plugin.getGuildFame().addFame(winner, plugin.getConfig().getInt("guild-war.rewards.winner-fame", 100), "war-win");
            winner.getTreasury().merge("fantasy_gold", plugin.getConfig().getDouble("guild-war.rewards.winner-gold", 100000.0), Double::sum);
        }
        if (winnerId != null && winnerId.equals(attacker.getGuildId())) {
            plugin.getGuildFame().addFame(defender, plugin.getConfig().getInt("guild-war.rewards.loser-fame", -50), "war-loss");
        } else if (winnerId != null && winnerId.equals(defender.getGuildId())) {
            plugin.getGuildFame().addFame(attacker, plugin.getConfig().getInt("guild-war.rewards.loser-fame", -50), "war-loss");
        }

        plugin.getGuildManager().broadcastToGuild(attacker, Message.prefixed("&a전쟁 종료! 승자: " + (winnerId == null ? "무승부" : plugin.getGuildManager().getGuild(winnerId).getGuildName())));
        plugin.getGuildManager().broadcastToGuild(defender, Message.prefixed("&a전쟁 종료! 승자: " + (winnerId == null ? "무승부" : plugin.getGuildManager().getGuild(winnerId).getGuildName())));
    }

    public GuildWar getActiveWar(Guild guild) {
        return activeWars.values().stream().filter(w ->
                w.getAttackerGuildId().equals(guild.getGuildId()) || w.getDefenderGuildId().equals(guild.getGuildId()))
                .findFirst().orElse(null);
    }

    public boolean isAtWar(Guild g1, Guild g2) {
        return activeWars.values().stream().anyMatch(w ->
                (w.getAttackerGuildId().equals(g1.getGuildId()) && w.getDefenderGuildId().equals(g2.getGuildId())) ||
                (w.getAttackerGuildId().equals(g2.getGuildId()) && w.getDefenderGuildId().equals(g1.getGuildId())));
    }
}