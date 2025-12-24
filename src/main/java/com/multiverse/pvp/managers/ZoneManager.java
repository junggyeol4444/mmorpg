package com.multiverse.pvp.managers;

import com. multiverse.pvp.PvPCore;
import com. multiverse.pvp.data.PvPZone;
import com.multiverse.pvp.enums.ZoneType;
import com.multiverse.pvp.utils.MessageUtil;
import org.bukkit.Location;
import org. bukkit.entity. Player;

import java.util.*;
import java.util. concurrent.ConcurrentHashMap;

public class ZoneManager {

    private final PvPCore plugin;
    private final Map<UUID, PvPZone> zones;
    private final Map<UUID, UUID> playerZoneMap; // 플레이어 UUID -> 현재 존 UUID

    private boolean enabled;
    private int defaultSafeZoneRadius;
    private double combatZoneRewardMultiplier;

    public ZoneManager(PvPCore plugin) {
        this.plugin = plugin;
        this. zones = new ConcurrentHashMap<>();
        this.playerZoneMap = new ConcurrentHashMap<>();
        loadConfig();
    }

    private void loadConfig() {
        this.enabled = plugin.getConfig().getBoolean("zones.enabled", true);
        this.defaultSafeZoneRadius = plugin.getConfig().getInt("zones.safe-zones.default-radius", 50);
        this.combatZoneRewardMultiplier = plugin.getConfig().getDouble("zones. combat-zones.reward-multiplier", 1.5);
    }

    /**
     * 지역 생성
     */
    public PvPZone createZone(String name, ZoneType type, Location corner1, Location corner2) {
        UUID zoneId = UUID.randomUUID();
        PvPZone zone = new PvPZone(zoneId, name, type);
        zone.setCorner1(corner1);
        zone.setCorner2(corner2);

        // 타입별 기본 설정
        if (type == ZoneType.COMBAT) {
            zone.setRewardMultiplier(combatZoneRewardMultiplier);
        }

        zones.put(zoneId, zone);
        plugin.getZoneStorage().saveZone(zone);

        plugin.getLogger().info("지역 생성됨: " + name + " (" + type. getDisplayName() + ")");
        return zone;
    }

    /**
     * 지역 삭제
     */
    public void deleteZone(UUID zoneId) {
        PvPZone zone = zones.get(zoneId);
        if (zone == null) {
            return;
        }

        // 해당 지역에 있는 플레이어 처리
        for (UUID playerId : new ArrayList<>(zone.getPlayersInZone())) {
            playerZoneMap.remove(playerId);
        }

        zones.remove(zoneId);
        plugin.getZoneStorage().deleteZone(zoneId);

        plugin.getLogger().info("지역 삭제됨: " + zone. getZoneName());
    }

    /**
     * 위치의 지역 조회
     */
    public PvPZone getZone(Location location) {
        if (!enabled || location == null) {
            return null;
        }

        for (PvPZone zone : zones.values()) {
            if (zone. isEnabled() && zone.isInZone(location)) {
                return zone;
            }
        }

        return null;
    }

    /**
     * ID로 지역 조회
     */
    public PvPZone getZone(UUID zoneId) {
        return zones. get(zoneId);
    }

    /**
     * 이름으로 지역 조회
     */
    public PvPZone getZoneByName(String name) {
        for (PvPZone zone : zones. values()) {
            if (zone.getZoneName().equalsIgnoreCase(name)) {
                return zone;
            }
        }
        return null;
    }

    /**
     * 모든 지역 조회
     */
    public List<PvPZone> getAllZones() {
        return new ArrayList<>(zones. values());
    }

    /**
     * 타입별 지역 조회
     */
    public List<PvPZone> getZonesByType(ZoneType type) {
        List<PvPZone> result = new ArrayList<>();
        for (PvPZone zone :  zones.values()) {
            if (zone.getType() == type) {
                result.add(zone);
            }
        }
        return result;
    }

    /**
     * 특정 타입 지역 내 여부 확인
     */
    public boolean isInZone(Location location, ZoneType type) {
        PvPZone zone = getZone(location);
        return zone != null && zone.getType() == type;
    }

    /**
     * 안전 지역 여부 확인
     */
    public boolean isInSafeZone(Location location) {
        return isInZone(location, ZoneType. SAFE);
    }

    /**
     * 전투 지역 여부 확인
     */
    public boolean isInCombatZone(Location location) {
        return isInZone(location, ZoneType.COMBAT);
    }

    /**
     * 혼돈 지역 여부 확인
     */
    public boolean isInChaosZone(Location location) {
        return isInZone(location, ZoneType.CHAOS);
    }

    /**
     * 플레이어 지역 입장 처리
     */
    public void onPlayerEnterZone(Player player, PvPZone zone) {
        UUID previousZoneId = playerZoneMap.get(player.getUniqueId());

        // 이전 지역에서 퇴장
        if (previousZoneId != null) {
            PvPZone previousZone = zones. get(previousZoneId);
            if (previousZone != null && ! previousZone.getZoneId().equals(zone.getZoneId())) {
                onPlayerLeaveZone(player, previousZone);
            }
        }

        // 새 지역 입장
        zone.playerEnter(player. getUniqueId());
        playerZoneMap.put(player.getUniqueId(), zone.getZoneId());

        // 입장 메시지
        String enterMessage = zone.getEnterMessage();
        if (enterMessage != null && !enterMessage.isEmpty()) {
            MessageUtil.sendMessage(player, enterMessage);
        }

        // 액션바로 지역 정보 표시
        sendZoneActionBar(player, zone);

        // 비행 제한
        if (! zone.isAllowFlight() && player.isFlying()) {
            player.setFlying(false);
            player.setAllowFlight(false);
            MessageUtil.sendMessage(player, "&c이 지역에서는 비행이 불가능합니다.");
        }

        // 보스바 표시 (선택적)
        showZoneBossBar(player, zone);
    }

    /**
     * 플레이어 지역 퇴장 처리
     */
    public void onPlayerLeaveZone(Player player, PvPZone zone) {
        zone.playerLeave(player.getUniqueId());

        // 퇴장 메시지
        String leaveMessage = zone. getLeaveMessage();
        if (leaveMessage != null && !leaveMessage. isEmpty()) {
            MessageUtil.sendMessage(player, leaveMessage);
        }

        // 보스바 제거
        hideZoneBossBar(player);
    }

    /**
     * 플레이어 위치 업데이트 (이동 이벤트에서 호출)
     */
    public void updatePlayerZone(Player player) {
        if (!enabled) {
            return;
        }

        PvPZone currentZone = getZone(player.getLocation());
        UUID currentZoneId = playerZoneMap.get(player.getUniqueId());

        if (currentZone == null) {
            // 지역 밖으로 나감
            if (currentZoneId != null) {
                PvPZone previousZone = zones. get(currentZoneId);
                if (previousZone != null) {
                    onPlayerLeaveZone(player, previousZone);
                }
                playerZoneMap. remove(player.getUniqueId());
            }
        } else {
            // 새 지역으로 입장
            if (currentZoneId == null || !currentZoneId.equals(currentZone.getZoneId())) {
                onPlayerEnterZone(player, currentZone);
            }
        }
    }

    /**
     * 지역 액션바 표시
     */
    private void sendZoneActionBar(Player player, PvPZone zone) {
        String actionBar = zone.getType().getColor() + "⚔ " +
                zone.getZoneName() + " &7[" + zone.getType().getDisplayName() + "]";

        player.spigot().sendMessage(
                net.md_5.bungee.api.ChatMessageType. ACTION_BAR,
                net.md_5.bungee.api.chat.TextComponent.fromLegacyText(
                        MessageUtil.colorize(actionBar)
                )[0]
        );
    }

    /**
     * 지역 보스바 표시
     */
    private void showZoneBossBar(Player player, PvPZone zone) {
        // 보스바 구현 (선택적)
        // BossBar API 사용
    }

    /**
     * 지역 보스바 제거
     */
    private void hideZoneBossBar(Player player) {
        // 보스바 제거 구현
    }

    /**
     * 플레이어의 현재 지역 조회
     */
    public PvPZone getPlayerZone(Player player) {
        UUID zoneId = playerZoneMap.get(player.getUniqueId());
        if (zoneId == null) {
            return null;
        }
        return zones.get(zoneId);
    }

    /**
     * 지역 보상 배율 조회
     */
    public double getRewardMultiplier(Location location) {
        PvPZone zone = getZone(location);
        if (zone == null) {
            return 1.0;
        }
        return zone.getRewardMultiplier();
    }

    /**
     * 지역 경험치 배율 조회
     */
    public double getExpMultiplier(Location location) {
        PvPZone zone = getZone(location);
        if (zone == null) {
            return 1.0;
        }
        return zone.getExpMultiplier();
    }

    /**
     * 지역 사망 패널티 배율 조회
     */
    public double getDeathPenaltyMultiplier(Location location) {
        PvPZone zone = getZone(location);
        if (zone == null) {
            return 1.0;
        }
        return zone.getDeathPenaltyMultiplier();
    }

    /**
     * PvP 허용 여부 확인
     */
    public boolean isPvPAllowed(Location location) {
        PvPZone zone = getZone(location);
        if (zone == null) {
            return true; // 지역 밖은 기본적으로 PvP 허용
        }
        return zone.isPvPAllowed();
    }

    /**
     * 레벨 제한 확인
     */
    public boolean meetsLevelRequirement(Player player, PvPZone zone) {
        if (zone == null) {
            return true;
        }

        // PlayerDataCore에서 레벨 조회
        int playerLevel = player.getLevel(); // 임시로 바닐라 레벨 사용
        return zone.meetsLevelRequirement(playerLevel);
    }

    /**
     * 지역 로드
     */
    public void loadZone(PvPZone zone) {
        zones.put(zone. getZoneId(), zone);
    }

    /**
     * 플레이어 데이터 언로드
     */
    public void unloadPlayerData(UUID playerId) {
        UUID zoneId = playerZoneMap.remove(playerId);
        if (zoneId != null) {
            PvPZone zone = zones.get(zoneId);
            if (zone != null) {
                zone.playerLeave(playerId);
            }
        }
    }

    public void reload() {
        loadConfig();
    }
}