package com.multiverse.playerdata.api;

import com.multiverse.playerdata.models.PlayerStats;
import com.multiverse.playerdata.models.Evolution;
import com.multiverse.playerdata.models.Transcendence;
import com.multiverse.playerdata.managers.PlayerDataManager;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 외부 플러그인 연동용 플레이어 데이터 API
 */
public class PlayerDataAPI {

    private static PlayerDataManager manager;

    public static void initialize(PlayerDataManager playerDataManager) {
        manager = playerDataManager;
    }

    /**
     * 플레이어 스탯 조회
     */
    public static PlayerStats getStats(Player player) {
        return manager.getPlayerStats(player.getUniqueId());
    }

    /**
     * 진화 정보 조회
     */
    public static List<Evolution> getEvolutions(Player player) {
        return manager.getPlayerEvolutions(player.getUniqueId());
    }

    /**
     * 초월 정보 조회
     */
    public static Transcendence getTranscendence(Player player) {
        return manager.getPlayerTranscendence(player.getUniqueId());
    }

    /**
     * 스탯 수치 가져오기
     */
    public static int getStat(Player player, String statType) {
        PlayerStats stats = getStats(player);
        try {
            return stats.getBaseStats().getOrDefault(
                com.multiverse.playerdata.models.enums.StatType.valueOf(statType.toUpperCase()), 0
            );
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 초월 능력 목록 반환
     */
    public static Set<com.multiverse.playerdata.models.enums.TranscendentPower> getUnlockedPowers(Player player) {
        Transcendence transc = getTranscendence(player);
        return transc != null ? transc.getUnlockedPowers() : java.util.Collections.emptySet();
    }
}