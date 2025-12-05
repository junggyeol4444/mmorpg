package com.multiverse.skill;

import com.multiverse.skill.commands.SkillCommand;
import com.multiverse.skill.commands.SkillAdminCommand;
import com.multiverse.skill.commands.SkillPresetCommand;
import com.multiverse.skill.data.storage.*;
import com.multiverse.skill.listeners.*;
import com.multiverse.skill.managers.*;
import com.multiverse.skill.tasks.*;
import org.bukkit. Bukkit;
import org.bukkit.plugin. PluginManager;
import org.bukkit. plugin.java.JavaPlugin;

import java.io.File;

public class SkillCore extends JavaPlugin {

    private static SkillCore instance;

    // 매니저들
    private SkillManager skillManager;
    private SkillLearningManager learningManager;
    private SkillCastManager castManager;
    private SkillEffectExecutor effectExecutor;
    private ComboManager comboManager;
    private SkillEvolutionManager evolutionManager;
    private LifeSkillManager lifeSkillManager;
    private SkillBookManager bookManager;

    // 저장소
    private YamlStorage yamlStorage;
    private SkillDataLoader skillDataLoader;
    private PlayerDataLoader playerDataLoader;
    private SkillTreeLoader treeLoader;
    private ComboDataLoader comboDataLoader;
    private EvolutionDataLoader evolutionDataLoader;
    private SkillBookLoader bookDataLoader;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("========================================");
        getLogger().info("SkillCore v1.0.0 플러그인이 활성화되고 있습니다.. .");
        getLogger().info("마인크래프트 버전: 1.21+");
        getLogger().info("========================================");

        // 설정 파일 생성
        createDefaultConfigs();

        // 저장소 초기화
        initializeStorage();

        // 매니저 초기화
        initializeManagers();

        // 명령어 등록
        registerCommands();

        // 리스너 등록
        registerListeners();

        // 스케줄러 등록
        registerSchedulers();

        getLogger().info("✅ SkillCore 플러그인이 성공적으로 로드되었습니다!");
    }

    @Override
    public void onDisable() {
        getLogger().info("========================================");
        getLogger().info("SkillCore v1.0.0 플러그인이 비활성화됩니다...");
        getLogger().info("========================================");

        // 모든 플레이어 데이터 저장
        if (playerDataLoader != null) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                playerDataLoader.savePlayerData(player. getUniqueId());
            });
        }

        getLogger().info("✅ SkillCore 플러그인이 성공적으로 언로드되었습니다!");
    }

    /**
     * 기본 설정 파일 생성
     */
    private void createDefaultConfigs() {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        // plugin.yml, config.yml 등이 resources에서 자동 복사됨
        saveDefaultConfig();

        // 스킬 폴더 생성
        File skillsFolder = new File(dataFolder, "skills");
        if (!skillsFolder.exists()) {
            skillsFolder.mkdirs();
        }

        // 플레이어 데이터 폴더 생성
        File playersFolder = new File(dataFolder, "players");
        if (!playersFolder.exists()) {
            playersFolder.mkdirs();
        }

        // 백업 폴더 생성
        File backupFolder = new File(dataFolder, "backups");
        if (!backupFolder.exists()) {
            backupFolder.mkdirs();
        }
    }

    /**
     * 저장소 초기화
     */
    private void initializeStorage() {
        yamlStorage = new YamlStorage(this);

        skillDataLoader = new SkillDataLoader(this, yamlStorage);
        playerDataLoader = new PlayerDataLoader(this, yamlStorage);
        treeLoader = new SkillTreeLoader(this, yamlStorage);
        comboDataLoader = new ComboDataLoader(this, yamlStorage);
        evolutionDataLoader = new EvolutionDataLoader(this, yamlStorage);
        bookDataLoader = new SkillBookLoader(this, yamlStorage);

        getLogger().info("💾 저장소가 초기화되었습니다!");
    }

    /**
     * 매니저 초기화
     */
    private void initializeManagers() {
        skillManager = new SkillManager(this);
        learningManager = new SkillLearningManager(this, skillManager);
        castManager = new SkillCastManager(this);
        effectExecutor = new SkillEffectExecutor(this, skillManager);
        comboManager = new ComboManager(this);
        evolutionManager = new SkillEvolutionManager(this, skillManager, learningManager);
        lifeSkillManager = new LifeSkillManager(this);
        bookManager = new SkillBookManager(this, learningManager);

        getLogger(). info("⚙️ 모든 매니저가 초기화되었습니다!");
    }

    /**
     * 명령어 등록
     */
    private void registerCommands() {
        getCommand("skill").setExecutor(new SkillCommand(this, skillManager, learningManager));
        getCommand("skill").setTabCompleter(new SkillCommand(this, skillManager, learningManager));

        // Admin 명령어 (선택사항)
        if (getConfig().getBoolean("commands.admin-enabled", true)) {
            // SkillAdminCommand와 SkillPresetCommand는 /skill 명령어의 하위 커맨드로 처리
        }

        getLogger().info("📝 명령어가 등록되었습니다!");
    }

    /**
     * 리스너 등록
     */
    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new PlayerJoinListener(this, playerDataLoader), this);
        pm.registerEvents(new SkillUseListener(this, skillManager, castManager, effectExecutor), this);
        pm.registerEvents(new LifeSkillListener(this, lifeSkillManager), this);
        pm.registerEvents(new CombatListener(this, castManager, comboManager), this);
        pm.registerEvents(new ProjectileListener(this, effectExecutor), this);

        getLogger().info("👂 리스너가 등록되었습니다!");
    }

    /**
     * 스케줄러 등록
     */
    private void registerSchedulers() {
        // 캐스팅 태스크 (매 틱마다)
        Bukkit.getScheduler().runTaskTimer(this, 
            new CastingTask(this, castManager), 0L, 1L);

        // 채널링 태스크
        Bukkit.getScheduler().runTaskTimer(this,
            new ChannelingTask(this, castManager), 0L, 1L);

        // DoT 태스크 (매 초마다)
        Bukkit.getScheduler().runTaskTimer(this,
            new DoTTask(this, effectExecutor), 0L, 20L);

        // 투사체 태스크 (매 틱마다)
        Bukkit.getScheduler().runTaskTimer(this,
            new ProjectileTask(this), 0L, 1L);

        // 콤보 타임아웃 체크 (매 초마다)
        Bukkit.getScheduler().runTaskTimer(this,
            new ComboTimeoutTask(this, comboManager), 0L, 20L);

        // 자동 저장 (5분마다)
        int autoSaveInterval = getConfig(). getInt("data.auto-save-interval", 300);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                playerDataLoader.savePlayerData(player.getUniqueId());
            });
            getLogger().info("💾 플레이어 데이터가 자동 저장되었습니다!");
        }, autoSaveInterval * 20L, autoSaveInterval * 20L);

        getLogger().info("⏰ 스케줄러가 등록되었습니다!");
    }

    // ===== Getter Methods =====

    public static SkillCore getInstance() {
        return instance;
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public SkillLearningManager getLearningManager() {
        return learningManager;
    }

    public SkillCastManager getCastManager() {
        return castManager;
    }

    public SkillEffectExecutor getEffectExecutor() {
        return effectExecutor;
    }

    public ComboManager getComboManager() {
        return comboManager;
    }

    public SkillEvolutionManager getEvolutionManager() {
        return evolutionManager;
    }

    public LifeSkillManager getLifeSkillManager() {
        return lifeSkillManager;
    }

    public SkillBookManager getBookManager() {
        return bookManager;
    }

    public YamlStorage getYamlStorage() {
        return yamlStorage;
    }

    public SkillDataLoader getSkillDataLoader() {
        return skillDataLoader;
    }

    public PlayerDataLoader getPlayerDataLoader() {
        return playerDataLoader;
    }

    public SkillTreeLoader getTreeLoader() {
        return treeLoader;
    }

    public ComboDataLoader getComboDataLoader() {
        return comboDataLoader;
    }

    public EvolutionDataLoader getEvolutionDataLoader() {
        return evolutionDataLoader;
    }

    public SkillBookLoader getBookDataLoader() {
        return bookDataLoader;
    }
}