package com.multiverse.core.data;

import com.multiverse.core.MultiverseCore;
import com.multiverse.core.models.*;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YAMLDataManager implements DataManager {
    private final MultiverseCore plugin;
    private final File dataFolder;

    public YAMLDataManager(MultiverseCore plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) dataFolder.mkdirs();
    }

    @Override
    public void load() {
        // 차원, 포탈, 로그 등 데이터 파일 로딩
        // 실제 구현에서는 각 storage 클래스를 활용해 분리할 수 있음
    }

    @Override
    public void save() {
        // 차원, 포탈, 로그 등 데이터 저장
    }

    @Override
    public void reload() {
        load();
    }

    // ===== 차원 데이터 관리 =====
    public void saveDimensions(List<Dimension> dimensions) {
        File file = new File(dataFolder, "dimensions.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        for (Dimension dim : dimensions) {
            String key = dim.getId();
            yaml.set("dimensions." + key + ".name", dim.getName());
            yaml.set("dimensions." + key + ".world-name", dim.getWorldName());
            yaml.set("dimensions." + key + ".type", dim.getType().toString());
            yaml.set("dimensions." + key + ".balance", dim.getBalanceValue());
            yaml.set("dimensions." + key + ".time-multiplier", dim.getTimeMultiplier());
            yaml.set("dimensions." + key + ".active", dim.isActive());
            yaml.set("dimensions." + key + ".level-requirement", dim.getLevelRequirement());
            yaml.set("dimensions." + key + ".quest-requirement", dim.getQuestRequirement());
            yaml.set("dimensions." + key + ".connected-dimensions", dim.getConnectedDimensions());
        }
        try { yaml.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public List<Dimension> loadDimensions() {
        File file = new File(dataFolder, "dimensions.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<Dimension> list = new ArrayList<>();
        if (yaml.contains("dimensions")) {
            for (String key : yaml.getConfigurationSection("dimensions").getKeys(false)) {
                String name = yaml.getString("dimensions." + key + ".name");
                String worldName = yaml.getString("dimensions." + key + ".world-name");
                String typeStr = yaml.getString("dimensions." + key + ".type", "MAIN");
                int balance = yaml.getInt("dimensions." + key + ".balance", 50);
                double multiplier = yaml.getDouble("dimensions." + key + ".time-multiplier", 1.0);
                boolean active = yaml.getBoolean("dimensions." + key + ".active", true);
                int levelReq = yaml.getInt("dimensions." + key + ".level-requirement", 0);
                String questReq = yaml.getString("dimensions." + key + ".quest-requirement");
                List<String> connects = yaml.getStringList("dimensions." + key + ".connected-dimensions");
                Dimension dim = new Dimension(key, name, worldName,
                        com.multiverse.core.models.enums.DimensionType.valueOf(typeStr),
                        balance, multiplier, active, levelReq, questReq, connects);
                list.add(dim);
            }
        }
        return list;
    }

    // ===== 포탈 데이터 관리 =====
    public void savePortals(List<Portal> portals) {
        File file = new File(dataFolder, "portals.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        for (Portal p : portals) {
            String key = p.getName();
            yaml.set("portals." + key + ".name", p.getName());
            yaml.set("portals." + key + ".from", p.getFromDimension());
            yaml.set("portals." + key + ".to", p.getToDimension());
            yaml.set("portals." + key + ".world", p.getWorldName());
            yaml.set("portals." + key + ".location.x", p.getLocation().getX());
            yaml.set("portals." + key + ".location.y", p.getLocation().getY());
            yaml.set("portals." + key + ".location.z", p.getLocation().getZ());
            yaml.set("portals." + key + ".type", p.getType().toString());
            yaml.set("portals." + key + ".cost", p.getCost());
            yaml.set("portals." + key + ".active", p.isActive());
            yaml.set("portals." + key + ".created", System.currentTimeMillis());
        }
        try { yaml.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public List<Portal> loadPortals() {
        File file = new File(dataFolder, "portals.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<Portal> list = new ArrayList<>();
        if (yaml.contains("portals")) {
            for (String key : yaml.getConfigurationSection("portals").getKeys(false)) {
                String name = yaml.getString("portals." + key + ".name");
                String from = yaml.getString("portals." + key + ".from");
                String to = yaml.getString("portals." + key + ".to");
                String world = yaml.getString("portals." + key + ".world");
                double x = yaml.getDouble("portals." + key + ".location.x");
                double y = yaml.getDouble("portals." + key + ".location.y");
                double z = yaml.getDouble("portals." + key + ".location.z");
                String typeStr = yaml.getString("portals." + key + ".type", "FIXED");
                int cost = yaml.getInt("portals." + key + ".cost", 100);
                boolean active = yaml.getBoolean("portals." + key + ".active", true);
                int id = Math.abs(name.hashCode());
                org.bukkit.Location loc = new org.bukkit.Location(org.bukkit.Bukkit.getWorld(world), x, y, z);
                Portal p = new Portal(id, name, from, to, world, loc,
                        com.multiverse.core.models.enums.PortalType.valueOf(typeStr), cost, active);
                list.add(p);
            }
        }
        return list;
    }

    // ===== 균형도 로그 관리 =====
    public void addBalanceLog(BalanceLog log) {
        List<BalanceLog> logs = loadBalanceLogs(100);
        logs.add(log);
        if (logs.size() > 100) logs = logs.subList(logs.size() - 100, logs.size());
        File file = new File(dataFolder, "balance_logs.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        int idx = 0;
        for (BalanceLog l : logs) {
            yaml.set("logs." + idx + ".dimension", l.getDimension());
            yaml.set("logs." + idx + ".old-value", l.getOldValue());
            yaml.set("logs." + idx + ".new-value", l.getNewValue());
            yaml.set("logs." + idx + ".delta", l.getDelta());
            yaml.set("logs." + idx + ".reason", l.getReason());
            yaml.set("logs." + idx + ".changed-by", l.getChangedBy());
            yaml.set("logs." + idx + ".timestamp", l.getTimestamp());
            idx++;
        }
        try { yaml.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public List<BalanceLog> loadBalanceLogs(int limit) {
        File file = new File(dataFolder, "balance_logs.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<BalanceLog> logs = new ArrayList<>();
        if (yaml.contains("logs")) {
            for (String key : yaml.getConfigurationSection("logs").getKeys(false)) {
                String dim = yaml.getString("logs." + key + ".dimension");
                int oldVal = yaml.getInt("logs." + key + ".old-value");
                int newVal = yaml.getInt("logs." + key + ".new-value");
                int delta = yaml.getInt("logs." + key + ".delta");
                String reason = yaml.getString("logs." + key + ".reason");
                String changer = yaml.getString("logs." + key + ".changed-by");
                long ts = yaml.getLong("logs." + key + ".timestamp");
                logs.add(new BalanceLog(dim, oldVal, newVal, delta, reason, changer, ts));
            }
        }
        return logs.size() > limit ? logs.subList(logs.size() - limit, logs.size()) : logs;
    }

    // ===== 웨이포인트 관리 =====
    public void saveWaypoints(Map<UUID, List<Waypoint>> waypoints) {
        File file = new File(dataFolder, "waypoints.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<UUID, List<Waypoint>> entry : waypoints.entrySet()) {
            String uuidStr = entry.getKey().toString();
            int idx = 0;
            for (Waypoint w : entry.getValue()) {
                String key = "waypoints." + uuidStr + ".waypoint-" + idx;
                yaml.set(key + ".name", w.getName());
                yaml.set(key + ".dimension", w.getDimensionId());
                yaml.set(key + ".world", w.getLocation().getWorld().getName());
                yaml.set(key + ".location.x", w.getLocation().getX());
                yaml.set(key + ".location.y", w.getLocation().getY());
                yaml.set(key + ".location.z", w.getLocation().getZ());
                yaml.set(key + ".created", w.getCreatedAt());
                idx++;
            }
        }
        try { yaml.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public Map<UUID, List<Waypoint>> loadWaypoints() {
        File file = new File(dataFolder, "waypoints.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        Map<UUID, List<Waypoint>> map = new HashMap<>();
        if (yaml.contains("waypoints")) {
            for (String uuidStr : yaml.getConfigurationSection("waypoints").getKeys(false)) {
                List<Waypoint> wps = new ArrayList<>();
                for (String key : yaml.getConfigurationSection("waypoints." + uuidStr).getKeys(false)) {
                    String name = yaml.getString("waypoints." + uuidStr + "." + key + ".name");
                    String dim = yaml.getString("waypoints." + uuidStr + "." + key + ".dimension");
                    String world = yaml.getString("waypoints." + uuidStr + "." + key + ".world");
                    double x = yaml.getDouble("waypoints." + uuidStr + "." + key + ".location.x");
                    double y = yaml.getDouble("waypoints." + uuidStr + "." + key + ".location.y");
                    double z = yaml.getDouble("waypoints." + uuidStr + "." + key + ".location.z");
                    long created = yaml.getLong("waypoints." + uuidStr + "." + key + ".created");
                    org.bukkit.Location loc = new org.bukkit.Location(org.bukkit.Bukkit.getWorld(world), x, y, z);
                    Waypoint wp = new Waypoint(Math.abs(name.hashCode()), UUID.fromString(uuidStr), name, dim, loc, created);
                    wps.add(wp);
                }
                map.put(UUID.fromString(uuidStr), wps);
            }
        }
        return map;
    }

    // ===== 쿨다운 관리 =====
    public void saveCooldowns(Map<UUID, Long> cooldowns) {
        File file = new File(dataFolder, "cooldowns.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<UUID, Long> entry : cooldowns.entrySet()) {
            yaml.set("cooldowns." + entry.getKey().toString() + ".end-time", entry.getValue());
        }
        try { yaml.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public Map<UUID, Long> loadCooldowns() {
        File file = new File(dataFolder, "cooldowns.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        Map<UUID, Long> map = new HashMap<>();
        if (yaml.contains("cooldowns")) {
            for (String uuidStr : yaml.getConfigurationSection("cooldowns").getKeys(false)) {
                long end = yaml.getLong("cooldowns." + uuidStr + ".end-time");
                map.put(UUID.fromString(uuidStr), end);
            }
        }
        return map;
    }

    // ===== 융합 상태 관리 =====
    public void saveFusionStatus(FusionStatus status) {
        File file = new File(dataFolder, "fusion.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("fusion.is-fused", status.isFused());
        yaml.set("fusion.current-stage", status.getCurrentStage());
        yaml.set("fusion.stage-start-time", status.getStageStartTime());
        yaml.set("fusion.stage-end-time", status.getStageEndTime());
        try { yaml.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public FusionStatus loadFusionStatus() {
        File file = new File(dataFolder, "fusion.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        boolean fused = yaml.getBoolean("fusion.is-fused", false);
        int stage = yaml.getInt("fusion.current-stage", 0);
        Long start = yaml.contains("fusion.stage-start-time") ? yaml.getLong("fusion.stage-start-time") : null;
        Long end = yaml.contains("fusion.stage-end-time") ? yaml.getLong("fusion.stage-end-time") : null;
        return new FusionStatus(fused, stage, start, end);
    }

    // ===== 플레이어 데이터 관리 =====
    public void savePlayerData(UUID uuid, PlayerDimensionData data) {
        File dir = new File(plugin.getDataFolder(), "players");
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, uuid + ".yml");
        YamlConfiguration yaml = new YamlConfiguration();

        yaml.set("player.uuid", data.getUuid().toString());
        yaml.set("player.name", data.getName());

        for (Map.Entry<String, DimensionVisitData> entry : data.getDimensionData().entrySet()) {
            String dim = entry.getKey();
            DimensionVisitData dv = entry.getValue();
            yaml.set("dimensions." + dim + ".last-visit", dv.getLastVisit());
            yaml.set("dimensions." + dim + ".visit-count", dv.getVisitCount());
            yaml.set("dimensions." + dim + ".discovered-regions", dv.getDiscoveredRegions());
        }
        if (data.getWarmup() != null) {
            yaml.set("warmup.active", data.getWarmup().isActive());
            yaml.set("warmup.destination", data.getWarmup().getDestination());
            yaml.set("warmup.start-time", data.getWarmup().getStartTime());
        }
        try { yaml.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public PlayerDimensionData loadPlayerData(UUID uuid) {
        File dir = new File(plugin.getDataFolder(), "players");
        File file = new File(dir, uuid + ".yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        String name = yaml.getString("player.name", "");
        PlayerDimensionData data = new PlayerDimensionData(uuid, name);

        if (yaml.contains("dimensions")) {
            for (String dim : yaml.getConfigurationSection("dimensions").getKeys(false)) {
                long lastVisit = yaml.getLong("dimensions." + dim + ".last-visit", 0L);
                int visitCount = yaml.getInt("dimensions." + dim + ".visit-count", 0);
                List<String> regions = yaml.getStringList("dimensions." + dim + ".discovered-regions");
                DimensionVisitData dv = new DimensionVisitData(lastVisit, visitCount, regions);
                data.getDimensionData().put(dim, dv);
            }
        }
        if (yaml.contains("warmup")) {
            boolean active = yaml.getBoolean("warmup.active", false);
            String dest = yaml.getString("warmup.destination", null);
            long startTime = yaml.getLong("warmup.start-time", 0L);
            WarmupData wd = new WarmupData(active, dest, startTime);
            data.setWarmup(wd);
        }
        return data;
    }

    // ===== 자동 저장 =====
    public void scheduleAutoSave() {
        int intervalSec = plugin.getConfig().getInt("data.auto-save-interval", 300);
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(
                plugin, this::save, intervalSec * 20L, intervalSec * 20L
        );
    }
}