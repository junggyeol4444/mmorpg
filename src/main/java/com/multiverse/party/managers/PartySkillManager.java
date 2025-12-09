package com. multiverse.party. managers;

import com.multiverse.party.PartyCore;
import com.multiverse.party.models.Party;
import com.multiverse. party.models.PartyLevel;
import com. multiverse.party. models.PartySkill;
import org.bukkit. Bukkit;
import org.bukkit. Location;
import org. bukkit. Particle;
import org.bukkit.Sound;
import org. bukkit.entity.Player;

import java.util.*;
import java.util. concurrent.ConcurrentHashMap;

public class PartySkillManager {

    private final PartyCore plugin;
    private final Map<UUID, Map<String, Long>> cooldowns;

    public PartySkillManager(PartyCore plugin) {
        this.plugin = plugin;
        this.cooldowns = new ConcurrentHashMap<>();
    }

    public boolean learnSkill(Party party, String skillId) {
        if (party == null || skillId == null) return false;

        PartySkill skill = plugin.getSkillRegistry().getSkill(skillId);
        if (skill == null) return false;

        PartyLevel partyLevel = party. getPartyLevel();
        if (partyLevel == null) return false;

        if (partyLevel.getLearnedSkills().contains(skillId)) {
            return false;
        }

        int currentLevel = partyLevel.getLevel();
        if (currentLevel < skill.getRequiredLevel()) {
            return false;
        }

        int availablePoints = plugin.getPartyLevelManager().getAvailableSkillPoints(party);
        if (availablePoints < skill. getCost()) {
            return false;
        }

        plugin.getPartyLevelManager().useSkillPoint(party, skill.getCost());
        partyLevel. getLearnedSkills().add(skillId);

        plugin.getDataManager().saveParty(party);

        return true;
    }

    public boolean hasSkill(Party party, String skillId) {
        if (party == null || party.getPartyLevel() == null) return false;
        return party.getPartyLevel().getLearnedSkills().contains(skillId);
    }

    public boolean useSkill(Party party, Player caster, String skillId) {
        if (party == null || caster == null || skillId == null) return false;

        if (!hasSkill(party, skillId)) {
            caster.sendMessage(plugin.getMessageUtil().getMessage("skill.not-learned"));
            return false;
        }

        PartySkill skill = plugin.getSkillRegistry().getSkill(skillId);
        if (skill == null) return false;

        if (isOnCooldown(party, skillId)) {
            long remaining = getRemainingCooldown(party, skillId);
            caster.sendMessage(plugin.getMessageUtil().getMessage("skill.on-cooldown",
                    "%time%", String.valueOf(remaining)));
            return false;
        }

        boolean success = executeSkill(party, caster, skill);

        if (success) {
            setCooldown(party, skillId, skill.getCooldown());

            plugin.getPartyChatManager().sendNotification(party,
                    plugin. getMessageUtil().getMessage("skill.used",
                            "%player%", caster. getName(),
                            "%skill%", skill. getName()));
        }

        return success;
    }

    private boolean executeSkill(Party party, Player caster, PartySkill skill) {
        String skillId = skill.getSkillId();

        switch (skillId) {
            case "party_heal":
                return executePartyHeal(party, caster, skill);
            case "party_shield":
                return executePartyShield(party, caster, skill);
            case "party_speed": 
                return executePartySpeed(party, caster, skill);
            case "party_teleport":
                return executePartyTeleport(party, caster, skill);
            case "party_revive":
                return executePartyRevive(party, caster, skill);
            case "party_buff":
                return executePartyBuff(party, caster, skill);
            default:
                return executeCustomSkill(party, caster, skill);
        }
    }

    private boolean executePartyHeal(Party party, Player caster, PartySkill skill) {
        double healAmount = skill.getValue("heal_amount", 10.0);
        double range = skill.getValue("range", 20.0);
        double rangeSquared = range * range;
        Location casterLoc = caster. getLocation();

        int healed = 0;
        for (UUID memberUUID : party. getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member == null || ! member.isOnline()) continue;
            if (! member.getWorld().equals(casterLoc.getWorld())) continue;
            if (member.getLocation().distanceSquared(casterLoc) > rangeSquared) continue;

            double maxHealth = member.getAttribute(org.bukkit.attribute.Attribute. GENERIC_MAX_HEALTH).getValue();
            double newHealth = Math.min(member.getHealth() + healAmount, maxHealth);
            member.setHealth(newHealth);

            member.getWorld().spawnParticle(Particle. HEART, member.getLocation().add(0, 2, 0), 5, 0.3, 0.3, 0.3, 0);
            member.playSound(member.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);

            healed++;
        }

        caster.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, casterLoc.add(0, 1, 0), 30, 3, 1, 3, 0.1);

        return healed > 0;
    }

    private boolean executePartyShield(Party party, Player caster, PartySkill skill) {
        double shieldAmount = skill.getValue("shield_amount", 20.0);
        int duration = (int) skill.getValue("duration", 10. 0);
        double range = skill.getValue("range", 15.0);
        double rangeSquared = range * range;
        Location casterLoc = caster. getLocation();

        for (UUID memberUUID : party.getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member == null || !member. isOnline()) continue;
            if (! member.getWorld().equals(casterLoc.getWorld())) continue;
            if (member.getLocation().distanceSquared(casterLoc) > rangeSquared) continue;

            member.setAbsorptionAmount(member.getAbsorptionAmount() + shieldAmount);

            member.getWorld().spawnParticle(Particle. END_ROD, member.getLocation().add(0, 1, 0), 20, 0.5, 1, 0.5, 0.05);
            member. playSound(member. getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.5f, 1.5f);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (UUID memberUUID : party. getMembers()) {
                Player member = Bukkit.getPlayer(memberUUID);
                if (member != null && member.isOnline()) {
                    member.setAbsorptionAmount(Math.max(0, member.getAbsorptionAmount() - shieldAmount));
                }
            }
        }, duration * 20L);

        return true;
    }

    private boolean executePartySpeed(Party party, Player caster, PartySkill skill) {
        int amplifier = (int) skill.getValue("amplifier", 1.0);
        int duration = (int) skill.getValue("duration", 30.0);
        double range = skill. getValue("range", 20.0);
        double rangeSquared = range * range;
        Location casterLoc = caster.getLocation();

        for (UUID memberUUID : party. getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member == null || ! member.isOnline()) continue;
            if (!member.getWorld().equals(casterLoc.getWorld())) continue;
            if (member.getLocation().distanceSquared(casterLoc) > rangeSquared) continue;

            member.addPotionEffect(new org.bukkit. potion.PotionEffect(
                    org.bukkit. potion.PotionEffectType. SPEED,
                    duration * 20,
                    amplifier,
                    true, true, true
            ));

            member.getWorld().spawnParticle(Particle.CLOUD, member.getLocation(), 10, 0.3, 0.1, 0.3, 0.05);
            member. playSound(member. getLocation(), Sound.ENTITY_HORSE_GALLOP, 0.5f, 1.2f);
        }

        return true;
    }

    private boolean executePartyTeleport(Party party, Player caster, PartySkill skill) {
        int partyLevel = plugin.getPartyLevelManager().getPartyLevel(party);
        if (partyLevel < 20) {
            caster.sendMessage(plugin.getMessageUtil().getMessage("skill.level-required", "%level%", "20"));
            return false;
        }

        Location targetLoc = caster. getLocation();
        int teleported = 0;

        for (UUID memberUUID : party. getMembers()) {
            if (memberUUID.equals(caster.getUniqueId())) continue;

            Player member = Bukkit.getPlayer(memberUUID);
            if (member == null || !member.isOnline()) continue;

            member.getWorld().spawnParticle(Particle.PORTAL, member.getLocation(), 50, 0.5, 1, 0.5, 0.5);
            member. teleport(targetLoc);
            member.getWorld().spawnParticle(Particle.PORTAL, member.getLocation(), 50, 0.5, 1, 0.5, 0.5);
            member.playSound(member.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

            teleported++;
        }

        caster.sendMessage(plugin.getMessageUtil().getMessage("skill.teleport-success",
                "%count%", String.valueOf(teleported)));

        return teleported > 0;
    }

    private boolean executePartyRevive(Party party, Player caster, PartySkill skill) {
        int partyLevel = plugin.getPartyLevelManager().getPartyLevel(party);
        if (partyLevel < 30) {
            caster.sendMessage(plugin.getMessageUtil().getMessage("skill.level-required", "%level%", "30"));
            return false;
        }

        caster.sendMessage(plugin.getMessageUtil().getMessage("skill.revive-info"));
        return true;
    }

    private boolean executePartyBuff(Party party, Player caster, PartySkill skill) {
        String buffId = skill. getStringValue("buff_id");
        if (buffId == null) return false;

        PartyBuff buffTemplate = plugin.getPartyBuffManager().getBuffTemplate(buffId);
        if (buffTemplate == null) return false;

        PartyBuff buff = cloneBuff(buffTemplate);
        int duration = (int) skill.getValue("duration", 60.0);
        buff.setDuration(duration);

        plugin.getPartyBuffManager().addBuff(party, buff, 
                com.multiverse.party.events.PartyBuffChangeEvent.BuffChangeReason. SKILL_USE);

        return true;
    }

    private PartyBuff cloneBuff(PartyBuff template) {
        PartyBuff buff = new PartyBuff();
        buff.setBuffId(template.getBuffId());
        buff.setName(template.getName());
        buff.setType(template. getType());
        buff.setEffects(new HashMap<>(template.getEffects()));
        buff.setRequiredMembers(template.getRequiredMembers());
        buff.setRequiredPartyLevel(template.getRequiredPartyLevel());
        buff.setDuration(template. getDuration());
        buff.setRange(template.getRange());
        return buff;
    }

    private boolean executeCustomSkill(Party party, Player caster, PartySkill skill) {
        caster.sendMessage(plugin.getMessageUtil().getMessage("skill.custom-executed",
                "%skill%", skill.getName()));
        return true;
    }

    public boolean isOnCooldown(Party party, String skillId) {
        return getRemainingCooldown(party, skillId) > 0;
    }

    public long getRemainingCooldown(Party party, String skillId) {
        Map<String, Long> partyCooldowns = cooldowns.get(party. getPartyId());
        if (partyCooldowns == null) return 0;

        Long endTime = partyCooldowns.get(skillId);
        if (endTime == null) return 0;

        long remaining = (endTime - System. currentTimeMillis()) / 1000;
        return Math. max(0, remaining);
    }

    public void setCooldown(Party party, String skillId, int seconds) {
        cooldowns.computeIfAbsent(party.getPartyId(), k -> new ConcurrentHashMap<>())
                .put(skillId, System.currentTimeMillis() + (seconds * 1000L));
    }

    public void clearCooldown(Party party, String skillId) {
        Map<String, Long> partyCooldowns = cooldowns.get(party.getPartyId());
        if (partyCooldowns != null) {
            partyCooldowns.remove(skillId);
        }
    }

    public void clearAllCooldowns(Party party) {
        cooldowns.remove(party.getPartyId());
    }

    public List<String> getLearnedSkills(Party party) {
        if (party == null || party.getPartyLevel() == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(party. getPartyLevel().getLearnedSkills());
    }

    public void cleanup() {
        cooldowns.clear();
    }

    private static class PartyBuff extends com.multiverse.party.models.PartyBuff {}
}