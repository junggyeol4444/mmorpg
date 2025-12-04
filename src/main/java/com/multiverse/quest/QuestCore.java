package com.multiverse.quest;

import com.multiverse.quest.api.QuestAPI;
import com.multiverse. quest.commands.*;
import com.multiverse.quest.listeners.*;
import com.multiverse.quest.managers. QuestDataManager;
import com.multiverse.quest.tasks.*;
import com.multiverse.quest.utils.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit. Bukkit;
import java.util.*;

/**
 * QuestCore 플러그인 메인 클래스
 * 퀨스트 시스템 플러그인의 진입점입니다.
 */
public class QuestCore extends JavaPlugin {
    
    private static QuestCore instance;
    private QuestDataManager questDataManager;
    private CommandManager commandManager;
    
    // 리스너
    private QuestObjectiveListener questObjectiveListener;
    private QuestEventListener questEventListener;
    private PlayerListener playerListener;
    private NPCListener npcListener;

    // 태스크
    private AutoSaveTask autoSaveTask;
    private TrackerUpdateTask trackerUpdateTask;
    private TimeLimitCheckTask timeLimitCheckTask;
    private DailyResetTask dailyResetTask;
    private WeeklyResetTask weeklyResetTask;

    // 유틸리티
    private ConfigUtil configUtil;
    private FileUtil fileUtil;
    private QuestLoader questLoader;

    // ============ Plugin Lifecycle ============

    @Override
    public void onEnable() {
        instance = this;
        
        long startTime = System.currentTimeMillis();
        
        getLogger().info("════════════════════════════════════════");
        getLogger().info("       QuestCore 플러그인 시작 중.. .");
        getLogger().info("════════════════════════════════════════");

        try {
            // 1. 설정 파일 로드
            initializeConfiguration();
            getLogger().info("✓ 설정 파일 로드 완료");

            // 2. 유틸리티 초기화
            initializeUtilities();
            getLogger().info("✓ 유틸리티 초기화 완료");

            // 3. 데이터 관리자 초기화
            initializeDataManager();
            getLogger().info("✓ 데이터 관리자 초기화 완료");

            // 4. API 초기화
            initializeAPI();
            getLogger().info("✓ API 초기화 완료");

            // 5. 리스너 등록
            registerListeners();
            getLogger().info("✓ 이벤트 리스너 등록 완료");

            // 6. 명령어 등록
            registerCommands();
            getLogger().info("✓ 명령어 등록 완료");

            // 7. 태스크 시작
            startTasks();
            getLogger().info("✓ 백그라운드 태스크 시작 완료");

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            getLogger().info("════════════════════════════════════════");
            getLogger().info("  QuestCore 플러그인이 활성화되었습니다!");
            getLogger().info("  버전: " + getDescription().getVersion());
            getLogger().info("  로딩 시간: " + duration + "ms");
            getLogger().info("════════════════════════════════════════");

        } catch (Exception e) {
            getLogger().severe("플러그인 초기화 중 오류 발생!");
            getLogger().severe(e.getMessage());
            e.printStackTrace();
            setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        try {
            // 태스크 중지
            stopTasks();
            getLogger().info("✓ 백그라운드 태스크 중지됨");

            // 데이터 저장
            if (questDataManager != null) {
                questDataManager.saveAll();
                getLogger().info("✓ 모든 데이터가 저장되었습니다.");
            }

            getLogger().info("════════════════════════════════════════");
            getLogger().info("  QuestCore 플러그인이 비활성화되었습니다.");
            getLogger().info("════════════════════════════════════════");
        } catch (Exception e) {
            getLogger().warning("플러그인 종료 중 오류 발생: " + e.getMessage());
        }
    }

    // ============ Initialization Methods ============

    /**
     * 설정 파일 초기화
     */
    private void initializeConfiguration() {
        if (! getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        saveDefaultConfig();
        reloadConfig();

        getLogger().info("설정 파일 위치: " + getDataFolder(). getAbsolutePath());
    }

    /**
     * 유틸리티 초기화
     */
    private void initializeUtilities() {
        configUtil = new ConfigUtil(this);
        fileUtil = new FileUtil(this);
        questLoader = new QuestLoader(this);
    }

    /**
     * 데이터 관리자 초기화
     */
    private void initializeDataManager() {
        questDataManager = new QuestDataManager(this);
        questDataManager.loadAll();
    }

    /**
     * API 초기화
     */
    private void initializeAPI() {
        QuestAPI.initialize(this);
    }

    /**
     * 이벤트 리스너 등록
     */
    private void registerListeners() {
        // 퀨스트 목표 리스너
        questObjectiveListener = new QuestObjectiveListener(questDataManager);
        getServer().getPluginManager().registerEvents(questObjectiveListener, this);

        // 퀨스트 이벤트 리스너
        questEventListener = new QuestEventListener(questDataManager);
        getServer().getPluginManager().registerEvents(questEventListener, this);

        // 플레이어 리스너
        playerListener = new PlayerListener(questDataManager);
        getServer().getPluginManager(). registerEvents(playerListener, this);

        // NPC 리스너
        npcListener = new NPCListener(questDataManager);
        getServer().getPluginManager().registerEvents(npcListener, this);
    }

    /**
     * 명령어 등록
     */
    private void registerCommands() {
        commandManager = new CommandManager(this, questDataManager);
        commandManager. registerAllCommands();
    }

    /**
     * 백그라운드 태스크 시작
     */
    private void startTasks() {
        // 자동 저장 태스크
        autoSaveTask = new AutoSaveTask(questDataManager, this);
        if (configUtil.isAutoSaveEnabled()) {
            autoSaveTask.start();
        }

        // 추적기 업데이트 태스크
        trackerUpdateTask = new TrackerUpdateTask(questDataManager, this);
        trackerUpdateTask.start();

        // 시간 제한 확인 태스크
        timeLimitCheckTask = new TimeLimitCheckTask(questDataManager, this);
        timeLimitCheckTask.start();

        // 일일 초기화 태스크
        dailyResetTask = new DailyResetTask(questDataManager, this);
        if (configUtil.isDailyResetEnabled()) {
            dailyResetTask.start();
        }

        // 주간 초기화 태스크
        weeklyResetTask = new WeeklyResetTask(questDataManager, this);
        if (configUtil.isWeeklyResetEnabled()) {
            weeklyResetTask.start();
        }
    }

    /**
     * 백그라운드 태스크 중지
     */
    private void stopTasks() {
        if (autoSaveTask != null) {
            autoSaveTask.stop();
        }
        if (trackerUpdateTask != null) {
            trackerUpdateTask. stop();
        }
        if (timeLimitCheckTask != null) {
            timeLimitCheckTask.stop();
        }
        if (dailyResetTask != null) {
            dailyResetTask.stop();
        }
        if (weeklyResetTask != null) {
            weeklyResetTask.stop();
        }
    }

    // ============ Status & Information ============

    /**
     * 플러그인 상태 정보 출력
     */
    public void printStatus() {
        getLogger().info(getPluginInfo());
        
        if (questDataManager != null) {
            getLogger().info(questDataManager.getStatusInfo());
        }
        
        if (autoSaveTask != null) {
            getLogger().info(autoSaveTask.getStatusInfo());
        }
        
        if (trackerUpdateTask != null) {
            getLogger().info(trackerUpdateTask. getStatusInfo());
        }
        
        if (commandManager != null) {
            getLogger().info(commandManager.getStatusInfo());
        }
    }

    /**
     * 플러그인 정보 조회
     */
    public String getPluginInfo() {
        return "§6QuestCore v" + getDescription().getVersion() + 
               "§r - " + getDescription().getDescription();
    }

    // ============ Reload ============

    /**
     * 플러그인 리로드
     */
    public boolean reloadPlugin() {
        try {
            getLogger().info("플러그인 리로드 중...");

            // 설정 파일 다시 로드
            reloadConfig();
            configUtil.reloadConfig();

            // 데이터 다시 로드
            if (questDataManager != null) {
                questDataManager.reloadAll();
            }

            getLogger().info("✓ 플러그인이 리로드되었습니다.");
            return true;
        } catch (Exception e) {
            getLogger().warning("플러그인 리로드 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    // ============ Getters ============

    /**
     * 플러그인 인스턴스 반환
     */
    public static QuestCore getInstance() {
        return instance;
    }

    /**
     * 데이터 관리자 반환
     */
    public QuestDataManager getQuestDataManager() {
        return questDataManager;
    }

    /**
     * 명령어 관리자 반환
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * 설정 유틸리티 반환
     */
    public ConfigUtil getConfigUtil() {
        return configUtil;
    }

    /**
     * 파일 유틸리티 반환
     */
    public FileUtil getFileUtil() {
        return fileUtil;
    }

    /**
     * 퀨스트 로더 반환
     */
    public QuestLoader getQuestLoader() {
        return questLoader;
    }

    /**
     * 자동 저장 태스크 반환
     */
    public AutoSaveTask getAutoSaveTask() {
        return autoSaveTask;
    }

    /**
     * 추적기 업데이트 태스크 반환
     */
    public TrackerUpdateTask getTrackerUpdateTask() {
        return trackerUpdateTask;
    }

    /**
     * 시간 제한 확인 태스크 반환
     */
    public TimeLimitCheckTask getTimeLimitCheckTask() {
        return timeLimitCheckTask;
    }

    /**
     * 일일 초기화 태스크 반환
     */
    public DailyResetTask getDailyResetTask() {
        return dailyResetTask;
    }

    /**
     * 주간 초기화 태스크 반환
     */
    public WeeklyResetTask getWeeklyResetTask() {
        return weeklyResetTask;
    }

    /**
     * 퀨스트 목표 리스너 반환
     */
    public QuestObjectiveListener getQuestObjectiveListener() {
        return questObjectiveListener;
    }

    /**
     * 퀨스트 이벤트 리스너 반환
     */
    public QuestEventListener getQuestEventListener() {
        return questEventListener;
    }

    /**
     * 플레이어 리스너 반환
     */
    public PlayerListener getPlayerListener() {
        return playerListener;
    }

    /**
     * NPC 리스너 반환
     */
    public NPCListener getNPCListener() {
        return npcListener;
    }
}