package com.multiverse.npcai;

import com.multiverse.npcai.managers.*;
import com.multiverse.npcai.data.DataManager;
import com.multiverse.npcai.data.YAMLDataManager;
import com.multiverse.npcai.listeners.*;
import com.multiverse.npcai.tasks.*;
import com.multiverse.npcai.citizens.CitizensNPCTrait;
import com.multiverse.npcai.api.NPCAIAPI;
import com.multiverse.npcai.utils.ConfigUtil;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.CitizensPlugin;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class NPCAICore extends JavaPlugin {

    // 매니저 및 주요 컴포넌트
    private NPCManager npcManager;
    private ReputationManager reputationManager;
    private DialogueManager dialogueManager;
    private ShopManager shopManager;
    private SkillTrainerManager skillTrainerManager;
    private AIBehaviorManager aiBehaviorManager;
    private ReactionManager reactionManager;
    private NPCDataManager npcDataManager;
    private DataManager dataManager;

    // Citizens, Vault, Economy 등 외부 의존성
    private NPCRegistry citizensRegistry;
    private CitizensPlugin citizensPlugin;
    private Economy economy;
    private Permission permission;

    // 설정/로깅
    private ConfigUtil configUtil;
    private Logger log;

    @Override
    public void onEnable() {
        log = getLogger();
        log.info("NPCAICore 1.0.0 Starting...");

        // === 설정 파일 로드 ===
        saveDefaultConfig();
        configUtil = new ConfigUtil(this);

        // === 외부 플러그인 연동 확인 ===
        if (!setupCitizens()) {
            log.severe("Citizens 플러그인이 필요합니다.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (!setupVault()) {
            log.severe("Vault 플러그인이 필요합니다.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (!setupEconomy()) {
            log.severe("Vault Economy 지원 필요. EconomyCore/Vault 확인!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        setupPermission();

        // === 데이터 매니저, YAML 접근 객체 초기화 ===
        dataManager = new YAMLDataManager(this, configUtil);
        npcDataManager = new NPCDataManager(this, dataManager);

        // === 매니저 및 API 초기화 ===
        npcManager = new NPCManager(this, citizensRegistry, npcDataManager, configUtil);
        reputationManager = new ReputationManager(this, dataManager, configUtil);
        dialogueManager = new DialogueManager(this, dataManager, reputationManager, configUtil);
        shopManager = new ShopManager(this, dataManager, reputationManager, configUtil);
        skillTrainerManager = new SkillTrainerManager(this, dataManager, configUtil, reputationManager);
        aiBehaviorManager = new AIBehaviorManager(this, npcManager, configUtil);
        reactionManager = new ReactionManager(this, aiBehaviorManager, dialogueManager, reputationManager, configUtil);

        // API static 저장
        NPCAIAPI.init(this);

        // === 이벤트 및 리스너 등록 ===
        getServer().getPluginManager().registerEvents(new NPCInteractionListener(npcManager, reputationManager, dialogueManager, shopManager, skillTrainerManager, configUtil), this);
        getServer().getPluginManager().registerEvents(new NPCDamageListener(npcManager, reputationManager, aiBehaviorManager, configUtil), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(npcManager, reactionManager, configUtil), this);
        getServer().getPluginManager().registerEvents(new CitizensListener(this, npcManager, configUtil), this);

        // Citizens Trait 등록
        CitizensAPI.getTraitFactory().registerTrait(CitizensNPCTrait.class);

        // === 명령어 등록 ===
        getCommand("npcai").setExecutor(new com.multiverse.npcai.commands.AdminCommand(this));
        getCommand("npc").setExecutor(new com.multiverse.npcai.commands.NPCCommand(this));
        // 명령어 탭 완성
        getCommand("npcai").setTabCompleter(new com.multiverse.npcai.commands.CommandManager(this));
        getCommand("npc").setTabCompleter(new com.multiverse.npcai.commands.CommandManager(this));

        // === 반복/스케줄링 태스크 ===
        int aiInterval = configUtil.getInt("ai.update-interval", 20);
        Bukkit.getScheduler().runTaskTimer(this, new AIUpdateTask(npcManager, aiBehaviorManager, reactionManager), 20L, aiInterval);

        int saveIntervalSec = configUtil.getInt("data.auto-save-interval", 300);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new AutoSaveTask(dataManager), saveIntervalSec * 20L, saveIntervalSec * 20L);

        if (configUtil.getBoolean("shops.daily-reset.enabled", true)) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, new ShopResetTask(shopManager, configUtil), 100L, 1200L);
        }
        if (configUtil.getBoolean("reputation.decay.enabled", true)) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, new ReputationDecayTask(reputationManager, configUtil), 1200L, 1200L);
        }
        Bukkit.getScheduler().runTaskTimer(this, new SkillLearningTask(skillTrainerManager, configUtil), 120L, 120L);

        // === 데이터 로드 및 캐싱 ===
        dataManager.loadAll();

        // === 백업 스케줄 시작 ===
        if (configUtil.getBoolean("data.backup.enabled", false)) {
            // 백업 Task 등 (이름은 예시, 구현에 따라 변동)
            // Bukkit.getScheduler().runTaskTimerAsynchronously(this, new BackupTask(dataManager, configUtil), ...);
        }

        log.info("NPCAICore 활성화 완료!");
    }

    @Override
    public void onDisable() {
        log.info("NPCAICore 비활성화. 데이터 저장 중...");
        if (dataManager != null) {
            dataManager.saveAll();
        }
        log.info("NPCAICore 정상 종료.");
    }

    /** Citizens 연동 */
    private boolean setupCitizens() {
        citizensPlugin = (CitizensPlugin) Bukkit.getServer().getPluginManager().getPlugin("Citizens");
        if (citizensPlugin == null || !citizensPlugin.isEnabled()) return false;
        citizensRegistry = CitizensAPI.getNPCRegistry();
        return citizensRegistry != null;
    }

    /** Vault 연동 */
    private boolean setupVault() {
        return Bukkit.getPluginManager().getPlugin("Vault") != null;
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        var rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return economy != null;
    }
    private boolean setupPermission() {
        var rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) return false;
        permission = rsp.getProvider();
        return permission != null;
    }

    // === Getter, API exposing ===
    public NPCManager getNPCManager() { return npcManager; }
    public ReputationManager getReputationManager() { return reputationManager; }
    public DialogueManager getDialogueManager() { return dialogueManager; }
    public ShopManager getShopManager() { return shopManager; }
    public SkillTrainerManager getSkillTrainerManager() { return skillTrainerManager; }
    public AIBehaviorManager getAIBehaviorManager() { return aiBehaviorManager; }
    public ReactionManager getReactionManager() { return reactionManager; }
    public ConfigUtil getConfigUtil() { return configUtil; }
    public DataManager getDataManager() { return dataManager; }

    public Economy getEconomy() { return economy; }
    public Permission getPermission() { return permission; }
    public Logger getLog() { return log; }
}