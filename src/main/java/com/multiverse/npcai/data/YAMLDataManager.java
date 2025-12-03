package com.multiverse.npcai.data;

import com.multiverse.npcai.models.*;
import com.multiverse.npcai.models.enums.*;
import com.multiverse.npcai.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * YAML 기반 실 데이터 저장/로드 구현체
 */
public class YAMLDataManager extends DataManager {

    private final Plugin plugin;
    private final ConfigUtil config;
    // 폴더/파일 캐시
    private final File baseDir;
    private final Map<Integer, NPCData> npcCache = new HashMap<>();
    private final Map<UUID, List<Reputation>> reputationCache = new HashMap<>();
    private final Map<String, Shop> shopCache = new HashMap<>();
    private final Map<Integer, SkillTrainer> trainerCache = new HashMap<>();

    public YAMLDataManager(Plugin plugin, ConfigUtil config) {
        this.plugin = plugin;
        this.config = config;
        this.baseDir = new File(plugin.getDataFolder(), "data");
        if (!baseDir.exists()) baseDir.mkdirs();
        loadAll();
    }

    // NPC -------------------------------------------------
    @Override
    public List<NPCData> loadAllNPCs() {
        File npcsDir = new File(baseDir, "npcs");
        if (!npcsDir.exists()) npcsDir.mkdirs();
        List<NPCData> npcs = new ArrayList<>();
        for (File f : Objects.requireNonNull(npcsDir.listFiles())) {
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
            NPCData npc = NPCData.fromYAML(yml);
            npcs.add(npc);
            npcCache.put(npc.getNpcId(), npc);
        }
        return npcs;
    }

    @Override
    public void saveNPC(NPCData npc) {
        File npcsDir = new File(baseDir, "npcs");
        if (!npcsDir.exists()) npcsDir.mkdirs();
        File file = new File(npcsDir, npc.getNpcId() + ".yml");
        YamlConfiguration yml = npc.toYAML();
        try { yml.save(file); } catch (IOException e) { e.printStackTrace(); }
        npcCache.put(npc.getNpcId(), npc);
    }

    @Override
    public void deleteNPC(int npcId) {
        File npcsDir = new File(baseDir, "npcs");
        if (!npcsDir.exists()) return;
        File file = new File(npcsDir, npcId + ".yml");
        if (file.exists()) file.delete();
        npcCache.remove(npcId);
    }

    // 호감도 -------------------------------------------------
    @Override
    public Reputation getReputation(UUID playerUUID, int npcId) {
        List<Reputation> reps = getAllReputations(playerUUID);
        for (Reputation r : reps) if (r.getNpcId() == npcId) return r;
        return null;
    }
    @Override
    public List<Reputation> getAllReputations(UUID playerUUID) {
        File playersDir = new File(plugin.getDataFolder(), "players");
        if (!playersDir.exists()) playersDir.mkdirs();
        File file = new File(playersDir, playerUUID + ".yml");
        List<Reputation> reps = new ArrayList<>();
        if (!file.exists()) return reps;
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        if (!yml.contains("reputations")) return reps;
        for (String key : yml.getConfigurationSection("reputations").getKeys(false)) {
            reps.add(Reputation.fromYAML(yml.getConfigurationSection("reputations." + key)));
        }
        reputationCache.put(playerUUID, reps);
        return reps;
    }
    @Override
    public void saveReputation(Reputation rep) {
        File playersDir = new File(plugin.getDataFolder(), "players");
        if (!playersDir.exists()) playersDir.mkdirs();
        File file = new File(playersDir, rep.getPlayerUUID() + ".yml");
        YamlConfiguration yml = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
        String path = "reputations." + rep.getNpcId();
        rep.toYAML(yml.createSection(path));
        try { yml.save(file); } catch (IOException e) { e.printStackTrace(); }
        reputationCache.computeIfAbsent(rep.getPlayerUUID(), k -> new ArrayList<>());
    }

    // 상점 -------------------------------------------------
    @Override
    public Shop getShop(String shopId) {
        File shopsDir = new File(baseDir, "shops");
        File file = new File(shopsDir, shopId + ".yml");
        if (!file.exists()) return null;
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        Shop shop = Shop.fromYAML(yml);
        shopCache.put(shopId, shop);
        return shop;
    }
    @Override
    public Shop getShopByNPC(int npcId) {
        for (Shop s : getAllShops()) if (s.getNpcId() == npcId) return s;
        return null;
    }
    @Override
    public void saveShop(Shop shop) {
        File shopsDir = new File(baseDir, "shops");
        if (!shopsDir.exists()) shopsDir.mkdirs();
        File file = new File(shopsDir, shop.getShopId() + ".yml");
        YamlConfiguration yml = shop.toYAML();
        try { yml.save(file); } catch (IOException e) { e.printStackTrace(); }
        shopCache.put(shop.getShopId(), shop);
    }
    @Override
    public List<Shop> getAllShops() {
        File shopsDir = new File(baseDir, "shops");
        if (!shopsDir.exists()) shopsDir.mkdirs();
        List<Shop> shops = new ArrayList<>();
        for (File f : Objects.requireNonNull(shopsDir.listFiles())) {
            shops.add(Shop.fromYAML(YamlConfiguration.loadConfiguration(f)));
        }
        return shops;
    }
    @Override
    public void deleteShop(String shopId) {
        File shopsDir = new File(baseDir, "shops");
        File file = new File(shopsDir, shopId + ".yml");
        if (file.exists()) file.delete();
        shopCache.remove(shopId);
    }

    // 트레이너 -------------------------------------------------
    @Override
    public SkillTrainer getTrainer(int npcId) {
        File trainersDir = new File(baseDir, "trainers");
        File file = new File(trainersDir, npcId + ".yml");
        if (!file.exists()) return null;
        SkillTrainer trainer = SkillTrainer.fromYAML(YamlConfiguration.loadConfiguration(file));
        trainerCache.put(npcId, trainer);
        return trainer;
    }
    @Override
    public void saveTrainer(SkillTrainer trainer) {
        File trainersDir = new File(baseDir, "trainers");
        if (!trainersDir.exists()) trainersDir.mkdirs();
        File file = new File(trainersDir, trainer.getNpcId() + ".yml");
        YamlConfiguration yml = trainer.toYAML();
        try { yml.save(file); } catch (IOException e) { e.printStackTrace(); }
        trainerCache.put(trainer.getNpcId(), trainer);
    }

    // 스킬 학습 -------------------------------------------------
    @Override
    public void startSkillLearning(UUID playerUUID, TrainableSkill skill, int npcId, int seconds) {
        // players/<UUID>.yml 안에 skill-learning.current 에 기록
        File playersDir = new File(plugin.getDataFolder(), "players");
        File file = new File(playersDir, playerUUID + ".yml");
        YamlConfiguration yml = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
        List<Map<String, Object>> curSkills = yml.getList("skill-learning.current", new ArrayList<>());
        Map<String, Object> rec = new HashMap<>();
        rec.put("skill-id", skill.getSkillId());
        rec.put("npc-id", npcId);
        long now = System.currentTimeMillis();
        rec.put("start-time", now);
        rec.put("end-time", now + seconds * 1000L);
        curSkills.add(rec);
        yml.set("skill-learning.current", curSkills);
        try { yml.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
    @Override
    public void completeSkillLearning(UUID playerUUID, String skillId) {
        // 완료 기록, player/<UUID>.yml에 기록 내용 변경
        File playersDir = new File(plugin.getDataFolder(), "players");
        File file = new File(playersDir, playerUUID + ".yml");
        YamlConfiguration yml = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
        List<Map<String, Object>> curSkills = yml.getList("skill-learning.current", new ArrayList<>());
        curSkills.removeIf(m -> skillId.equals(m.get("skill-id")));
        List<Map<String, Object>> completed = yml.getList("skill-learning.completed", new ArrayList<>());
        Map<String, Object> rec = new HashMap<>();
        rec.put("skill-id", skillId);
        rec.put("learned-at", System.currentTimeMillis());
        completed.add(rec);
        yml.set("skill-learning.current", curSkills);
        yml.set("skill-learning.completed", completed);
        try { yml.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
    @Override
    public List<String> getLearningSkills(UUID playerUUID) {
        File playersDir = new File(plugin.getDataFolder(), "players");
        File file = new File(playersDir, playerUUID + ".yml");
        YamlConfiguration yml = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
        List<Map<String, Object>> curSkills = yml.getList("skill-learning.current", new ArrayList<>());
        List<String> list = new ArrayList<>();
        for (Map<String, Object> m : curSkills) {
            list.add((String) m.get("skill-id"));
        }
        return list;
    }
    @Override
    public List<String> getCompletedSkills(UUID playerUUID) {
        File playersDir = new File(plugin.getDataFolder(), "players");
        File file = new File(playersDir, playerUUID + ".yml");
        YamlConfiguration yml = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
        List<Map<String, Object>> completed = yml.getList("skill-learning.completed", new ArrayList<>());
        List<String> list = new ArrayList<>();
        for (Map<String, Object> m : completed) {
            list.add((String) m.get("skill-id"));
        }
        return list;
    }
    @Override
    public int getDailyLearnedSkillsCount(UUID playerUUID, int npcId) {
        File playersDir = new File(plugin.getDataFolder(), "players");
        File file = new File(playersDir, playerUUID + ".yml");
        YamlConfiguration yml = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
        if (!yml.contains("skill-learning.daily-learned." + npcId + ".count")) return 0;
        return yml.getInt("skill-learning.daily-learned." + npcId + ".count");
    }

    // 대화 ----------------------------------------------------------
    @Override
    public Map<String, DialogueNode> loadDialogueTree(int npcId, String filePath) {
        File dialoguesDir = new File(baseDir, "dialogues");
        File file = new File(dialoguesDir, npcId + ".yml");
        if (!file.exists()) file = new File(dialoguesDir, filePath); // 직접 경로 입력도 대응
        if (!file.exists()) return new HashMap<>();
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        Map<String, DialogueNode> tree = DialogueNode.fromYAMLTree(yml.getConfigurationSection("dialogue-tree.nodes"));
        return tree;
    }
    @Override
    public String getDialogueStartNode(int npcId, String filePath) {
        File dialoguesDir = new File(baseDir, "dialogues");
        File file = new File(dialoguesDir, npcId + ".yml");
        if (!file.exists()) file = new File(dialoguesDir, filePath);
        if (!file.exists()) return null;
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml.getString("dialogue-tree.start-node");
    }

    // 캐싱/로딩/저장 -------------------------------------------------
    @Override
    public void loadAll() {
        loadAllNPCs();
        // load others as needed
    }

    @Override
    public void saveAll() {
        for (NPCData npc : npcCache.values()) saveNPC(npc);
        for (Shop s : shopCache.values()) saveShop(s);
        for (SkillTrainer t : trainerCache.values()) saveTrainer(t);
        // etc.
    }

    @Override
    public void reload() {
        npcCache.clear();
        shopCache.clear();
        trainerCache.clear();
        reputationCache.clear();
        loadAll();
    }
}