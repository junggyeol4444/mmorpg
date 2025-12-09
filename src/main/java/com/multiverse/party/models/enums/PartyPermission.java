package com.multiverse.party.models.enums;

/**
 * 파티 역할별 권한 목록
 */
public enum PartyPermission {
    INVITE_MEMBERS,         // 멤버 초대
    KICK_MEMBERS,           // 멤버 추방
    CHANGE_LOOT_SETTINGS,   // 아이템 분배 방식 설정
    USE_PARTY_CHAT,         // 파티 채팅
    USE_PARTY_SKILLS,       // 파티 스킬 사용
    SEND_ANNOUNCEMENT,      // 공지 발송
    VIEW_PARTY_INFO         // 파티 정보 열람
}