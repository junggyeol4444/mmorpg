package com.multiverse.pet. entity;

import com.multiverse.pet.PetCore;
import com. multiverse.pet. model.Pet;
import com. multiverse.pet. model.PetStatus;
import org.bukkit. Bukkit;
import org.bukkit. Location;
import org. bukkit. Particle;
import org. bukkit.entity. Entity;
import org. bukkit.entity. LivingEntity;
import org.bukkit.entity.Player;
import org. bukkit.scheduler.BukkitTask;

import java. util.*;
import java.util.concurrent. ConcurrentHashMap;

/**
 * 펫 엔티티 매니저 클래스
 * 모든 펫 엔티티의 생성, 관리, 제거를 담당
 */
public class PetEntityManager {

    private final PetCore plugin;

    // 펫 엔티티 맵 (펫ID -> 펫 엔티티)
    private final Map<UUID, PetEntity> petEntities;

    // 엔티티 UUID -> 펫ID 매핑
    private final Map<UUID, UUID> entityToPetMap;

    // 플레이어별 활성 펫 엔티티
    private final Map<UUID, List<PetEntity>> playerPetEntities;

    // 엔티티 관리 태스크
    private BukkitTask managementTask;

    // 설정
    private int maxActiveEntities;
    private double entityCleanupDistance;
    private int entityTickRate;

    /**
     * 생성자
     */
    public PetEntityManager(PetCore plugin) {
        this.plugin = plugin;
        this.petEntities = new ConcurrentHashMap<>();
        this.entityToPetMap = new ConcurrentHashMap<>();
        this.playerPetEntities = new ConcurrentHashMap<>();
        loadSettings();
        startManagementTask();
    }

    /**
     * 설정 로드
     */
    private void loadSettings() {
        this.maxActiveEntities = plugin.getConfigManager().getPetSettings().getMaxActiveEntities();
        this.entityCleanupDistance = plugin.getConfigManager().getPetSettings().getEntityCleanupDistance();
        this.entityTickRate = plugin.getConfigManager().getPetSettings().getEntityTickRate();
    }

    /**
     * 관리 태스크 시작
     */
    private void startManagementTask() {
        managementTask = Bukkit.getScheduler().runTaskTimer(plugin, this:: managementTick, 
                entityTickRate, entityTickRate);
    }

    /**
     * 관리 태스크 중지
     */
    public void stopManagementTask() {
        if (managementTask != null && !managementTask. isCancelled()) {
            managementTask.cancel();
        }
    }

    // ===== 펫 스폰 =====

    /**
     * 펫 스폰
     *
     * @param pet 스폰할 펫
     * @param location 스폰 위치
     * @param owner 소유자
     * @return 생성된 펫 엔티티 또는 null
     */
    public PetEntity spawnPet(Pet pet, Location location, Player owner) {
        UUID playerId = owner.getUniqueId();
        UUID petId = pet.getPetId();

        // 이미 스폰되어 있는지 확인
        if (petEntities. containsKey(petId)) {
            PetEntity existing = petEntities.get(petId);
            if (existing.isValid()) {
                return existing;
            } else {
                // 유효하지 않은 엔티티 정리
                despawnPet(existing);
            }
        }

        // 최대 활성 엔티티 수 확인
        List<PetEntity> playerPets = playerPetEntities.getOrDefault(playerId, new ArrayList<>());
        if (playerPets. size() >= maxActiveEntities) {
            plugin.getMessageUtil().sendMessage(owner, 
                plugin.getConfigManager().getMessage("pet.max-active-entities")
                    .replace("{max}", String.valueOf(maxActiveEntities)));
            return null;
        }

        // 펫 엔티티 생성
        PetEntity petEntity = new PetEntity(plugin, pet, playerId);

        // 스폰
        if (! petEntity.spawn(location)) {
            return null;
        }

        // 등록
        petEntities.put(petId, petEntity);
        entityToPetMap.put(petEntity.getEntityUUID(), petId);
        playerPetEntities.computeIfAbsent(playerId, k -> new ArrayList<>()).add(petEntity);

        // 펫 상태 변경
        pet. setStatus(PetStatus.ACTIVE);

        return petEntity;
    }

    /**
     * 펫 디스폰
     */
    public void despawnPet(PetEntity petEntity) {
        if (petEntity == null) return;

        UUID petId = petEntity.getPet().getPetId();
        UUID playerId = petEntity.getOwnerPlayerId();
        UUID entityUUID = petEntity.getEntityUUID();

        // 디스폰
        petEntity.despawn();

        // 등록 해제
        petEntities.remove(petId);
        if (entityUUID != null) {
            entityToPetMap.remove(entityUUID);
        }

        List<PetEntity> playerPets = playerPetEntities. get(playerId);
        if (playerPets != null) {
            playerPets.remove(petEntity);
            if (playerPets.isEmpty()) {
                playerPetEntities.remove(playerId);
            }
        }

        // 펫 상태 변경
        petEntity.getPet().setStatus(PetStatus. STORED);
    }

    /**
     * 펫 ID로 디스폰
     */
    public void despawnPet(UUID petId) {
        PetEntity petEntity = petEntities.get(petId);
        if (petEntity != null) {
            despawnPet(petEntity);
        }
    }

    /**
     * 플레이어의 모든 펫 디스폰
     */
    public void despawnAllPets(UUID playerId) {
        List<PetEntity> playerPets = playerPetEntities.get(playerId);
        if (playerPets != null) {
            for (PetEntity petEntity : new ArrayList<>(playerPets)) {
                despawnPet(petEntity);
            }
        }
    }

    /**
     * 모든 펫 디스폰
     */
    public void despawnAllPets() {
        for (PetEntity petEntity : new ArrayList<>(petEntities.values())) {
            despawnPet(petEntity);
        }
    }

    // ===== 펫 조회 =====

    /**
     * 펫 ID로 펫 엔티티 가져오기
     */
    public PetEntity getPetEntity(UUID petId) {
        return petEntities.get(petId);
    }

    /**
     * 버킷 엔티티 UUID로 펫 엔티티 가져오기
     */
    public PetEntity getPetEntityByEntityUUID(UUID entityUUID) {
        UUID petId = entityToPetMap.get(entityUUID);
        return petId != null ? petEntities.get(petId) : null;
    }

    /**
     * 버킷 엔티티로 펫 엔티티 가져오기
     */
    public PetEntity getPetEntityByEntity(Entity entity) {
        return getPetEntityByEntityUUID(entity. getUniqueId());
    }

    /**
     * 플레이어의 활성 펫 엔티티 목록
     */
    public List<PetEntity> getPlayerPetEntities(UUID playerId) {
        return new ArrayList<>(playerPetEntities.getOrDefault(playerId, new ArrayList<>()));
    }

    /**
     * 플레이어의 첫 번째 활성 펫 엔티티
     */
    public PetEntity getPlayerFirstPetEntity(UUID playerId) {
        List<PetEntity> pets = playerPetEntities. get(playerId);
        return (pets != null && ! pets.isEmpty()) ? pets.get(0) : null;
    }

    /**
     * 펫 엔티티인지 확인
     */
    public boolean isPetEntity(Entity entity) {
        if (entity == null) return false;
        
        // 메타데이터 확인
        if (entity. hasMetadata(PetEntity. METADATA_KEY)) {
            return true;
        }
        
        // UUID 매핑 확인
        return entityToPetMap. containsKey(entity.getUniqueId());
    }

    /**
     * 활성 펫 수
     */
    public int getActivePetCount() {
        return petEntities.size();
    }

    /**
     * 플레이어의 활성 펫 수
     */
    public int getPlayerActivePetCount(UUID playerId) {
        List<PetEntity> pets = playerPetEntities.get(playerId);
        return pets != null ? pets. size() : 0;
    }

    // ===== 관리 =====

    /**
     * 관리 틱 (주기적 호출)
     */
    private void managementTick() {
        // 유효하지 않은 엔티티 정리
        cleanupInvalidEntities();

        // 거리 기반 정리
        cleanupDistantEntities();

        // 오프라인 플레이어 펫 정리
        cleanupOfflinePlayerPets();
    }

    /**
     * 유효하지 않은 엔티티 정리
     */
    private void cleanupInvalidEntities() {
        Iterator<Map.Entry<UUID, PetEntity>> iterator = petEntities. entrySet().iterator();
        
        while (iterator. hasNext()) {
            Map.Entry<UUID, PetEntity> entry = iterator. next();
            PetEntity petEntity = entry.getValue();
            
            if (! petEntity.isValid()) {
                // 엔티티가 제거됨 - 재스폰 시도
                Player owner = petEntity.getOwner();
                if (owner != null && owner.isOnline()) {
                    // 재스폰
                    Location spawnLoc = getSpawnLocationNearPlayer(owner);
                    if (! petEntity.spawn(spawnLoc)) {
                        // 재스폰 실패 - 정리
                        iterator.remove();
                        cleanupPetEntityReferences(petEntity);
                    }
                } else {
                    // 오프라인 - 정리
                    iterator.remove();
                    cleanupPetEntityReferences(petEntity);
                }
            }
        }
    }

    /**
     * 거리 기반 엔티티 정리
     */
    private void cleanupDistantEntities() {
        for (PetEntity petEntity :  new ArrayList<>(petEntities.values())) {
            if (! petEntity.isValid()) continue;
            
            Player owner = petEntity. getOwner();
            if (owner == null) continue;
            
            LivingEntity entity = petEntity.getEntity();
            if (entity == null) continue;
            
            double distance = entity.getLocation().distance(owner.getLocation());
            
            if (distance > entityCleanupDistance) {
                // 텔레포트
                petEntity.teleportToOwner();
            }
        }
    }

    /**
     * 오프라인 플레이어 펫 정리
     */
    private void cleanupOfflinePlayerPets() {
        Iterator<Map.Entry<UUID, List<PetEntity>>> iterator = playerPetEntities.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map. Entry<UUID, List<PetEntity>> entry = iterator.next();
            UUID playerId = entry.getKey();
            Player player = Bukkit.getPlayer(playerId);
            
            if (player == null || ! player.isOnline()) {
                // 오프라인 - 모든 펫 디스폰
                for (PetEntity petEntity : new ArrayList<>(entry. getValue())) {
                    despawnPet(petEntity);
                }
            }
        }
    }

    /**
     * 펫 엔티티 참조 정리
     */
    private void cleanupPetEntityReferences(PetEntity petEntity) {
        UUID petId = petEntity.getPet().getPetId();
        UUID playerId = petEntity. getOwnerPlayerId();
        UUID entityUUID = petEntity.getEntityUUID();
        
        entityToPetMap. remove(entityUUID);
        
        List<PetEntity> playerPets = playerPetEntities.get(playerId);
        if (playerPets != null) {
            playerPets.remove(petEntity);
            if (playerPets.isEmpty()) {
                playerPetEntities.remove(playerId);
            }
        }
        
        petEntity.getPet().setStatus(PetStatus.STORED);
    }

    /**
     * 플레이어 근처 스폰 위치 계산
     */
    private Location getSpawnLocationNearPlayer(Player player) {
        Location playerLoc = player.getLocation();
        double angle = Math.random() * 2 * Math.PI;
        double distance = 2.0;
        
        double x = playerLoc. getX() + Math.cos(angle) * distance;
        double z = playerLoc. getZ() + Math.sin(angle) * distance;
        double y = playerLoc.getY();
        
        Location spawnLoc = new Location(playerLoc.getWorld(), x, y, z);
        
        while (! spawnLoc. getBlock().isPassable() && spawnLoc.getY() < playerLoc.getY() + 10) {
            spawnLoc.add(0, 1, 0);
        }
        
        return spawnLoc;
    }

    // ===== 이펙트 =====

    /**
     * 레벨업 이펙트 재생
     */
    public void playLevelUpEffect(UUID petId) {
        PetEntity petEntity = petEntities.get(petId);
        if (petEntity != null && petEntity.isValid()) {
            petEntity.playLevelUpEffect();
        }
    }

    /**
     * 행복 이펙트 재생
     */
    public void playHappyEffect(UUID petId) {
        PetEntity petEntity = petEntities.get(petId);
        if (petEntity != null && petEntity.isValid()) {
            petEntity.playHappyEffect();
        }
    }

    /**
     * 스킬 이펙트 재생
     */
    public void playSkillEffect(UUID petId, Particle particle, int count) {
        PetEntity petEntity = petEntities.get(petId);
        if (petEntity != null && petEntity.isValid()) {
            petEntity. playSkillEffect(particle, count);
        }
    }

    /**
     * 펫 이름 업데이트
     */
    public void updatePetName(UUID petId) {
        PetEntity petEntity = petEntities.get(petId);
        if (petEntity != null && petEntity. isValid()) {
            petEntity.updateDisplayName();
        }
    }

    // ===== 텔레포트 =====

    /**
     * 펫을 주인에게 텔레포트
     */
    public void teleportPetToOwner(UUID petId) {
        PetEntity petEntity = petEntities.get(petId);
        if (petEntity != null && petEntity.isValid()) {
            petEntity. teleportToOwner();
        }
    }

    /**
     * 플레이어의 모든 펫을 텔레포트
     */
    public void teleportAllPetsToOwner(UUID playerId) {
        List<PetEntity> pets = playerPetEntities. get(playerId);
        if (pets != null) {
            for (PetEntity petEntity : pets) {
                if (petEntity.isValid()) {
                    petEntity.teleportToOwner();
                }
            }
        }
    }

    /**
     * 펫을 특정 위치로 텔레포트
     */
    public void teleportPet(UUID petId, Location location) {
        PetEntity petEntity = petEntities.get(petId);
        if (petEntity != null && petEntity.isValid()) {
            petEntity.teleport(location);
        }
    }

    // ===== 행동 제어 =====

    /**
     * 펫 앉기/서기 토글
     */
    public void togglePetSit(UUID petId) {
        PetEntity petEntity = petEntities.get(petId);
        if (petEntity != null && petEntity. isValid()) {
            petEntity.toggleSit();
        }
    }

    /**
     * 펫 따라오기 토글
     */
    public void togglePetFollow(UUID petId) {
        PetEntity petEntity = petEntities.get(petId);
        if (petEntity != null && petEntity. isValid()) {
            petEntity.toggleFollow();
        }
    }

    /**
     * 펫 공격 대상 설정
     */
    public void setPetAttackTarget(UUID petId, LivingEntity target) {
        PetEntity petEntity = petEntities.get(petId);
        if (petEntity != null && petEntity.isValid()) {
            petEntity. setAttackTarget(target);
        }
    }

    /**
     * 펫 공격 대상 해제
     */
    public void clearPetAttackTarget(UUID petId) {
        PetEntity petEntity = petEntities.get(petId);
        if (petEntity != null && petEntity. isValid()) {
            petEntity.clearAttackTarget();
        }
    }

    /**
     * 펫 이동 목표 설정
     */
    public void setPetMoveTarget(UUID petId, Location location) {
        PetEntity petEntity = petEntities.get(petId);
        if (petEntity != null && petEntity.isValid()) {
            petEntity. setMoveTarget(location);
        }
    }

    // ===== 데미지 처리 =====

    /**
     * 펫 데미지 처리
     */
    public void handlePetDamage(UUID petId, double damage, Entity damager) {
        PetEntity petEntity = petEntities.get(petId);
        if (petEntity != null && petEntity.isValid()) {
            petEntity. takeDamage(damage, damager);
            
            // 기절 시 디스폰
            if (petEntity. getPet().getHealth() <= 0) {
                despawnPet(petEntity);
            }
        }
    }

    /**
     * 펫 회복
     */
    public void healPet(UUID petId, double amount) {
        PetEntity petEntity = petEntities.get(petId);
        if (petEntity != null && petEntity.isValid()) {
            petEntity. heal(amount);
        }
    }

    // ===== 월드 이벤트 =====

    /**
     * 월드 변경 시 처리
     */
    public void handleWorldChange(Player player) {
        UUID playerId = player. getUniqueId();
        List<PetEntity> pets = playerPetEntities.get(playerId);
        
        if (pets != null) {
            for (PetEntity petEntity : new ArrayList<>(pets)) {
                // 같은 월드로 텔레포트
                petEntity.teleportToOwner();
            }
        }
    }

    /**
     * 청크 언로드 시 처리
     */
    public void handleChunkUnload(org.bukkit. Chunk chunk) {
        for (PetEntity petEntity : new ArrayList<>(petEntities.values())) {
            if (! petEntity.isValid()) continue;
            
            LivingEntity entity = petEntity.getEntity();
            if (entity == null) continue;
            
            if (entity. getLocation().getChunk().equals(chunk)) {
                // 주인에게 텔레포트
                Player owner = petEntity.getOwner();
                if (owner != null && owner.isOnline()) {
                    petEntity.teleportToOwner();
                } else {
                    despawnPet(petEntity);
                }
            }
        }
    }

    // ===== 통계 =====

    /**
     * 모든 활성 펫 엔티티 목록
     */
    public Collection<PetEntity> getAllPetEntities() {
        return Collections.unmodifiableCollection(petEntities. values());
    }

    /**
     * 활성 펫 통계
     */
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total_entities", petEntities.size());
        stats.put("total_players", playerPetEntities.size());
        
        int validCount = 0;
        int invalidCount = 0;
        
        for (PetEntity petEntity : petEntities.values()) {
            if (petEntity.isValid()) {
                validCount++;
            } else {
                invalidCount++;
            }
        }
        
        stats.put("valid_entities", validCount);
        stats.put("invalid_entities", invalidCount);
        
        return stats;
    }

    /**
     * 설정 리로드
     */
    public void reload() {
        loadSettings();
    }

    /**
     * 종료 처리
     */
    public void shutdown() {
        stopManagementTask();
        despawnAllPets();
    }
}