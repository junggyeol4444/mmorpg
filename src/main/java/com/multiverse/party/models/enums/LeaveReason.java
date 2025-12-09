package com.multiverse.party.models.enums;

/**
 * 파티 탈퇴 이유
 */
public enum LeaveReason {
    VOLUNTARY,          // 자진 탈퇴
    KICKED,             // 추방
    PARTY_DISBAND,      // 파티 해체에 따른 탈퇴
    INACTIVITY,         // 비활동/자동
    OTHER               // 기타
}