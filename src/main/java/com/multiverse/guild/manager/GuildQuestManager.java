package com.multiverse.guild.manager;

import com.multiverse.guild.GuildCore;
import com.multiverse.guild.model.*;
import com.multiverse.guild.util.Message;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GuildQuestManager {

    private final GuildCore plugin;
    private final Map<UUID, List<GuildQuest>> activeQuests = new ConcurrentHashMap<>();

    public GuildQuestManager(GuildCore plugin) {
        this.plugin = plugin;
    }

    public void generateDailyQuests(Guild guild) {
        if (!plugin.getConfig().getBoolean("quests.enabled", true)) return;
        List<GuildQuest> list = new ArrayList<>();
        list.add(simpleQuest("daily_kill", "몬스터 처치 100마리", QuestType.DAILY, 100, 0, 0, 200));
        list.add(simpleQuest("daily_donate", "기부 10000골드", QuestType.DAILY, 10000, 0, 0, 200));
        list.add(simpleQuest("daily_dungeon", "던전 3회 클리어", QuestType.DAILY, 3, 0, 0, 500));
        activeQuests.put(guild.getGuildId(), list);
    }

    public void generateWeeklyQuests(Guild guild) {
        if (!plugin.getConfig().getBoolean("quests.enabled", true)) return;
        List<GuildQuest> list = activeQuests.getOrDefault(guild.getGuildId(), new ArrayList<>());
        list.add(simpleQuest("weekly_boss", "보스 10마리 처치", QuestType.WEEKLY, 10, 50, 10000, 1000));
        list.add(simpleQuest("weekly_war", "길드 전쟁 승리 1회", QuestType.WEEKLY, 1, 100, 20000, 1500));
        activeQuests.put(guild.getGuildId(), list);
    }

    private GuildQuest simpleQuest(String id, String name, QuestType type, int required, int fame, double gold, long exp) {
        QuestObjective obj = new QuestObjective(id + "_obj", name, required, 0);
        GuildQuestReward reward = new GuildQuestReward(exp, Map.of("fantasy_gold", gold), fame);
        List<QuestObjective> objectives = List.of(obj);
        return new GuildQuest(id, name, type, objectives, new ConcurrentHashMap<>(), reward,
                System.currentTimeMillis(), 0L);
    }

    public void updateProgress(Guild guild, String questId, String objectiveId, int amount) {
        List<GuildQuest> list = activeQuests.get(guild.getGuildId());
        if (list == null) return;
        for (GuildQuest q : list) {
            if (!q.getQuestId().equals(questId)) continue;
            for (QuestObjective o : q.getObjectives()) {
                if (o.getObjectiveId().equals(objectiveId)) {
                    o.setCurrent(Math.min(o.getRequired(), o.getCurrent() + amount));
                    if (o.getCurrent() >= o.getRequired()) {
                        completeQuest(guild, questId);
                    }
                    return;
                }
            }
        }
    }

    public void completeQuest(Guild guild, String questId) {
        List<GuildQuest> list = activeQuests.get(guild.getGuildId());
        if (list == null) return;
        Iterator<GuildQuest> it = list.iterator();
        while (it.hasNext()) {
            GuildQuest q = it.next();
            if (q.getQuestId().equals(questId)) {
                giveReward(guild, q.getReward());
                it.remove();
                plugin.getGuildStorage().save(guild);
                guild.getOnlinePlayers().forEach(p -> p.sendMessage(Message.prefixed("&a길드 퀘스트 완료: " + q.getName())));
                break;
            }
        }
    }

    public void giveReward(Guild guild, GuildQuestReward reward) {
        plugin.getGuildLevelManager().addExp(guild, reward.getExperience(), "quest");
        reward.getMoney().forEach((cur, amt) -> guild.getTreasury().merge(cur, amt, Double::sum));
        plugin.getGuildFame().addFame(guild, reward.getFame(), "quest");
    }

    public void tickReset() {
        LocalTime resetDaily = LocalTime.parse(plugin.getConfig().getString("quests.daily.reset-time", "00:00:00"));
        DayOfWeek weeklyDay = DayOfWeek.valueOf(plugin.getConfig().getString("quests.weekly.reset-day", "MONDAY"));
        LocalDate now = LocalDate.now();
        activeQuests.keySet().forEach(gid -> {
            Guild g = plugin.getGuildManager().getGuild(gid);
            if (g == null) return;
            // For simplicity, regenerate both daily/weekly every call (called by scheduled task once per minute)
            generateDailyQuests(g);
            if (now.getDayOfWeek() == weeklyDay) generateWeeklyQuests(g);
        });
    }
}