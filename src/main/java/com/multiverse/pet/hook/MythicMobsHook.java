package com.multiverse.pet.hook;

import com.multiverse. pet.PetCore;
import org. bukkit.Bukkit;
import org.bukkit.Location;
import org. bukkit.entity. Entity;
import org. bukkit.entity. LivingEntity;
import org.bukkit.plugin.Plugin;

import java. lang.reflect.Method;
import java. util.Optional;
import java.util. UUID;

/**
 * MythicMobs 연동 훅
 * MythicMobs 엔티티를 펫 외형으로 사용
 */
public class MythicMobsHook {

    private final PetCore plugin;
    private Plugin mythicMobs;
    private boolean enabled;

    // 리플렉션용 캐시
    private Object mobManager;
    private Object apiHelper;
    private Method spawnMobMethod;
    private Method getMythicMobMethod;
    private Method isMythicMobMethod;
    private Method getMythicMobTypeMethod;

    public MythicMobsHook(PetCore plugin) {
        this.plugin = plugin;
        this.enabled = false;
        initialize();
    }

    /**
     * 초기화
     */
    private void initialize() {
        mythicMobs = Bukkit. getPluginManager().getPlugin("MythicMobs");

        if (mythicMobs == null || !mythicMobs.isEnabled()) {
            plugin.getLogger().info("MythicMobs를 찾을 수 없습니다. 기본 엔티티로 동작합니다.");
            return;
        }

        try {
            // MythicMobs 5.x API
            Class<?> mythicBukkitClass = Class.forName("io.lumine.mythic.bukkit.MythicBukkit");
            Method instMethod = mythicBukkitClass.getMethod("inst");
            Object mythicInst = instMethod. invoke(null);

            // Mob Manager
            Method getMobManager = mythicBukkitClass.getMethod("getMobManager");
            mobManager = getMobManager. invoke(mythicInst);

            // API Helper
            Method getAPIHelper = mythicBukkitClass. getMethod("getAPIHelper");
            apiHelper = getAPIHelper.invoke(mythicInst);

            if (apiHelper != null) {
                Class<?> apiHelperClass = apiHelper.getClass();
                spawnMobMethod = apiHelperClass.getMethod("spawnMythicMob", String.class, Location.class);
                isMythicMobMethod = apiHelperClass.getMethod("isMythicMob", Entity.class);
                getMythicMobTypeMethod = apiHelperClass.getMethod("getMythicMobInstance", Entity.class);
            }

            if (mobManager != null) {
                Class<?> mobManagerClass = mobManager.getClass();
                getMythicMobMethod = mobManagerClass. getMethod("getMythicMob", String.class);
            }

            enabled = true;
            plugin.getLogger().info("MythicMobs 연동 완료!");

        } catch (ClassNotFoundException e) {
            // MythicMobs 4.x 시도
            try {
                initializeLegacy();
            } catch (Exception ex) {
                plugin.getLogger().warning("MythicMobs 연동 실패:  " + e.getMessage());
                enabled = false;
            }
        } catch (Exception e) {
            plugin. getLogger().warning("MythicMobs 연동 실패: " + e.getMessage());
            enabled = false;
        }
    }

    /**
     * 레거시 초기화 (MythicMobs 4.x)
     */
    private void initializeLegacy() throws Exception {
        Class<?> mythicMobsClass = Class.forName("io.lumine.xikage.mythicmobs.MythicMobs");
        Method instMethod = mythicMobsClass.getMethod("inst");
        Object mythicInst = instMethod.invoke(null);

        // Mob Manager
        Method getMobManager = mythicMobsClass.getMethod("getMobManager");
        mobManager = getMobManager.invoke(mythicInst);

        // API Helper
        Method getAPIHelper = mythicMobsClass.getMethod("getAPIHelper");
        apiHelper = getAPIHelper. invoke(mythicInst);

        if (apiHelper != null) {
            Class<?> apiHelperClass = apiHelper. getClass();
            spawnMobMethod = apiHelperClass.getMethod("spawnMythicMob", String.class, Location.class);
            isMythicMobMethod = apiHelperClass. getMethod("isMythicMob", Entity.class);
        }

        enabled = true;
        plugin.getLogger().info("MythicMobs (Legacy) 연동 완료!");
    }

    /**
     * 연동 활성화 여부
     */
    public boolean isEnabled() {
        return enabled;
    }

    // ===== MythicMob 스폰 =====

    /**
     * MythicMob 스폰
     */
    public Entity spawnMythicMob(String mobType, Location location) {
        if (! enabled || apiHelper == null) {
            return null;
        }

        try {
            Object result = spawnMobMethod.invoke(apiHelper, mobType, location);
            if (result instanceof Entity) {
                return (Entity) result;
            } else if (result instanceof Optional) {
                Optional<? > optional = (Optional<?>) result;
                if (optional.isPresent() && optional.get() instanceof Entity) {
                    return (Entity) optional.get();
                }
            }
        } catch (Exception e) {
            if (plugin.isDebugMode()) {
                plugin.getLogger().warning("[DEBUG] MythicMob 스폰 실패: " + e. getMessage());
            }
        }

        return null;
    }

    /**
     * 펫용 MythicMob 스폰
     */
    public LivingEntity spawnPetMythicMob(String mobType, Location location, UUID ownerId) {
        Entity entity = spawnMythicMob(mobType, location);

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;

            // 펫 설정
            livingEntity.setAI(false);
            livingEntity.setInvulnerable(false);
            livingEntity.setCustomNameVisible(true);
            livingEntity.setPersistent(true);

            // 메타데이터 설정
            livingEntity.setMetadata("pet_owner", 
                    new org.bukkit.metadata. FixedMetadataValue(plugin, ownerId.toString()));
            livingEntity.setMetadata("is_pet", 
                    new org. bukkit.metadata. FixedMetadataValue(plugin, true));

            return livingEntity;
        }

        return null;
    }

    // ===== MythicMob 확인 =====

    /**
     * MythicMob 여부 확인
     */
    public boolean isMythicMob(Entity entity) {
        if (!enabled || apiHelper == null) {
            return false;
        }

        try {
            Object result = isMythicMobMethod. invoke(apiHelper, entity);
            return result != null && (boolean) result;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * MythicMob 타입 가져오기
     */
    public String getMythicMobType(Entity entity) {
        if (! enabled || ! isMythicMob(entity)) {
            return null;
        }

        try {
            Object activeMob = getMythicMobTypeMethod.invoke(apiHelper, entity);
            if (activeMob != null) {
                Method getTypeMethod = activeMob.getClass().getMethod("getType");
                Object mobType = getTypeMethod.invoke(activeMob);
                if (mobType != null) {
                    Method getInternalNameMethod = mobType.getClass().getMethod("getInternalName");
                    return (String) getInternalNameMethod. invoke(mobType);
                }
            }
        } catch (Exception e) {
            if (plugin.isDebugMode()) {
                plugin.getLogger().warning("[DEBUG] MythicMob 타입 조회 실패:  " + e.getMessage());
            }
        }

        return null;
    }

    /**
     * MythicMob 존재 확인
     */
    public boolean mythicMobExists(String mobType) {
        if (!enabled || mobManager == null) {
            return false;
        }

        try {
            Object result = getMythicMobMethod.invoke(mobManager, mobType);
            if (result instanceof Optional) {
                return ((Optional<?>) result).isPresent();
            }
            return result != null;
        } catch (Exception e) {
            return false;
        }
    }

    // ===== 펫 특화 기능 =====

    /**
     * 펫 외형용 MythicMob ID 생성
     */
    public String getPetMobId(String speciesId) {
        return "Pet_" + speciesId;
    }

    /**
     * 종족에 대한 MythicMob 존재 확인
     */
    public boolean hasPetMob(String speciesId) {
        return mythicMobExists(getPetMobId(speciesId));
    }

    /**
     * 펫 외형 스폰 시도
     * MythicMob이 없으면 null 반환 (기본 엔티티 사용)
     */
    public LivingEntity trySpawnPetMob(String speciesId, Location location, UUID ownerId) {
        String mobId = getPetMobId(speciesId);

        if (!mythicMobExists(mobId)) {
            return null;
        }

        return spawnPetMythicMob(mobId, location, ownerId);
    }

    /**
     * MythicMob 제거
     */
    public void removeMythicMob(Entity entity) {
        if (entity != null && ! entity.isDead()) {
            entity.remove();
        }
    }

    // ===== 기타 =====

    /**
     * 모든 MythicMob 목록 가져오기
     */
    public java.util.List<String> getAllMythicMobTypes() {
        java.util.List<String> types = new java.util.ArrayList<>();

        if (! enabled || mobManager == null) {
            return types;
        }

        try {
            Method getMobNamesMethod = mobManager.getClass().getMethod("getMobNames");
            Object result = getMobNamesMethod.invoke(mobManager);

            if (result instanceof java.util.Collection) {
                for (Object name : (java.util.Collection<?>) result) {
                    types.add(name. toString());
                }
            }
        } catch (Exception e) {
            if (plugin.isDebugMode()) {
                plugin.getLogger().warning("[DEBUG] MythicMob 목록 조회 실패:  " + e.getMessage());
            }
        }

        return types;
    }

    /**
     * 펫용 MythicMob 목록
     */
    public java.util.List<String> getPetMythicMobTypes() {
        java. util.List<String> petMobs = new java.util.ArrayList<>();

        for (String mobType : getAllMythicMobTypes()) {
            if (mobType.startsWith("Pet_")) {
                petMobs. add(mobType. substring(4)); // "Pet_" 제거
            }
        }

        return petMobs;
    }

    /**
     * 리로드
     */
    public void reload() {
        enabled = false;
        mobManager = null;
        apiHelper = null;
        initialize();
    }

    /**
     * 종료
     */
    public void shutdown() {
        enabled = false;
        mobManager = null;
        apiHelper = null;
    }
}