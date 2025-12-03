package com.multiverse.npcai.models.enums;

/**
 * 대화/행동/AI 조건 판별에 사용되는 타입 열거형
 */
public enum ConditionType {
    PLAYER_HAS_ITEM,
    PLAYER_LEVEL_AT_LEAST,
    QUEST_COMPLETED,
    REPUTATION_AT_LEAST,
    GOLD_AT_LEAST,
    NPC_IS_TYPE,
    TIME_OF_DAY,
    WEATHER,
    CUSTOM_FLAG
}