package com.multiverse.death.data;

import com.multiverse.death.DeathAndRebirthCore;
import com.multiverse.death.models.*;
import com.multiverse.death.models.enums.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class YAMLDataManager implements DataManager {

    private final DeathAndRebirthCore plugin;
    private final File dataDir;
    private final File playerDir;
    private final File locationsFile;
    private final File npcsFile;
    private final File soulCoinTotalsFile;

    private Map<LocationType, NetherRealmLocation> realmLocationsCache = new EnumMap<>(LocationType.class);
    private Map<String, NetherRealmNPC> npcsCache = new HashMap<>();

    public YAMLDataManager(DeathAndRebirthCore plugin) {
        this.plugin = plugin;
        this.dataDir = new File(plugin.getDataFolder(), "data");
        this.playerDir = new File(plugin.getDataFolder(), "players");
        this.locationsFile = new File(dataDir, "nether_realm_locations.yml");
        this.npcsFile = new File(dataDir, "npcs.yml");
        this.soulCoinTotalsFile = new File(dataDir, "soul_coin_totals.yml");
        loadLocations();
        loadNPCs();
    }

    // -------- 사망 기록 --------
    @Override
    public void saveDeathRecord(Player player, DeathRecord deathRecord) {
        File file = getPlayerFile(player);
        Map<String, Object> yaml = loadYaml(file);
        Map<String, Object> death = getNestedMap(yaml, "death");

        death.put("death-count", ((int) death.getOrDefault("death-count", 0)) + 1);

        List<Map<String, Object>> history = (List<Map<String, Object>>) death.getOrDefault("death-history", new ArrayList<>());
        if (history.size() >= 10) history.remove(0);

        Map<String, Object> recordMap = new HashMap<>();
        recordMap.put("time", deathRecord.getDeathTime());
        recordMap.put("dimension", deathRecord.getDimension());
        recordMap.put("cause", deathRecord.getCause().name());

        history.add(recordMap);
        death.put("death-history", history);

        // last-death
        death.put("last-death", recordToMap(deathRecord));

        yaml.put("death", death);
        saveYaml(file, yaml);
    }

    @Override
    public DeathRecord getLastDeathRecord(Player player) {
        Map<String, Object> yaml = loadYaml(getPlayerFile(player));
        Map<String, Object> death = getNestedMap(yaml, "death");
        Map<String, Object> rec = (Map<String, Object>) death.get("last-death");
        if (rec == null) return null;
        return mapToDeathRecord(rec);
    }

    @Override
    public List<DeathRecord> getDeathHistory(Player player, int limit) {
        Map<String, Object> yaml = loadYaml(getPlayerFile(player));
        Map<String, Object> death = getNestedMap(yaml, "death");
        List<DeathRecord> result = new ArrayList<>();
        List<Map<String,Object>> list = (List<Map<String,Object>>) death.getOrDefault("death-history", new ArrayList<>());
        for (int i = Math.max(0, list.size()-limit); i < list.size(); i++) {
            Map<String,Object> rec = list.get(i);
            DeathRecord r = new DeathRecord();
            r.setDeathTime(((Number)rec.get("time")).longValue());
            r.setDimension((String)rec.get("dimension"));
            r.setCause(DeathCause.valueOf((String)rec.get("cause")));
            result.add(r);
        }
        return result;
    }

    @Override
    public int getDeathCount(Player player) {
        Map<String, Object> yaml = loadYaml(getPlayerFile(player));
        Map<String, Object> death = getNestedMap(yaml, "death");
        return (int) death.getOrDefault("death-count", 0);
    }

    @Override
    public void clearDeathLocation(Player player) {
        Map<String, Object> yaml = loadYaml(getPlayerFile(player));
        Map<String, Object> death = getNestedMap(yaml, "death");
        death.remove("last-death");
        yaml.put("death", death);
        saveYaml(getPlayerFile(player), yaml);
    }

    @Override
    public boolean isPlayerSpiritRace(Player player) {
        Map<String, Object> yaml = loadYaml(getPlayerFile(player));
        Map<String, Object> race = getNestedMap(yaml, "player");
        return "spirit".equalsIgnoreCase((String) race.getOrDefault("race", ""));
    }

    // -------- 명계 위치 --------
    @Override
    public NetherRealmLocation getNetherRealmLocation(LocationType type) {
        return realmLocationsCache.get(type);
    }

    @Override
    public void setNetherRealmLocation(LocationType type, Location loc) {
        NetherRealmLocation realmLoc = new NetherRealmLocation();
        realmLoc.setId(type.name().toLowerCase());
        realmLoc.setType(type);
        realmLoc.setName(type.name());
        realmLoc.setLocation(loc);
        realmLocationsCache.put(type, realmLoc);
        saveLocations();
    }

    @Override
    public List<NetherRealmLocation> getAllNetherRealmLocations() {
        return new ArrayList<>(realmLocationsCache.values());
    }

    private void loadLocations() {
        // YAML 파싱
        realmLocationsCache.clear();
        Map<String, Object> yaml = loadYaml(locationsFile);
        Map<String, Object> locations = getNestedMap(yaml, "locations");
        for (String key : locations.keySet()) {
            Map<String,Object> locMap = getNestedMap(locations, key);
            NetherRealmLocation loc = new NetherRealmLocation();
            loc.setId(key);
            loc.setName((String)locMap.get("name"));
            loc.setType(LocationType.valueOf((String)locMap.get("type")));
            World world = Bukkit.getWorld((String)locMap.get("world"));
            double x = doubleVal(locMap.get("x"));
            double y = doubleVal(locMap.get("y"));
            double z = doubleVal(locMap.get("z"));
            float yaw = floatVal(locMap.get("yaw"));
            float pitch = floatVal(locMap.get("pitch"));
            loc.setLocation(new Location(world, x, y, z, yaw, pitch));
            realmLocationsCache.put(loc.getType(), loc);
        }
    }

    private void saveLocations() {
        Map<String, Object> locations = new LinkedHashMap<>();
        for (Map.Entry<LocationType, NetherRealmLocation> entry : realmLocationsCache.entrySet()) {
            NetherRealmLocation loc = entry.getValue();
            Map<String,Object> locMap = new LinkedHashMap<>();
            Location l = loc.getLocation();
            locMap.put("name", loc.getName());
            locMap.put("type", loc.getType().name());
            locMap.put("world", l.getWorld().getName());
            locMap.put("x", l.getX());
            locMap.put("y", l.getY());
            locMap.put("z", l.getZ());
            locMap.put("yaw", l.getYaw());
            locMap.put("pitch", l.getPitch());
            locations.put(loc.getId(), locMap);
        }
        Map<String,Object> root = new HashMap<>();
        root.put("locations", locations);
        saveYaml(locationsFile, root);
    }

    // -------- 플레이어 보험 --------
    @Override
    public Insurance getPlayerInsurance(Player player) {
        Map<String, Object> yaml = loadYaml(getPlayerFile(player));
        Map<String, Object> insurance = getNestedMap(yaml, "insurance");
        if (insurance.isEmpty() || !Boolean.valueOf(String.valueOf(insurance.getOrDefault("active", false)))) return null;

        Insurance ins = new Insurance();
        ins.setPlayerUUID(player.getUniqueId());
        ins.setType(InsuranceType.valueOf((String) insurance.get("type")));
        ins.setPurchaseDate(longVal(insurance.get("purchase-date")));
        ins.setExpiryDate(longVal(insurance.get("expiry-date")));
        ins.setRemainingUses(intVal(insurance.get("remaining-uses")));
        ins.setActive(Boolean.valueOf(String.valueOf(insurance.get("active"))));
        return ins;
    }

    @Override
    public void savePlayerInsurance(Player player, Insurance insurance) {
        Map<String, Object> yaml = loadYaml(getPlayerFile(player));
        Map<String, Object> ins = getNestedMap(yaml, "insurance");
        ins.put("active", insurance.isActive());
        ins.put("type", insurance.getType().name());
        ins.put("purchase-date", insurance.getPurchaseDate());
        ins.put("expiry-date", insurance.getExpiryDate());
        ins.put("remaining-uses", insurance.getRemainingUses());
        yaml.put("insurance", ins);
        saveYaml(getPlayerFile(player), yaml);
    }

    // -------- 부활 퀘스트 --------
    @Override
    public RevivalQuest getRevivalQuest(Player player) {
        Map<String,Object> yaml = loadYaml(getPlayerFile(player));
        Map<String,Object> revival = getNestedMap(yaml, "revival");
        RevivalQuest quest = new RevivalQuest();
        quest.setPlayerUUID(player.getUniqueId());
        String t = (String) revival.get("quest-type");
        if (t == null || "null".equalsIgnoreCase(t)) return null;
        quest.setType(QuestType.valueOf(t));
        quest.setCompleted(Boolean.valueOf(String.valueOf(revival.getOrDefault("quest-completed", false))));
        quest.setStartTime(longVal(revival.get("quest-start-time")));
        quest.setProgress((Map<String, Integer>) revival.getOrDefault("quest-progress", new HashMap<String,Integer>()));
        quest.setRequired((Map<String, Integer>) revival.getOrDefault("quest-required", new HashMap<String,Integer>()));
        return quest;
    }

    @Override
    public void saveRevivalQuest(Player player, RevivalQuest quest) {
        Map<String,Object> yaml = loadYaml(getPlayerFile(player));
        Map<String,Object> revival = getNestedMap(yaml, "revival");
        revival.put("quest-type", quest.getType().name());
        revival.put("quest-completed", quest.isCompleted());
        revival.put("quest-start-time", quest.getStartTime());
        revival.put("quest-progress", quest.getProgress());
        revival.put("quest-required", quest.getRequired());
        yaml.put("revival", revival);
        saveYaml(getPlayerFile(player), yaml);
    }

    // -------- 소울 코인 --------
    @Override
    public double getSoulCoinBalance(Player player) {
        Map<String,Object> yaml = loadYaml(getPlayerFile(player));
        Map<String,Object> soul = getNestedMap(yaml, "soul-coin");
        return doubleVal(soul.getOrDefault("balance", 0.0));
    }

    @Override
    public void setSoulCoinBalance(Player player, double balance) {
        Map<String,Object> yaml = loadYaml(getPlayerFile(player));
        Map<String,Object> soul = getNestedMap(yaml, "soul-coin");
        soul.put("balance", balance);
        yaml.put("soul-coin", soul);
        saveYaml(getPlayerFile(player), yaml);
    }

    @Override
    public double getTotalSoulCoinBurned() {
        Map<String,Object> yaml = loadYaml(soulCoinTotalsFile);
        Map<String,Object> stat = getNestedMap(yaml, "statistics");
        return doubleVal(stat.getOrDefault("total-burned", 0.0));
    }

    @Override
    public void setTotalSoulCoinBurned(double burned) {
        Map<String,Object> yaml = loadYaml(soulCoinTotalsFile);
        Map<String,Object> stat = getNestedMap(yaml, "statistics");
        stat.put("total-burned", burned);
        yaml.put("statistics", stat);
        saveYaml(soulCoinTotalsFile, yaml);
    }

    @Override
    public double getTotalSoulCoinCirculation() {
        Map<String,Object> yaml = loadYaml(soulCoinTotalsFile);
        Map<String,Object> stat = getNestedMap(yaml, "statistics");
        return doubleVal(stat.getOrDefault("total-circulation", 0.0));
    }
    @Override
    public double getTotalSoulCoinEarned() {
        Map<String,Object> yaml = loadYaml(soulCoinTotalsFile);
        Map<String,Object> stat = getNestedMap(yaml, "statistics");
        return doubleVal(stat.getOrDefault("total-earned", 0.0));
    }
    @Override
    public double getTotalSoulCoinSpent() {
        Map<String,Object> yaml = loadYaml(soulCoinTotalsFile);
        Map<String,Object> stat = getNestedMap(yaml, "statistics");
        return doubleVal(stat.getOrDefault("total-spent", 0.0));
    }

    @Override
    public void recordGlobalBurn(String reason, double amount) {
        Map<String,Object> yaml = loadYaml(soulCoinTotalsFile);
        Map<String,Object> stat = getNestedMap(yaml, "statistics");
        Map<String,Object> burns = getNestedMap(stat, "burn-sources");
        burns.put(reason, doubleVal(burns.getOrDefault(reason, 0.0)) + amount);
        stat.put("burn-sources", burns);
        yaml.put("statistics", stat);
        saveYaml(soulCoinTotalsFile, yaml);
    }

    @Override
    public void addSoulCoinTransaction(Player player, SoulCoinTransaction transaction) {
        Map<String,Object> yaml = loadYaml(getPlayerFile(player));
        Map<String,Object> soul = getNestedMap(yaml, "soul-coin");
        List<Map<String,Object>> txs = (List<Map<String,Object>>) soul.getOrDefault("transactions", new ArrayList<Map<String,Object>>());
        if (txs.size() >= 50) txs.remove(0);
        Map<String,Object> tx = new LinkedHashMap<>();
        tx.put("time", transaction.getTimestamp());
        tx.put("type", transaction.getType().name());
        tx.put("amount", transaction.getAmount());
        tx.put("reason", transaction.getReason());
        txs.add(tx);
        soul.put("transactions", txs);
        yaml.put("soul-coin", soul);
        saveYaml(getPlayerFile(player), yaml);
    }

    @Override
    public List<SoulCoinTransaction> getSoulCoinTransactions(Player player, int limit) {
        Map<String,Object> yaml = loadYaml(getPlayerFile(player));
        Map<String,Object> soul = getNestedMap(yaml, "soul-coin");
        List<SoulCoinTransaction> result = new ArrayList<>();
        List<Map<String,Object>> txs = (List<Map<String,Object>>) soul.getOrDefault("transactions", new ArrayList<Map<String,Object>>());
        for (int i = Math.max(0, txs.size()-limit); i < txs.size(); i++) {
            Map<String,Object> tx = txs.get(i);
            SoulCoinTransaction t = new SoulCoinTransaction();
            t.setTimestamp(longVal(tx.get("time")));
            t.setType(TransactionType.valueOf((String)tx.get("type")));
            t.setAmount(doubleVal(tx.get("amount")));
            t.setReason((String)tx.get("reason"));
            result.add(t);
        }
        return result;
    }

    // -------- NPC --------
    @Override
    public void spawnNPC(NetherRealmNPC npc) {
        npcsCache.put(npc.getId(), npc);
        saveNPCs();
    }
    @Override
    public void removeNPC(String id) {
        npcsCache.remove(id);
        saveNPCs();
    }
    @Override
    public NetherRealmNPC getNPC(String id) {
        return npcsCache.get(id);
    }
    @Override
    public List<NetherRealmNPC> getAllNPCs() {
        return new ArrayList<>(npcsCache.values());
    }
    private void loadNPCs() {
        npcsCache.clear();
        Map<String,Object> yaml = loadYaml(npcsFile);
        Map<String,Object> nmap = getNestedMap(yaml, "npcs");
        for (String key : nmap.keySet()) {
            Map<String,Object> npcMap = getNestedMap(nmap, key);
            NetherRealmNPC npc = new NetherRealmNPC();
            npc.setId(key);
            npc.setName((String)npcMap.get("name"));
            npc.setType(NPCType.valueOf((String)npcMap.get("type")));
            npc.setLocation(yamlToLocation((Map<String,Object>)npcMap.get("location")));
            npc.setDialogues((List<String>)npcMap.getOrDefault("dialogues", new ArrayList<String>()));
            npcsCache.put(key, npc);
        }
    }
    private void saveNPCs() {
        Map<String,Object> nmap = new LinkedHashMap<>();
        for (NetherRealmNPC npc : npcsCache.values()) {
            Map<String,Object> npcMap = new LinkedHashMap<>();
            npcMap.put("name", npc.getName());
            npcMap.put("type", npc.getType().name());
            npcMap.put("location", locationToYaml(npc.getLocation()));
            npcMap.put("dialogues", npc.getDialogues());
            nmap.put(npc.getId(), npcMap);
        }
        Map<String,Object> out = new HashMap<>();
        out.put("npcs", nmap);
        saveYaml(npcsFile, out);
    }

    // -------- 기타 --------
    @Override
    public List<Player> getAllOnlinePlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    @Override
    public void saveAll() {
        // 모든 플레이어 정보 파일 저장
        // 플레이어 관련 캐시 없음: 자동 저장됨
        saveLocations();
        saveNPCs();
    }

    // ----------- Util: File & YAML -----------
    private File getPlayerFile(Player player) {
        return new File(playerDir, player.getUniqueId().toString() + ".yml");
    }

    private Map<String,Object> loadYaml(File file) {
        if (!file.exists()) return new HashMap<>();
        try (InputStream in = new FileInputStream(file)) {
            Yaml yaml = new Yaml();
            Object obj = yaml.load(in);
            return obj == null ? new HashMap<>() : (Map<String,Object>)obj;
        } catch(Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private void saveYaml(File file, Map<String,Object> data) {
        try (Writer writer = new FileWriter(file)) {
            Yaml yaml = new Yaml();
            yaml.dump(data, writer);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String,Object> getNestedMap(Map<String,Object> map, String key) {
        Object obj = map.get(key);
        if (obj instanceof Map) return (Map<String,Object>) obj;
        if (obj == null) return new HashMap<>();
        return new HashMap<>();
    }

    private Map<String,Object> recordToMap(DeathRecord record) {
        Map<String,Object> map = new HashMap<>();
        map.put("time", record.getDeathTime());
        map.put("dimension", record.getDimension());
        map.put("location", locationToYaml(record.getDeathLocation()));
        map.put("cause", record.getCause().name());
        map.put("exp-lost", record.getExpLost());
        map.put("money-lost", record.getMoneyLost());
        map.put("dropped-items", itemsToYaml(record.getDroppedItems()));
        map.put("had-insurance", record.isHasInsurance());
        map.put("insurance-type", record.getInsuranceType() != null ? record.getInsuranceType().name() : null);
        return map;
    }

    private DeathRecord mapToDeathRecord(Map<String,Object> m) {
        DeathRecord r = new DeathRecord();
        r.setDeathTime(longVal(m.get("time")));
        r.setDimension((String)m.get("dimension"));
        r.setDeathLocation(yamlToLocation((Map<String,Object>)m.get("location")));
        r.setCause(DeathCause.valueOf((String)m.get("cause")));
        r.setExpLost(intVal(m.get("exp-lost")));
        r.setMoneyLost(doubleVal(m.get("money-lost")));
        r.setDroppedItems(yamlToItems((List<String>)m.get("dropped-items")));
        r.setHasInsurance(Boolean.valueOf(String.valueOf(m.getOrDefault("had-insurance", false))));
        String it = (String)m.get("insurance-type");
        r.setInsuranceType(it == null ? null : InsuranceType.valueOf(it));
        return r;
    }

    private Map<String,Object> locationToYaml(Location loc) {
        Map<String,Object> map = new HashMap<>();
        map.put("world", loc.getWorld().getName());
        map.put("x", loc.getX());
        map.put("y", loc.getY());
        map.put("z", loc.getZ());
        map.put("yaw", loc.getYaw());
        map.put("pitch", loc.getPitch());
        return map;
    }
    private Location yamlToLocation(Map<String,Object> map) {
        World world = Bukkit.getWorld((String)map.get("world"));
        double x = doubleVal(map.get("x"));
        double y = doubleVal(map.get("y"));
        double z = doubleVal(map.get("z"));
        float yaw = map.containsKey("yaw") ? floatVal(map.get("yaw")) : 0f;
        float pitch = map.containsKey("pitch") ? floatVal(map.get("pitch")) : 0f;
        return new Location(world, x, y, z, yaw, pitch);
    }

    private List<String> itemsToYaml(List<ItemStack> items) {
        List<String> out = new ArrayList<>();
        if (items == null) return out;
        for (ItemStack item : items) {
            out.add(item.getType().name() + ":" + item.getAmount());
        }
        return out;
    }

    private List<ItemStack> yamlToItems(List<String> list) {
        List<ItemStack> out = new ArrayList<>();
        if (list == null) return out;
        for (String s : list) {
            String[] split = s.split(":");
            try {
                ItemStack item = new ItemStack(org.bukkit.Material.valueOf(split[0]), Integer.parseInt(split[1]));
                out.add(item);
            } catch(Exception ignore) {}
        }
        return out;
    }

    private int intVal(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Number) return ((Number)obj).intValue();
        try { return Integer.parseInt(obj.toString()); } catch(Exception ignored) {}
        return 0;
    }

    private double doubleVal(Object obj) {
        if (obj == null) return 0.0;
        if (obj instanceof Number) return ((Number)obj).doubleValue();
        try { return Double.parseDouble(obj.toString()); } catch(Exception ignored) {}
        return 0.0;
    }

    private long longVal(Object obj) {
        if (obj == null) return 0L;
        if (obj instanceof Number) return ((Number)obj).longValue();
        try { return Long.parseLong(obj.toString()); } catch(Exception ignored) {}
        return 0L;
    }

    private float floatVal(Object obj) {
        if (obj == null) return 0f;
        if (obj instanceof Number) return ((Number)obj).floatValue();
        try { return Float.parseFloat(obj.toString()); } catch(Exception ignored) {}
        return 0f;
    }
}