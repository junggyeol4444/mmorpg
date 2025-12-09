package com.multiverse.party.managers;

import com.multiverse.party.PartyCore;
import com.multiverse. party.events.PartyBuffChangeEvent;
import com.multiverse.party.events.PartyBuffChangeEvent.BuffChangeReason;
import com.multiverse. party.events.PartyBuffChangeEvent. ChangeType;
import com. multiverse.party. models.Party;
import com.multiverse. party.models.PartyBuff;
import com.multiverse.party.models.enums.BuffType;
import org.bukkit. Bukkit;
import org.bukkit. configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class PartyBuffManager {

    private final PartyCore plugin;
    private final Map<String, PartyBuff> buffTemplates;

    public PartyBuffManager(PartyCore plugin) {
        this.plugin = plugin;
        this. buffTemplates = new HashMap<>();
        loadBuffTemplates();
    }

    private void loadBuffTemplates() {
        ConfigurationSection buffsSection = plugin.getConfig().getConfigurationSection("buffs. templates");
        if (buffsSection == null) return;

        for (String buffId : buffsSection. getKeys(false)) {
            ConfigurationSection buffConfig = buffsSection. getConfigurationSection(buffId);
            if (buffConfig == null) continue;

            PartyBuff buff = new PartyBuff();
            buff.setBuffId(buffId);
            buff.setName(buffConfig.getString("name", buffId));
            buff.setType(BuffType.valueOf(buffConfig.getString("type", "ITEM").toUpperCase()));
            buff.setRequiredMembers(buffConfig. getInt("required-members", 0));
            buff.setRequiredPartyLevel(buffConfig.getInt("required-level", 0));
            buff.setDuration(buffConfig. getInt("duration", -1));
            buff.setRange(buffConfig.getDouble("range", 50.0));

            Map<String, Double> effects = new HashMap<>();
            ConfigurationSection effectsSection = buffConfig.getConfigurationSection("effects");
            if (effectsSection != null) {
                for (String effect : effectsSection. getKeys(false)) {
                    effects.put(effect, effectsSection.getDouble(effect));
                }
            }
            buff.setEffects(effects);

            buffTemplates.put(buffId, buff);
        }

        plugin.getLogger().info("버프 템플릿 " + buffTemplates. size() + "개 로드 완료");
    }

    public List<PartyBuff> getActiveBuffs(Party party) {
        if (party == null || party.getActiveBuffs() == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(party. getActiveBuffs());
    }

    public void addBuff(Party party, PartyBuff buff, BuffChangeReason reason) {
        if (party == null || buff == null) return;

        List<PartyBuff> activeBuffs = party.getActiveBuffs();
        if (activeBuffs == null) {
            activeBuffs = new ArrayList<>();
            party. setActiveBuffs(activeBuffs);
        }

        PartyBuff existingBuff = findBuff(party, buff.getBuffId());
        if (existingBuff != null) {
            existingBuff.setStartTime(System.currentTimeMillis());
            existingBuff.setDuration(buff.getDuration());

            PartyBuffChangeEvent event = new PartyBuffChangeEvent(
                    party, existingBuff, ChangeType.REFRESHED, reason);
            Bukkit. getPluginManager().callEvent(event);
        } else {
            buff.setStartTime(System.currentTimeMillis());
            activeBuffs.add(buff);

            PartyBuffChangeEvent event = new PartyBuffChangeEvent(
                    party, buff, ChangeType. ADDED, reason);
            Bukkit. getPluginManager().callEvent(event);
        }

        plugin.getDataManager().saveParty(party);
    }

    public void removeBuff(Party party, String buffId, BuffChangeReason reason) {
        if (party == null || buffId == null) return;

        PartyBuff buff = findBuff(party, buffId);
        if (buff == null) return;

        party.getActiveBuffs().remove(buff);

        PartyBuffChangeEvent event = new PartyBuffChangeEvent(
                party, buff, ChangeType. REMOVED, reason);
        Bukkit.getPluginManager().callEvent(event);

        plugin.getDataManager().saveParty(party);
    }

    public void removeExpiredBuffs(Party party) {
        if (party == null || party.getActiveBuffs() == null) return;

        List<PartyBuff> toRemove = new ArrayList<>();
        long currentTime = System. currentTimeMillis();

        for (PartyBuff buff :  party.getActiveBuffs()) {
            if (buff.getDuration() != -1) {
                long elapsed = (currentTime - buff. getStartTime()) / 1000;
                if (elapsed >= buff.getDuration()) {
                    toRemove.add(buff);
                }
            }
        }

        for (PartyBuff buff : toRemove) {
            party. getActiveBuffs().remove(buff);

            PartyBuffChangeEvent event = new PartyBuffChangeEvent(
                    party, buff, ChangeType.EXPIRED, BuffChangeReason.TIME_EXPIRED);
            Bukkit.getPluginManager().callEvent(event);
        }

        if (!toRemove. isEmpty()) {
            plugin.getDataManager().saveParty(party);
        }
    }

    public PartyBuff findBuff(Party party, String buffId) {
        if (party == null || party.getActiveBuffs() == null) return null;

        for (PartyBuff buff : party.getActiveBuffs()) {
            if (buff.getBuffId().equals(buffId)) {
                return buff;
            }
        }
        return null;
    }

    public boolean hasBuff(Party party, String buffId) {
        return findBuff(party, buffId) != null;
    }

    public void updateMemberCountBuffs(Party party) {
        if (party == null) return;

        int memberCount = party. getMembers().size();
        ConfigurationSection memberBuffs = plugin.getConfig().getConfigurationSection("buffs.member-count");
        if (memberBuffs == null) return;

        for (String countStr : memberBuffs. getKeys(false)) {
            try {
                int requiredCount = Integer.parseInt(countStr. replace("-members", ""));
                String buffId = "member_count_" + requiredCount;

                if (memberCount >= requiredCount) {
                    if (!hasBuff(party, buffId)) {
                        PartyBuff buff = createMemberCountBuff(requiredCount, memberBuffs. getDouble(countStr));
                        addBuff(party, buff, BuffChangeReason.MEMBER_COUNT_CHANGE);
                    }
                } else {
                    if (hasBuff(party, buffId)) {
                        removeBuff(party, buffId, BuffChangeReason.MEMBER_COUNT_CHANGE);
                    }
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    private PartyBuff createMemberCountBuff(int memberCount, double expBonus) {
        PartyBuff buff = new PartyBuff();
        buff.setBuffId("member_count_" + memberCount);
        buff.setName(memberCount + "인 파티 버프");
        buff.setType(BuffType.MEMBER_COUNT);
        buff.setRequiredMembers(memberCount);
        buff.setDuration(-1);
        buff.setRange(50.0);

        Map<String, Double> effects = new HashMap<>();
        effects.put("exp_bonus", expBonus);
        buff.setEffects(effects);

        return buff;
    }

    public void updateLevelBuffs(Party party) {
        if (party == null) return;

        int partyLevel = plugin.getPartyLevelManager().getPartyLevel(party);
        ConfigurationSection levelBuffs = plugin.getConfig().getConfigurationSection("buffs.level");
        if (levelBuffs == null) return;

        for (String levelStr : levelBuffs.getKeys(false)) {
            try {
                int requiredLevel = Integer. parseInt(levelStr);
                String buffId = "level_" + requiredLevel;

                if (partyLevel >= requiredLevel) {
                    if (!hasBuff(party, buffId)) {
                        PartyBuff buff = createLevelBuff(requiredLevel, levelBuffs.getConfigurationSection(levelStr));
                        if (buff != null) {
                            addBuff(party, buff, BuffChangeReason.PARTY_LEVEL_UP);
                        }
                    }
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    private PartyBuff createLevelBuff(int level, ConfigurationSection config) {
        if (config == null) return null;

        PartyBuff buff = new PartyBuff();
        buff.setBuffId("level_" + level);
        buff.setName("Lv." + level + " 파티 버프");
        buff.setType(BuffType. PARTY_LEVEL);
        buff.setRequiredPartyLevel(level);
        buff.setDuration(-1);
        buff.setRange(50.0);

        Map<String, Double> effects = new HashMap<>();
        for (String effect : config.getKeys(false)) {
            effects.put(effect, config.getDouble(effect));
        }
        buff.setEffects(effects);

        return buff;
    }

    public double getExpBonus(Party party) {
        return getTotalEffectValue(party, "exp_bonus");
    }

    public double getDamageBonus(Party party) {
        return getTotalEffectValue(party, "damage_bonus");
    }

    public double getDefenseBonus(Party party) {
        return getTotalEffectValue(party, "defense_bonus");
    }

    public double getCriticalBonus(Party party) {
        return getTotalEffectValue(party, "critical_bonus");
    }

    public double getSpeedBonus(Party party) {
        return getTotalEffectValue(party, "speed_bonus");
    }

    public double getHealthBonus(Party party) {
        return getTotalEffectValue(party, "health_bonus");
    }

    public double getAttackSpeedBonus(Party party) {
        return getTotalEffectValue(party, "attack_speed_bonus");
    }

    private double getTotalEffectValue(Party party, String effectKey) {
        if (party == null || party.getActiveBuffs() == null) return 0;

        double total = 0;
        for (PartyBuff buff : party.getActiveBuffs()) {
            if (buff.getEffects() != null) {
                total += buff.getEffects().getOrDefault(effectKey, 0.0);
            }
        }
        return total;
    }

    public void applyBuffsToPlayer(Player player, Party party) {
        if (player == null || party == null) return;

        plugin.getBuffListener().applyAttributeModifiers(player, party);
        plugin.getBuffListener().applyPotionEffects(player, party);
    }

    public void applyBuffToPlayer(Player player, PartyBuff buff) {
        if (player == null || buff == null) return;

        Party party = plugin.getPartyManager().getPlayerParty(player);
        if (party != null) {
            applyBuffsToPlayer(player, party);
        }
    }

    public void removeBuffsFromPlayer(Player player) {
        if (player == null) return;

        plugin. getBuffListener().removeAttributeModifiers(player);
        plugin.getBuffListener().removePotionEffects(player);
    }

    public void removeBuffFromPlayer(Player player, PartyBuff buff) {
        if (player == null || buff == null) return;

        Party party = plugin.getPartyManager().getPlayerParty(player);
        if (party != null) {
            applyBuffsToPlayer(player, party);
        }
    }

    public PartyBuff getBuffTemplate(String buffId) {
        return buffTemplates.get(buffId);
    }

    public Collection<PartyBuff> getAllBuffTemplates() {
        return buffTemplates. values();
    }

    public void reload() {
        buffTemplates.clear();
        loadBuffTemplates();
    }
}