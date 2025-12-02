package com.multiverse.playerdata.managers;

import com.multiverse.playerdata.PlayerDataCore;
import com.multiverse.playerdata.models.Transcendence;
import com.multiverse.playerdata.models.enums.TranscendentPower;
import com.multiverse.playerdata.data.DataManager;
import com.multiverse.playerdata.utils.ConfigUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TranscendenceManager {

    private final PlayerDataCore plugin;
    private final DataManager dataManager;
    private final ConfigUtil configUtil;

    public TranscendenceManager(PlayerDataCore plugin, DataManager dataManager, ConfigUtil configUtil) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.configUtil = configUtil;
    }

    // 초월 상태 체크
    public boolean isTranscendent(Player player) {
        Transcendence t = getTranscendence(player);
        return t != null && t.isTranscendent();
    }

    public Transcendence getTranscendence(Player player) {
        return dataManager.loadPlayerTranscendence(player.getUniqueId());
    }

    // 초월 조건 체크
    public boolean canTranscend(Player player) {
        Transcendence t = getTranscendence(player);
        if (t != null && t.isTranscendent()) return false; // 이미 초월
        int reqLevel = configUtil.getInt("transcendence.requirements.level", 200);
        int reqStat = configUtil.getInt("transcendence.requirements.base-stat-min", 500);
        int reqSpecial = configUtil.getInt("transcendence.requirements.special-stat-min", 1000);

        int level = plugin.getStatsManager().getLevel(player);
        boolean baseStatOk = true;
        for (var type : plugin.getStatsManager().getBaseStats(player).keySet()) {
            if (plugin.getStatsManager().getBaseStat(player, type) < reqStat) {
                baseStatOk = false;
                break;
            }
        }
        boolean specialStatOk = true;
        // special stat 이름 목록 필요, 예시로 mana/qi 등
        for (String name : plugin.getStatsManager().loadStats(player).getSpecialStats().keySet()) {
            if (plugin.getStatsManager().getSpecialStat(player, name) < reqSpecial) {
                specialStatOk = false;
                break;
            }
        }
        return level >= reqLevel && baseStatOk && specialStatOk;
    }

    public Map<String, Boolean> checkTranscendenceRequirements(Player player) {
        Map<String, Boolean> map = new HashMap<>();
        int reqLevel = configUtil.getInt("transcendence.requirements.level", 200);
        int reqStat = configUtil.getInt("transcendence.requirements.base-stat-min", 500);
        int reqSpecial = configUtil.getInt("transcendence.requirements.special-stat-min", 1000);

        int level = plugin.getStatsManager().getLevel(player);
        map.put("level", level >= reqLevel);

        for (var type : plugin.getStatsManager().getBaseStats(player).keySet()) {
            map.put(type.name(), plugin.getStatsManager().getBaseStat(player, type) >= reqStat);
        }
        for (String name : plugin.getStatsManager().loadStats(player).getSpecialStats().keySet()) {
            map.put(name, plugin.getStatsManager().getSpecialStat(player, name) >= reqSpecial);
        }
        // TODO: 시험 통과 등 추가적 조건 체크 가능
        return map;
    }

    // 초월 실행
    public void transcendPlayer(Player player, TranscendentPower power) {
        Transcendence transc = getTranscendence(player);
        if (transc == null) transc = new Transcendence(player.getUniqueId());
        transc.setTranscendent(true);
        transc.setTranscendenceDate(System.currentTimeMillis());
        transc.setChosenPower(power);
        transc.setLevelCap(configUtil.getInt("transcendence.level-cap", 500));
        dataManager.savePlayerTranscendence(player.getUniqueId(), transc);

        applyPowerEffects(player);

        player.sendMessage(configUtil.replaceVariables("transcendence.success", player.getName(), power.name()));
    }

    // 권능 효과 전체 적용
    public void applyPowerEffects(Player player) {
        Transcendence transc = getTranscendence(player);
        if (transc == null || transc.getChosenPower() == null) return;
        switch (transc.getChosenPower()) {
            case TIME_CONTROL:
                // 쿨타임 감소 로직
                break;
            case SPACE_MASTERY:
                // 텔레포트 거리 증가 로직
                break;
            case LIFE_CREATION:
                // 소환물 능력 강화 등
                break;
            case DEATH_POWER:
                // 즉사 확률 적용
                break;
            case CHAOS_FORCE:
                // 버프/디버프 랜덤 효과
                break;
            case ORDER_FORCE:
                // 모든 능력 안정화
                break;
            case VOID_STATE:
                // 무적 효과
                break;
        }
    }

    public void activatePower(Player player) {
        // 실시간 플레이어 권능 발동(스킬 등) 처리, 쿨타임/효과 관리
    }

    // 초월 전수
    public boolean canTransfer(Player from, Player to) {
        Transcendence tFrom = getTranscendence(from);
        Transcendence tTo = getTranscendence(to);

        if (tFrom == null || !tFrom.isTranscendent()) return false; // 반드시 초월자
        if (tTo != null && tTo.isTranscendent()) return false; // 이미 초월

        int minLevel = configUtil.getInt("transcendence.transfer.min-receiver-level", 150);
        int cooldownDays = configUtil.getInt("transcendence.transfer.cooldown-days", 30);

        if (plugin.getStatsManager().getLevel(to) < minLevel) return false;
        long now = System.currentTimeMillis();
        if (tFrom.getLastTransferDate() > 0 &&
                (now - tFrom.getLastTransferDate()) < cooldownDays * 24L * 60L * 60L * 1000L) return false;
        return true;
    }

    public void transferKnowledge(Player from, Player to) {
        // 전수받는 자 설정
        Transcendence tTo = new Transcendence(to.getUniqueId());
        tTo.setTranscendent(true);
        tTo.setTranscendenceDate(System.currentTimeMillis());
        tTo.setLevelCap(configUtil.getInt("transcendence.level-cap", 500));
        tTo.setTransferredFrom(from.getUniqueId());
        dataManager.savePlayerTranscendence(to.getUniqueId(), tTo);

        // 전수자 페널티 적용
        applyTransferPenalty(from);

        // 전수 횟수/날짜 갱신
        Transcendence tFrom = getTranscendence(from);
        tFrom.setTransferCount(tFrom.getTransferCount() + 1);
        tFrom.setLastTransferDate(System.currentTimeMillis());
        dataManager.savePlayerTranscendence(from.getUniqueId(), tFrom);
    }

    public void applyTransferPenalty(Player player) {
        Transcendence t = getTranscendence(player);
        if (t == null) return;
        t.setHasPenalty(true);
        int penaltyDuration = configUtil.getInt("transcendence.transfer.penalty-duration-days", 7);
        t.setPenaltyEndDate(System.currentTimeMillis() + penaltyDuration * 86400_000L);
        // 스탯/레벨 감소 등 실제 효과 적용은 StatsManager 등에서 구현 필요
        dataManager.savePlayerTranscendence(player.getUniqueId(), t);
    }

    public void removeTransferPenalty(Player player) {
        Transcendence t = getTranscendence(player);
        if (t == null) return;
        t.setHasPenalty(false);
        t.setPenaltyEndDate(0L);
        // 스탯/레벨 복원 처리 필요
        dataManager.savePlayerTranscendence(player.getUniqueId(), t);
    }
}