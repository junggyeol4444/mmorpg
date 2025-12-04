package com.multiverse.dungeon.managers;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse. dungeon.data.model.*;
import org.bukkit.entity.LivingEntity;

import java.util.*;

/**
 * 보스 관리 매니저
 */
public class BossManager {

    private final DungeonCore plugin;
    private final InstanceManager instanceManager;
    private final Map<UUID, DungeonBoss> bosses; // bossId -> DungeonBoss
    private final Map<UUID, Double> bossHealthMap; // entityId -> currentHealth

    /**
     * 생성자
     */
    public BossManager(DungeonCore plugin, InstanceManager instanceManager) {
        this.plugin = plugin;
        this.instanceManager = instanceManager;
        this.bosses = new HashMap<>();
        this.bossHealthMap = new HashMap<>();
    }

    /**
     * 보스 등록
     *
     * @param boss 등록할 보스
     */
    public void registerBoss(DungeonBoss boss) {
        if (boss == null || bosses.containsKey(UUID.fromString(boss.getBossId()))) {
            return;
        }

        try {
            bosses.put(UUID.fromString(boss.getBossId()), boss);
            plugin.getLogger().info("✅ 보스 '" + boss.getName() + "'이(가) 등록되었습니다.");
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("⚠️ 보스 ID 형식 오류: " + boss.getBossId());
        }
    }

    /**
     * 보스 ID로 보스 조회
     *
     * @param bossId 보스 ID
     * @return 보스 객체, 없으면 null
     */
    public DungeonBoss getBoss(String bossId) {
        try {
            return bosses.get(UUID.fromString(bossId));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 모든 보스 조회
     *
     * @return 보스 목록
     */
    public List<DungeonBoss> getAllBosses() {
        return new ArrayList<>(bosses.values());
    }

    /**
     * 보스 체력 업데이트
     *
     * @param entityId 엔티티 ID
     * @param currentHealth 현재 체력
     */
    public void updateBossHealth(UUID entityId, double currentHealth) {
        bossHealthMap.put(entityId, Math.max(0, currentHealth));
    }

    /**
     * 보스 체력 조회
     *
     * @param entityId 엔티티 ID
     * @return 체력, 없으면 -1
     */
    public double getBossHealth(UUID entityId) {
        return bossHealthMap.getOrDefault(entityId, -1. 0);
    }

    /**
     * 보스 체력 비율 (%)
     *
     * @param entity 보스 엔티티
     * @param maxHealth 최대 체력
     * @return 체력 비율
     */
    public double getBossHealthPercent(LivingEntity entity, double maxHealth) {
        if (entity == null || maxHealth <= 0) {
            return 0;
        }
        return (entity.getHealth() / maxHealth) * 100. 0;
    }

    /**
     * 보스 페이즈 변경 확인
     *
     * @param boss 보스
     * @param previousHealth 이전 체력 (%)
     * @param currentHealth 현재 체력 (%)
     * @return 페이즈 변경되었으면 true
     */
    public boolean hasPhaseChanged(DungeonBoss boss, double previousHealth, double currentHealth) {
        if (boss == null) {
            return false;
        }

        return boss.hasPhaseChanged(previousHealth, currentHealth);
    }

    /**
     * 현재 페이즈 조회
     *
     * @param boss 보스
     * @param healthPercent 체력 (%)
     * @return 현재 페이즈, 없으면 null
     */
    public BossPhase getCurrentPhase(DungeonBoss boss, double healthPercent) {
        if (boss == null) {
            return null;
        }

        return boss.getCurrentPhase(healthPercent);
    }

    /**
     * 사용 가능한 스킬 목록
     *
     * @param boss 보스
     * @param healthPercent 체력 (%)
     * @return 사용 가능한 스킬 목록
     */
    public List<BossSkill> getAvailableSkills(DungeonBoss boss, double healthPercent) {
        if (boss == null) {
            return new ArrayList<>();
        }

        return boss.getAvailableSkills(healthPercent);
    }

    /**
     * 랜덤 스킬 선택
     *
     * @param boss 보스
     * @param healthPercent 체력 (%)
     * @return 선택된 스킬, 없으면 null
     */
    public BossSkill getRandomSkill(DungeonBoss boss, double healthPercent) {
        if (boss == null) {
            return null;
        }

        return boss.getRandomSkill(healthPercent);
    }

    /**
     * 보스가 엔레이지되어야 하는지 확인
     *
     * @param boss 보스
     * @param instance 인스턴스
     * @return 엔레이지 대상이면 true
     */
    public boolean shouldEnrage(DungeonBoss boss, DungeonInstance instance) {
        if (boss == null || instance == null) {
            return false;
        }

        long elapsedSeconds = instance.getElapsedTime();
        return elapsedSeconds >= boss.getEnrageTime();
    }

    /**
     * 엔레이지 데미지 배율
     *
     * @param boss 보스
     * @return 데미지 배율
     */
    public double getEnrageDamageMultiplier(DungeonBoss boss) {
        if (boss == null) {
            return 1.0;
        }

        return boss.getEnrageDamageMultiplier();
    }

    /**
     * 보스 스케일링 체력 계산
     *
     * @param boss 보스
     * @param difficulty 난이도
     * @param partySize 파티 크기
     * @return 스케일링된 체력
     */
    public double getScaledBossHealth(DungeonBoss boss, com.multiverse.dungeon.data.enums.DungeonDifficulty difficulty, int partySize) {
        if (boss == null) {
            return 100;
        }

        double healthMultiplier = plugin.getScalingManager()
            .getMobHealthMultiplier(difficulty, partySize);
        return boss.getScaledHealth(healthMultiplier);
    }

    /**
     * 보스 스케일링 데미지 계산
     *
     * @param boss 보스
     * @param difficulty 난이도
     * @param partySize 파티 크기
     * @return 스케일링된 데미지
     */
    public double getScaledBossDamage(DungeonBoss boss, com.multiverse. dungeon.data.enums. DungeonDifficulty difficulty, int partySize) {
        if (boss == null) {
            return 5;
        }

        double damageMultiplier = plugin.getScalingManager()
            . getMobDamageMultiplier(difficulty, partySize);
        return boss.getScaledDamage(damageMultiplier);
    }

    /**
     * 보스 체력 맵 정리 (엔티티 삭제 시)
     *
     * @param entityId 엔티티 ID
     */
    public void removeBossHealth(UUID entityId) {
        bossHealthMap. remove(entityId);
    }

    /**
     * 모든 활성 보스 정리
     */
    public void cleanup() {
        bossHealthMap.clear();
        plugin.getLogger().info("✅ 모든 보스 데이터가 정리되었습니다.");
    }
}