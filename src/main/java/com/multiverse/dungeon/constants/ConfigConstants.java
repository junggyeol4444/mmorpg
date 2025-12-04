package com.multiverse.dungeon. constants;

/**
 * config. yml 설정 상수들
 */
public class ConfigConstants {

    // ===== 데이터 저장 =====
    public static final String DATA_TYPE = "data. type";
    public static final String AUTO_SAVE_INTERVAL = "data.auto-save-interval";
    public static final String BACKUP_ENABLED = "data.backup.enabled";

    // ===== 던전 시스템 =====
    public static final String DUNGEONS_ENABLED = "dungeons.enabled";
    public static final String MAX_CONCURRENT_INSTANCES = "dungeons.instance.max-concurrent";
    public static final String CLEANUP_DELAY = "dungeons.instance.cleanup-delay";
    public static final String USE_SLIME_WORLDS = "dungeons.instance.use-slime-worlds";
    
    public static final String ENABLE_LEVEL_SCALING = "dungeons. scaling.enable-level-scaling";
    public static final String ENABLE_PARTY_SCALING = "dungeons. scaling.enable-party-scaling";
    public static final String PARTY_SCALING_PER_PLAYER = "dungeons.scaling.party-scaling-per-player";
    
    public static final String TIME_LIMIT_WARNINGS = "dungeons.time-limit. warnings";

    // ===== 파티 시스템 =====
    public static final String PARTY_ENABLED = "party.enabled";
    public static final String PARTY_MAX_MEMBERS = "party.max-members";
    public static final String PARTY_EXPERIENCE_BONUS = "party.experience-bonus";
    public static final String PARTY_REWARD_BONUS = "party.reward-bonus";
    public static final String PARTY_INVITE_EXPIRE_TIME = "party.invite. expire-time";

    // ===== 보스 시스템 =====
    public static final String BOSSES_ENABLED = "bosses.enabled";
    public static final String MYTHICMOBS_INTEGRATION = "bosses.mythicmobs-integration";
    public static final String PHASE_TRANSITIONS_SHOW_MESSAGE = "bosses.phase-transitions.show-message";
    public static final String PHASE_TRANSITIONS_PLAY_SOUND = "bosses. phase-transitions.play-sound";
    public static final String PHASE_TRANSITIONS_PARTICLE_EFFECTS = "bosses.phase-transitions.particle-effects";
    public static final String SKILLS_CAST_TIME = "bosses.skills.cast-time";
    public static final String SKILLS_SHOW_CAST_BAR = "bosses.skills. show-cast-bar";
    public static final String ENRAGE_ENABLED = "bosses.enrage. enabled";
    public static final String ENRAGE_SHOW_WARNING = "bosses.enrage.show-warning";

    // ===== 보상 시스템 =====
    public static final String REWARDS_ENABLED = "rewards.enabled";
    public static final String REWARDS_DISTRIBUTION_MODE = "rewards.distribution. mode";
    public static final String REWARDS_EXPERIENCE_PARTY_BONUS = "rewards.experience. party-bonus";
    public static final String REWARDS_EXPERIENCE_LEVEL_PENALTY = "rewards.experience.level-difference-penalty";
    public static final String REWARDS_ITEMS_DROP_ON_GROUND = "rewards.items.drop-on-ground";
    public static final String REWARDS_ITEMS_AUTO_PICKUP = "rewards.items.auto-pickup";
    public static final String FIRST_CLEAR_BONUS_MULTIPLIER = "rewards.first-clear.bonus-multiplier";
    public static final String FIRST_CLEAR_TITLE_ENABLED = "rewards.first-clear. title-enabled";

    // ===== 난이도 설정 =====
    public static final String DIFFICULTY_EASY_MOB_HEALTH = "difficulty. EASY.mob-health";
    public static final String DIFFICULTY_EASY_MOB_DAMAGE = "difficulty. EASY.mob-damage";
    public static final String DIFFICULTY_EASY_REWARD = "difficulty.EASY.reward";
    
    public static final String DIFFICULTY_NORMAL_MOB_HEALTH = "difficulty.NORMAL.mob-health";
    public static final String DIFFICULTY_NORMAL_MOB_DAMAGE = "difficulty.NORMAL.mob-damage";
    public static final String DIFFICULTY_NORMAL_REWARD = "difficulty. NORMAL.reward";
    
    public static final String DIFFICULTY_HARD_MOB_HEALTH = "difficulty.HARD.mob-health";
    public static final String DIFFICULTY_HARD_MOB_DAMAGE = "difficulty. HARD.mob-damage";
    public static final String DIFFICULTY_HARD_REWARD = "difficulty. HARD.reward";
    
    public static final String DIFFICULTY_EXTREME_MOB_HEALTH = "difficulty.EXTREME.mob-health";
    public static final String DIFFICULTY_EXTREME_MOB_DAMAGE = "difficulty.EXTREME.mob-damage";
    public static final String DIFFICULTY_EXTREME_REWARD = "difficulty. EXTREME.reward";

    // ===== 랜덤 던전 =====
    public static final String RANDOM_DUNGEONS_ENABLED = "random-dungeons.enabled";
    public static final String RANDOM_GENERATION_MIN_ROOMS = "random-dungeons.generation.min-rooms";
    public static final String RANDOM_GENERATION_MAX_ROOMS = "random-dungeons.generation.max-rooms";
    public static final String RANDOM_SEED_BASED = "random-dungeons.seed-based";

    // ===== 리더보드 =====
    public static final String LEADERBOARD_ENABLED = "leaderboard.enabled";
    public static final String LEADERBOARD_UPDATE_INTERVAL = "leaderboard.update-interval";
    public static final String LEADERBOARD_DISPLAY_TOP = "leaderboard.display-top";
    public static final String LEADERBOARD_RESET_WEEKLY = "leaderboard.reset. weekly";
    public static final String LEADERBOARD_RESET_MONTHLY = "leaderboard.reset.monthly";

    // ===== GUI =====
    public static final String GUI_DUNGEON_BROWSER_TITLE = "gui.dungeon-browser.title";
    public static final String GUI_DUNGEON_BROWSER_ROWS = "gui.dungeon-browser.rows";
    public static final String GUI_PARTY_MENU_TITLE = "gui.party-menu.title";
    public static final String GUI_PARTY_MENU_ROWS = "gui.party-menu.rows";
    public static final String GUI_DIFFICULTY_SELECTOR_TITLE = "gui.difficulty-selector.title";
    public static final String GUI_DIFFICULTY_SELECTOR_ROWS = "gui.difficulty-selector.rows";

    // ===== 성능 =====
    public static final String PERFORMANCE_ASYNC_INSTANCE_CREATION = "performance.async-instance-creation";
    public static final String PERFORMANCE_MOB_SPAWN_DELAY = "performance.mob-spawn-delay";
    public static final String PERFORMANCE_MAX_MOBS_PER_ROOM = "performance.max-mobs-per-room";
}