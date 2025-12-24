package com.multiverse.pvp.data;

import com. multiverse.pvp.enums.ZoneType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit. entity.Player;

import java.util. HashSet;
import java.util.Set;
import java.util.UUID;

public class PvPZone {

    private UUID zoneId;
    private String zoneName;
    private ZoneType type;

    // 영역
    private String worldName;
    private Location corner1;
    private Location corner2;

    // 설정
    private boolean autoPvP;
    private double rewardMultiplier;
    private double expMultiplier;

    // 제한
    private int minLevel;
    private int maxLevel;

    // 활성화 여부
    private boolean enabled;

    // 입장 메시지
    private String enterMessage;
    private String leaveMessage;

    // 현재 지역 내 플레이어
    private Set<UUID> playersInZone;

    // 특수 설정
    private boolean allowFlight;
    private boolean allowTeleport;
    private boolean keepInventory;
    private boolean dropItems;

    // 사망 패널티 배율
    private double deathPenaltyMultiplier;

    public PvPZone(UUID zoneId, String zoneName, ZoneType type) {
        this. zoneId = zoneId;
        this.zoneName = zoneName;
        this.type = type;

        this.autoPvP = type != ZoneType. SAFE;
        this. rewardMultiplier = type.getDefaultRewardMultiplier();
        this.expMultiplier = type.getDefaultExpMultiplier();

        this.minLevel = 0;
        this. maxLevel = Integer.MAX_VALUE;

        this.enabled = true;

        this.enterMessage = type.getDefaultEnterMessage();
        this.leaveMessage = type.getDefaultLeaveMessage();

        this.playersInZone = new HashSet<>();

        // 기본 설정
        setDefaultSettings();
    }

    private void setDefaultSettings() {
        switch (type) {
            case SAFE: 
                this.allowFlight = true;
                this.allowTeleport = true;
                this. keepInventory = true;
                this. dropItems = false;
                this.deathPenaltyMultiplier = 0.0;
                break;
            case COMBAT:
                this. allowFlight = false;
                this. allowTeleport = false;
                this.keepInventory = false;
                this. dropItems = true;
                this.deathPenaltyMultiplier = 1.0;
                break;
            case CHAOS:
                this. allowFlight = false;
                this. allowTeleport = false;
                this.keepInventory = false;
                this.dropItems = true;
                this. deathPenaltyMultiplier = 2.0;
                break;
            default:
                this. allowFlight = true;
                this. allowTeleport = true;
                this.keepInventory = false;
                this. dropItems = true;
                this.deathPenaltyMultiplier = 1.0;
                break;
        }
    }

    // ==================== Getters ====================

    public UUID getZoneId() {
        return zoneId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public ZoneType getType() {
        return type;
    }

    public String getWorldName() {
        return worldName;
    }

    public Location getCorner1() {
        return corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public boolean isAutoPvP() {
        return autoPvP;
    }

    public double getRewardMultiplier() {
        return rewardMultiplier;
    }

    public double getExpMultiplier() {
        return expMultiplier;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getEnterMessage() {
        return enterMessage;
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public Set<UUID> getPlayersInZone() {
        return playersInZone;
    }

    public boolean isAllowFlight() {
        return allowFlight;
    }

    public boolean isAllowTeleport() {
        return allowTeleport;
    }

    public boolean isKeepInventory() {
        return keepInventory;
    }

    public boolean isDropItems() {
        return dropItems;
    }

    public double getDeathPenaltyMultiplier() {
        return deathPenaltyMultiplier;
    }

    // ==================== Setters ====================

    public void setZoneId(UUID zoneId) {
        this.zoneId = zoneId;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public void setType(ZoneType type) {
        this.type = type;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public void setCorner1(Location corner1) {
        this.corner1 = corner1;
        if (corner1 != null) {
            this.worldName = corner1.getWorld().getName();
        }
    }

    public void setCorner2(Location corner2) {
        this. corner2 = corner2;
    }

    public void setAutoPvP(boolean autoPvP) {
        this.autoPvP = autoPvP;
    }

    public void setRewardMultiplier(double rewardMultiplier) {
        this.rewardMultiplier = rewardMultiplier;
    }

    public void setExpMultiplier(double expMultiplier) {
        this.expMultiplier = expMultiplier;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this. maxLevel = maxLevel;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEnterMessage(String enterMessage) {
        this.enterMessage = enterMessage;
    }

    public void setLeaveMessage(String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

    public void setPlayersInZone(Set<UUID> playersInZone) {
        this.playersInZone = playersInZone;
    }

    public void setAllowFlight(boolean allowFlight) {
        this.allowFlight = allowFlight;
    }

    public void setAllowTeleport(boolean allowTeleport) {
        this.allowTeleport = allowTeleport;
    }

    public void setKeepInventory(boolean keepInventory) {
        this. keepInventory = keepInventory;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    public void setDeathPenaltyMultiplier(double deathPenaltyMultiplier) {
        this.deathPenaltyMultiplier = deathPenaltyMultiplier;
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 위치가 지역 내부인지 확인
     */
    public boolean isInZone(Location location) {
        if (! enabled || corner1 == null || corner2 == null) {
            return false;
        }

        if (location.getWorld() == null || ! location.getWorld().getName().equals(worldName)) {
            return false;
        }

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double minY = Math. min(corner1.getY(), corner2.getY());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    /**
     * 플레이어가 지역에 있는지 확인
     */
    public boolean hasPlayer(UUID playerId) {
        return playersInZone.contains(playerId);
    }

    /**
     * 플레이어 입장 처리
     */
    public void playerEnter(UUID playerId) {
        playersInZone. add(playerId);
    }

    /**
     * 플레이어 퇴장 처리
     */
    public void playerLeave(UUID playerId) {
        playersInZone. remove(playerId);
    }

    /**
     * 레벨 제한 충족 여부
     */
    public boolean meetsLevelRequirement(int playerLevel) {
        return playerLevel >= minLevel && playerLevel <= maxLevel;
    }

    /**
     * 지역 중심점
     */
    public Location getCenter() {
        if (corner1 == null || corner2 == null) {
            return null;
        }

        World world = corner1.getWorld();
        double x = (corner1.getX() + corner2.getX()) / 2;
        double y = (corner1.getY() + corner2.getY()) / 2;
        double z = (corner1.getZ() + corner2.getZ()) / 2;

        return new Location(world, x, y, z);
    }

    /**
     * 지역 크기 (블록 수)
     */
    public long getVolume() {
        if (corner1 == null || corner2 == null) {
            return 0;
        }

        long width = (long) Math.abs(corner1.getX() - corner2.getX()) + 1;
        long height = (long) Math.abs(corner1.getY() - corner2.getY()) + 1;
        long depth = (long) Math.abs(corner1.getZ() - corner2.getZ()) + 1;

        return width * height * depth;
    }

    /**
     * 지역 크기 문자열
     */
    public String getSizeString() {
        if (corner1 == null || corner2 == null) {
            return "미설정";
        }

        int width = (int) Math.abs(corner1.getX() - corner2.getX()) + 1;
        int height = (int) Math.abs(corner1.getY() - corner2.getY()) + 1;
        int depth = (int) Math.abs(corner1.getZ() - corner2.getZ()) + 1;

        return width + " x " + height + " x " + depth;
    }

    /**
     * PvP 허용 여부 (지역 타입 기반)
     */
    public boolean isPvPAllowed() {
        return type != ZoneType. SAFE && autoPvP;
    }

    /**
     * 지역 경계가 설정되었는지 확인
     */
    public boolean isBoundsSet() {
        return corner1 != null && corner2 != null;
    }

    /**
     * 위치에서 지역 경계까지의 거리
     */
    public double getDistanceFromBorder(Location location) {
        if (! isInZone(location)) {
            return -1;
        }

        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math. max(corner1.getZ(), corner2.getZ());

        double distX = Math.min(location.getX() - minX, maxX - location.getX());
        double distZ = Math.min(location.getZ() - minZ, maxZ - location.getZ());

        return Math.min(distX, distZ);
    }

    /**
     * 현재 지역 내 플레이어 수
     */
    public int getPlayerCount() {
        return playersInZone.size();
    }

    /**
     * 복사본 생성
     */
    public PvPZone clone() {
        PvPZone clone = new PvPZone(UUID.randomUUID(), this.zoneName + "_copy", this.type);
        clone.worldName = this.worldName;
        clone.corner1 = this.corner1 != null ? this. corner1.clone() : null;
        clone.corner2 = this.corner2 != null ? this.corner2.clone() : null;
        clone.autoPvP = this. autoPvP;
        clone.rewardMultiplier = this.rewardMultiplier;
        clone.expMultiplier = this.expMultiplier;
        clone.minLevel = this.minLevel;
        clone. maxLevel = this. maxLevel;
        clone.enabled = this.enabled;
        clone.enterMessage = this.enterMessage;
        clone.leaveMessage = this. leaveMessage;
        clone.allowFlight = this. allowFlight;
        clone.allowTeleport = this. allowTeleport;
        clone.keepInventory = this. keepInventory;
        clone.dropItems = this.dropItems;
        clone. deathPenaltyMultiplier = this.deathPenaltyMultiplier;
        return clone;
    }

    @Override
    public String toString() {
        return "PvPZone{" +
                "zoneId=" + zoneId +
                ", zoneName='" + zoneName + '\'' +
                ", type=" + type +
                ", worldName='" + worldName + '\'' +
                ", enabled=" + enabled +
                ", playersInZone=" + playersInZone.size() +
                '}';
    }
}