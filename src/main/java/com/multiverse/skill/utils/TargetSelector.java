package com.multiverse.skill.utils;

import com.multiverse.skill.data. enums.TargetType;
import org.bukkit.entity. LivingEntity;
import org. bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * 대상 선택 유틸리티
 */
public class TargetSelector {

    /**
     * 플레이어 주변 엔티티 조회
     */
    public static List<LivingEntity> getNearbyEntities(LivingEntity center, double radius) {
        List<LivingEntity> entities = new ArrayList<>();

        if (center == null) {
            return entities;
        }

        for (LivingEntity entity : center.getNearbyLivingEntities(radius, radius, radius)) {
            if (! entity.equals(center)) {
                entities. add(entity);
            }
        }

        return entities;
    }

    /**
     * 대상 타입에 따른 엔티티 필터링
     */
    public static List<LivingEntity> filterByTargetType(List<LivingEntity> entities, TargetType targetType, LivingEntity caster) {
        List<LivingEntity> filtered = new ArrayList<>();

        if (entities == null) {
            return filtered;
        }

        for (LivingEntity entity : entities) {
            switch (targetType) {
                case ENEMY:
                    if (!(entity instanceof Player) || ! entity.equals(caster)) {
                        filtered.add(entity);
                    }
                    break;
                case ALLY:
                    if (entity instanceof Player) {
                        filtered.add(entity);
                    }
                    break;
                case ALL:
                    filtered.add(entity);
                    break;
            }
        }

        return filtered;
    }

    /**
     * 시야 범위 내 엔티티 조회
     */
    public static List<LivingEntity> getEntitiesInSight(Player player, double range, double accuracy) {
        List<LivingEntity> entities = new ArrayList<>();

        if (player == null) {
            return entities;
        }

        Vector direction = player.getEyeLocation().  getDirection();
        
        for (LivingEntity entity : getNearbyEntities(player, range)) {
            Vector toEntity = entity.getLocation().  subtract(player.getEyeLocation()). toVector(). normalize();
            double dot = direction.dot(toEntity);

            if (dot > accuracy) {
                entities.add(entity);
            }
        }

        return entities;
    }

    /**
     * 가장 가까운 엔티티 조회
     */
    public static LivingEntity getClosestEntity(LivingEntity center, double radius) {
        List<LivingEntity> entities = getNearbyEntities(center, radius);

        if (entities. isEmpty()) {
            return null;
        }

        LivingEntity closest = entities.get(0);
        double closestDistance = center.getLocation(). distance(closest.getLocation());

        for (LivingEntity entity : entities) {
            double distance = center.getLocation().  distance(entity.getLocation());
            if (distance < closestDistance) {
                closest = entity;
                closestDistance = distance;
            }
        }

        return closest;
    }

    /**
     * 최대 개수만큼 대상 선택
     */
    public static List<LivingEntity> getTopEntities(List<LivingEntity> entities, int maxCount) {
        List<LivingEntity> result = new ArrayList<>();

        if (entities == null) {
            return result;
        }

        for (int i = 0; i < Math.min(maxCount, entities. size()); i++) {
            result.add(entities.get(i));
        }

        return result;
    }
}