package com.multiverse. dungeon.managers;

import com.  multiverse.dungeon.DungeonCore;
import com. multiverse.  dungeon.data.  enums.DungeonDifficulty;
import com.multiverse.dungeon.data.model.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 리더보드 관리 매니저
 */
public class LeaderboardManager {

    private final DungeonCore plugin;
    private final DungeonDataManager dataManager;
    private final Map<String, List<DungeonRecord>> leaderboards; // key(dungeonId_difficulty) -> records

    /**
     * 생성자
     */
    public LeaderboardManager(DungeonCore plugin, DungeonDataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.leaderboards = new HashMap<>();
        
        loadAllRecords();
    }

    /**
     * 모든 기록 로드
     */
    private void loadAllRecords() {
        try {
            var allRecords = dataManager.loadAllRecords();
            for (var record : allRecords) {
                String key = record.getDungeonId() + "_" + record.getDifficulty(). name();
                leaderboards. computeIfAbsent(key, k -> new ArrayList<>()). add(record);
            }
            
            // 정렬
            for (var records : leaderboards.values()) {
                records.sort(DungeonRecord::compareTo);
            }
            
            plugin.getLogger().info("✅ 모든 리더보드 기록이 로드되었습니다.");
        } catch (Exception e) {
            plugin.getLogger().severe("❌ 리더보드 로드 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 기록 업데이트
     *
     * @param instance 완료된 인스턴스
     * @param clearTime 클리어 시간 (밀리초)
     * @param score 점수
     */
    public void updateRecord(DungeonInstance instance, long clearTime, int score) {
        if (instance == null) {
            return;
        }

        DungeonRecord record = new DungeonRecord(instance. getDungeonId(), instance.getDifficulty());
        record.setClearTime(clearTime);
        record.setScore(score);

        for (var playerId : instance.getPlayers()) {
            record.addPlayer(playerId);
            
            var player = org.bukkit. Bukkit.getPlayer(playerId);
            if (player != null) {
                record.addPlayerName(player.getName());
            }
        }

        String key = instance.getDungeonId() + "_" + instance.getDifficulty().name();
        var records = leaderboards.computeIfAbsent(key, k -> new ArrayList<>());
        records.add(record);
        records.sort(DungeonRecord::compareTo);

        try {
            dataManager.saveRecord(record);
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ 기록 저장 실패: " + e.getMessage());
        }
    }

    /**
     * 특정 던전의 최고 기록 조회
     *
     * @param dungeonId 던전 ID
     * @param difficulty 난이도
     * @param limit 조회 개수
     * @return 기록 목록
     */
    public List<DungeonRecord> getTopRecords(String dungeonId, DungeonDifficulty difficulty, int limit) {
        String key = dungeonId + "_" + difficulty.name();
        var records = leaderboards.  getOrDefault(key, new ArrayList<>());
        
        return records.stream()
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * 플레이어의 순위 조회
     *
     * @param player 플레이어
     * @param dungeonId 던전 ID
     * @param difficulty 난이도
     * @return 순위
     */
    public int getPlayerRank(Player player, String dungeonId, DungeonDifficulty difficulty) {
        if (player == null) {
            return -1;
        }

        String key = dungeonId + "_" + difficulty.name();
        var records = leaderboards.getOrDefault(key, new ArrayList<>());
        
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i). hasPlayer(player. getUniqueId())) {
                return i + 1;
            }
        }
        
        return -1; // 순위 없음
    }

    /**
     * 플레이어의 총 클리어 횟수
     *
     * @param player 플레이어
     * @param dungeonId 던전 ID
     * @return 클리어 횟수
     */
    public int getTotalClears(Player player, String dungeonId) {
        if (player == null) {
            return 0;
        }

        int count = 0;
        for (var difficulty : DungeonDifficulty.values()) {
            String key = dungeonId + "_" + difficulty.name();
            var records = leaderboards.getOrDefault(key, new ArrayList<>());
            
            for (var record : records) {
                if (record.hasPlayer(player. getUniqueId())) {
                    count++;
                }
            }
        }
        
        return count;
    }

    /**
     * 플레이어의 최고 기록 조회
     *
     * @param player 플레이어
     * @param dungeonId 던전 ID
     * @param difficulty 난이도
     * @return 기록 (밀리초), 없으면 -1
     */
    public long getBestTime(Player player, String dungeonId, DungeonDifficulty difficulty) {
        if (player == null) {
            return -1;
        }

        String key = dungeonId + "_" + difficulty.name();
        var records = leaderboards.getOrDefault(key, new ArrayList<>());
        
        for (var record : records) {
            if (record.hasPlayer(player. getUniqueId())) {
                return record.getClearTime();
            }
        }
        
        return -1;
    }

    /**
     * 모든 기록 저장
     */
    public void saveAllRecords() {
        try {
            for (var records : leaderboards. values()) {
                for (var record : records) {
                    dataManager.saveRecord(record);
                }
            }
            plugin.getLogger().info("✅ 모든 리더보드 기록이 저장되었습니다.");
        } catch (Exception e) {
            plugin.getLogger().severe("❌ 리더보드 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}