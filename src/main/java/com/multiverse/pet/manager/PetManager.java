package com. multiverse.pet. manager;

import com.multiverse.pet.PetCore;
import com.multiverse.pet.api.event.PetSummonEvent;
import com.multiverse.pet.api.event.PetUnsummonEvent;
import com. multiverse.pet. data.PlayerPetData;
import com.multiverse.pet.entity.PetEntity;
import com.multiverse.pet.entity.PetEntityManager;
import com.multiverse. pet.model.*;
import com.multiverse.pet.util. MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit.Location;
import org.bukkit. entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java. util.concurrent.ConcurrentHashMap;

/**
 * 펫 매니저 클래스
 * 펫 소환, 해제, 관리 등 핵심 펫 시스템 관리
 */
public class PetManager {

    private final PetCore plugin;

    // 활성 펫 맵 (플레이어 UUID -> 펫 엔티티)
    private final Map<UUID, PetEntity> activePets;

    // 다중 펫 지원 (플레이어 UUID -> 펫 엔티티 목록)
    private final Map<UUID, List<PetEntity>> multipleActivePets;

    // 플레이어별 펫 데이터 캐시
    private final Map<UUID, PlayerPetData> playerPetDataCache;

    // 펫 소환 쿨다운
    private final Map<UUID, Long> summonCooldowns;

    // 펫 UUID -> 엔티티 UUID 매핑
    private final Map<UUID, UUID> petToEntityMap;

    // 설정 값
    private int maxActivePets;
    private double teleportDistance;
    private long summonCooldown;
    private boolean allowRename;
    private double renameCost;
    private boolean allowMultiplePets;

    /**
     * 생성자
     */
    public PetManager(PetCore plugin) {
        this.plugin = plugin;
        this. activePets = new ConcurrentHashMap<>();
        this.multipleActivePets = new ConcurrentHashMap<>();
        this.playerPetDataCache = new ConcurrentHashMap<>();
        this.summonCooldowns = new ConcurrentHashMap<>();
        this.petToEntityMap = new ConcurrentHashMap<>();
        loadSettings();
    }

    /**
     * 설정 로드
     */
    private void loadSettings() {
        this.maxActivePets = plugin. getPetSettings().getMaxActivePets();
        this.teleportDistance = plugin. getPetSettings().getTeleportDistance();
        this.summonCooldown = plugin.getPetSettings().getSummonCooldown();
        this.allowRename = plugin.getPetSettings().isAllowRename();
        this.renameCost = plugin.getPetSettings().getRenameCost();
        this.allowMultiplePets = maxActivePets > 1;
    }

    // ===== 펫 소환 =====

    /**
     * 펫 소환
     *
     * @param player 소환하는 플레이어
     * @param petId 소환할 펫 ID
     * @return 소환 성공 여부
     */
    public boolean summonPet(Player player, UUID petId) {
        UUID playerId = player.getUniqueId();

        // 쿨다운 체크
        if (isOnSummonCooldown(playerId)) {
            long remaining = getSummonCooldownRemaining(playerId);
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.cooldown")
                    .replace("{time}", String.valueOf(remaining / 1000)));
            return false;
        }

        // 펫 데이터 가져오기
        Pet pet = getPetById(playerId, petId);
        if (pet == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.not-found"));
            return false;
        }

        // 이미 소환된 펫인지 확인
        if (pet.isActive()) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.already-summoned"));
            return false;
        }

        // 펫 상태 확인
        if (! pet.getStatus().canBeSummoned()) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.cannot-summon")
                    .replace("{reason}", pet.getStatus().getDescription()));
            return false;
        }

        // 활성 펫 수 제한 확인
        int currentActivePets = getActivePetCount(playerId);
        if (currentActivePets >= maxActivePets) {
            if (maxActivePets == 1) {
                // 단일 펫만 허용 시 기존 펫 자동 해제
                unsummonAllPets(player);
            } else {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.max-active")
                        . replace("{max}", String.valueOf(maxActivePets)));
                return false;
            }
        }

        // 배고픔/행복도 체크
        if (pet.getHunger() <= 0) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.too-hungry"));
            return false;
        }

        if (pet.getHappiness() <= 0) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.too-sad"));
            return false;
        }

        // 소환 이벤트 발생
        PetSummonEvent summonEvent = new PetSummonEvent(player, pet);
        Bukkit.getPluginManager().callEvent(summonEvent);

        if (summonEvent. isCancelled()) {
            return false;
        }

        // 펫 엔티티 생성
        Location spawnLocation = getSpawnLocation(player);
        PetEntity petEntity = plugin.getEntityManager().spawnPet(pet, spawnLocation, player);

        if (petEntity == null) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.spawn-failed"));
            return false;
        }

        // 펫 상태 변경
        pet.setStatus(PetStatus.ACTIVE);

        // 활성 펫 등록
        registerActivePet(playerId, petEntity);

        // 쿨다운 설정
        setSummonCooldown(playerId);

        // 데이터 저장
        savePetData(playerId, pet);

        // 메시지 전송
        MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("pet.summoned")
                .replace("{name}", pet.getPetName()));

        return true;
    }

    /**
     * 소환 위치 계산
     */
    private Location getSpawnLocation(Player player) {
        Location playerLoc = player.getLocation();
        double angle = Math.random() * 2 * Math.PI;
        double distance = 2.0;

        double x = playerLoc.getX() + Math.cos(angle) * distance;
        double z = playerLoc.getZ() + Math.sin(angle) * distance;
        double y = playerLoc.getY();

        Location spawnLoc = new Location(playerLoc.getWorld(), x, y, z);

        // 안전한 위치 찾기
        while (! spawnLoc. getBlock().isPassable() && spawnLoc.getY() < playerLoc.getY() + 10) {
            spawnLoc.add(0, 1, 0);
        }

        return spawnLoc;
    }

    // ===== 펫 해제 =====

    /**
     * 펫 해제
     *
     * @param player 플레이어
     * @param petId 해제할 펫 ID
     * @return 해제 성공 여부
     */
    public boolean unsummonPet(Player player, UUID petId) {
        UUID playerId = player. getUniqueId();

        // 활성 펫 찾기
        PetEntity petEntity = getActivePetEntity(playerId, petId);
        if (petEntity == null) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.not-active"));
            return false;
        }

        return unsummonPetEntity(player, petEntity);
    }

    /**
     * 펫 엔티티 해제
     */
    public boolean unsummonPetEntity(Player player, PetEntity petEntity) {
        UUID playerId = player. getUniqueId();
        Pet pet = petEntity. getPet();

        // 대결 중인지 확인
        if (pet.getStatus() == PetStatus.BATTLING) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.in-battle"));
            return false;
        }

        // 교배 중인지 확인
        if (pet.getStatus() == PetStatus.BREEDING) {
            MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("pet.in-breeding"));
            return false;
        }

        // 해제 이벤트 발생
        PetUnsummonEvent unsummonEvent = new PetUnsummonEvent(player, pet);
        Bukkit.getPluginManager().callEvent(unsummonEvent);

        if (unsummonEvent.isCancelled()) {
            return false;
        }

        // 엔티티 제거
        plugin.getEntityManager().despawnPet(petEntity);

        // 펫 상태 변경
        pet.setStatus(PetStatus.STORED);

        // 활성 펫에서 제거
        unregisterActivePet(playerId, petEntity);

        // 데이터 저장
        savePetData(playerId, pet);

        // 메시지 전송
        MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.unsummoned")
                .replace("{name}", pet.getPetName()));

        return true;
    }

    /**
     * 플레이어의 모든 펫 해제
     */
    public void unsummonAllPets(Player player) {
        UUID playerId = player.getUniqueId();

        // 단일 펫
        PetEntity activePet = activePets.get(playerId);
        if (activePet != null) {
            unsummonPetEntity(player, activePet);
        }

        // 다중 펫
        List<PetEntity> pets = multipleActivePets.get(playerId);
        if (pets != null && ! pets.isEmpty()) {
            for (PetEntity petEntity : new ArrayList<>(pets)) {
                unsummonPetEntity(player, petEntity);
            }
        }
    }

    /**
     * 모든 플레이어의 모든 펫 해제 (서버 종료 시)
     */
    public void unsummonAllPets() {
        for (UUID playerId : new HashSet<>(activePets.keySet())) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                unsummonAllPets(player);
            } else {
                // 오프라인 플레이어 처리
                PetEntity petEntity = activePets.remove(playerId);
                if (petEntity != null) {
                    plugin.getEntityManager().despawnPet(petEntity);
                    petEntity.getPet().setStatus(PetStatus. STORED);
                }
            }
        }

        for (UUID playerId :  new HashSet<>(multipleActivePets.keySet())) {
            Player player = Bukkit. getPlayer(playerId);
            List<PetEntity> pets = multipleActivePets.remove(playerId);
            if (pets != null) {
                for (PetEntity petEntity : pets) {
                    plugin.getEntityManager().despawnPet(petEntity);
                    petEntity.getPet().setStatus(PetStatus.STORED);
                }
            }
        }

        activePets.clear();
        multipleActivePets.clear();
        petToEntityMap.clear();
    }

    // ===== 활성 펫 관리 =====

    /**
     * 활성 펫 등록
     */
    private void registerActivePet(UUID playerId, PetEntity petEntity) {
        if (allowMultiplePets) {
            multipleActivePets. computeIfAbsent(playerId, k -> new ArrayList<>()).add(petEntity);
        } else {
            activePets.put(playerId, petEntity);
        }

        petToEntityMap.put(petEntity. getPet().getPetId(), petEntity. getEntityUUID());
    }

    /**
     * 활성 펫 등록 해제
     */
    private void unregisterActivePet(UUID playerId, PetEntity petEntity) {
        if (allowMultiplePets) {
            List<PetEntity> pets = multipleActivePets.get(playerId);
            if (pets != null) {
                pets.remove(petEntity);
                if (pets.isEmpty()) {
                    multipleActivePets.remove(playerId);
                }
            }
        } else {
            activePets.remove(playerId);
        }

        petToEntityMap.remove(petEntity.getPet().getPetId());
    }

    /**
     * 플레이어의 활성 펫 존재 여부
     */
    public boolean hasActivePet(UUID playerId) {
        if (allowMultiplePets) {
            List<PetEntity> pets = multipleActivePets.get(playerId);
            return pets != null && !pets.isEmpty();
        }
        return activePets.containsKey(playerId);
    }

    /**
     * 플레이어의 활성 펫 수
     */
    public int getActivePetCount(UUID playerId) {
        if (allowMultiplePets) {
            List<PetEntity> pets = multipleActivePets. get(playerId);
            return pets != null ? pets.size() : 0;
        }
        return activePets. containsKey(playerId) ? 1 : 0;
    }

    /**
     * 플레이어의 활성 펫 엔티티 가져오기 (단일)
     */
    public PetEntity getActivePet(UUID playerId) {
        if (allowMultiplePets) {
            List<PetEntity> pets = multipleActivePets.get(playerId);
            return pets != null && !pets.isEmpty() ? pets.get(0) : null;
        }
        return activePets.get(playerId);
    }

    /**
     * 플레이어의 모든 활성 펫 엔티티 가져오기
     */
    public List<PetEntity> getActivePets(UUID playerId) {
        if (allowMultiplePets) {
            List<PetEntity> pets = multipleActivePets.get(playerId);
            return pets != null ?  new ArrayList<>(pets) : new ArrayList<>();
        }
        PetEntity pet = activePets.get(playerId);
        return pet != null ?  Collections.singletonList(pet) : new ArrayList<>();
    }

    /**
     * 특정 펫 ID로 활성 펫 엔티티 가져오기
     */
    public PetEntity getActivePetEntity(UUID playerId, UUID petId) {
        if (allowMultiplePets) {
            List<PetEntity> pets = multipleActivePets.get(playerId);
            if (pets != null) {
                for (PetEntity petEntity : pets) {
                    if (petEntity.getPet().getPetId().equals(petId)) {
                        return petEntity;
                    }
                }
            }
        } else {
            PetEntity petEntity = activePets.get(playerId);
            if (petEntity != null && petEntity.getPet().getPetId().equals(petId)) {
                return petEntity;
            }
        }
        return null;
    }

    /**
     * 엔티티 UUID로 펫 엔티티 가져오기
     */
    public PetEntity getPetEntityByEntityUUID(UUID entityUUID) {
        for (PetEntity petEntity : activePets.values()) {
            if (petEntity.getEntityUUID().equals(entityUUID)) {
                return petEntity;
            }
        }

        for (List<PetEntity> pets : multipleActivePets.values()) {
            for (PetEntity petEntity : pets) {
                if (petEntity.getEntityUUID().equals(entityUUID)) {
                    return petEntity;
                }
            }
        }

        return null;
    }

    /**
     * 버킷 엔티티로 펫 엔티티 가져오기
     */
    public PetEntity getPetEntityByEntity(Entity entity) {
        return getPetEntityByEntityUUID(entity.getUniqueId());
    }

    /**
     * 펫인지 확인
     */
    public boolean isPetEntity(Entity entity) {
        return getPetEntityByEntity(entity) != null;
    }

    // ===== 쿨다운 관리 =====

    /**
     * 소환 쿨다운 설정
     */
    private void setSummonCooldown(UUID playerId) {
        if (summonCooldown > 0) {
            summonCooldowns.put(playerId, System.currentTimeMillis() + summonCooldown);
        }
    }

    /**
     * 소환 쿨다운 중인지 확인
     */
    public boolean isOnSummonCooldown(UUID playerId) {
        Long cooldownEnd = summonCooldowns.get(playerId);
        if (cooldownEnd == null) return false;
        if (System.currentTimeMillis() >= cooldownEnd) {
            summonCooldowns. remove(playerId);
            return false;
        }
        return true;
    }

    /**
     * 소환 쿨다운 남은 시간
     */
    public long getSummonCooldownRemaining(UUID playerId) {
        Long cooldownEnd = summonCooldowns.get(playerId);
        if (cooldownEnd == null) return 0;
        return Math.max(0, cooldownEnd - System.currentTimeMillis());
    }

    // ===== 펫 데이터 관리 =====

    /**
     * 플레이어 펫 데이터 가져오기
     */
    public PlayerPetData getPlayerPetData(UUID playerId) {
        return playerPetDataCache.computeIfAbsent(playerId, k -> 
            plugin.getDataManager().loadPlayerData(playerId));
    }

    /**
     * 펫 ID로 펫 가져오기
     */
    public Pet getPetById(UUID playerId, UUID petId) {
        PlayerPetData data = getPlayerPetData(playerId);
        return data != null ? data.getPetById(petId) : null;
    }

    /**
     * 플레이어의 모든 펫 가져오기
     */
    public List<Pet> getAllPets(UUID playerId) {
        PlayerPetData data = getPlayerPetData(playerId);
        return data != null ? data.getAllPets() : new ArrayList<>();
    }

    /**
     * 펫 데이터 저장
     */
    public void savePetData(UUID playerId, Pet pet) {
        PlayerPetData data = getPlayerPetData(playerId);
        if (data != null) {
            data. updatePet(pet);
            plugin.getDataManager().savePlayerData(playerId, data);
        }
    }

    /**
     * 플레이어 데이터 캐시 제거
     */
    public void unloadPlayerData(UUID playerId) {
        playerPetDataCache. remove(playerId);
    }

    /**
     * 플레이어 데이터 저장 및 언로드
     */
    public void saveAndUnloadPlayerData(UUID playerId) {
        PlayerPetData data = playerPetDataCache. remove(playerId);
        if (data != null) {
            plugin.getDataManager().savePlayerData(playerId, data);
        }
    }

    // ===== 펫 이름 변경 =====

    /**
     * 펫 이름 변경
     *
     * @param player 플레이어
     * @param petId 펫 ID
     * @param newName 새 이름
     * @return 변경 성공 여부
     */
    public boolean renamePet(Player player, UUID petId, String newName) {
        if (! allowRename) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.rename-disabled"));
            return false;
        }

        UUID playerId = player. getUniqueId();
        Pet pet = getPetById(playerId, petId);

        if (pet == null) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.not-found"));
            return false;
        }

        // 이름 길이 제한
        if (newName.length() > 20) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.name-too-long"));
            return false;
        }

        // 금지 문자 체크
        if (! isValidPetName(newName)) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.invalid-name"));
            return false;
        }

        // 비용 확인 및 차감
        if (renameCost > 0) {
            if (! plugin.getPlayerDataCoreHook().hasGold(playerId, renameCost)) {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.not-enough-gold")
                        .replace("{cost}", String.valueOf(renameCost)));
                return false;
            }
            plugin.getPlayerDataCoreHook().withdrawGold(playerId, renameCost);
        }

        String oldName = pet. getPetName();
        pet.setPetName(newName);

        // 활성 펫인 경우 엔티티 이름도 변경
        PetEntity petEntity = getActivePetEntity(playerId, petId);
        if (petEntity != null) {
            petEntity.updateDisplayName();
        }

        // 저장
        savePetData(playerId, pet);

        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.renamed")
                .replace("{old}", oldName)
                .replace("{new}", newName));

        return true;
    }

    /**
     * 유효한 펫 이름인지 확인
     */
    private boolean isValidPetName(String name) {
        if (name == null || name.isEmpty()) return false;
        // 허용:  한글, 영문, 숫자, 공백, 일부 특수문자
        return name.matches("^[가-힣a-zA-Z0-9 _\\-]+$");
    }

    // ===== 펫 행동 변경 =====

    /**
     * 펫 행동 변경
     *
     * @param player 플레이어
     * @param petId 펫 ID
     * @param behavior 새 행동
     * @return 변경 성공 여부
     */
    public boolean setPetBehavior(Player player, UUID petId, PetBehavior behavior) {
        UUID playerId = player. getUniqueId();
        Pet pet = getPetById(playerId, petId);

        if (pet == null) {
            MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("pet.not-found"));
            return false;
        }

        // 활성 펫인지 확인
        if (!pet.isActive()) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.not-active"));
            return false;
        }

        // 행동 호환성 확인
        if (!behavior.isSuitableFor(pet. getType())) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.behavior-incompatible")
                    .replace("{behavior}", behavior.getDisplayName())
                    .replace("{type}", pet.getType().getDisplayName()));
            return false;
        }

        pet.setBehavior(behavior);

        // 펫 엔티티에 적용
        PetEntity petEntity = getActivePetEntity(playerId, petId);
        if (petEntity != null) {
            petEntity.updateBehavior(behavior);
        }

        // 저장
        savePetData(playerId, pet);

        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.behavior-changed")
                .replace("{name}", pet.getPetName())
                .replace("{behavior}", behavior.getDisplayName()));

        return true;
    }

    /**
     * 펫 행동 순환 변경
     */
    public boolean cyclePetBehavior(Player player, UUID petId) {
        UUID playerId = player.getUniqueId();
        Pet pet = getPetById(playerId, petId);

        if (pet == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.not-found"));
            return false;
        }

        PetBehavior currentBehavior = pet.getBehavior();
        PetBehavior nextBehavior = currentBehavior.getNextBehavior();

        // 적합한 행동 찾기
        int attempts = 0;
        while (! nextBehavior. isSuitableFor(pet.getType()) && attempts < PetBehavior. values().length) {
            nextBehavior = nextBehavior.getNextBehavior();
            attempts++;
        }

        return setPetBehavior(player, petId, nextBehavior);
    }

    // ===== 펫 텔레포트 =====

    /**
     * 펫을 플레이어에게 텔레포트
     */
    public void teleportPetToPlayer(Player player) {
        UUID playerId = player.getUniqueId();
        List<PetEntity> pets = getActivePets(playerId);

        for (PetEntity petEntity : pets) {
            Location targetLoc = getSpawnLocation(player);
            petEntity.teleport(targetLoc);
        }
    }

    /**
     * 거리가 너무 멀면 자동 텔레포트
     */
    public void checkAndTeleportDistantPets(Player player) {
        UUID playerId = player.getUniqueId();
        List<PetEntity> pets = getActivePets(playerId);
        Location playerLoc = player. getLocation();

        for (PetEntity petEntity : pets) {
            Entity entity = petEntity. getEntity();
            if (entity == null || ! entity.isValid()) continue;

            double distance = entity.getLocation().distance(playerLoc);
            if (distance > teleportDistance) {
                Location targetLoc = getSpawnLocation(player);
                petEntity.teleport(targetLoc);
            }
        }
    }

    // ===== 펫 새로 추가 =====

    /**
     * 새 펫 추가
     *
     * @param playerId 플레이어 ID
     * @param pet 추가할 펫
     * @return 추가 성공 여부
     */
    public boolean addNewPet(UUID playerId, Pet pet) {
        PlayerPetData data = getPlayerPetData(playerId);
        if (data == null) {
            data = new PlayerPetData(playerId);
            playerPetDataCache. put(playerId, data);
        }

        // 보관함 용량 확인
        int maxStorage = plugin.getStorageSettings().getMaxStorageSlots();
        if (data.getPetCount() >= maxStorage) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.storage-full"));
            }
            return false;
        }

        pet.setOwnerId(playerId);
        pet.setStatus(PetStatus. STORED);
        data.addPet(pet);

        // 저장
        plugin.getDataManager().savePlayerData(playerId, data);

        // 캐시에 추가
        plugin.getPetCache().cachePet(pet);

        return true;
    }

    /**
     * 펫 삭제
     *
     * @param playerId 플레이어 ID
     * @param petId 삭제할 펫 ID
     * @return 삭제 성공 여부
     */
    public boolean removePet(UUID playerId, UUID petId) {
        PlayerPetData data = getPlayerPetData(playerId);
        if (data == null) return false;

        Pet pet = data.getPetById(petId);
        if (pet == null) return false;

        // 활성 펫이면 먼저 해제
        if (pet.isActive()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                unsummonPet(player, petId);
            }
        }

        data.removePet(petId);

        // 저장
        plugin. getDataManager().savePlayerData(playerId, data);

        // 캐시에서 제거
        plugin.getPetCache().removePet(petId);

        return true;
    }

    // ===== 설정 리로드 =====

    /**
     * 설정 리로드
     */
    public void reload() {
        loadSettings();
    }

    // ===== Getter =====

    public int getMaxActivePets() {
        return maxActivePets;
    }

    public double getTeleportDistance() {
        return teleportDistance;
    }

    public long getSummonCooldown() {
        return summonCooldown;
    }

    public boolean isAllowRename() {
        return allowRename;
    }

    public double getRenameCost() {
        return renameCost;
    }

    public boolean isAllowMultiplePets() {
        return allowMultiplePets;
    }

    public Map<UUID, PetEntity> getActivePetsMap() {
        return Collections.unmodifiableMap(activePets);
    }

    public Map<UUID, List<PetEntity>> getMultipleActivePetsMap() {
        return Collections.unmodifiableMap(multipleActivePets);
    }
}