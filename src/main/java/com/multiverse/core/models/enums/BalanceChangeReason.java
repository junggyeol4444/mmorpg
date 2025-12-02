package com.multiverse.core.models.enums;

public enum BalanceChangeReason {
    QUEST_COMPLETE,      // 퀘스트 클리어
    PLAYER_ACTION,       // 플레이어 직접 조작
    PORTAL_USE,          // 포탈 이동
    EVENT,               // 이벤트 효과
    ADMIN,               // 운영자/관리자 명령
    OTHER                // 기타 사유
}