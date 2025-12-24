package com.multiverse.guild.manager;

import com.multiverse.guild.GuildCore;
import com.multiverse.guild.model.*;
import com.multiverse.guild.storage.YamlTerritoryStorage;
import com.multiverse.guild.util.LocationUtil;
import com.multiverse.guild.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TerritoryManager {

    private final GuildCore plugin;
    private final Map<UUID, GuildTerritory> territories = new ConcurrentHashMap<>();

    public TerritoryManager(GuildCore plugin) {
        this.plugin = plugin;
        territories.putAll(plugin.getTerritoryStorage().loadAll());
    }

    public void claimTerritoryCmd(Player player, Guild guild) {
        if (!plugin.getConfig().getBoolean("territory.enabled", true)) {
            player.sendMessage(Message.prefixed("&c영지 기능이 비활성화되어 있습니다."));
            return;
        }
        if (!plugin.getEconomy().has(player, plugin.getConfig().getDouble("territory.cost.claim", 500000.0))) {
            player.sendMessage(Message.prefixed("&c영지 구입 비용이 부족합니다."));
            return;
        }
        // For simplicity, claim a 50x50 square around player
        int half = 25;
        Location loc = player.getLocation();
        Location c1 = new Location(loc.getWorld(), loc.getBlockX() - half, 0, loc.getBlockZ() - half);
        Location c2 = new Location(loc.getWorld(), loc.getBlockX() + half, loc.getWorld().getMaxHeight(), loc.getBlockZ() + half);
        GuildTerritory territory = new GuildTerritory(UUID.randomUUID(), guild.getGuildId(),
                loc.getWorld().getName(), c1, c2, loc, new ConcurrentHashMap<>(), new TerritorySettings(false, false, false, false));
        territories.put(territory.getTerritoryId(), territory);
        guild.setTerritoryId(territory.getTerritoryId());
        plugin.getTerritoryStorage().save(territory);
        plugin.getGuildStorage().save(guild);
        plugin.getEconomy().withdrawPlayer(player, plugin.getConfig().getDouble("territory.cost.claim", 500000.0));
        player.sendMessage(Message.prefixed("&a영지를 획득했습니다."));
    }

    public void unclaimTerritoryCmd(Player player, Guild guild) {
        UUID tid = guild.getTerritoryId();
        if (tid == null) { player.sendMessage(Message.prefixed("&c보유 영지가 없습니다.")); return; }
        territories.remove(tid);
        plugin.getTerritoryStorage().delete(tid);
        guild.setTerritoryId(null);
        plugin.getGuildStorage().save(guild);
        player.sendMessage(Message.prefixed("&c영지를 해제했습니다."));
    }

    public GuildTerritory getTerritory(Location loc) {
        return territories.values().stream().filter(t -> LocationUtil.isInside(loc, t.getCorner1(), t.getCorner2())).findFirst().orElse(null);
    }

    public boolean isInTerritory(Location loc, Guild guild) {
        if (guild == null || guild.getTerritoryId() == null) return false;
        GuildTerritory t = territories.get(guild.getTerritoryId());
        if (t == null) return false;
        return LocationUtil.isInside(loc, t.getCorner1(), t.getCorner2());
    }

    public void buildBuilding(Guild guild, BuildingType type, Location loc) {
        GuildTerritory t = territories.get(guild.getTerritoryId());
        if (t == null) return;
        GuildBuilding b = new GuildBuilding(type, 1, loc, new ConcurrentHashMap<>());
        t.getBuildings().put(type, b);
        plugin.getTerritoryStorage().save(t);
    }

    public void upgradeBuilding(Guild guild, BuildingType type) {
        GuildTerritory t = territories.get(guild.getTerritoryId());
        if (t == null) return;
        GuildBuilding b = t.getBuildings().get(type);
        if (b == null) return;
        b.setLevel(b.getLevel() + 1);
        plugin.getTerritoryStorage().save(t);
    }

    public boolean canBreak(Player player, Location loc) {
        GuildTerritory t = getTerritory(loc);
        if (t == null) return true;
        return t.getGuildId().equals(plugin.getGuildManager().getPlayerGuild(player.getUniqueId()).getGuildId())
                || t.getSettings().isBlockBreak();
    }

    public boolean canPlace(Player player, Location loc) {
        GuildTerritory t = getTerritory(loc);
        if (t == null) return true;
        return t.getGuildId().equals(plugin.getGuildManager().getPlayerGuild(player.getUniqueId()).getGuildId())
                || t.getSettings().isBlockBreak();
    }

    public boolean canPvP(Location loc) {
        GuildTerritory t = getTerritory(loc);
        if (t == null) return true;
        return t.getSettings().isPvpEnabled();
    }
}