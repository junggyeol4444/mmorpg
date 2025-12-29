package com. multiverse.pet. data;

import com.multiverse.pet.model.Pet;

import java.util.*;
import java.util. concurrent.ConcurrentHashMap;

/**
 * 플레이어 펫 데이터
 * 플레이어가 보유한 모든 펫 정보 및 설정
 */
public class PlayerPetData {

    private final UUID playerId;

    // 보유 펫 목록
    private final Map<UUID, Pet> pets;

    // 설정
    private boolean autoSummonEnabled;
    private String sortType;
    private String lastActivePetId;

    // 필터
    private String currentFilter;

    // 통계
    private int totalPetsOwned;
    private int battleWins;
    private int battleLosses;
    private int rating;

    // 교배 쿨다운
    private final Map<UUID, Long> breedingCooldowns;

    // 알 목록
    private final List<UUID> eggs;

    // 진행 중인 교배
    private final List<UUID> activeBreedings;

    /**
     * 생성자
     */
    public PlayerPetData(UUID playerId) {
        this.playerId = playerId;
        this.pets = new ConcurrentHashMap<>();
        this.breedingCooldowns = new ConcurrentHashMap<>();
        this.eggs = Collections.synchronizedList(new ArrayList<>());
        this.activeBreedings = Collections. synchronizedList(new ArrayList<>());

        // 기본값
        this.autoSummonEnabled = true;
        this. sortType = "LEVEL";
        this. rating = 1000;
    }

    // ===== 펫 관리 =====

    /**
     * 펫 추가
     */
    public void addPet(Pet pet) {
        pets.put(pet.getPetId(), pet);
        totalPetsOwned++;
    }

    /**
     * 펫 제거
     */
    public Pet removePet(UUID petId) {
        return pets.remove(petId);
    }

    /**
     * 펫 가져오기
     */
    public Pet getPet(UUID petId) {
        return pets.get(petId);
    }

    /**
     * 모든 펫 가져오기
     */
    public List<Pet> getPets() {
        return new ArrayList<>(pets.values());
    }

    /**
     * 펫 수
     */
    public int getPetCount() {
        return pets.size();
    }

    /**
     * 펫 보유 여부
     */
    public boolean hasPet(UUID petId) {
        return pets.containsKey(petId);
    }

    // ===== Getter/Setter =====

    public UUID getPlayerId() {
        return playerId;
    }

    public boolean isAutoSummonEnabled() {
        return autoSummonEnabled;
    }

    public void setAutoSummonEnabled(boolean autoSummonEnabled) {
        this.autoSummonEnabled = autoSummonEnabled;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getLastActivePetId() {
        return lastActivePetId;
    }

    public void setLastActivePetId(String lastActivePetId) {
        this.lastActivePetId = lastActivePetId;
    }

    public String getCurrentFilter() {
        return currentFilter;
    }

    public void setCurrentFilter(String currentFilter) {
        this. currentFilter = currentFilter;
    }

    public int getTotalPetsOwned() {
        return totalPetsOwned;
    }

    public void setTotalPetsOwned(int totalPetsOwned) {
        this.totalPetsOwned = totalPetsOwned;
    }

    public int getBattleWins() {
        return battleWins;
    }

    public void setBattleWins(int battleWins) {
        this.battleWins = battleWins;
    }

    public void incrementBattleWins() {
        this.battleWins++;
    }

    public int getBattleLosses() {
        return battleLosses;
    }

    public void setBattleLosses(int battleLosses) {
        this.battleLosses = battleLosses;
    }

    public void incrementBattleLosses() {
        this.battleLosses++;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = Math.max(0, rating);
    }

    public void addRating(int amount) {
        this.rating = Math.max(0, this.rating + amount);
    }

    // ===== 교배 쿨다운 =====

    public void setBreedingCooldown(UUID petId, long cooldownEnd) {
        breedingCooldowns.put(petId, cooldownEnd);
    }

    public long getBreedingCooldown(UUID petId) {
        return breedingCooldowns.getOrDefault(petId, 0L);
    }

    public boolean isOnBreedingCooldown(UUID petId) {
        Long cooldown = breedingCooldowns. get(petId);
        return cooldown != null && cooldown > System.currentTimeMillis();
    }

    public void clearBreedingCooldown(UUID petId) {
        breedingCooldowns.remove(petId);
    }

    // ===== 알 =====

    public List<UUID> getEggs() {
        return new ArrayList<>(eggs);
    }

    public void addEgg(UUID eggId) {
        eggs.add(eggId);
    }

    public void removeEgg(UUID eggId) {
        eggs.remove(eggId);
    }

    // ===== 진행 중인 교배 =====

    public List<UUID> getActiveBreedings() {
        return new ArrayList<>(activeBreedings);
    }

    public void addActiveBreeding(UUID breedingId) {
        activeBreedings.add(breedingId);
    }

    public void removeActiveBreeding(UUID breedingId) {
        activeBreedings.remove(breedingId);
    }

    // ===== 통계 =====

    /**
     * 승률 계산
     */
    public double getWinRate() {
        int total = battleWins + battleLosses;
        if (total == 0) return 0;
        return (double) battleWins / total * 100;
    }

    /**
     * 통계 요약
     */
    public String getStatsSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("보유 펫:  ").append(pets.size()).append("마리\n");
        sb.append("총 획득: ").append(totalPetsOwned).append("마리\n");
        sb.append("레이팅: ").append(rating).append("\n");
        sb.append("전적:  ").append(battleWins).append("승 ").append(battleLosses).append("패\n");
        sb.append("승률: ").append(String.format("%.1f", getWinRate())).append("%");
        return sb.toString();
    }
}