package com.multiverse.pet;

import com.multiverse. pet.api.PetCoreAPI;
import com.multiverse. pet.command.PetAdminCommand;
import com.multiverse. pet.command.PetCommand;
import com. multiverse.pet. config.*;
import com.multiverse.pet.data.PetDataManager;
import com. multiverse.pet. data.PetEvolutionLoader;
import com. multiverse.pet. data.PetSkillLoader;
import com.multiverse. pet.data.PetSpeciesLoader;
import com.multiverse.pet.data.cache.PetCache;
import com.multiverse.pet.data.cache. SpeciesCache;
import com.multiverse. pet.entity.PetEntityManager;
import com.multiverse.pet.hook.ItemCoreHook;
import com.multiverse.pet.hook.MythicMobsHook;
import com.multiverse.pet.hook.PlayerDataCoreHook;
import com.multiverse. pet.listener.*;
import com.multiverse.pet.manager.*;
import com.multiverse.pet.task.*;
import org.bukkit. Bukkit;
import org.bukkit. plugin.java.JavaPlugin;

import java.io. File;
import java.util.logging. Level;

/**
 * PetCore 메인 플러그인 클래스
 * 펫 소환, 레벨링, 스킬, 진화, 장비, 교배, 대결 등 종합 펫 시스템
 */
public class PetCore extends JavaPlugin {

    private static PetCore instance;

    // 설정 관리
    private ConfigManager configManager;
    private PetSettings petSettings;
    private LevelSettings levelSettings;
    private SkillSettings skillSettings;
    private EvolutionSettings evolutionSettings;
    private CareSettings careSettings;
    private BreedingSettings breedingSettings;
    private BattleSettings battleSettings;
    private StorageSettings storageSettings;

    // 데이터 로더
    private PetSpeciesLoader speciesLoader;
    private PetSkillLoader skillLoader;
    private PetEvolutionLoader evolutionLoader;
    private PetDataManager dataManager;

    // 캐시
    private PetCache petCache;
    private SpeciesCache speciesCache;

    // 매니저
    private PetManager petManager;
    private PetLevelManager levelManager;
    private PetSkillManager skillManager;
    private EvolutionManager evolutionManager;
    private PetEquipmentManager equipmentManager;
    private PetCareManager careManager;
    private BreedingManager breedingManager;
    private PetStorageManager storageManager;
    private PetBattleManager battleManager;
    private PetAcquisitionManager acquisitionManager;
    private PetRankingManager rankingManager;
    private PetEntityManager entityManager;

    // 외부 플러그인 연동
    private PlayerDataCoreHook playerDataCoreHook;
    private ItemCoreHook itemCoreHook;
    private MythicMobsHook mythicMobsHook;

    // API
    private PetCoreAPI api;

    // 태스크 ID
    private int followTaskId = -1;
    private int careTaskId = -1;
    private int regenTaskId = -1;
    private int breedingTaskId = -1;
    private int eggHatchTaskId = -1;
    private int autoSaveTaskId = -1;

    @Override
    public void onEnable() {
        instance = this;

        // 설정 파일 생성
        saveDefaultConfigs();

        // 설정 로드
        loadConfigs();

        // 외부 플러그인 연동
        setupHooks();

        // 캐시 초기화
        initCaches();

        // 데이터 로더 초기화
        initLoaders();

        // 매니저 초기화
        initManagers();

        // API 초기화
        api = new PetCoreAPI(this);

        // 명령어 등록
        registerCommands();

        // 이벤트 리스너 등록
        registerListeners();

        // 태스크 시작
        startTasks();

        getLogger().info("PetCore v" + getDescription().getVersion() + " 활성화 완료!");
    }

    @Override
    public void onDisable() {
        // 태스크 중지
        stopTasks();

        // 모든 펫 해제
        if (petManager != null) {
            petManager.unsummonAllPets();
        }

        // 데이터 저장
        if (dataManager != null) {
            dataManager. saveAllData();
        }

        // 진행 중인 교배 저장
        if (breedingManager != null) {
            breedingManager. saveAllBreedings();
        }

        // 진행 중인 배틀 종료
        if (battleManager != null) {
            battleManager.endAllBattles();
        }

        getLogger().info("PetCore 비활성화 완료!");
        instance = null;
    }

    /**
     * 기본 설정 파일 저장
     */
    private void saveDefaultConfigs() {
        saveDefaultConfig();

        // messages.yml
        if (! new File(getDataFolder(), "messages.yml").exists()) {
            saveResource("messages.yml", false);
        }

        // 펫 종족 파일
        File speciesDir = new File(getDataFolder(), "pets/species");
        if (!speciesDir. exists()) {
            speciesDir.mkdirs();
            saveResource("pets/species/wolf.yml", false);
            saveResource("pets/species/dragon.yml", false);
            saveResource("pets/species/phoenix.yml", false);
            saveResource("pets/species/golem.yml", false);
        }

        // 스킬 파일
        File skillsDir = new File(getDataFolder(), "pets/skills");
        if (!skillsDir.exists()) {
            skillsDir. mkdirs();
            saveResource("pets/skills/combat_skills.yml", false);
            saveResource("pets/skills/gathering_skills.yml", false);
            saveResource("pets/skills/support_skills.yml", false);
            saveResource("pets/skills/special_skills.yml", false);
        }

        // 진화 파일
        File evolutionsDir = new File(getDataFolder(), "pets/evolutions");
        if (!evolutionsDir.exists()) {
            evolutionsDir.mkdirs();
            saveResource("pets/evolutions/evolutions.yml", false);
        }

        // GUI 파일
        File guiDir = new File(getDataFolder(), "gui");
        if (!guiDir.exists()) {
            guiDir.mkdirs();
            saveResource("gui/main_menu.yml", false);
            saveResource("gui/storage_menu.yml", false);
            saveResource("gui/battle_menu.yml", false);
        }

        // 플레이어 데이터 폴더
        File playersDir = new File(getDataFolder(), "players");
        if (!playersDir.exists()) {
            playersDir.mkdirs();
        }

        // 백업 폴더
        File backupsDir = new File(getDataFolder(), "backups");
        if (!backupsDir.exists()) {
            backupsDir.mkdirs();
        }
    }

    /**
     * 설정 로드
     */
    private void loadConfigs() {
        try {
            configManager = new ConfigManager(this);
            petSettings = new PetSettings(configManager);
            levelSettings = new LevelSettings(configManager);
            skillSettings = new SkillSettings(configManager);
            evolutionSettings = new EvolutionSettings(configManager);
            careSettings = new CareSettings(configManager);
            breedingSettings = new BreedingSettings(configManager);
            battleSettings = new BattleSettings(configManager);
            storageSettings = new StorageSettings(configManager);

            getLogger().info("설정 파일 로드 완료");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "설정 파일 로드 실패", e);
        }
    }

    /**
     * 외부 플러그인 연동
     */
    private void setupHooks() {
        // PlayerDataCore (필수)
        if (Bukkit.getPluginManager().getPlugin("PlayerDataCore") != null) {
            playerDataCoreHook = new PlayerDataCoreHook(this);
            getLogger().info("PlayerDataCore 연동 완료");
        } else {
            getLogger().severe("PlayerDataCore 플러그인을 찾을 수 없습니다!  플러그인을 비활성화합니다.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // ItemCore (선택)
        if (Bukkit.getPluginManager().getPlugin("ItemCore") != null) {
            itemCoreHook = new ItemCoreHook(this);
            getLogger().info("ItemCore 연동 완료");
        } else {
            getLogger().info("ItemCore를 찾을 수 없습니다.  기본 아이템을 사용합니다.");
        }

        // MythicMobs (선택)
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            mythicMobsHook = new MythicMobsHook(this);
            getLogger().info("MythicMobs 연동 완료");
        } else {
            getLogger().info("MythicMobs를 찾을 수 없습니다. 바닐라 엔티티를 사용합니다.");
        }
    }

    /**
     * 캐시 초기화
     */
    private void initCaches() {
        speciesCache = new SpeciesCache();
        petCache = new PetCache();
        getLogger().info("캐시 초기화 완료");
    }

    /**
     * 데이터 로더 초기화
     */
    private void initLoaders() {
        try {
            speciesLoader = new PetSpeciesLoader(this, speciesCache);
            speciesLoader.loadAllSpecies();

            skillLoader = new PetSkillLoader(this);
            skillLoader. loadAllSkills();

            evolutionLoader = new PetEvolutionLoader(this);
            evolutionLoader.loadAllEvolutions();

            dataManager = new PetDataManager(this, petCache);

            getLogger().info("데이터 로더 초기화 완료");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "데이터 로더 초기화 실패", e);
        }
    }

    /**
     * 매니저 초기화
     */
    private void initManagers() {
        entityManager = new PetEntityManager(this);
        petManager = new PetManager(this);
        levelManager = new PetLevelManager(this);
        skillManager = new PetSkillManager(this);
        evolutionManager = new EvolutionManager(this);
        equipmentManager = new PetEquipmentManager(this);
        careManager = new PetCareManager(this);
        breedingManager = new BreedingManager(this);
        storageManager = new PetStorageManager(this);
        battleManager = new PetBattleManager(this);
        acquisitionManager = new PetAcquisitionManager(this);
        rankingManager = new PetRankingManager(this);

        getLogger().info("매니저 초기화 완료");
    }

    /**
     * 명령어 등록
     */
    private void registerCommands() {
        PetCommand petCommand = new PetCommand(this);
        PetAdminCommand adminCommand = new PetAdminCommand(this);

        getCommand("pet").setExecutor(petCommand);
        getCommand("pet").setTabCompleter(petCommand);

        getLogger().info("명령어 등록 완료");
    }

    /**
     * 이벤트 리스너 등록
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PetSpawnListener(this), this);
        getServer().getPluginManager().registerEvents(new PetCombatListener(this), this);
        getServer().getPluginManager().registerEvents(new PetExpListener(this), this);
        getServer().getPluginManager().registerEvents(new PetInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PetCareListener(this), this);
        getServer().getPluginManager().registerEvents(new PetSkillListener(this), this);
        getServer().getPluginManager().registerEvents(new PetBattleListener(this), this);
        getServer().getPluginManager().registerEvents(new PetCaptureListener(this), this);
        getServer().getPluginManager().registerEvents(new PetEggListener(this), this);

        getLogger().info("이벤트 리스너 등록 완료");
    }

    /**
     * 태스크 시작
     */
    private void startTasks() {
        // 펫 따라가기 태스크 (5틱마다)
        PetFollowTask followTask = new PetFollowTask(this);
        followTaskId = followTask. runTaskTimer(this, 20L, 5L).getTaskId();

        // 펫 케어 태스크 (1분마다)
        PetCareTask careTask = new PetCareTask(this);
        careTaskId = careTask.runTaskTimer(this, 20L * 60, 20L * 60).getTaskId();

        // 펫 체력 재생 태스크 (10초마다)
        PetRegenTask regenTask = new PetRegenTask(this);
        regenTaskId = regenTask.runTaskTimer(this, 20L * 10, 20L * 10).getTaskId();

        // 교배 확인 태스크 (1분마다)
        BreedingCheckTask breedingCheckTask = new BreedingCheckTask(this);
        breedingTaskId = breedingCheckTask.runTaskTimer(this, 20L * 60, 20L * 60).getTaskId();

        // 알 부화 태스크 (1분마다)
        EggHatchTask eggHatchTask = new EggHatchTask(this);
        eggHatchTaskId = eggHatchTask.runTaskTimer(this, 20L * 60, 20L * 60).getTaskId();

        // 자동 저장 태스크
        int saveInterval = configManager.getConfig().getInt("data.auto-save-interval", 300);
        AutoSaveTask autoSaveTask = new AutoSaveTask(this);
        autoSaveTaskId = autoSaveTask.runTaskTimer(this, 20L * saveInterval, 20L * saveInterval).getTaskId();

        getLogger().info("태스크 시작 완료");
    }

    /**
     * 태스크 중지
     */
    private void stopTasks() {
        if (followTaskId != -1) {
            Bukkit.getScheduler().cancelTask(followTaskId);
        }
        if (careTaskId != -1) {
            Bukkit.getScheduler().cancelTask(careTaskId);
        }
        if (regenTaskId != -1) {
            Bukkit. getScheduler().cancelTask(regenTaskId);
        }
        if (breedingTaskId != -1) {
            Bukkit. getScheduler().cancelTask(breedingTaskId);
        }
        if (eggHatchTaskId != -1) {
            Bukkit.getScheduler().cancelTask(eggHatchTaskId);
        }
        if (autoSaveTaskId != -1) {
            Bukkit.getScheduler().cancelTask(autoSaveTaskId);
        }

        getLogger().info("태스크 중지 완료");
    }

    /**
     * 설정 리로드
     */
    public void reload() {
        reloadConfig();
        loadConfigs();

        speciesLoader.loadAllSpecies();
        skillLoader.loadAllSkills();
        evolutionLoader.loadAllEvolutions();

        getLogger().info("PetCore 리로드 완료");
    }

    // ===== Getter 메서드 =====

    public static PetCore getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PetSettings getPetSettings() {
        return petSettings;
    }

    public LevelSettings getLevelSettings() {
        return levelSettings;
    }

    public SkillSettings getSkillSettings() {
        return skillSettings;
    }

    public EvolutionSettings getEvolutionSettings() {
        return evolutionSettings;
    }

    public CareSettings getCareSettings() {
        return careSettings;
    }

    public BreedingSettings getBreedingSettings() {
        return breedingSettings;
    }

    public BattleSettings getBattleSettings() {
        return battleSettings;
    }

    public StorageSettings getStorageSettings() {
        return storageSettings;
    }

    public PetSpeciesLoader getSpeciesLoader() {
        return speciesLoader;
    }

    public PetSkillLoader getSkillLoader() {
        return skillLoader;
    }

    public PetEvolutionLoader getEvolutionLoader() {
        return evolutionLoader;
    }

    public PetDataManager getDataManager() {
        return dataManager;
    }

    public PetCache getPetCache() {
        return petCache;
    }

    public SpeciesCache getSpeciesCache() {
        return speciesCache;
    }

    public PetManager getPetManager() {
        return petManager;
    }

    public PetLevelManager getLevelManager() {
        return levelManager;
    }

    public PetSkillManager getSkillManager() {
        return skillManager;
    }

    public EvolutionManager getEvolutionManager() {
        return evolutionManager;
    }

    public PetEquipmentManager getEquipmentManager() {
        return equipmentManager;
    }

    public PetCareManager getCareManager() {
        return careManager;
    }

    public BreedingManager getBreedingManager() {
        return breedingManager;
    }

    public PetStorageManager getStorageManager() {
        return storageManager;
    }

    public PetBattleManager getBattleManager() {
        return battleManager;
    }

    public PetAcquisitionManager getAcquisitionManager() {
        return acquisitionManager;
    }

    public PetRankingManager getRankingManager() {
        return rankingManager;
    }

    public PetEntityManager getEntityManager() {
        return entityManager;
    }

    public PlayerDataCoreHook getPlayerDataCoreHook() {
        return playerDataCoreHook;
    }

    public ItemCoreHook getItemCoreHook() {
        return itemCoreHook;
    }

    public MythicMobsHook getMythicMobsHook() {
        return mythicMobsHook;
    }

    public PetCoreAPI getAPI() {
        return api;
    }

    public boolean hasItemCore() {
        return itemCoreHook != null;
    }

    public boolean hasMythicMobs() {
        return mythicMobsHook != null;
    }
}