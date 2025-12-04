package com.multiverse.dungeon.  data.model;

import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 랜덤 던전 방 데이터 클래스
 */
public class DungeonRoom {

    private UUID roomId; // 방 ID
    private int roomNumber; // 방 번호
    private String roomTemplate; // 방 템플릿 (구조)
    
    private Location cornerMin; // 방의 최소 좌표
    private Location cornerMax; // 방의 최대 좌표
    
    private List<String> connectedRooms; // 연결된 방 ID 목록
    private List<Location> mobSpawns; // 몬스터 스폰 위치
    private List<Location> treasureChests; // 보물 상자 위치
    private List<DungeonTrap> traps; // 함정 목록
    
    private int mobCount; // 현재 몬스터 개수
    private boolean cleared; // 방 클리어 여부

    /**
     * 생성자
     */
    public DungeonRoom(UUID roomId, int roomNumber, String roomTemplate) {
        this.roomId = roomId;
        this. roomNumber = roomNumber;
        this.roomTemplate = roomTemplate;
        this.connectedRooms = new ArrayList<>();
        this.mobSpawns = new ArrayList<>();
        this.treasureChests = new ArrayList<>();
        this.traps = new ArrayList<>();
        this.mobCount = 0;
        this.cleared = false;
    }

    /**
     * 기본 생성자
     */
    public DungeonRoom() {
        this(UUID.randomUUID(), 0, "default");
    }

    // ===== Getters & Setters =====

    public UUID getRoomId() {
        return roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomTemplate() {
        return roomTemplate;
    }

    public void setRoomTemplate(String roomTemplate) {
        this.roomTemplate = roomTemplate;
    }

    public Location getCornerMin() {
        return cornerMin;
    }

    public void setCornerMin(Location cornerMin) {
        this.cornerMin = cornerMin;
    }

    public Location getCornerMax() {
        return cornerMax;
    }

    public void setCornerMax(Location cornerMax) {
        this.cornerMax = cornerMax;
    }

    public List<String> getConnectedRooms() {
        return connectedRooms;
    }

    public void setConnectedRooms(List<String> connectedRooms) {
        this.connectedRooms = connectedRooms != null ? connectedRooms : new ArrayList<>();
    }

    public void addConnectedRoom(String roomId) {
        if (!this.connectedRooms.contains(roomId)) {
            this.connectedRooms.add(roomId);
        }
    }

    public List<Location> getMobSpawns() {
        return mobSpawns;
    }

    public void setMobSpawns(List<Location> mobSpawns) {
        this.mobSpawns = mobSpawns != null ? mobSpawns : new ArrayList<>();
    }

    public void addMobSpawn(Location location) {
        this.mobSpawns.add(location);
    }

    public List<Location> getTreasureChests() {
        return treasureChests;
    }

    public void setTreasureChests(List<Location> treasureChests) {
        this.treasureChests = treasureChests != null ? treasureChests : new ArrayList<>();
    }

    public void addTreasureChest(Location location) {
        this.treasureChests.add(location);
    }

    public List<DungeonTrap> getTraps() {
        return traps;
    }

    public void setTraps(List<DungeonTrap> traps) {
        this.traps = traps != null ? traps : new ArrayList<>();
    }

    public void addTrap(DungeonTrap trap) {
        this.traps.add(trap);
    }

    public int getMobCount() {
        return mobCount;
    }

    public void setMobCount(int mobCount) {
        this.mobCount = Math.max(0, mobCount);
    }

    public void incrementMobCount() {
        this.mobCount++;
    }

    public void decrementMobCount() {
        this.mobCount = Math.max(0, mobCount - 1);
    }

    public boolean isCleared() {
        return cleared;
    }

    public void setCleared(boolean cleared) {
        this.cleared = cleared;
    }

    /**
     * 방이 모두 클리어되었는지 확인
     * (모든 몬스터 처치)
     *
     * @return 클리어되었으면 true
     */
    public boolean isFullyCleared() {
        return cleared && mobCount == 0;
    }

    /**
     * 방에 몬스터가 있는지 확인
     *
     * @return 몬스터가 있으면 true
     */
    public boolean hasMobs() {
        return mobCount > 0;
    }

    /**
     * 방의 크기 계산
     *
     * @return 방의 부피
     */
    public long getVolume() {
        if (cornerMin == null || cornerMax == null) {
            return 0;
        }
        long x = Math.abs(cornerMax. getBlockX() - cornerMin.getBlockX());
        long y = Math.abs(cornerMax.getBlockY() - cornerMin.getBlockY());
        long z = Math.abs(cornerMax.getBlockZ() - cornerMin.getBlockZ());
        return x * y * z;
    }

    @Override
    public String toString() {
        return "DungeonRoom{" +
                "roomId=" + roomId +
                ", roomNumber=" + roomNumber +
                ", mobCount=" + mobCount +
                ", cleared=" + cleared +
                ", connectedRooms=" + connectedRooms. size() +
                ", traps=" + traps.size() +
                '}';
    }
}