package com.multiverse.pet.data. cache;

import com.multiverse.pet.PetCore;
import com.multiverse.pet.model.PetRarity;
import com. multiverse.pet. model.PetSpecies;
import com.multiverse.pet.model. PetType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util. stream.Collectors;

/**
 * 펫 종족 캐시
 * 로드된 종족 데이터를 캐싱하고 빠른 검색 제공
 */
public class SpeciesCache {

    private final PetCore plugin;

    // 종족 ID -> 종족 데이터
    private final Map<String, PetSpecies> speciesMap;

    // 타입별 종족 목록
    private final Map<PetType, List<String>> speciesByType;

    // 희귀도별 종족 목록
    private final Map<PetRarity, List<String>> speciesByRarity;

    // 포획 가능한 종족 목록
    private final List<String> capturableSpecies;

    // 진화 관계 맵 (from -> to 목록)
    private final Map<String, List<String>> evolutionMap;

    public SpeciesCache(PetCore plugin) {
        this. plugin = plugin;
        this.speciesMap = new ConcurrentHashMap<>();
        this.speciesByType = new ConcurrentHashMap<>();
        this.speciesByRarity = new ConcurrentHashMap<>();
        this.capturableSpecies = Collections.synchronizedList(new ArrayList<>());
        this.evolutionMap = new ConcurrentHashMap<>();
    }

    // ===== 캐시 로드 =====

    /**
     * 종족 데이터 로드
     */
    public void loadSpecies(Map<String, PetSpecies> species) {
        clear();

        for (Map.Entry<String, PetSpecies> entry : species.entrySet()) {
            String speciesId = entry.getKey();
            PetSpecies sp = entry.getValue();

            speciesMap.put(speciesId, sp);

            // 타입별 분류
            if (sp.getType() != null) {
                speciesByType.computeIfAbsent(sp.getType(), k -> new ArrayList<>())
                        . add(speciesId);
            }

            // 희귀도별 분류
            if (sp.getBaseRarity() != null) {
                speciesByRarity. computeIfAbsent(sp.getBaseRarity(), k -> new ArrayList<>())
                        . add(speciesId);
            }

            // 포획 가능 분류
            if (sp.isCapturable()) {
                capturableSpecies.add(speciesId);
            }

            // 진화 관계
            if (sp.getEvolutionPaths() != null && !sp.getEvolutionPaths().isEmpty()) {
                evolutionMap.put(speciesId, new ArrayList<>(sp.getEvolutionPaths()));
            }
        }

        if (plugin.isDebugMode()) {
            plugin.getLogger().info("[DEBUG] 종족 캐시 로드 완료:  " + speciesMap.size() + "개");
        }
    }

    // ===== 조회 =====

    /**
     * 종족 가져오기
     */
    public PetSpecies getSpecies(String speciesId) {
        return speciesMap. get(speciesId);
    }

    /**
     * 종족 존재 확인
     */
    public boolean hasSpecies(String speciesId) {
        return speciesMap.containsKey(speciesId);
    }

    /**
     * 모든 종족 ID
     */
    public Set<String> getAllSpeciesIds() {
        return new HashSet<>(speciesMap.keySet());
    }

    /**
     * 모든 종족
     */
    public Collection<PetSpecies> getAllSpecies() {
        return new ArrayList<>(speciesMap.values());
    }

    /**
     * 종족 수
     */
    public int getSpeciesCount() {
        return speciesMap. size();
    }

    // ===== 타입별 조회 =====

    /**
     * 타입별 종족 ID 목록
     */
    public List<String> getSpeciesByType(PetType type) {
        List<String> result = speciesByType.get(type);
        return result != null ?  new ArrayList<>(result) : new ArrayList<>();
    }

    /**
     * 타입별 종족 목록
     */
    public List<PetSpecies> getSpeciesListByType(PetType type) {
        return getSpeciesByType(type).stream()
                .map(this::getSpecies)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ===== 희귀도별 조회 =====

    /**
     * 희귀도별 종족 ID 목록
     */
    public List<String> getSpeciesByRarity(PetRarity rarity) {
        List<String> result = speciesByRarity.get(rarity);
        return result != null ? new ArrayList<>(result) : new ArrayList<>();
    }

    /**
     * 희귀도별 종족 목록
     */
    public List<PetSpecies> getSpeciesListByRarity(PetRarity rarity) {
        return getSpeciesByRarity(rarity).stream()
                .map(this::getSpecies)
                .filter(Objects::nonNull)
                .collect(Collectors. toList());
    }

    // ===== 포획 관련 =====

    /**
     * 포획 가능한 종족 ID 목록
     */
    public List<String> getCapturableSpecies() {
        return new ArrayList<>(capturableSpecies);
    }

    /**
     * 포획 가능한 종족 목록
     */
    public List<PetSpecies> getCapturableSpeciesList() {
        return capturableSpecies. stream()
                .map(this::getSpecies)
                .filter(Objects:: nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 포획 가능 여부
     */
    public boolean isCapturable(String speciesId) {
        return capturableSpecies. contains(speciesId);
    }

    // ===== 진화 관련 =====

    /**
     * 진화 가능한 대상 종족 목록
     */
    public List<String> getEvolutionTargets(String speciesId) {
        List<String> result = evolutionMap. get(speciesId);
        return result != null ? new ArrayList<>(result) : new ArrayList<>();
    }

    /**
     * 진화 가능 여부
     */
    public boolean canEvolve(String speciesId) {
        List<String> targets = evolutionMap.get(speciesId);
        return targets != null && !targets.isEmpty();
    }

    /**
     * 진화 전 종족 찾기
     */
    public List<String> getPreEvolutions(String speciesId) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : evolutionMap. entrySet()) {
            if (entry.getValue().contains(speciesId)) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    // ===== 검색 =====

    /**
     * 이름으로 종족 검색
     */
    public PetSpecies findByName(String name) {
        // 정확히 일치
        for (PetSpecies species : speciesMap.values()) {
            if (species.getName().equalsIgnoreCase(name)) {
                return species;
            }
        }

        // ID로 검색
        PetSpecies byId = speciesMap.get(name. toLowerCase());
        if (byId != null) return byId;

        // 부분 일치
        for (PetSpecies species : speciesMap.values()) {
            if (species. getName().toLowerCase().contains(name.toLowerCase())) {
                return species;
            }
        }

        return null;
    }

    /**
     * 조건으로 종족 필터링
     */
    public List<PetSpecies> filter(java.util.function. Predicate<PetSpecies> predicate) {
        return speciesMap.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * 랜덤 종족 가져오기
     */
    public PetSpecies getRandomSpecies() {
        if (speciesMap.isEmpty()) return null;
        List<String> ids = new ArrayList<>(speciesMap.keySet());
        String randomId = ids. get(new Random().nextInt(ids.size()));
        return speciesMap. get(randomId);
    }

    /**
     * 희귀도 기반 랜덤 종족
     */
    public PetSpecies getRandomSpeciesByRarity(PetRarity rarity) {
        List<String> ids = getSpeciesByRarity(rarity);
        if (ids. isEmpty()) return null;
        String randomId = ids.get(new Random().nextInt(ids.size()));
        return speciesMap.get(randomId);
    }

    /**
     * 가중치 기반 랜덤 종족
     */
    public PetSpecies getWeightedRandomSpecies(Map<PetRarity, Double> weights) {
        double totalWeight = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        double random = new Random().nextDouble() * totalWeight;

        double current = 0;
        for (Map. Entry<PetRarity, Double> entry : weights.entrySet()) {
            current += entry.getValue();
            if (random <= current) {
                return getRandomSpeciesByRarity(entry.getKey());
            }
        }

        return getRandomSpecies();
    }

    // ===== 정리 =====

    /**
     * 캐시 정리
     */
    public void clear() {
        speciesMap.clear();
        speciesByType.clear();
        speciesByRarity.clear();
        capturableSpecies.clear();
        evolutionMap. clear();
    }

    /**
     * 리로드
     */
    public void reload(Map<String, PetSpecies> species) {
        loadSpecies(species);
    }

    /**
     * 종료 처리
     */
    public void shutdown() {
        clear();
    }
}