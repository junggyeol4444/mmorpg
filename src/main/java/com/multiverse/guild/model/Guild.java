package com.multiverse.guild.model;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Guild {
    private UUID guildId;
    private String guildName;
    private String guildTag;            // [TAG]
    private UUID masterId;

    private Map<UUID, GuildMember> members;
    private int maxMembers = 10;

    private Map<String, GuildRank> ranks;

    private String description = "";
    private String announcement = "";

    private int level = 1;
    private long experience = 0;
    private int skillPoints = 0;
    private int usedSkillPoints = 0;

    private Map<String, Double> treasury;

    private UUID territoryId;

    private List<String> skills;
    private Set<UUID> allies;
    private Set<UUID> enemies;

    private GuildStatistics statistics;
    private GuildSettings settings;
    private GuildLevel levelData; // 확장 데이터 (옵션)
    private int fame = 0;

    private long createdTime;

    public Guild(UUID guildId, String guildName, String guildTag, UUID masterId) {
        this.guildId = guildId;
        this.guildName = guildName;
        this.guildTag = guildTag;
        this.masterId = masterId;
        this.members = new ConcurrentHashMap<>();
        this.ranks = new ConcurrentHashMap<>();
        this.treasury = new ConcurrentHashMap<>();
        this.skills = new ArrayList<>();
        this.allies = ConcurrentHashMap.newKeySet();
        this.enemies = ConcurrentHashMap.newKeySet();
        this.statistics = new GuildStatistics();
        this.settings = new GuildSettings();
        this.levelData = new GuildLevel();
        this.createdTime = System.currentTimeMillis();
        // 기본 계급
        ranks.put("Guild Master", GuildRank.defaultMaster());
        ranks.put("Officer", GuildRank.defaultOfficer());
        ranks.put("Member", GuildRank.defaultMember());
    }

    public Collection<Player> getOnlinePlayers() {
        List<Player> res = new ArrayList<>();
        for (UUID u : members.keySet()) {
            Player p = Bukkit.getPlayer(u);
            if (p != null) res.add(p);
        }
        return res;
    }

    // getters / setters
    public UUID getGuildId() { return guildId; }
    public String getGuildName() { return guildName; }
    public void setGuildName(String guildName) { this.guildName = guildName; }
    public String getGuildTag() { return guildTag; }
    public void setGuildTag(String guildTag) { this.guildTag = guildTag; }
    public UUID getMasterId() { return masterId; }
    public void setMasterId(UUID masterId) { this.masterId = masterId; }
    public Map<UUID, GuildMember> getMembers() { return members; }
    public int getMaxMembers() { return maxMembers; }
    public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }
    public Map<String, GuildRank> getRanks() { return ranks; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAnnouncement() { return announcement; }
    public void setAnnouncement(String announcement) { this.announcement = announcement; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public long getExperience() { return experience; }
    public void setExperience(long experience) { this.experience = experience; }
    public Map<String, Double> getTreasury() { return treasury; }
    public UUID getTerritoryId() { return territoryId; }
    public void setTerritoryId(UUID territoryId) { this.territoryId = territoryId; }
    public List<String> getSkills() { return skills; }
    public Set<UUID> getAllies() { return allies; }
    public Set<UUID> getEnemies() { return enemies; }
    public GuildStatistics getStatistics() { return statistics; }
    public GuildSettings getSettings() { return settings; }
    public int getSkillPoints() { return skillPoints; }
    public void setSkillPoints(int skillPoints) { this.skillPoints = skillPoints; }
    public int getUsedSkillPoints() { return usedSkillPoints; }
    public void setUsedSkillPoints(int usedSkillPoints) { this.usedSkillPoints = usedSkillPoints; }
    public GuildLevel getLevelData() { return levelData; }
    public void setLevelData(GuildLevel levelData) { this.levelData = levelData; }
    public int getFame() { return fame; }
    public void setFame(int fame) { this.fame = fame; }
    public long getCreatedTime() { return createdTime; }
    public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }
}