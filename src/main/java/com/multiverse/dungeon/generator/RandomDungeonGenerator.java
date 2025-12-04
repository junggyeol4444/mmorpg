package com.multiverse.dungeon.generator;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse. dungeon.data.model.*;
import com.multiverse.dungeon.data.enums.DungeonDifficulty;
import org.bukkit.Location;

import java.util.*;

/**
 * 랜덤 던전 생성기
 * 설정에 따라 랜덤 던전 생성
 */
public class RandomDungeonGenerator {

    private final DungeonCore plugin;
    private final Random random;

    /**
     * 생성자
     */
    public RandomDungeonGenerator(DungeonCore plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    /**
     * 랜덤 방 생성
     *
     * @param config 랜덤 던전 설정
     * @return 생성된 방 목록
     */
    public List<DungeonRoom> generateRooms(RandomDungeonConfig config) {
        List<DungeonRoom> rooms = new ArrayList<>();
        
        int roomCount = config.generateRandomRoomCount();
        
        for (int i = 0; i < roomCount; i++) {
            UUID roomId = UUID.randomUUID();
            int roomNumber = i + 1;
            
            // 랜덤 템플릿 선택
            String template = config.getRoomTemplates().isEmpty() ?  
                "default" : config.getRoomTemplates().get(random.nextInt(config.getRoomTemplates().size()));
            
            DungeonRoom room = new DungeonRoom(roomId, roomNumber, template);
            
            // 몬스터 스폰 위치 추가
            int mobCount = config.generateRandomMobCount();
            for (int j = 0; j < mobCount; j++) {
                Location spawnLoc = generateRandomLocation();
                room.addMobSpawn(spawnLoc);
            }
            
            // 보물 상자 추가
            if (random.nextDouble() < 0.5) { // 50% 확률
                Location chestLoc = generateRandomLocation();
                room.addTreasureChest(chestLoc);
            }
            
            // 방 연결 (이전 방과 연결)
            if (i > 0) {
                UUID prevRoomId = rooms.get(i - 1). getRoomId();
                room.addConnectedRoom(prevRoomId. toString());
                rooms.get(i - 1). addConnectedRoom(room.getRoomId().toString());
            }
            
            rooms. add(room);
        }
        
        plugin.getLogger().info("✅ " + roomCount + "개의 랜덤 방이 생성되었습니다.");
        
        return rooms;
    }

    /**
     * 랜덤 보스 생성
     *
     * @param config 랜덤 던전 설정
     * @param difficulty 난이도
     * @return 생성된 보스 목록
     */
    public List<DungeonBoss> generateBosses(RandomDungeonConfig config, DungeonDifficulty difficulty) {
        List<DungeonBoss> bosses = new ArrayList<>();
        
        int bossCount = config.getBossCount();
        
        for (int i = 0; i < bossCount; i++) {
            String bossId = config.getRandomBoss();
            if (bossId == null) {
                continue;
            }
            
            DungeonBoss boss = new DungeonBoss(
                "random_boss_" + UUID.randomUUID(). toString(). substring(0, 8),
                "랜덤 보스 #" + (i + 1),
                bossId
            );
            
            // 난이도에 따른 스케일링
            double healthMultiplier = difficulty.getHealthMultiplier();
            double damageMultiplier = difficulty.getDamageMultiplier();
            
            boss.setBaseHealth(100.0 * healthMultiplier);
            boss.setBaseDamage(10.0 * damageMultiplier);
            
            // 페이즈 생성
            generateBossPhases(boss);
            
            // 스킬 생성
            generateBossSkills(boss);
            
            bosses.add(boss);
        }
        
        plugin.getLogger().info("✅ " + bossCount + "개의 랜덤 보스가 생성되었습니다.");
        
        return bosses;
    }

    /**
     * 보스 페이즈 생성
     */
    private void generateBossPhases(DungeonBoss boss) {
        // 페이즈 1: 100% ~ 66%
        BossPhase phase1 = new BossPhase(1, 66.0, 100.0);
        phase1.setPhaseMessage("§c보스가 공격을 시작했습니다!");
        boss.addPhase(phase1);
        
        // 페이즈 2: 66% ~ 33%
        BossPhase phase2 = new BossPhase(2, 33.0, 66.0);
        phase2.setPhaseMessage("§c보스가 더욱 강력해졌습니다!");
        boss.addPhase(phase2);
        
        // 페이즈 3: 33% ~ 0%
        BossPhase phase3 = new BossPhase(3, 0.0, 33.0);
        phase3.setPhaseMessage("§4보스가 광기로 변했습니다!");
        boss.addPhase(phase3);
    }

    /**
     * 보스 스킬 생성
     */
    private void generateBossSkills(DungeonBoss boss) {
        // 스킬 1: AOE 데미지
        BossSkill skill1 = new BossSkill("skill_aoe", "범위 공격", 
            com.multiverse.dungeon.data.enums.BossSkillType.AOE_DAMAGE);
        skill1. setHealthThreshold(0);
        skill1.setCooldown(15);
        skill1.setDamage(20. 0);
        skill1.setRadius(5. 0);
        skill1.setDuration(0);
        skill1.setCastMessage("보스가 범위 공격을 시작했습니다!");
        boss.addSkill(skill1);
        
        // 스킬 2: 소환
        BossSkill skill2 = new BossSkill("skill_summon", "추가 소환", 
            com.multiverse.dungeon.data.enums.BossSkillType. SUMMON);
        skill2.setHealthThreshold(50);
        skill2.setCooldown(30);
        skill2.setDamage(0);
        skill2.setRadius(0);
        skill2.setDuration(0);
        skill2.setCastMessage("보스가 추가 몬스터를 소환했습니다!");
        boss. addSkill(skill2);
        
        // 스킬 3: 텔레포트
        BossSkill skill3 = new BossSkill("skill_teleport", "텔레포트", 
            com.multiverse.dungeon.data.enums.BossSkillType.TELEPORT);
        skill3.setHealthThreshold(0);
        skill3.setCooldown(20);
        skill3.setDamage(0);
        skill3.setRadius(0);
        skill3.setDuration(0);
        skill3.setCastMessage("보스가 순간이동했습니다!");
        boss. addSkill(skill3);
    }

    /**
     * 랜덤 함정 생성
     *
     * @param config 랜덤 던전 설정
     * @param rooms 방 목록
     * @return 생성된 함정 목록
     */
    public List<DungeonTrap> generateTraps(RandomDungeonConfig config, List<DungeonRoom> rooms) {
        List<DungeonTrap> traps = new ArrayList<>();
        
        double trapDensity = config.getTrapDensity();
        
        for (var room : rooms) {
            // 각 방에 함정 배치
            int trapCount = (int) (room.getVolume() * (trapDensity / 100.0) / 1000);
            
            for (int i = 0; i < trapCount; i++) {
                String trapType = config.getRandomTrapType();
                if (trapType == null) {
                    continue;
                }
                
                try {
                    var type = com.multiverse.dungeon.data.enums.TrapType.valueOf(trapType. toUpperCase());
                    Location trapLoc = generateRandomLocation();
                    
                    DungeonTrap trap = new DungeonTrap(type, trapLoc);
                    trap.setDamage(type.getBaseDamage());
                    trap.setRadius(2.0);
                    trap. setDuration(5);
                    trap.setMaxTriggers(-1); // 무제한
                    
                    traps.add(trap);
                    room.addTrap(trap);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("⚠️ 알 수 없는 함정 타입: " + trapType);
                }
            }
        }
        
        plugin.getLogger().info("✅ " + traps.size() + "개의 랜덤 함정이 생성되었습니다.");
        
        return traps;
    }

    /**
     * 보물 상자 생성
     *
     * @param config 랜덤 던전 설정
     * @param rooms 방 목록
     * @return 생성된 보물 상자 위치
     */
    public List<Location> generateTreasureChests(RandomDungeonConfig config, List<DungeonRoom> rooms) {
        List<Location> chests = new ArrayList<>();
        
        int chestCount = config.generateRandomChestCount();
        
        for (int i = 0; i < chestCount; i++) {
            if (rooms. isEmpty()) {
                break;
            }
            
            // 랜덤 방 선택
            DungeonRoom room = rooms.get(random.nextInt(rooms.size()));
            
            // 랜덤 위치 생성
            Location chestLoc = generateRandomLocation();
            
            chests.add(chestLoc);
            room.addTreasureChest(chestLoc);
        }
        
        plugin.getLogger().info("✅ " + chestCount + "개의 보물 상자가 생성되었습니다.");
        
        return chests;
    }

    /**
     * 랜덤 위치 생성 (임시)
     * 실제로는 WorldEdit 또는 별도의 위치 생성 로직 사용
     */
    private Location generateRandomLocation() {
        double x = random.nextDouble() * 100 - 50;
        double y = 64;
        double z = random. nextDouble() * 100 - 50;
        
        return new Location(org.bukkit. Bukkit.getWorld("world"), x, y, z);
    }
}