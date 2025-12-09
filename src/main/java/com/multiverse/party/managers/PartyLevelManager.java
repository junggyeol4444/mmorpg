package com.multiverse.party. managers;

import com. multiverse.party. PartyCore;
import com. multiverse.party. events.PartyExpGainEvent;
import com.multiverse. party.events.PartyLevelUpEvent;
import com.multiverse.party.models.Party;
import com.multiverse. party.models.PartyLevel;
import org.bukkit. Bukkit;
import org.bukkit. configuration.ConfigurationSection;

import java.util. HashMap;
import java.util.Map;

public class PartyLevelManager {

    private final PartyCore plugin;
    private final Map<Integer, Long> expRequirements;
    private int maxLevel;

    public PartyLevelManager(PartyCore plugin) {
        this.plugin = plugin;
        this.expRequirements = new HashMap<>();
        loadExpRequirements();
    }

    private void loadExpRequirements() {
        maxLevel = plugin.getConfig().getInt("party-level.max-level", 50);
        
        String formula = plugin.getConfig().getString("party-level.exp-formula", "level * 1000");
        double baseExp = plugin.getConfig().getDouble("party-level.base-exp", 1000);
        double multiplier = plugin. getConfig().getDouble("party-level. exp-multiplier", 1.5);

        for (int level = 1; level <= maxLevel; level++) {
            long requiredExp;
            
            if (formula.equals("exponential")) {
                requiredExp = Math.round(baseExp * Math.pow(multiplier, level - 1));
            } else if (formula.equals("linear")) {
                requiredExp = Math.round(baseExp * level);
            } else {
                requiredExp = Math.round(baseExp + (level * 500));
            }
            
            expRequirements.put(level, requiredExp);
        }

        ConfigurationSection customExp = plugin.getConfig().getConfigurationSection("party-level.custom-exp");
        if (customExp != null) {
            for (String key :  customExp.getKeys(false)) {
                try {
                    int level = Integer.parseInt(key);
                    long exp = customExp.getLong(key);
                    expRequirements.put(level, exp);
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    public int getPartyLevel(Party party) {
        if (party == null || party.getPartyLevel() == null) {
            return 1;
        }
        return party.getPartyLevel().getLevel();
    }

    public long getPartyExp(Party party) {
        if (party == null || party. getPartyLevel() == null) {
            return 0;
        }
        return party.getPartyLevel().getExperience();
    }

    public long getExpToNextLevel(Party party) {
        int currentLevel = getPartyLevel(party);
        if (currentLevel >= maxLevel) {
            return 0;
        }
        return expRequirements. getOrDefault(currentLevel + 1, 0L);
    }

    public long getCurrentLevelExp(Party party) {
        int currentLevel = getPartyLevel(party);
        return expRequirements. getOrDefault(currentLevel, 0L);
    }

    public double getExpProgress(Party party) {
        long currentExp = getPartyExp(party);
        long currentLevelExp = getCurrentLevelExp(party);
        long nextLevelExp = getExpToNextLevel(party);

        if (nextLevelExp <= currentLevelExp) {
            return 1.0;
        }

        long expInLevel = currentExp - currentLevelExp;
        long expNeeded = nextLevelExp - currentLevelExp;

        return Math.min(1.0, (double) expInLevel / expNeeded);
    }

    public void addPartyExp(Party party, long amount) {
        if (party == null || amount <= 0) return;

        PartyLevel partyLevel = party. getPartyLevel();
        if (partyLevel == null) {
            partyLevel = new PartyLevel();
            partyLevel.setLevel(1);
            partyLevel.setExperience(0);
            party.setPartyLevel(partyLevel);
        }

        int currentLevel = partyLevel.getLevel();
        if (currentLevel >= maxLevel) {
            return;
        }

        long newExp = partyLevel. getExperience() + amount;
        partyLevel.setExperience(newExp);

        checkLevelUp(party);

        plugin.getDataManager().saveParty(party);
    }

    private void checkLevelUp(Party party) {
        PartyLevel partyLevel = party.getPartyLevel();
        int currentLevel = partyLevel.getLevel();
        long currentExp = partyLevel. getExperience();

        int levelsGained = 0;
        int originalLevel = currentLevel;

        while (currentLevel < maxLevel) {
            long requiredExp = expRequirements.getOrDefault(currentLevel + 1, Long.MAX_VALUE);
            
            if (currentExp >= requiredExp) {
                currentLevel++;
                levelsGained++;
            } else {
                break;
            }
        }

        if (levelsGained > 0) {
            partyLevel.setLevel(currentLevel);

            int skillPointsPerLevel = plugin.getConfig().getInt("party-level.skill-points-per-level", 1);
            int bonusSkillPoints = levelsGained * skillPointsPerLevel;
            partyLevel.setSkillPoints(partyLevel. getSkillPoints() + bonusSkillPoints);

            PartyLevelUpEvent levelUpEvent = new PartyLevelUpEvent(party, originalLevel, currentLevel);
            levelUpEvent.setBonusSkillPoints(bonusSkillPoints);

            checkUnlocks(levelUpEvent, originalLevel, currentLevel);

            Bukkit.getPluginManager().callEvent(levelUpEvent);
        }
    }

    private void checkUnlocks(PartyLevelUpEvent event, int oldLevel, int newLevel) {
        ConfigurationSection buffs = plugin.getConfig().getConfigurationSection("buffs. level-unlocks");
        if (buffs != null) {
            for (String levelStr : buffs.getKeys(false)) {
                try {
                    int level = Integer.parseInt(levelStr);
                    if (level > oldLevel && level <= newLevel) {
                        String buffName = buffs.getString(levelStr);
                        if (buffName != null) {
                            event.addUnlockedBuff(buffName);
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        ConfigurationSection skills = plugin.getConfig().getConfigurationSection("skills.level-unlocks");
        if (skills != null) {
            for (String levelStr :  skills.getKeys(false)) {
                try {
                    int level = Integer. parseInt(levelStr);
                    if (level > oldLevel && level <= newLevel) {
                        String skillName = skills. getString(levelStr);
                        if (skillName != null) {
                            event.addUnlockedSkill(skillName);
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        if (event.reachedLevel(10)) {
            event.addUnlockedFeature("파티 채팅 이모지");
        }
        if (event.reachedLevel(20)) {
            event.addUnlockedFeature("파티 순간이동");
        }
        if (event.reachedLevel(30)) {
            event.addUnlockedFeature("파티 부활");
        }
    }

    public void setPartyLevel(Party party, int level) {
        if (party == null) return;
        
        level = Math.max(1, Math.min(level, maxLevel));

        PartyLevel partyLevel = party. getPartyLevel();
        if (partyLevel == null) {
            partyLevel = new PartyLevel();
            party.setPartyLevel(partyLevel);
        }

        int oldLevel = partyLevel.getLevel();
        partyLevel. setLevel(level);

        long requiredExp = expRequirements.getOrDefault(level, 0L);
        partyLevel.setExperience(requiredExp);

        if (level > oldLevel) {
            int skillPointsPerLevel = plugin.getConfig().getInt("party-level.skill-points-per-level", 1);
            int bonusPoints = (level - oldLevel) * skillPointsPerLevel;
            partyLevel.setSkillPoints(partyLevel.getSkillPoints() + bonusPoints);
        }

        plugin.getDataManager().saveParty(party);
    }

    public void setPartyExp(Party party, long exp) {
        if (party == null) return;

        PartyLevel partyLevel = party. getPartyLevel();
        if (partyLevel == null) {
            partyLevel = new PartyLevel();
            partyLevel.setLevel(1);
            party.setPartyLevel(partyLevel);
        }

        partyLevel.setExperience(Math.max(0, exp));
        checkLevelUp(party);

        plugin.getDataManager().saveParty(party);
    }

    public int getAvailableSkillPoints(Party party) {
        if (party == null || party.getPartyLevel() == null) {
            return 0;
        }

        PartyLevel partyLevel = party. getPartyLevel();
        return partyLevel.getSkillPoints() - partyLevel. getUsedSkillPoints();
    }

    public boolean useSkillPoint(Party party, int points) {
        if (party == null || points <= 0) return false;

        int available = getAvailableSkillPoints(party);
        if (available < points) return false;

        PartyLevel partyLevel = party.getPartyLevel();
        partyLevel.setUsedSkillPoints(partyLevel.getUsedSkillPoints() + points);

        plugin.getDataManager().saveParty(party);
        return true;
    }

    public void refundSkillPoints(Party party, int points) {
        if (party == null || points <= 0) return;

        PartyLevel partyLevel = party.getPartyLevel();
        if (partyLevel == null) return;

        int newUsed = Math.max(0, partyLevel.getUsedSkillPoints() - points);
        partyLevel. setUsedSkillPoints(newUsed);

        plugin.getDataManager().saveParty(party);
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public long getExpRequirement(int level) {
        return expRequirements.getOrDefault(level, 0L);
    }

    public String formatExp(long exp) {
        if (exp >= 1_000_000) {
            return String.format("%.1fM", exp / 1_000_000. 0);
        } else if (exp >= 1_000) {
            return String.format("%.1fK", exp / 1_000.0);
        }
        return String. valueOf(exp);
    }

    public String getProgressBar(Party party, int length) {
        double progress = getExpProgress(party);
        int filled = (int) (progress * length);
        int empty = length - filled;

        StringBuilder bar = new StringBuilder();
        bar.append("&a");
        for (int i = 0; i < filled; i++) {
            bar.append("|");
        }
        bar.append("&7");
        for (int i = 0; i < empty; i++) {
            bar.append("|");
        }

        return bar.toString();
    }

    public void reload() {
        expRequirements.clear();
        loadExpRequirements();
    }
}