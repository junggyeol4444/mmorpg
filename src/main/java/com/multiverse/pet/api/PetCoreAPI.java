package com.multiverse.pet. api;

import com.multiverse.pet.PetCore;
import com. multiverse.pet. entity.PetEntity;
import com. multiverse.pet. model.Pet;
import com. multiverse.pet. model.PetRarity;
import com. multiverse.pet. model.PetSpecies;
import com.multiverse.pet.model.PetType;
import org.bukkit.entity.Player;

import java.util. Collection;
import java. util.List;
import java.util. UUID;

/**
 * PetCore 공개 API
 * 외부 플러그인에서 펫 시스템과 상호작용하기 위한 API
 */
public class PetCoreAPI {

    private static PetCore plugin;

    /**
     * API 초기화 (내부용)
     */
    public static void init(PetCore petCore) {
        plugin = petCore;
    }

    /**
     * API 사용 가능 여부
     */
    public static boolean isAvailable() {
        return plugin != null && plugin.isEnabled();
    }

    /**
     * 플러그인 인스턴스 가져오기
     */
    public static PetCore getPlugin() {
        return plugin;
    }

    // ===== 펫 조회 =====

    /**
     * 플레이어의 모든 펫 가져오기
     */
    public static List<Pet> getPlayerPets(UUID playerId) {
        checkAvailable();
        return plugin.getPetManager().getAllPets(playerId);
    }

    /**
     * 플레이어의 모든 펫 가져오기
     */
    public static List<Pet> getPlayerPets(Player player) {
        return getPlayerPets(player.getUniqueId());
    }

    /**
     * 특정 펫 가져오기
     */
    public static Pet getPet(UUID playerId, UUID petId) {
        checkAvailable();
        return plugin. getPetManager().getPetById(playerId, petId);
    }

    /**
     * 펫 ID로 펫 가져오기
     */
    public static Pet getPetById(UUID petId) {
        checkAvailable();
        return plugin.getPetCache().getPetById(petId);
    }

    /**
     * 플레이어의 활성 펫 가져오기
     */
    public static Pet getActivePet(UUID playerId) {
        checkAvailable();
        PetEntity petEntity = plugin.getPetManager().getActivePet(playerId);
        return petEntity != null ? petEntity.getPet() : null;
    }

    /**
     * 플레이어의 활성 펫 가져오기
     */
    public static Pet getActivePet(Player player) {
        return getActivePet(player.getUniqueId());
    }

    /**
     * 플레이어의 모든 활성 펫 가져오기
     */
    public static List<Pet> getActivePets(UUID playerId) {
        checkAvailable();
        List<PetEntity> entities = plugin.getPetManager().getActivePets(playerId);
        return entities. stream()
                .map(PetEntity::getPet)
                .collect(java.util. stream.Collectors. toList());
    }

    /**
     * 펫 수 가져오기
     */
    public static int getPetCount(UUID playerId) {
        checkAvailable();
        return plugin.getPetManager().getAllPets(playerId).size();
    }

    /**
     * 활성 펫 여부 확인
     */
    public static boolean hasActivePet(UUID playerId) {
        checkAvailable();
        return plugin. getPetManager().hasActivePet(playerId);
    }

    // ===== 펫 소환/해제 =====

    /**
     * 펫 소환
     */
    public static boolean summonPet(Player player, UUID petId) {
        checkAvailable();
        return plugin.getPetManager().summonPet(player, petId);
    }

    /**
     * 펫 해제
     */
    public static boolean unsummonPet(Player player, UUID petId) {
        checkAvailable();
        return plugin.getPetManager().unsummonPet(player, petId);
    }

    /**
     * 모든 펫 해제
     */
    public static void unsummonAllPets(Player player) {
        checkAvailable();
        plugin.getPetManager().unsummonAllPets(player);
    }

    // ===== 펫 생성/제거 =====

    /**
     * 새 펫 생성 및 추가
     */
    public static Pet createPet(UUID ownerId, String speciesId, String name) {
        checkAvailable();

        PetSpecies species = plugin. getSpeciesCache().getSpecies(speciesId);
        if (species == null) {
            return null;
        }

        Pet pet = new Pet();
        pet.setOwnerId(ownerId);
        pet.setSpeciesId(speciesId);
        pet.setPetName(name != null ? name : species.getName());
        pet.setType(species.getType());
        pet.setRarity(species.getBaseRarity());
        pet.setLevel(1);
        pet.setEntityType(species.getEntityType());
        pet.setBaseStats(species.getAllStatsAtLevel(1));
        pet.setMaxHealth(pet.getTotalStat("health"));
        pet.setHealth(pet.getMaxHealth());
        pet.setHunger(100);
        pet.setHappiness(100);

        if (plugin.getPetManager().addNewPet(ownerId, pet)) {
            return pet;
        }

        return null;
    }

    /**
     * 새 펫 생성 (희귀도 지정)
     */
    public static Pet createPet(UUID ownerId, String speciesId, String name, PetRarity rarity) {
        Pet pet = createPet(ownerId, speciesId, name);
        if (pet != null && rarity != null) {
            pet. setRarity(rarity);
        }
        return pet;
    }

    /**
     * 펫 제거
     */
    public static boolean removePet(UUID ownerId, UUID petId) {
        checkAvailable();
        return plugin. getPetManager().removePet(ownerId, petId);
    }

    // ===== 펫 수정 =====

    /**
     * 펫 경험치 추가
     */
    public static void addExperience(Pet pet, int amount, Player owner) {
        checkAvailable();
        plugin.getPetLevelManager().addExperience(pet, amount, owner);
    }

    /**
     * 펫 레벨 설정
     */
    public static void setLevel(Pet pet, int level) {
        checkAvailable();
        plugin.getPetLevelManager().setLevel(pet, level);
    }

    /**
     * 펫 체력 회복
     */
    public static void healPet(Pet pet, double amount) {
        checkAvailable();
        pet.heal(amount);
        plugin.getPetManager().savePetData(pet. getOwnerId(), pet);
    }

    /**
     * 펫 배고픔 채우기
     */
    public static void feedPet(Pet pet, double amount) {
        checkAvailable();
        pet.increaseHunger(amount);
        plugin.getPetManager().savePetData(pet. getOwnerId(), pet);
    }

    /**
     * 펫 행복도 증가
     */
    public static void makeHappy(Pet pet, double amount) {
        checkAvailable();
        pet.increaseHappiness(amount);
        plugin.getPetManager().savePetData(pet.getOwnerId(), pet);
    }

    /**
     * 펫 이름 변경
     */
    public static boolean renamePet(Pet pet, String newName) {
        checkAvailable();
        if (pet.isNameLocked()) {
            return false;
        }
        pet. setPetName(newName);
        plugin.getPetManager().savePetData(pet.getOwnerId(), pet);
        return true;
    }

    // ===== 종족 정보 =====

    /**
     * 종족 정보 가져오기
     */
    public static PetSpecies getSpecies(String speciesId) {
        checkAvailable();
        return plugin.getSpeciesCache().getSpecies(speciesId);
    }

    /**
     * 모든 종족 ID 가져오기
     */
    public static Collection<String> getAllSpeciesIds() {
        checkAvailable();
        return plugin.getSpeciesCache().getAllSpeciesIds();
    }

    /**
     * 모든 종족 가져오기
     */
    public static Collection<PetSpecies> getAllSpecies() {
        checkAvailable();
        return plugin.getSpeciesCache().getAllSpecies();
    }

    /**
     * 타입별 종족 가져오기
     */
    public static List<PetSpecies> getSpeciesByType(PetType type) {
        checkAvailable();
        return plugin.getSpeciesCache().getSpeciesListByType(type);
    }

    /**
     * 희귀도별 종족 가져오기
     */
    public static List<PetSpecies> getSpeciesByRarity(PetRarity rarity) {
        checkAvailable();
        return plugin.getSpeciesCache().getSpeciesListByRarity(rarity);
    }

    // ===== 랭킹 =====

    /**
     * 플레이어 레이팅 가져오기
     */
    public static int getPlayerRating(UUID playerId) {
        checkAvailable();
        return plugin.getPetRankingManager().getPlayerRating(playerId);
    }

    /**
     * 플레이어 랭크 가져오기
     */
    public static int getPlayerRank(UUID playerId) {
        checkAvailable();
        return plugin.getPetRankingManager().getPlayerRank(playerId);
    }

    /**
     * 레이팅 추가
     */
    public static void addRating(UUID playerId, int amount) {
        checkAvailable();
        plugin.getPetRankingManager().addRating(playerId, amount);
    }

    // ===== 보관함 =====

    /**
     * 보관함 용량 가져오기
     */
    public static int getStorageCapacity(UUID playerId) {
        checkAvailable();
        return plugin.getPetStorageManager().getStorageCapacity(playerId);
    }

    /**
     * 보관함 가득 찼는지 확인
     */
    public static boolean isStorageFull(UUID playerId) {
        checkAvailable();
        return plugin. getPetStorageManager().isStorageFull(playerId);
    }

    // ===== 배틀 =====

    /**
     * 배틀 중인지 확인
     */
    public static boolean isInBattle(UUID playerId) {
        checkAvailable();
        return plugin. getPetBattleManager().isInBattle(playerId);
    }

    /**
     * 배틀 승리 횟수
     */
    public static int getBattleWins(UUID playerId) {
        checkAvailable();
        return plugin.getPetBattleManager().getPlayerWins(playerId);
    }

    /**
     * 배틀 패배 횟수
     */
    public static int getBattleLosses(UUID playerId) {
        checkAvailable();
        return plugin.getPetBattleManager().getPlayerLosses(playerId);
    }

    // ===== 교배 =====

    /**
     * 교배 진행 중인지 확인
     */
    public static boolean hasActiveBreeding(UUID playerId) {
        checkAvailable();
        return ! plugin.getBreedingManager().getPlayerBreedings(playerId).isEmpty();
    }

    /**
     * 교배 가능 여부 확인
     */
    public static boolean canBreed(Pet pet) {
        checkAvailable();
        return plugin.getBreedingManager().canBreed(pet);
    }

    // ===== 진화 =====

    /**
     * 진화 가능 여부 확인
     */
    public static boolean canEvolve(Pet pet) {
        checkAvailable();
        return plugin.getEvolutionManager().hasAvailableEvolution(pet);
    }

    // ===== 유틸리티 =====

    /**
     * API 사용 가능 확인
     */
    private static void checkAvailable() {
        if (! isAvailable()) {
            throw new IllegalStateException("PetCore API is not available");
        }
    }

    /**
     * 데이터 저장
     */
    public static void savePlayerData(UUID playerId) {
        checkAvailable();
        plugin.getPetDataManager().savePlayerData(playerId);
    }

    /**
     * 데이터 리로드
     */
    public static void reloadData() {
        checkAvailable();
        plugin.reload();
    }
}