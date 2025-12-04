package com.multiverse.dungeon.managers;

import com.multiverse.dungeon. DungeonCore;
import com.multiverse. dungeon.data.model.*;
import com.multiverse.dungeon.generator.RandomDungeonGenerator;

import java.util.*;

/**
 * 랜덤 던전 관리 매니저
 */
public class RandomDungeonManager {

    private final DungeonCore plugin;
    private final RandomDungeonGenerator generator;

    /**
     * 생성자
     */
    public RandomDungeonManager(DungeonCore plugin) {
        this.plugin = plugin;
        this.generator = new RandomDungeonGenerator(plugin);
    }

    /**
     * 랜덤 던전 인스턴스 생성
     *
     * @param config 랜덤 던전 설정
     * @param partyId 파티 ID
     * @param difficulty 난이도
     * @return 생성된 인스턴스, 실패하면 null
     */
    public DungeonInstance generateRandomDungeonInstance(RandomDungeonConfig config, 
                                                         UUID partyId, 
                                                         com.multiverse.dungeon.data.enums.DungeonDifficulty difficulty) {
        if (config == null) {
            return null;
        }

        try {
            // 랜덤 던전 생성
            List<DungeonRoom> rooms = generator.generateRooms(config);
            List<DungeonBoss> bosses = generator.generateBosses(config, difficulty);
            List<DungeonTrap> traps = generator.  generateTraps(config, rooms);

            // 인스턴스 생성
            UUID instanceId = UUID.randomUUID();
            DungeonInstance instance = new DungeonInstance(instanceId, "random_" + instanceId. toString(). substring(0, 8), 
                partyId, difficulty, 1800);

            // 진행도 설정
            int totalMobs = 0;
            for (var room : rooms) {
                totalMobs += config.generateRandomMobCount();
            }
            instance.getProgress().setTotalMobs(totalMobs);
            instance.getProgress().setTotalBosses(bosses.size());

            plugin.getLogger().info("✅ 랜덤 던전 인스턴스 생성됨: " + instanceId 
                + " (방: " + rooms.size() + ", 보스: " + bosses.size() + ")");

            return instance;
        } catch (Exception e) {
            plugin.getLogger().severe("❌ 랜덤 던전 생성 실패: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 방 생성
     *
     * @param config 설정
     * @return 생성된 방 목록
     */
    public List<DungeonRoom> generateRooms(RandomDungeonConfig config) {
        return generator.generateRooms(config);
    }

    /**
     * 보스 생성
     *
     * @param config 설정
     * @param difficulty 난이도
     * @return 생성된 보스 목록
     */
    public List<DungeonBoss> generateBosses(RandomDungeonConfig config, 
                                            com.multiverse.dungeon.data.enums.DungeonDifficulty difficulty) {
        return generator.generateBosses(config, difficulty);
    }

    /**
     * 함정 생성
     *
     * @param config 설정
     * @param rooms 방 목록
     * @return 생성된 함정 목록
     */
    public List<DungeonTrap> generateTraps(RandomDungeonConfig config, List<DungeonRoom> rooms) {
        return generator.generateTraps(config, rooms);
    }

    /**
     * 보물 상자 생성
     *
     * @param config 설정
     * @param rooms 방 목록
     * @return 생성된 보물 상자 위치
     */
    public List<org.bukkit.Location> generateTreasureChests(RandomDungeonConfig config, List<DungeonRoom> rooms) {
        return generator.generateTreasureChests(config, rooms);
    }
}