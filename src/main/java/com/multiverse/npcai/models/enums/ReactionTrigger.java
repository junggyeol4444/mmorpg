package com.multiverse.npcai.models.enums;

/**
 * NPC 리액션이 발생하는 트리거 조건을 정의하는 열거형
 */
public enum ReactionTrigger {
    ON_TALK,
    ON_ATTACKED,
    ON_TRADE,
    ON_HELP,
    ON_GIFT,
    ON_QUEST_COMPLETE,
    ON_LEVEL_UP,
    ON_REPUTATION_CHANGE,
    CUSTOM
}