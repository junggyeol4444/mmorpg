package com.multiverse.party.data;

import com. multiverse.party. PartyCore;
import com. multiverse.party. models.*;
import com.multiverse.party.models.enums.*;
import org.bukkit. Bukkit;
import org.bukkit. OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org. bukkit.configuration. file.YamlConfiguration;

import java.io. File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java. util.concurrent.ConcurrentHashMap;
import java.util. logging.Level;

public class YAMLDataManager implements DataManager {

    private final PartyCore plugin;
    private final File partiesFolder;
    private final File playersFolder;
    private final File listingsFolder;
    private final File backupsFolder;

    private final Map<UUID, Party> partyCache;
    private final Map<UUID, PlayerPartyData> playerCache;
    private final Map<UUID, PartyListing> listingCache;
    private final Map<String, UUID> nameToUUIDCache;
    private final Map<UUID, String> uuidToNameCache;

    private boolean initialized;

    public YAMLDataManager(PartyCore plugin) {
        this.plugin = plugin;
        this.partiesFolder = new File(plugin.getDataFolder(), "parties");
        this.playersFolder = new File(plugin.getDataFolder(), "players");
        this.listingsFolder = new File(plugin.getDataFolder(), "listings");
        this.backupsFolder = new File(plugin.getDataFolder(), "backups");

        this.partyCache = new ConcurrentHashMap<>();
        this.playerCache = new ConcurrentHashMap<>();
        this.listingCache = new ConcurrentHashMap<>();
        this.nameToUUIDCache = new ConcurrentHashMap<>();
        this.uuidToNameCache = new ConcurrentHashMap<>();

        this.initialized = false;
    }

    @Override
    public void initialize() {
        createDirectories();
        initialized = true;
        plugin.getLogger().info("YAML 데이터 매니저 초기화 완료");
    }

    @Override
    public void shutdown() {
        saveAllParties();
        saveAllPlayerData();
        saveAllListings();
        
        partyCache.clear();
        playerCache. clear();
        listingCache.clear();
        
        initialized = false;
        plugin.getLogger().info("YAML 데이터 매니저 종료 완료");
    }

    private void createDirectories() {
        if (! partiesFolder.exists()) partiesFolder.mkdirs();
        if (!playersFolder. exists()) playersFolder.mkdirs();
        if (!listingsFolder.exists()) listingsFolder.mkdirs();
        if (!backupsFolder.exists()) backupsFolder.mkdirs();
    }

    @Override
    public void saveParty(Party party) {
        if (party == null) return;

        File file = new File(partiesFolder, party. getPartyId().toString() + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        try {
            config.set("party. party-id", party.getPartyId().toString());
            config. set("party.leader-id", party.getLeaderId().toString());
            config.set("party.party-name", party. getPartyName());

            List<String> memberStrings = new ArrayList<>();
            for (UUID member : party.getMembers()) {
                memberStrings. add(member.toString());
            }
            config.set("party.members", memberStrings);

            ConfigurationSection rolesSection = config. createSection("party.roles");
            for (Map.Entry<UUID, PartyRole> entry :  party.getRoles().entrySet()) {
                rolesSection.set(entry. getKey().toString(), entry.getValue().name());
            }

            config.set("party. max-members", party. getMaxMembers());
            config.set("party.settings. is-public", party.isPublic());
            config.set("party. settings.allow-invites", party.isAllowInvites());
            config.set("party.settings.privacy", party.getPrivacy().name());

            savePartyLevel(config, party. getPartyLevel());
            saveActiveBuffs(config, party. getActiveBuffs());

            config.set("party.loot-distribution", party.getLootDistribution().name());
            config.set("party. exp-distribution", party.getExpDistribution().name());

            savePartyStatistics(config, party.getStatistics());

            config.set("party. created-time", party.getCreatedTime());

            config.save(file);
            partyCache.put(party.getPartyId(), party);

        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "파티 저장 실패:  " + party.getPartyId(), e);
        }
    }

    private void savePartyLevel(YamlConfiguration config, PartyLevel partyLevel) {
        if (partyLevel == null) {
            partyLevel = new PartyLevel();
        }

        config.set("party.party-level. level", partyLevel.getLevel());
        config.set("party.party-level.experience", partyLevel.getExperience());
        config.set("party.party-level. skill-points", partyLevel.getSkillPoints());
        config.set("party. party-level.used-skill-points", partyLevel.getUsedSkillPoints());
        config.set("party.party-level.learned-skills", partyLevel. getLearnedSkills());
    }

    private void saveActiveBuffs(YamlConfiguration config, List<PartyBuff> buffs) {
        if (buffs == null || buffs.isEmpty()) {
            config.set("party. active-buffs", new ArrayList<>());
            return;
        }

        List<Map<String, Object>> buffList = new ArrayList<>();
        for (PartyBuff buff : buffs) {
            Map<String, Object> buffMap = new HashMap<>();
            buffMap.put("buff-id", buff.getBuffId());
            buffMap.put("name", buff.getName());
            buffMap. put("type", buff.getType().name());
            buffMap.put("effects", buff.getEffects());
            buffMap.put("required-members", buff.getRequiredMembers());
            buffMap.put("required-party-level", buff.getRequiredPartyLevel());
            buffMap. put("duration", buff.getDuration());
            buffMap.put("start-time", buff. getStartTime());
            buffMap.put("range", buff.getRange());
            buffList.add(buffMap);
        }
        config.set("party.active-buffs", buffList);
    }

    private void savePartyStatistics(YamlConfiguration config, PartyStatistics stats) {
        if (stats == null) {
            stats = new PartyStatistics();
        }

        config.set("party.statistics.total-play-time", stats. getTotalPlayTime());
        config.set("party.statistics. monsters-killed", stats. getMonstersKilled());
        config.set("party.statistics. bosses-killed", stats.getBossesKilled());
        config.set("party. statistics.total-damage", stats.getTotalDamage());
        config.set("party.statistics.total-healing", stats.getTotalHealing());
        config.set("party.statistics.dungeons-completed", stats. getDungeonsCompleted());
        config.set("party.statistics. quests-completed", stats.getQuestsCompleted());

        ConfigurationSection memberStatsSection = config. createSection("party. statistics.member-stats");
        for (Map.Entry<UUID, MemberStatistics> entry : stats.getMemberStats().entrySet()) {
            ConfigurationSection memberSection = memberStatsSection.createSection(entry.getKey().toString());
            MemberStatistics memberStats = entry.getValue();
            memberSection.set("damage-dealt", memberStats.getDamageDealt());
            memberSection.set("healing-done", memberStats.getHealingDone());
            memberSection.set("damage-taken", memberStats.getDamageTaken());
            memberSection.set("mvp-count", memberStats.getMvpCount());
        }
    }

    @Override
    public Party loadParty(UUID partyId) {
        if (partyCache.containsKey(partyId)) {
            return partyCache.get(partyId);
        }

        File file = new File(partiesFolder, partyId.toString() + ".yml");
        if (!file.exists()) {
            return null;
        }

        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            Party party = new Party();
            party.setPartyId(UUID.fromString(config.getString("party.party-id")));
            party.setLeaderId(UUID.fromString(config.getString("party.leader-id")));
            party.setPartyName(config.getString("party.party-name"));

            List<UUID> members = new ArrayList<>();
            List<String> memberStrings = config.getStringList("party.members");
            for (String memberStr : memberStrings) {
                members. add(UUID.fromString(memberStr));
            }
            party.setMembers(members);

            Map<UUID, PartyRole> roles = new HashMap<>();
            ConfigurationSection rolesSection = config.getConfigurationSection("party. roles");
            if (rolesSection != null) {
                for (String key : rolesSection. getKeys(false)) {
                    UUID memberUUID = UUID.fromString(key);
                    PartyRole role = PartyRole.valueOf(rolesSection.getString(key));
                    roles.put(memberUUID, role);
                }
            }
            party.setRoles(roles);

            party.setMaxMembers(config.getInt("party.max-members", 5));
            party. setPublic(config.getBoolean("party.settings.is-public", false));
            party. setAllowInvites(config.getBoolean("party. settings.allow-invites", true));
            party. setPrivacy(PartyPrivacy.valueOf(config.getString("party.settings.privacy", "INVITE_ONLY")));

            party.setPartyLevel(loadPartyLevel(config));
            party.setActiveBuffs(loadActiveBuffs(config));

            party.setLootDistribution(LootDistribution. valueOf(config.getString("party.loot-distribution", "FREE_FOR_ALL")));
            party.setExpDistribution(ExpDistribution.valueOf(config.getString("party. exp-distribution", "EQUAL")));

            party. setStatistics(loadPartyStatistics(config));
            party.setCreatedTime(config. getLong("party. created-time", System.currentTimeMillis()));

            partyCache.put(partyId, party);
            return party;

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "파티 로드 실패: " + partyId, e);
            return null;
        }
    }

    private PartyLevel loadPartyLevel(YamlConfiguration config) {
        PartyLevel partyLevel = new PartyLevel();
        partyLevel.setLevel(config.getInt("party.party-level.level", 1));
        partyLevel.setExperience(config.getLong("party.party-level.experience", 0));
        partyLevel.setSkillPoints(config.getInt("party.party-level. skill-points", 0));
        partyLevel.setUsedSkillPoints(config.getInt("party.party-level.used-skill-points", 0));
        partyLevel.setLearnedSkills(config. getStringList("party.party-level. learned-skills"));
        return partyLevel;
    }

    @SuppressWarnings("unchecked")
    private List<PartyBuff> loadActiveBuffs(YamlConfiguration config) {
        List<PartyBuff> buffs = new ArrayList<>();
        List<? > buffList = config.getList("party.active-buffs");
        if (buffList == null) return buffs;

        for (Object obj : buffList) {
            if (obj instanceof Map) {
                Map<String, Object> buffMap = (Map<String, Object>) obj;
                PartyBuff buff = new PartyBuff();

                buff.setBuffId((String) buffMap.get("buff-id"));
                buff. setName((String) buffMap.get("name"));
                buff.setType(BuffType.valueOf((String) buffMap.get("type")));

                Object effectsObj = buffMap.get("effects");
                if (effectsObj instanceof Map) {
                    Map<String, Double> effects = new HashMap<>();
                    Map<String, Object> effectsMap = (Map<String, Object>) effectsObj;
                    for (Map.Entry<String, Object> entry : effectsMap.entrySet()) {
                        if (entry.getValue() instanceof Number) {
                            effects.put(entry.getKey(), ((Number) entry.getValue()).doubleValue());
                        }
                    }
                    buff.setEffects(effects);
                }

                buff.setRequiredMembers(((Number) buffMap.getOrDefault("required-members", 0)).intValue());
                buff.setRequiredPartyLevel(((Number) buffMap.getOrDefault("required-party-level", 0)).intValue());
                buff. setDuration(((Number) buffMap.getOrDefault("duration", -1)).intValue());
                buff.setStartTime(((Number) buffMap.getOrDefault("start-time", 0L)).longValue());
                buff.setRange(((Number) buffMap.getOrDefault("range", 50. 0)).doubleValue());

                buffs.add(buff);
            }
        }
        return buffs;
    }

    private PartyStatistics loadPartyStatistics(YamlConfiguration config) {
        PartyStatistics stats = new PartyStatistics();

        stats.setTotalPlayTime(config.getLong("party.statistics.total-play-time", 0));
        stats.setMonstersKilled(config. getInt("party. statistics.monsters-killed", 0));
        stats.setBossesKilled(config.getInt("party.statistics.bosses-killed", 0));
        stats.setTotalDamage(config.getDouble("party.statistics. total-damage", 0));
        stats.setTotalHealing(config.getDouble("party.statistics.total-healing", 0));
        stats.setDungeonsCompleted(config. getInt("party. statistics.dungeons-completed", 0));
        stats.setQuestsCompleted(config.getInt("party.statistics. quests-completed", 0));

        Map<UUID, MemberStatistics> memberStats = new HashMap<>();
        ConfigurationSection memberStatsSection = config.getConfigurationSection("party.statistics.member-stats");
        if (memberStatsSection != null) {
            for (String key : memberStatsSection.getKeys(false)) {
                UUID memberUUID = UUID.fromString(key);
                ConfigurationSection memberSection = memberStatsSection. getConfigurationSection(key);
                
                MemberStatistics ms = new MemberStatistics();
                ms.setDamageDealt(memberSection.getLong("damage-dealt", 0));
                ms.setHealingDone(memberSection.getLong("healing-done", 0));
                ms.setDamageTaken(memberSection.getInt("damage-taken", 0));
                ms.setMvpCount(memberSection.getInt("mvp-count", 0));
                
                memberStats.put(memberUUID, ms);
            }
        }
        stats.setMemberStats(memberStats);

        return stats;
    }

    @Override
    public void deleteParty(UUID partyId) {
        partyCache.remove(partyId);
        File file = new File(partiesFolder, partyId.toString() + ".yml");
        if (file. exists()) {
            file.delete();
        }
    }

    @Override
    public void saveAllParties() {
        for (Party party : partyCache.values()) {
            saveParty(party);
        }
        plugin.getLogger().info("모든 파티 데이터 저장 완료 (" + partyCache.size() + "개)");
    }

    @Override
    public Map<UUID, Party> loadAllParties() {
        partyCache.clear();

        File[] files = partiesFolder. listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return partyCache;

        for (File file :  files) {
            String fileName = file.getName();
            String uuidStr = fileName. substring(0, fileName.length() - 4);
            
            try {
                UUID partyId = UUID. fromString(uuidStr);
                Party party = loadParty(partyId);
                if (party != null) {
                    partyCache.put(partyId, party);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("잘못된 파티 파일: " + fileName);
            }
        }

        plugin.getLogger().info("모든 파티 데이터 로드 완료 (" + partyCache.size() + "개)");
        return partyCache;
    }

    @Override
    public boolean partyExists(UUID partyId) {
        if (partyCache. containsKey(partyId)) return true;
        File file = new File(partiesFolder, partyId.toString() + ".yml");
        return file.exists();
    }

    @Override
    public void savePlayerData(UUID playerUUID, PlayerPartyData data) {
        if (data == null) return;

        File file = new File(playersFolder, playerUUID.toString() + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        try {
            config.set("player.uuid", playerUUID. toString());
            config.set("player. name", data.getPlayerName());
            config.set("player.current-party", data.getCurrentParty() != null ? data. getCurrentParty().toString() : null);

            config.set("player. party-stats. total-parties", data.getTotalParties());
            config.set("player.party-stats.parties-created", data.getPartiesCreated());
            config.set("player.party-stats.parties-joined", data.getPartiesJoined());

            config.set("player.invite-settings.auto-decline", data.isAutoDecline());
            config.set("player.invite-settings.friends-only", data. isFriendsOnly());

            config.set("player.last-party-time", data.getLastPartyTime());

            config.save(file);
            playerCache.put(playerUUID, data);

            if (data.getPlayerName() != null) {
                nameToUUIDCache.put(data. getPlayerName().toLowerCase(), playerUUID);
                uuidToNameCache.put(playerUUID, data.getPlayerName());
            }

        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "플레이어 데이터 저장 실패: " + playerUUID, e);
        }
    }

    @Override
    public PlayerPartyData loadPlayerData(UUID playerUUID) {
        if (playerCache.containsKey(playerUUID)) {
            return playerCache.get(playerUUID);
        }

        File file = new File(playersFolder, playerUUID.toString() + ".yml");
        if (!file.exists()) {
            return null;
        }

        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            PlayerPartyData data = new PlayerPartyData();
            data.setPlayerUUID(playerUUID);
            data.setPlayerName(config.getString("player.name"));

            String currentPartyStr = config.getString("player.current-party");
            if (currentPartyStr != null && !currentPartyStr.isEmpty()) {
                data.setCurrentParty(UUID.fromString(currentPartyStr));
            }

            data.setTotalParties(config.getInt("player.party-stats.total-parties", 0));
            data.setPartiesCreated(config.getInt("player.party-stats.parties-created", 0));
            data.setPartiesJoined(config.getInt("player.party-stats. parties-joined", 0));

            data.setAutoDecline(config.getBoolean("player.invite-settings.auto-decline", false));
            data.setFriendsOnly(config. getBoolean("player.invite-settings. friends-only", false));

            data. setLastPartyTime(config.getLong("player.last-party-time", 0));

            playerCache.put(playerUUID, data);

            if (data. getPlayerName() != null) {
                nameToUUIDCache.put(data.getPlayerName().toLowerCase(), playerUUID);
                uuidToNameCache.put(playerUUID, data.getPlayerName());
            }

            return data;

        } catch (Exception e) {
            plugin.getLogger().log(Level. SEVERE, "플레이어 데이터 로드 실패: " + playerUUID, e);
            return null;
        }
    }

    @Override
    public void deletePlayerData(UUID playerUUID) {
        PlayerPartyData data = playerCache.remove(playerUUID);
        if (data != null && data.getPlayerName() != null) {
            nameToUUIDCache.remove(data.getPlayerName().toLowerCase());
        }
        uuidToNameCache. remove(playerUUID);
        
        File file = new File(playersFolder, playerUUID. toString() + ".yml");
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void saveAllPlayerData() {
        for (Map.Entry<UUID, PlayerPartyData> entry : playerCache. entrySet()) {
            savePlayerData(entry.getKey(), entry.getValue());
        }
        plugin.getLogger().info("모든 플레이어 데이터 저장 완료 (" + playerCache.size() + "개)");
    }

    @Override
    public Map<UUID, PlayerPartyData> loadAllPlayerData() {
        playerCache.clear();

        File[] files = playersFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return playerCache;

        for (File file : files) {
            String fileName = file.getName();
            String uuidStr = fileName.substring(0, fileName. length() - 4);
            
            try {
                UUID playerUUID = UUID.fromString(uuidStr);
                PlayerPartyData data = loadPlayerData(playerUUID);
                if (data != null) {
                    playerCache.put(playerUUID, data);
                }
            } catch (IllegalArgumentException e) {
                plugin. getLogger().warning("잘못된 플레이어 파일: " + fileName);
            }
        }

        plugin.getLogger().info("모든 플레이어 데이터 로드 완료 (" + playerCache.size() + "개)");
        return playerCache;
    }

    @Override
    public void saveListing(PartyListing listing) {
        if (listing == null) return;

        File file = new File(listingsFolder, listing.getPartyId().toString() + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        try {
            config.set("listing.party-id", listing.getPartyId().toString());
            config.set("listing.title", listing.getTitle());
            config. set("listing.description", listing.getDescription());
            config. set("listing.min-level", listing.getMinLevel());
            config.set("listing. max-level", listing.getMaxLevel());
            config.set("listing.required-roles", listing.getRequiredRoles());
            config.set("listing.purpose", listing.getPurpose().name());
            config.set("listing.created-time", listing. getCreatedTime());
            config.set("listing.expire-time", listing. getExpireTime());

            config.save(file);
            listingCache.put(listing. getPartyId(), listing);

        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "모집 공고 저장 실패: " + listing.getPartyId(), e);
        }
    }

    @Override
    public PartyListing loadListing(UUID partyId) {
        if (listingCache.containsKey(partyId)) {
            return listingCache.get(partyId);
        }

        File file = new File(listingsFolder, partyId.toString() + ".yml");
        if (!file.exists()) {
            return null;
        }

        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            PartyListing listing = new PartyListing();
            listing.setPartyId(UUID.fromString(config.getString("listing.party-id")));
            listing.setTitle(config.getString("listing.title"));
            listing.setDescription(config.getString("listing.description"));
            listing.setMinLevel(config.getInt("listing.min-level", 0));
            listing. setMaxLevel(config.getInt("listing.max-level", 100));
            listing. setRequiredRoles(config.getStringList("listing.required-roles"));
            listing.setPurpose(PartyPurpose.valueOf(config.getString("listing.purpose", "GENERAL")));
            listing.setCreatedTime(config.getLong("listing.created-time", System.currentTimeMillis()));
            listing. setExpireTime(config.getLong("listing.expire-time", 0));

            listingCache.put(partyId, listing);
            return listing;

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "모집 공고 로드 실패: " + partyId, e);
            return null;
        }
    }

    @Override
    public void deleteListing(UUID partyId) {
        listingCache. remove(partyId);
        File file = new File(listingsFolder, partyId.toString() + ".yml");
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void saveAllListings() {
        for (PartyListing listing : listingCache.values()) {
            saveListing(listing);
        }
        plugin. getLogger().info("모든 모집 공고 저장 완료 (" + listingCache.size() + "개)");
    }

    @Override
    public List<PartyListing> loadAllListings() {
        listingCache.clear();

        File[] files = listingsFolder. listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return new ArrayList<>(listingCache. values());

        for (File file : files) {
            String fileName = file.getName();
            String uuidStr = fileName. substring(0, fileName.length() - 4);
            
            try {
                UUID partyId = UUID.fromString(uuidStr);
                PartyListing listing = loadListing(partyId);
                if (listing != null) {
                    listingCache.put(partyId, listing);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("잘못된 모집 공고 파일: " + fileName);
            }
        }

        plugin.getLogger().info("모든 모집 공고 로드 완료 (" + listingCache.size() + "개)");
        return new ArrayList<>(listingCache.values());
    }

    @Override
    public String getDataType() {
        return "yaml";
    }

    @Override
    public boolean isConnected() {
        return initialized;
    }

    @Override
    public void backup(String backupName) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String timestamp = backupName != null ? backupName : sdf.format(new Date());
            
            File backupDir = new File(backupsFolder, timestamp);
            backupDir.mkdirs();

            if (partiesFolder.exists()) {
                copyDirectory(partiesFolder, new File(backupDir, "parties"));
            }
            if (playersFolder.exists()) {
                copyDirectory(playersFolder, new File(backupDir, "players"));
            }
            if (listingsFolder.exists()) {
                copyDirectory(listingsFolder, new File(backupDir, "listings"));
            }

            plugin.getLogger().info("백업 완료: " + backupDir. getPath());

        } catch (IOException e) {
            plugin.getLogger().log(Level. SEVERE, "백업 실패", e);
        }
    }

    private void copyDirectory(File source, File target) throws IOException {
        if (! target.exists()) {
            target.mkdirs();
        }

        File[] files = source. listFiles();
        if (files != null) {
            for (File file : files) {
                File targetFile = new File(target, file.getName());
                if (file.isDirectory()) {
                    copyDirectory(file, targetFile);
                } else {
                    Files.copy(file. toPath(), targetFile.toPath(), StandardCopyOption. REPLACE_EXISTING);
                }
            }
        }
    }

    @Override
    public UUID getPlayerUUID(String playerName) {
        if (playerName == null) return null;

        String lowerName = playerName.toLowerCase();
        if (nameToUUIDCache.containsKey(lowerName)) {
            return nameToUUIDCache. get(lowerName);
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if (offlinePlayer. hasPlayedBefore() || offlinePlayer.isOnline()) {
            UUID uuid = offlinePlayer.getUniqueId();
            nameToUUIDCache.put(lowerName, uuid);
            uuidToNameCache. put(uuid, playerName);
            return uuid;
        }

        return null;
    }

    @Override
    public String getPlayerName(UUID playerUUID) {
        if (playerUUID == null) return null;

        if (uuidToNameCache.containsKey(playerUUID)) {
            return uuidToNameCache. get(playerUUID);
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        String name = offlinePlayer.getName();
        
        if (name != null) {
            uuidToNameCache.put(playerUUID, name);
            nameToUUIDCache.put(name.toLowerCase(), playerUUID);
        }

        return name;
    }

    public Map<UUID, Party> getPartyCache() {
        return partyCache;
    }

    public Map<UUID, PlayerPartyData> getPlayerCache() {
        return playerCache;
    }

    public Map<UUID, PartyListing> getListingCache() {
        return listingCache;
    }

    public void updatePartyCache(Party party) {
        if (party != null) {
            partyCache.put(party.getPartyId(), party);
        }
    }

    public void updatePlayerCache(UUID playerUUID, PlayerPartyData data) {
        if (playerUUID != null && data != null) {
            playerCache. put(playerUUID, data);
        }
    }

    public void removeFromPartyCache(UUID partyId) {
        partyCache.remove(partyId);
    }

    public void removeFromPlayerCache(UUID playerUUID) {
        playerCache. remove(playerUUID);
    }
}