package com.multiverse.party. events;

import com.multiverse. party.models.Party;
import org.bukkit.Location;
import org. bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org. bukkit.event. Cancellable;
import org.bukkit. event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java. util.List;
import java. util.Map;
import java.util. UUID;

public class PartyExpGainEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Party party;
    private final ExpSource source;
    private long baseExp;
    private double bonusMultiplier;
    private final Location location;
    private final UUID triggerPlayer;
    private final String sourceId;
    private final Map<UUID, Long> expDistribution;
    private boolean cancelled;
    private String cancelReason;

    public PartyExpGainEvent(Party party, long baseExp, ExpSource source, 
                              Location location, UUID triggerPlayer) {
        this(party, baseExp, source, location, triggerPlayer, null);
    }

    public PartyExpGainEvent(Party party, long baseExp, ExpSource source,
                              Location location, UUID triggerPlayer, String sourceId) {
        this. party = party;
        this.baseExp = baseExp;
        this.source = source;
        this.bonusMultiplier = 1.0;
        this.location = location;
        this.triggerPlayer = triggerPlayer;
        this.sourceId = sourceId;
        this.expDistribution = new HashMap<>();
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
        return party.getPartyName();
    }

    /**
     * 기본 경험치 반환
     * @return 기본 경험치
     */
    public long getBaseExp() {
        return baseExp;
    }

    /**
     * 기본 경험치 설정
     * @param baseExp 기본 경험치
     */
    public void setBaseExp(long baseExp) {
        this.baseExp = Math.max(0, baseExp);
    }

    /**
     * 보너스 배율 반환
     * @return 보너스 배율
     */
    public double getBonusMultiplier() {
        return bonusMultiplier;
    }

    /**
     * 보너스 배율 설정
     * @param multiplier 보너스 배율
     */
    public void setBonusMultiplier(double multiplier) {
        this.bonusMultiplier = Math.max(0, multiplier);
    }

    /**
     * 보너스 배율 추가
     * @param additionalMultiplier 추가 배율
     */
    public void addBonusMultiplier(double additionalMultiplier) {
        this.bonusMultiplier += additionalMultiplier;
    }

    /**
     * 최종 경험치 반환 (기본 * 보너스)
     * @return 최종 경험치
     */
    public long getFinalExp() {
        return Math.round(baseExp * bonusMultiplier);
    }

    /**
     * 경험치 소스 반환
     * @return 경험치 소스
     */
    public ExpSource getSource() {
        return source;
    }

    /**
     * 소스 ID 반환 (던전 ID, 퀘스트 ID 등)
     * @return 소스 ID
     */
    public String getSourceId() {
        return sourceId;
    }

    /**
     * 위치 반환
     * @return 위치
     */
    public Location getLocation() {
        return location;
    }

    /**
     * 경험치를 발생시킨 플레이어 UUID 반환
     * @return 트리거 플레이어 UUID
     */
    public UUID getTriggerPlayer() {
        return triggerPlayer;
    }

    /**
     * 몬스터 처치로 인한 경험치인지 확인
     * @return 몬스터 처치 여부
     */
    public boolean isMonsterKill() {
        return source == ExpSource.MONSTER_KILL;
    }

    /**
     * 보스 처치로 인한 경험치인지 확인
     * @return 보스 처치 여부
     */
    public boolean isBossKill() {
        return source == ExpSource. BOSS_KILL;
    }

    /**
     * 던전 완료로 인한 경험치인지 확인
     * @return 던전 완료 여부
     */
    public boolean isDungeonClear() {
        return source == ExpSource.DUNGEON_CLEAR;
    }

    /**
     * 퀘스트 완료로 인한 경험치인지 확인
     * @return 퀘스트 완료 여부
     */
    public boolean isQuestComplete() {
        return source == ExpSource. QUEST_COMPLETE;
    }

    /**
     * 경험치 분배 맵 반환
     * @return 플레이어별 경험치 분배 맵
     */
    public Map<UUID, Long> getExpDistribution() {
        return expDistribution;
    }

    /**
     * 특정 플레이어의 분배 경험치 설정
     * @param playerUUID 플레이어 UUID
     * @param exp 경험치
     */
    public void setPlayerExp(UUID playerUUID, long exp) {
        expDistribution.put(playerUUID, Math.max(0, exp));
    }

    /**
     * 특정 플레이어의 분배 경험치 반환
     * @param playerUUID 플레이어 UUID
     * @return 경험치
     */
    public long getPlayerExp(UUID playerUUID) {
        return expDistribution.getOrDefault(playerUUID, 0L);
    }

    /**
     * 파티 멤버 목록 반환
     * @return 멤버 UUID 목록
     */
    public List<UUID> getMembers() {
        return party.getMembers();
    }

    /**
     * 파티 멤버 수 반환
     * @return 멤버 수
     */
    public int getMemberCount() {
        return party.getMembers().size();
    }

    /**
     * 파티 레벨 반환
     * @return 파티 레벨
     */
    public int getPartyLevel() {
        return party.getPartyLevel() != null ? party.getPartyLevel().getLevel() : 1;
    }

    /**
     * 현재 파티 경험치 반환
     * @return 현재 경험치
     */
    public long getCurrentPartyExp() {
        return party.getPartyLevel() != null ? party.getPartyLevel().getExperience() : 0;
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
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * 사유와 함께 이벤트 취소
     * @param cancel 취소 여부
     * @param reason 취소 사유
     */
    public void setCancelled(boolean cancel, String reason) {
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
     * 경험치 소스 열거형
     */
    public enum ExpSource {
        /** 몬스터 처치 */
        MONSTER_KILL,
        /** 보스 처치 */
        BOSS_KILL,
        /** 던전 클리어 */
        DUNGEON_CLEAR,
        /** 퀘스트 완료 */
        QUEST_COMPLETE,
        /** 파티 활동 (함께 플레이) */
        PARTY_ACTIVITY,
        /** 관리자 지급 */
        ADMIN_GRANT,
        /** 이벤트 보너스 */
        EVENT_BONUS,
        /** 기타 */
        OTHER
    }
}