package com.multiverse.pet. data. cache;

import com.multiverse.pet.PetCore;
import com.multiverse. pet.model.Pet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util. stream.Collectors;

/**
 * 펫 캐시
 * 자주 접근하는 펫 데이터를 메모리에 캐싱
 */
public class PetCache {

    private final PetCore plugin;

    // 플레이어별 펫 캐시
    private final Map<UUID, Map<UUID, Pet>> playerPetCache;

    // 활성 펫 캐시 (빠른 접근용)
    private final Map<UUID, Set<UUID>> activePetCache;

    // 펫 ID -> 소유자 ID 매핑
    private final Map<UUID, UUID> petOwnerMap;

    // 캐시 통계
    private long cacheHits;
    private long cacheMisses;

    public PetCache(PetCore plugin) {
        this.plugin = plugin;
        this.playerPetCache = new ConcurrentHashMap<>();
        this.activePetCache = new ConcurrentHashMap<>();
        this.petOwnerMap = new ConcurrentHashMap<>();
        this.cacheHits = 0;
        this.cacheMisses = 0;
    }

    // ===== 펫 캐시 관리 =====

    /**
     * 펫 캐시에 추가
     */
    public void cachePet(UUID playerId, Pet pet) {
        playerPetCache.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>())
                .put(pet.getPetId(), pet);
        petOwnerMap.put(pet.getPetId(), playerId);
    }

    /**
     * 펫 캐시에서 가져오기
     */
    public Pet getPet(UUID playerId, UUID petId) {
        Map<UUID, Pet> pets = playerPetCache.get(playerId);
        if (pets != null) {
            Pet pet = pets.get(petId);
            if (pet != null) {
                cacheHits++;
                return pet;
            }
        }
        cacheMisses++;
        return null;
    }

    /**
     * 펫 ID로 펫 가져오기
     */
    public Pet getPetById(UUID petId) {
        UUID ownerId = petOwnerMap.get(petId);
        if (ownerId != null) {
            return getPet(ownerId, petId);
        }
        return null;
    }

    /**
     * 펫 소유자 가져오기
     */
    public UUID getPetOwner(UUID petId) {
        return petOwnerMap.get(petId);
    }

    /**
     * 플레이어의 모든 펫 가져오기
     */
    public List<Pet> getAllPets(UUID playerId) {
        Map<UUID, Pet> pets = playerPetCache.get(playerId);
        if (pets != null) {
            cacheHits++;
            return new ArrayList<>(pets. values());
        }
        cacheMisses++;
        return new ArrayList<>();
    }

    /**
     * 플레이어의 펫 수
     */
    public int getPetCount(UUID playerId) {
        Map<UUID, Pet> pets = playerPetCache. get(playerId);
        return pets != null ? pets.size() : 0;
    }

    /**
     * 펫 캐시에서 제거
     */
    public Pet removePet(UUID playerId, UUID petId) {
        petOwnerMap.remove(petId);
        removeFromActivePets(playerId, petId);

        Map<UUID, Pet> pets = playerPetCache.get(playerId);
        if (pets != null) {
            return pets.remove(petId);
        }
        return null;
    }

    /**
     * 펫 업데이트
     */
    public void updatePet(Pet pet) {
        UUID ownerId = pet.getOwnerId();
        if (ownerId != null) {
            cachePet(ownerId, pet);
        }
    }

    // ===== 활성 펫 캐시 =====

    /**
     * 활성 펫으로 표시
     */
    public void markAsActive(UUID playerId, UUID petId) {
        activePetCache.computeIfAbsent(playerId, k -> ConcurrentHashMap. newKeySet())
                .add(petId);

        Pet pet = getPet(playerId, petId);
        if (pet != null) {
            pet.setActive(true);
        }
    }

    /**
     * 활성 펫에서 제거
     */
    public void removeFromActivePets(UUID playerId, UUID petId) {
        Set<UUID> activePets = activePetCache.get(playerId);
        if (activePets != null) {
            activePets. remove(petId);
        }

        Pet pet = getPet(playerId, petId);
        if (pet != null) {
            pet.setActive(false);
        }
    }

    /**
     * 플레이어의 활성 펫 ID 목록
     */
    public Set<UUID> getActivePetIds(UUID playerId) {
        Set<UUID> activePets = activePetCache.get(playerId);
        return activePets != null ? new HashSet<>(activePets) : new HashSet<>();
    }

    /**
     * 플레이어의 활성 펫 목록
     */
    public List<Pet> getActivePets(UUID playerId) {
        Set<UUID> activePetIds = getActivePetIds(playerId);
        List<Pet> result = new ArrayList<>();

        for (UUID petId : activePetIds) {
            Pet pet = getPet(playerId, petId);
            if (pet != null) {
                result. add(pet);
            }
        }

        return result;
    }

    /**
     * 활성 펫 여부 확인
     */
    public boolean isActivePet(UUID playerId, UUID petId) {
        Set<UUID> activePets = activePetCache.get(playerId);
        return activePets != null && activePets.contains(petId);
    }

    /**
     * 활성 펫이 있는지 확인
     */
    public boolean hasActivePet(UUID playerId) {
        Set<UUID> activePets = activePetCache.get(playerId);
        return activePets != null && !activePets. isEmpty();
    }

    /**
     * 모든 활성 펫 해제
     */
    public void clearActivePets(UUID playerId) {
        Set<UUID> activePets = activePetCache.remove(playerId);
        if (activePets != null) {
            for (UUID petId :  activePets) {
                Pet pet = getPet(playerId, petId);
                if (pet != null) {
                    pet.setActive(false);
                }
            }
        }
    }

    // ===== 플레이어 캐시 관리 =====

    /**
     * 플레이어 캐시 로드
     */
    public void loadPlayerCache(UUID playerId, List<Pet> pets) {
        Map<UUID, Pet> petMap = new ConcurrentHashMap<>();
        for (Pet pet : pets) {
            petMap. put(pet.getPetId(), pet);
            petOwnerMap. put(pet.getPetId(), playerId);
        }
        playerPetCache.put(playerId, petMap);
    }

    /**
     * 플레이어 캐시 제거
     */
    public void removePlayer(UUID playerId) {
        Map<UUID, Pet> pets = playerPetCache.remove(playerId);
        if (pets != null) {
            for (UUID petId : pets. keySet()) {
                petOwnerMap.remove(petId);
            }
        }
        activePetCache.remove(playerId);
    }

    /**
     * 플레이어 캐시 존재 확인
     */
    public boolean hasPlayerCache(UUID playerId) {
        return playerPetCache. containsKey(playerId);
    }

    // ===== 검색 =====

    /**
     * 이름으로 펫 검색
     */
    public Pet findPetByName(UUID playerId, String name) {
        Map<UUID, Pet> pets = playerPetCache.get(playerId);
        if (pets == null) return null;

        // 정확히 일치
        for (Pet pet : pets.values()) {
            if (pet. getPetName().equalsIgnoreCase(name)) {
                return pet;
            }
        }

        // 부분 일치
        for (Pet pet : pets.values()) {
            if (pet.getPetName().toLowerCase().contains(name.toLowerCase())) {
                return pet;
            }
        }

        return null;
    }

    /**
     * 조건으로 펫 필터링
     */
    public List<Pet> filterPets(UUID playerId, java.util.function. Predicate<Pet> predicate) {
        Map<UUID, Pet> pets = playerPetCache.get(playerId);
        if (pets == null) return new ArrayList<>();

        return pets. values().stream()
                .filter(predicate)
                .collect(Collectors. toList());
    }

    // ===== 통계 =====

    /**
     * 캐시 크기
     */
    public int getCacheSize() {
        int total = 0;
        for (Map<UUID, Pet> pets : playerPetCache.values()) {
            total += pets. size();
        }
        return total;
    }

    /**
     * 캐시된 플레이어 수
     */
    public int getCachedPlayerCount() {
        return playerPetCache.size();
    }

    /**
     * 캐시 히트율
     */
    public double getCacheHitRate() {
        long total = cacheHits + cacheMisses;
        if (total == 0) return 0;
        return (double) cacheHits / total * 100;
    }

    /**
     * 캐시 통계 초기화
     */
    public void resetStats() {
        cacheHits = 0;
        cacheMisses = 0;
    }

    /**
     * 캐시 통계 문자열
     */
    public String getStatsString() {
        return String.format("캐시 크기: %d, 플레이어:  %d, 히트율: %.1f%% (%d/%d)",
                getCacheSize(), getCachedPlayerCount(), getCacheHitRate(),
                cacheHits, cacheHits + cacheMisses);
    }

    // ===== 정리 =====

    /**
     * 전체 캐시 정리
     */
    public void clear() {
        playerPetCache.clear();
        activePetCache.clear();
        petOwnerMap.clear();
        resetStats();
    }

    /**
     * 종료 처리
     */
    public void shutdown() {
        clear();
    }
}