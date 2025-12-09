package com.multiverse.party.events;

import com. multiverse.party. models.Party;
import com.multiverse.party.models.PartyBuff;
import com.multiverse.party.models.enums.BuffType;
import org.bukkit.event.Cancellable;
import org. bukkit.event. Event;
import org.bukkit.event.HandlerList;

import java.util.List;
import java. util.Map;
import java. util.UUID;

public class PartyBuffChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Party party;
    private final PartyBuff buff;
    private final ChangeType changeType;
    private final BuffChangeReason reason;
    private boolean cancelled;
    private String cancelReason;

    public PartyBuffChangeEvent(Party party, PartyBuff buff, ChangeType changeType, BuffChangeReason reason) {
        this. party = party;
        this.buff = buff;
        this.changeType = changeType;
        this.reason = reason;
        this.cancelled = false;
        this.cancelReason = null;
    }

    /**
     * 파티 반환
     * @return 파티
     */
    public Party getParty() {
        return party;
    }

    /**
     * 파티 ID 반환
     * @return 파티 UUID
     */
    public UUID getPartyId() {
        return party.getPartyId();
    }

    /**
     * 파티 이름 반환
     * @return 파티 이름
     */
    public String getPartyName() {
        return party. getPartyName();
    }

    /**
     * 버프 반환
     * @return 파티 버프
     */
    public PartyBuff getBuff() {
        return buff;
    }

    /**
     * 버프 ID 반환
     * @return 버프 ID
     */
    public String getBuffId() {
        return buff.getBuffId();
    }

    /**
     * 버프 이름 반환
     * @return 버프 이름
     */
    public String getBuffName() {
        return buff.getName();
    }

    /**
     * 버프 타입 반환
     * @return 버프 타입
     */
    public BuffType getBuffType() {
        return buff.getType();
    }

    /**
     * 버프 효과 맵 반환
     * @return 효과 맵
     */
    public Map<String, Double> getBuffEffects() {
        return buff.getEffects();
    }

    /**
     * 특정 효과 값 반환
     * @param effectKey 효과 키
     * @return 효과 값
     */
    public double getEffectValue(String effectKey) {
        return buff.getEffects().getOrDefault(effectKey, 0.0);
    }

    /**
     * 변경 타입 반환
     * @return 변경 타입
     */
    public ChangeType getChangeType() {
        return changeType;
    }

    /**
     * 버프 추가인지 확인
     * @return 추가 여부
     */
    public boolean isBuffAdded() {
        return changeType == ChangeType.ADDED;
    }

    /**
     * 버프 제거인지 확인
     * @return 제거 여부
     */
    public boolean isBuffRemoved() {
        return changeType == ChangeType. REMOVED;
    }

    /**
     * 버프 갱신인지 확인
     * @return 갱신 여부
     */
    public boolean isBuffRefreshed() {
        return changeType == ChangeType.REFRESHED;
    }

    /**
     * 버프 만료인지 확인
     * @return 만료 여부
     */
    public boolean isBuffExpired() {
        return changeType == ChangeType. EXPIRED;
    }

    /**
     * 변경 사유 반환
     * @return 변경 사유
     */
    public BuffChangeReason getReason() {
        return reason;
    }

    /**
     * 인원수 변경으로 인한 변경인지 확인
     * @return 인원수 변경 여부
     */
    public boolean isMemberCountChange() {
        return reason == BuffChangeReason.MEMBER_COUNT_CHANGE;
    }

    /**
     * 파티 레벨업으로 인한 변경인지 확인
     * @return 레벨업 여부
     */
    public boolean isLevelUp() {
        return reason == BuffChangeReason.PARTY_LEVEL_UP;
    }

    /**
     * 아이템 사용으로 인한 변경인지 확인
     * @return 아이템 사용 여부
     */
    public boolean isItemUse() {
        return reason == BuffChangeReason.ITEM_USE;
    }

    /**
     * 스킬 사용으로 인한 변경인지 확인
     * @return 스킬 사용 여부
     */
    public boolean isSkillUse() {
        return reason == BuffChangeReason.SKILL_USE;
    }

    /**
     * 버프 지속 시간 반환
     * @return 지속 시간 (초), -1은 영구
     */
    public int getDuration() {
        return buff.getDuration();
    }

    /**
     * 영구 버프인지 확인
     * @return 영구 여부
     */
    public boolean isPermanent() {
        return buff.getDuration() == -1;
    }

    /**
     * 남은 시간 반환 (초)
     * @return 남은 시간
     */
    public long getRemainingSeconds() {
        if (buff.getDuration() == -1) return -1;
        long elapsed = (System.currentTimeMillis() - buff.getStartTime()) / 1000;
        return Math.max(0, buff.getDuration() - elapsed);
    }

    /**
     * 버프 범위 반환
     * @return 버프 범위
     */
    public double getBuffRange() {
        return buff.getRange();
    }

    /**
     * 필요 인원수 반환
     * @return 필요 인원수
     */
    public int getRequiredMembers() {
        return buff.getRequiredMembers();
    }

    /**
     * 필요 파티 레벨 반환
     * @return 필요 파티 레벨
     */
    public int getRequiredPartyLevel() {
        return buff. getRequiredPartyLevel();
    }

    /**
     * 현재 파티 멤버 수 반환
     * @return 멤버 수
     */
    public int getMemberCount() {
        return party.getMembers().size();
    }

    /**
     * 현재 파티 레벨 반환
     * @return 파티 레벨
     */
    public int getPartyLevel() {
        return party.getPartyLevel() != null ? party.getPartyLevel().getLevel() : 1;
    }

    /**
     * 현재 활성 버프 목록 반환
     * @return 활성 버프 목록
     */
    public List<PartyBuff> getActiveBuffs() {
        return party.getActiveBuffs();
    }

    /**
     * 취소 사유 반환
     * @return 취소 사유
     */
    public String getCancelReason() {
        return cancelReason;
    }

    /**
     * 취소 사유 설정
     * @param reason 취소 사유
     */
    public void setCancelReason(String reason) {
        this.cancelReason = reason;
    }

    @Override
    public boolean isCancelled() {
        // 만료는 취소 불가
        if (changeType == ChangeType.EXPIRED) {
            return false;
        }
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        // 만료는 취소 불가
        if (changeType == ChangeType. EXPIRED) {
            return;
        }
        this.cancelled = cancel;
    }

    /**
     * 사유와 함께 이벤트 취소
     * 참고:  EXPIRED 타입의 경우 취소가 무시됨
     * @param cancel 취소 여부
     * @param reason 취소 사유
     */
    public void setCancelled(boolean cancel, String reason) {
        if (changeType == ChangeType.EXPIRED) {
            return;
        }
        this.cancelled = cancel;
        this.cancelReason = reason;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * 버프 변경 타입 열거형
     */
    public enum ChangeType {
        /** 버프 추가 */
        ADDED,
        /** 버프 제거 */
        REMOVED,
        /** 버프 갱신 (시간 연장 등) */
        REFRESHED,
        /** 버프 만료 */
        EXPIRED,
        /** 버프 업그레이드 */
        UPGRADED,
        /** 버프 다운그레이드 */
        DOWNGRADED
    }

    /**
     * 버프 변경 사유 열거형
     */
    public enum BuffChangeReason {
        /** 멤버 수 변경 */
        MEMBER_COUNT_CHANGE,
        /** 파티 레벨업 */
        PARTY_LEVEL_UP,
        /** 아이템 사용 */
        ITEM_USE,
        /** 스킬 사용 */
        SKILL_USE,
        /** 시간 만료 */
        TIME_EXPIRED,
        /** 범위 이탈 */
        OUT_OF_RANGE,
        /** 관리자 조작 */
        ADMIN_ACTION,
        /** 던전 입장 */
        DUNGEON_ENTER,
        /** 던전 퇴장 */
        DUNGEON_EXIT,
        /** 기타 */
        OTHER
    }
}