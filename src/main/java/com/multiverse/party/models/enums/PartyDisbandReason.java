package com.multiverse.party.models.enums;

/**
 * 파티 해체 이유
 */
public enum PartyDisbandReason {
    LEADER_LEFT,        // 리더 탈퇴
    NO_MEMBERS,         // 멤버 전원 탈퇴
    EXPLICIT_COMMAND,   // 명령어/UI 통한 해체
    KICKED,             // 모든 멤버 추방됨
    TIMEOUT,            // 비활동 시간 초과
    OTHER               // 기타
}