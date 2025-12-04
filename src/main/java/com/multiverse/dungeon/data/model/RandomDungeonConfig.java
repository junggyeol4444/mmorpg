package com.multiverse.dungeon. data.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 랜덤 던전 설정 데이터 클래스
 */
public class RandomDungeonConfig {

    // ===== 구조 설정 =====
    private int minRooms; // 최소 방 개수
    private int maxRooms; // 최대 방 개수
    private List<String> roomTemplates; // 방 템플릿 목록

    // ===== 몬스터 설정 =====
    private List<String> mobPool; // 몬스터 풀
    private int minMobsPerRoom; // 방당 최소 몬스터
    private int maxMobsPerRoom; // 방당 최대 몬스터

    // ===== 보스 설정 =====
    private List<String> bossPool; // 보스 풀
    private int bossCount; // 보스 개수

    // ===== 보물 설정 =====
    private int minChests; // 최소 보물 상자
    private int maxChests; // 최대 보물 상자

    // ===== 함정 설정 =====
    private List<String> trapTypes; // 함정 타입 목록
    private double trapDensity; // 함정 밀도 (%)

    /**
     * 생성자
     */
    public RandomDungeonConfig() {
        this. minRooms = 5;
        this.maxRooms = 15;
        this.roomTemplates = new ArrayList<>();
        
        this.mobPool = new ArrayList<>();
        this.minMobsPerRoom = 3;
        this.maxMobsPerRoom = 8;
        
        this.bossPool = new ArrayList<>();
        this. bossCount = 1;
        
        this.minChests = 2;
        this.maxChests = 5;
        
        this.trapTypes = new ArrayList<>();
        this.trapDensity = 15.0;
    }

    // ===== Getters & Setters =====

    public int getMinRooms() {
        return minRooms;
    }

    public void setMinRooms(int minRooms) {
        this.minRooms = Math.max(1, minRooms);
    }

    public int getMaxRooms() {
        return maxRooms;
    }

    public void setMaxRooms(int maxRooms) {
        this.maxRooms = Math.max(this.minRooms, maxRooms);
    }

    public List<String> getRoomTemplates() {
        return roomTemplates;
    }

    public void setRoomTemplates(List<String> roomTemplates) {
        this.roomTemplates = roomTemplates != null ? roomTemplates : new ArrayList<>();
    }

    public void addRoomTemplate(String template) {
        this.roomTemplates.add(template);
    }

    public List<String> getMobPool() {
        return mobPool;
    }

    public void setMobPool(List<String> mobPool) {
        this.mobPool = mobPool != null ? mobPool : new ArrayList<>();
    }

    public void addMobToPool(String mobId) {
        this.mobPool.add(mobId);
    }

    public int getMinMobsPerRoom() {
        return minMobsPerRoom;
    }

    public void setMinMobsPerRoom(int minMobsPerRoom) {
        this.minMobsPerRoom = Math.max(0, minMobsPerRoom);
    }

    public int getMaxMobsPerRoom() {
        return maxMobsPerRoom;
    }

    public void setMaxMobsPerRoom(int maxMobsPerRoom) {
        this.maxMobsPerRoom = Math.max(this.minMobsPerRoom, maxMobsPerRoom);
    }

    public List<String> getBossPool() {
        return bossPool;
    }

    public void setBossPool(List<String> bossPool) {
        this.bossPool = bossPool != null ? bossPool : new ArrayList<>();
    }

    public void addBossToPool(String bossId) {
        this. bossPool.add(bossId);
    }

    public int getBossCount() {
        return bossCount;
    }

    public void setBossCount(int bossCount) {
        this.bossCount = Math. max(0, bossCount);
    }

    public int getMinChests() {
        return minChests;
    }

    public void setMinChests(int minChests) {
        this.minChests = Math. max(0, minChests);
    }

    public int getMaxChests() {
        return maxChests;
    }

    public void setMaxChests(int maxChests) {
        this.maxChests = Math. max(this.minChests, maxChests);
    }

    public List<String> getTrapTypes() {
        return trapTypes;
    }

    public void setTrapTypes(List<String> trapTypes) {
        this.trapTypes = trapTypes != null ? trapTypes : new ArrayList<>();
    }

    public void addTrapType(String trapType) {
        this.trapTypes.add(trapType);
    }

    public double getTrapDensity() {
        return trapDensity;
    }

    public void setTrapDensity(double trapDensity) {
        this.trapDensity = Math.max(0, Math.min(100, trapDensity));
    }

    /**
     * 랜덤 방 개수 생성
     *
     * @return 랜덤 방 개수
     */
    public int generateRandomRoomCount() {
        if (minRooms == maxRooms) {
            return minRooms;
        }
        return minRooms + (int) (Math.random() * (maxRooms - minRooms + 1));
    }

    /**
     * 방당 랜덤 몬스터 개수 생성
     *
     * @return 랜덤 몬스터 개수
     */
    public int generateRandomMobCount() {
        if (minMobsPerRoom == maxMobsPerRoom) {
            return minMobsPerRoom;
        }
        return minMobsPerRoom + (int) (Math.random() * (maxMobsPerRoom - minMobsPerRoom + 1));
    }

    /**
     * 랜덤 보물 상자 개수 생성
     *
     * @return 랜덤 보물 상자 개수
     */
    public int generateRandomChestCount() {
        if (minChests == maxChests) {
            return minChests;
        }
        return minChests + (int) (Math.random() * (maxChests - minChests + 1));
    }

    /**
     * 몬스터 풀에서 랜덤 몬스터 선택
     *
     * @return 선택된 몬스터 ID
     */
    public String getRandomMob() {
        if (mobPool.isEmpty()) {
            return null;
        }
        return mobPool.get((int) (Math.random() * mobPool.size()));
    }

    /**
     * 보스 풀에서 랜덤 보스 선택
     *
     * @return 선택된 보스 ID
     */
    public String getRandomBoss() {
        if (bossPool.isEmpty()) {
            return null;
        }
        return bossPool.get((int) (Math. random() * bossPool.size()));
    }

    /**
     * 함정 타입 풀에서 랜덤 함정 선택
     *
     * @return 선택된 함정 타입
     */
    public String getRandomTrapType() {
        if (trapTypes.isEmpty()) {
            return null;
        }
        return trapTypes.get((int) (Math.random() * trapTypes.size()));
    }

    @Override
    public String toString() {
        return "RandomDungeonConfig{" +
                "minRooms=" + minRooms +
                ", maxRooms=" + maxRooms +
                ", minMobsPerRoom=" + minMobsPerRoom +
                ", maxMobsPerRoom=" + maxMobsPerRoom +
                ", bossCount=" + bossCount +
                ", trapDensity=" + trapDensity +
                '}';
    }
}