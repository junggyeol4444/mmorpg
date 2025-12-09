package com.multiverse.party.models.enums;

/**
 * 파티 공개 설정 (공개/비공개/초대만 가능)
 */
public enum PartyPrivacy {
    PUBLIC,        // 누구나 참여(공개)
    PRIVATE,       // 비공개, 검색 불가
    INVITE_ONLY    // 초대된 경우만 참여 가능
}