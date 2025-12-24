package com.multiverse.pvp.storage;

import com. multiverse.pvp.PvPCore;
import com. multiverse.pvp.data. ArenaReward;
import com.multiverse.pvp.data.PvPArena;
import com.multiverse.pvp. enums.ArenaType;
import org.bukkit. Bukkit;
import org.bukkit. Location;
import org. bukkit.World;
import org. bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class ArenaStorage {

    private final PvPCore plugin;
    private final DataManager dataManager;

    public ArenaStorage(PvPCore plugin, DataManager dataManager) {
        this. plugin = plugin;
        this.dataManager = dataManager;
    }

    /**
     * 모든 아레나 로드
     */
    public void loadAllArenas() {
        FileConfiguration config = dataManager.getArenasConfig();

        ConfigurationSection arenasSection = config.getConfigurationSection("arenas");
        if (arenasSection == null) {
            plugin.getLogger().info("저장된 아레나가 없습니다.");
            return;
        }

        int loaded = 0;
        for (String key : arenasSection.getKeys(false)) {
            ConfigurationSection arenaSection = arenasSection.getConfigurationSection(key);
            if (arenaSection == null) continue;

            PvPArena arena = loadArena(arenaSection);
            if (arena != null) {
                plugin.getArenaManager().loadArena(arena);
                loaded++;
            }
        }

        plugin.getLogger().info("아레나 " + loaded + "개 로드 완료");
    }

    /**
     * 아레나 로드
     */
    private PvPArena loadArena(ConfigurationSection section) {
        try {
            UUID arenaId = UUID. fromString(section. getString("id"));
            String name = section.getString("name");
            ArenaType type = ArenaType.valueOf(section.getString("type", "DUEL_1V1"));

            PvPArena arena = new PvPArena(arenaId, name, type);

            // 기본 설정
            arena.setWorldName(section.getString("world"));
            arena.setMinPlayers(section. getInt("min-players", type.getMinPlayers()));
            arena.setMaxPlayers(section.getInt("max-players", type.getMaxPlayers()));
            arena.setTeamSize(section. getInt("team-size", type.getDefaultTeamSize()));
            arena.setMatchDuration(section.getInt("match-duration", type.getDefaultDuration()));
            arena.setPreparationTime(section. getInt("preparation-time", 10));

            // 위치
            arena.setLobby(loadLocation(section.getConfigurationSection("lobby")));
            arena.setSpectatorSpawn(loadLocation(section.getConfigurationSection("spectator-spawn")));
            arena.setCorner1(loadLocation(section.getConfigurationSection("corner1")));
            arena.setCorner2(loadLocation(section.getConfigurationSection("corner2")));

            // 스폰 포인트
            List<Location> spawnPoints = new ArrayList<>();
            ConfigurationSection spawnsSection = section. getConfigurationSection("spawn-points");
            if (spawnsSection != null) {
                for (String key : spawnsSection. getKeys(false)) {
                    Location spawn = loadLocation(spawnsSection.getConfigurationSection(key));
                    if (spawn != null) {
                        spawnPoints.add(spawn);
                    }
                }
            }
            arena. setSpawnPoints(spawnPoints);

            // 보상
            ConfigurationSection rewardSection = section.getConfigurationSection("reward");
            if (rewardSection != null) {
                arena.setReward(loadArenaReward(rewardSection));
            }

            // 통계
            arena. setTotalMatches(section.getInt("total-matches", 0));
            arena.setTotalKills(section.getInt("total-kills", 0));

            return arena;

        } catch (Exception e) {
            plugin.getLogger().warning("아레나 로드 실패: " + e. getMessage());
            return null;
        }
    }

    /**
     * 아레나 저장
     */
    public void saveArena(PvPArena arena) {
        FileConfiguration config = dataManager.getArenasConfig();

        String path = "arenas." + arena.getArenaId().toString() + ".";

        config.set(path + "id", arena.getArenaId().toString());
        config.set(path + "name", arena.getArenaName());
        config.set(path + "type", arena.getType().name());
        config.set(path + "world", arena.getWorldName());
        config.set(path + "min-players", arena. getMinPlayers());
        config.set(path + "max-players", arena. getMaxPlayers());
        config.set(path + "team-size", arena. getTeamSize());
        config.set(path + "match-duration", arena. getMatchDuration());
        config.set(path + "preparation-time", arena. getPreparationTime());

        // 위치
        saveLocation(config, path + "lobby", arena.getLobby());
        saveLocation(config, path + "spectator-spawn", arena.getSpectatorSpawn());
        saveLocation(config, path + "corner1", arena. getCorner1());
        saveLocation(config, path + "corner2", arena. getCorner2());

        // 스폰 포인트
        List<Location> spawnPoints = arena.getSpawnPoints();
        for (int i = 0; i < spawnPoints.size(); i++) {
            saveLocation(config, path + "spawn-points." + i, spawnPoints.get(i));
        }

        // 보상
        saveArenaReward(config, path + "reward", arena.getReward());

        // 통계
        config.set(path + "total-matches", arena.getTotalMatches());
        config.set(path + "total-kills", arena.getTotalKills());

        dataManager.saveArenasConfig();
    }

    /**
     * 아레나 삭제
     */
    public void deleteArena(UUID arenaId) {
        FileConfiguration config = dataManager.getArenasConfig();
        config.set("arenas." + arenaId. toString(), null);
        dataManager.saveArenasConfig();
    }

    /**
     * 모든 아레나 저장
     */
    public void saveAllArenas() {
        for (PvPArena arena : plugin.getArenaManager().getAllArenas()) {
            saveArena(arena);
        }
        plugin.getLogger().info("모든 아레나 저장 완료");
    }

    /**
     * 위치 로드
     */
    private Location loadLocation(ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        String worldName = section.getString("world");
        if (worldName == null) {
            return null;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }

        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        float yaw = (float) section.getDouble("yaw", 0);
        float pitch = (float) section.getDouble("pitch", 0);

        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * 위치 저장
     */
    private void saveLocation(FileConfiguration config, String path, Location location) {
        if (location == null) {
            config.set(path, null);
            return;
        }

        config.set(path + ". world", location.getWorld().getName());
        config.set(path + ".x", location.getX());
        config.set(path + ".y", location.getY());
        config.set(path + ".z", location.getZ());
        config.set(path + ". yaw", location. getYaw());
        config.set(path + ".pitch", location.getPitch());
    }

    /**
     * 아레나 보상 로드
     */
    private ArenaReward loadArenaReward(ConfigurationSection section) {
        ArenaReward reward = new ArenaReward();

        reward.setWinExperience(section.getLong("win-experience", 100));
        reward.setWinDefaultMoney(section.getDouble("win-money", 100. 0));
        reward.setWinPvpPoints(section. getInt("win-pvp-points", 10));
        reward.setWinRating(section.getInt("win-rating", 25));

        reward.setLoseExperience(section. getLong("lose-experience", 25));
        reward.setLoseDefaultMoney(section.getDouble("lose-money", 25.0));
        reward.setLosePvpPoints(section. getInt("lose-pvp-points", 2));
        reward.setLoseRating(section.getInt("lose-rating", -20));

        reward.setKillExperience(section.getLong("kill-experience", 20));
        reward.setKillDefaultMoney(section.getDouble("kill-money", 10.0));
        reward.setKillPvpPoints(section.getInt("kill-pvp-points", 1));

        reward.setWinStreakBonus(section.getDouble("win-streak-bonus", 0.1));
        reward.setFirstBloodBonus(section. getDouble("first-blood-bonus", 0.25));
        reward.setShutdownBonus(section.getDouble("shutdown-bonus", 0.5));

        return reward;
    }

    /**
     * 아레나 보상 저장
     */
    private void saveArenaReward(FileConfiguration config, String path, ArenaReward reward) {
        if (reward == null) {
            return;
        }

        config.set(path + ". win-experience", reward.getWinExperience());
        config.set(path + ".win-money", reward.getWinMoney().getOrDefault("default", 100.0));
        config.set(path + ".win-pvp-points", reward.getWinPvpPoints());
        config.set(path + ".win-rating", reward.getWinRating());

        config.set(path + ".lose-experience", reward. getLoseExperience());
        config.set(path + ".lose-money", reward. getLoseMoney().getOrDefault("default", 25.0));
        config.set(path + ".lose-pvp-points", reward.getLosePvpPoints());
        config.set(path + ".lose-rating", reward.getLoseRating());

        config.set(path + ".kill-experience", reward.getKillExperience());
        config.set(path + ". kill-money", reward.getKillMoney().getOrDefault("default", 10.0));
        config.set(path + ".kill-pvp-points", reward.getKillPvpPoints());

        config.set(path + ".win-streak-bonus", reward.getWinStreakBonus());
        config.set(path + ".first-blood-bonus", reward.getFirstBloodBonus());
        config.set(path + ".shutdown-bonus", reward.getShutdownBonus());
    }
}