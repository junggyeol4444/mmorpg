package com.multiverse.dungeon.constants;

/**
 * messages/ko_KR.yml 메시지 상수들
 */
public class MessageConstants {

    public static final String PREFIX = "messages.prefix";

    // ===== 던전 메시지 =====
    public static final String DUNGEON_ENTERED = "messages.dungeon. entered";
    public static final String DUNGEON_COMPLETED = "messages.dungeon.completed";
    public static final String DUNGEON_FAILED = "messages.dungeon.failed";
    public static final String DUNGEON_TIME_WARNING = "messages.dungeon.time-warning";
    public static final String DUNGEON_TIME_EXPIRED = "messages.dungeon.time-expired";
    public static final String DUNGEON_NOT_FOUND = "messages.dungeon.not-found";
    public static final String DUNGEON_LEVEL_REQUIRED = "messages.dungeon.level-required";
    public static final String DUNGEON_QUEST_REQUIRED = "messages.dungeon.quest-required";
    public static final String DUNGEON_ITEM_LEVEL_REQUIRED = "messages.dungeon.item-level-required";

    // ===== 파티 메시지 =====
    public static final String PARTY_CREATED = "messages.party.created";
    public static final String PARTY_DISBANDED = "messages.party.disbanded";
    public static final String PARTY_INVITED = "messages.party.invited";
    public static final String PARTY_JOINED = "messages.party.joined";
    public static final String PARTY_LEFT = "messages.party.left";
    public static final String PARTY_KICKED = "messages.party.kicked";
    public static final String PARTY_FULL = "messages.party.full";
    public static final String PARTY_NOT_FOUND = "messages.party.not-found";
    public static final String PARTY_NOT_MEMBER = "messages.party.not-member";
    public static final String PARTY_NOT_LEADER = "messages.party.not-leader";
    public static final String PARTY_INVITE_EXPIRED = "messages.party.invite-expired";
    public static final String PARTY_INVITE_ALREADY_SENT = "messages.party.invite-already-sent";

    // ===== 보스 메시지 =====
    public static final String BOSS_SPAWNED = "messages.boss.spawned";
    public static final String BOSS_PHASE = "messages.boss.phase";
    public static final String BOSS_SKILL_CAST = "messages.boss. skill-cast";
    public static final String BOSS_DEFEATED = "messages.boss.defeated";
    public static final String BOSS_ENRAGE = "messages.boss.enrage";

    // ===== 보상 메시지 =====
    public static final String REWARD_RECEIVED = "messages.reward.received";
    public static final String REWARD_DUNGEON_POINTS = "messages.reward.dungeon-points";
    public static final String REWARD_EXPERIENCE = "messages.reward.experience";
    public static final String REWARD_GOLD = "messages.reward.gold";

    // ===== 제한 메시지 =====
    public static final String LIMIT_DAILY_REACHED = "messages.limit.daily-reached";
    public static final String LIMIT_WEEKLY_REACHED = "messages.limit.weekly-reached";

    // ===== 에러 메시지 =====
    public static final String ERROR_COMMAND_USAGE = "messages.error.command-usage";
    public static final String ERROR_NO_PERMISSION = "messages.error.no-permission";
    public static final String ERROR_PLAYER_NOT_FOUND = "messages.error.player-not-found";
    public static final String ERROR_NOT_IN_DUNGEON = "messages.error.not-in-dungeon";
    public static final String ERROR_NOT_IN_PARTY = "messages.error.not-in-party";
    public static final String ERROR_ALREADY_IN_PARTY = "messages.error.already-in-party";
}