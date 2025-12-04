package com.multiverse.combat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin. PluginManager;
import org. bukkit.configuration.file.FileConfiguration;
import com.multiverse.combat.managers.*;
import com.multiverse.combat.calculator.*;
import com.multiverse.combat.listeners.*;
import com.multiverse.combat.commands.*;
import com.multiverse. combat.tasks.*;
import com.multiverse. combat.data.*;
import com.multiverse. combat.api.CombatAPI;

/**
 * CombatCore 플러그인 메인 클래스
 * 전투 시스템의 중심 관리 클래스
 * 
 * @author YourName
 * @version 1.0. 0
 */
public class CombatCore extends JavaPlugin {
    
    // 싱글톤 인스턴스
    private static CombatCore instance;
    
    // 설정
    private FileConfiguration config;
    
    // 매니저들
    private SkillManager skillManager;
    private ComboManager comboManager;
    private StatusEffectManager statusEffectManager;
    private PvPManager pvpManager;
    private DurabilityManager durabilityManager;
    private CombatDataManager combatDataManager;
    
    // 계산기
    private DamageCalculator damageCalculator;
    private DefenseCalculator defenseCalculator;
    private ElementalDamage elementalDamage;
    
    // 데이터
    private DataManager dataManager;
    
    // 명령어
    private CommandManager commandManager;
    
    /**
     * 플러그인 활성화
     */
    @Override
    public void onEnable() {
        instance = this;
        
        // 설정 로드
        loadConfig();
        
        // 데이터 초기화
        initializeDataManager();
        
        // 매니저 초기화
        initializeManagers();
        
        // 계산기 초기화
        initializeCalculators();
        
        // 리스너 등록
        registerListeners();
        
        // 명령어 등록
        registerCommands();
        
        // 비동기 작업 시작
        startAsyncTasks();
        
        // API 초기화
        CombatAPI.init(this);
        
        getLogger().info("=" . repeat(50));
        getLogger().info("CombatCore v1.0.0이 활성화되었습니다!");
        getLogger().info("전투 시스템이 준비되었습니다.");
        getLogger().info("=" .repeat(50));
    }
    
    /**
     * 플러그인 비활성화
     */
    @Override
    public void onDisable() {
        // 데이터 저장
        if (dataManager != null) {
            dataManager.saveAll();
        }
        
        // 모든 비동기 작업 취소
        Bukkit.getScheduler().cancelTasks(this);
        
        getLogger().info("CombatCore가 비활성화되었습니다.");
    }
    
    /**
     * 설정 로드
     */
    private void loadConfig() {
        // config.yml 생성 및 로드
        saveDefaultConfig();
        config = getConfig();
        
        // 기본 설정 확인
        if (! config.contains("combat. enabled")) {
            config.set("combat.enabled", true);
        }
        if (!config.contains("data.type")) {
            config.set("data.type", "yaml");
        }
        if (!config.contains("data. auto-save-interval")) {
            config.set("data.auto-save-interval", 300);
        }
        
        saveConfig();
    }
    
    /**
     * 데이터 매니저 초기화
     */
    private void initializeDataManager() {
        String dataType = config.getString("data.type", "yaml");
        
        if ("yaml".equalsIgnoreCase(dataType)) {
            dataManager = new YAMLDataManager(this);
        } else {
            // 향후 MySQL 등 확장 가능
            dataManager = new YAMLDataManager(this);
        }
        
        dataManager.loadAll();
        getLogger().info("✓ 데이터 시스템 초기화 완료");
    }
    
    /**
     * 매니저 초기화
     */
    private void initializeManagers() {
        skillManager = new SkillManager(this);
        comboManager = new ComboManager(this);
        statusEffectManager = new StatusEffectManager(this);
        pvpManager = new PvPManager(this);
        durabilityManager = new DurabilityManager(this);
        combatDataManager = new CombatDataManager(this);
        
        getLogger().info("✓ 매니저 시스템 초기화 완료");
    }
    
    /**
     * 계산기 초기화
     */
    private void initializeCalculators() {
        damageCalculator = new DamageCalculator(this);
        defenseCalculator = new DefenseCalculator(this);
        elementalDamage = new ElementalDamage(this);
        
        getLogger().info("✓ 계산 시스템 초기화 완료");
    }
    
    /**
     * 리스너 등록
     */
    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        
        pm.registerEvents(new CombatListener(this), this);
        pm.registerEvents(new SkillListener(this), this);
        pm.registerEvents(new PvPListener(this), this);
        
        getLogger().info("✓ 이벤트 리스너 등록 완료");
    }
    
    /**
     * 명령어 등록
     */
    private void registerCommands() {
        commandManager = new CommandManager(this);
        commandManager.register();
        
        getLogger().info("✓ 명령어 시스템 등록 완료");
    }
    
    /**
     * 비동기 작업 시작
     */
    private void startAsyncTasks() {
        // 자동 저장
        int autoSaveInterval = config.getInt("data.auto-save-interval", 300);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, 
            new AutoSaveTask(this), 
            autoSaveInterval * 20L, 
            autoSaveInterval * 20L);
        
        // 콤보 타임아웃 체크
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this,
            new ComboTimeoutTask(this),
            0L,
            10L);  // 0. 5초마다
        
        // 상태이상 업데이트
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this,
            new StatusEffectTask(this),
            0L,
            1L);  // 매 틱
        
        // 쿨다운 업데이트
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this,
            new CooldownTask(this),
            0L,
            20L);  // 1초마다
        
        getLogger(). info("✓ 비동기 작업 시작 완료");
    }
    
    /**
     * 싱글톤 인스턴스 반환
     * @return CombatCore 인스턴스
     */
    public static CombatCore getInstance() {
        return instance;
    }
    
    /**
     * 설정 반환
     * @return FileConfiguration
     */
    public FileConfiguration getCombatConfig() {
        return config;
    }
    
    /**
     * 스킬 매니저 반환
     * @return SkillManager
     */
    public SkillManager getSkillManager() {
        return skillManager;
    }
    
    /**
     * 콤보 매니저 반환
     * @return ComboManager
     */
    public ComboManager getComboManager() {
        return comboManager;
    }
    
    /**
     * 상태이상 매니저 반환
     * @return StatusEffectManager
     */
    public StatusEffectManager getStatusEffectManager() {
        return statusEffectManager;
    }
    
    /**
     * PvP 매니저 반환
     * @return PvPManager
     */
    public PvPManager getPvPManager() {
        return pvpManager;
    }
    
    /**
     * 내구도 매니저 반환
     * @return DurabilityManager
     */
    public DurabilityManager getDurabilityManager() {
        return durabilityManager;
    }
    
    /**
     * 전투 데이터 매니저 반환
     * @return CombatDataManager
     */
    public CombatDataManager getCombatDataManager() {
        return combatDataManager;
    }
    
    /**
     * 데미지 계산기 반환
     * @return DamageCalculator
     */
    public DamageCalculator getDamageCalculator() {
        return damageCalculator;
    }
    
    /**
     * 방어력 계산기 반환
     * @return DefenseCalculator
     */
    public DefenseCalculator getDefenseCalculator() {
        return defenseCalculator;
    }
    
    /**
     * 속성 데미지 반환
     * @return ElementalDamage
     */
    public ElementalDamage getElementalDamage() {
        return elementalDamage;
    }
    
    /**
     * 데이터 매니저 반환
     * @return DataManager
     */
    public DataManager getDataManager() {
        return dataManager;
    }
}