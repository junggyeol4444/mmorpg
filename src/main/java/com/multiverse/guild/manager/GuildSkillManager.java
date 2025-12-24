package com.multiverse.guild.manager;

import com.multiverse.guild.GuildCore;
import com.multiverse.guild.model.Guild;
import com.multiverse.guild.model.GuildSkill;
import com.multiverse.guild.model.SkillCategory;
import com.multiverse.guild.util.Message;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GuildSkillManager {

    private final GuildCore plugin;
    private final Map<String, GuildSkill> skillDefs = new HashMap<>(); // skillId -> definition

    public GuildSkillManager(GuildCore plugin) {
        this.plugin = plugin;
        loadDefaultSkills();
    }

    private void loadDefaultSkills() {
        skillDefs.put("economy_boost", new GuildSkill("economy_boost", "경제 수입 증가", SkillCategory.ECONOMY, 0, 5, 1, 10000, Map.of("income_multiplier", 0.05), null, 0));
        skillDefs.put("combat_power", new GuildSkill("combat_power", "전투력 증가", SkillCategory.COMBAT, 0, 5, 1, 15000, Map.of("damage_bonus", 0.03), null, 0));
        skillDefs.put("guild_teleport", new GuildSkill("guild_teleport", "길드 텔레포트", SkillCategory.SPECIAL, 0, 1, 2, 50000, Map.of(), null, 0));
    }

    public void learnSkill(Guild guild, String skillId) {
        GuildSkill def = skillDefs.get(skillId);
        if (def == null) return;
        if (hasSkill(guild, skillId)) return;
        if (guild.getSkillPoints() < def.getRequiredSkillPoints()) {
            return;
        }
        guild.setSkillPoints(guild.getSkillPoints() - def.getRequiredSkillPoints());
        def.setCurrentLevel(1);
        guild.getSkills().add(skillId + "_lv1");
        plugin.getGuildStorage().save(guild);
    }

    public void upgradeSkill(Guild guild, String skillId) {
        GuildSkill def = skillDefs.get(skillId);
        if (def == null) return;
        int current = getSkillLevel(guild, skillId);
        if (current >= def.getMaxLevel()) return;
        int costSp = def.getRequiredSkillPoints();
        long costGold = def.getRequiredGold();
        if (guild.getSkillPoints() < costSp) return;
        // gold cost from treasury (use default currency fantasy_gold)
        double bal = guild.getTreasury().getOrDefault("fantasy_gold", 0.0);
        if (bal < costGold) return;
        guild.getTreasury().put("fantasy_gold", bal - costGold);
        guild.setSkillPoints(guild.getSkillPoints() - costSp);
        // update stored skill string
        guild.getSkills().removeIf(s -> s.startsWith(skillId + "_lv"));
        guild.getSkills().add(skillId + "_lv" + (current + 1));
        plugin.getGuildStorage().save(guild);
    }

    public boolean hasSkill(Guild guild, String skillId) {
        return guild.getSkills().stream().anyMatch(s -> s.startsWith(skillId + "_lv"));
    }

    public int getSkillLevel(Guild guild, String skillId) {
        return guild.getSkills().stream()
                .filter(s -> s.startsWith(skillId + "_lv"))
                .map(s -> s.replace(skillId + "_lv", ""))
                .mapToInt(Integer::parseInt)
                .findFirst().orElse(0);
    }

    public void applySkillEffects(Guild guild, Player player) {
        // Hook point for applying potion effects/buffs; left minimal here.
    }

    public double getSkillBonus(Guild guild, String bonusKey) {
        double total = 0;
        for (String s : guild.getSkills()) {
            String skillId = s.split("_lv")[0];
            int lv = getSkillLevel(guild, skillId);
            GuildSkill def = skillDefs.get(skillId);
            if (def != null && def.getEffects().containsKey(bonusKey)) {
                total += def.getEffects().get(bonusKey) * lv;
            }
        }
        return total;
    }
}