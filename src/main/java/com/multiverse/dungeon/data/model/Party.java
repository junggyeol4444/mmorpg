package com.multiverse.dungeon.data.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 파티 데이터 클래스
 */
public class Party {

    private UUID partyId; // 파티 ID
    private UUID leaderId; // 리더 ID
    private List<UUID> members; // 멤버 ID 목록
    
    // 설정
    private int maxMembers; // 최대 멤버 수
    private boolean isOpen; // 공개 파티 여부
    
    // 던전
    private UUID currentInstanceId; // 현재 인스턴스 ID
    
    // 생성 시간
    private long createdTime; // 생성 시간 (밀리초)

    /**
     * 생성자
     */
    public Party(UUID partyId, UUID leaderId) {
        this.partyId = partyId;
        this.leaderId = leaderId;
        this.members = new ArrayList<>();
        this.members.add(leaderId); // 리더를 멤버에 추가
        this.maxMembers = 5;
        this.isOpen = true;
        this.currentInstanceId = null;
        this.createdTime = System.currentTimeMillis();
    }

    /**
     * 기본 생성자
     */
    public Party() {
        this(UUID.randomUUID(), UUID.randomUUID());
    }

    // ===== Getters & Setters =====

    public UUID getPartyId() {
        return partyId;
    }

    public void setPartyId(UUID partyId) {
        this.partyId = partyId;
    }

    public UUID getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(UUID leaderId) {
        this.leaderId = leaderId;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public void setMembers(List<UUID> members) {
        this.members = members != null ? members : new ArrayList<>();
    }

    public void addMember(UUID memberId) {
        if (! this.members.contains(memberId)) {
            this.members.add(memberId);
        }
    }

    public void removeMember(UUID memberId) {
        this.members.remove(memberId);
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = Math.max(1, maxMembers);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public UUID getCurrentInstanceId() {
        return currentInstanceId;
    }

    public void setCurrentInstanceId(UUID currentInstanceId) {
        this. currentInstanceId = currentInstanceId;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    // ===== 비즈니스 로직 =====

    /**
     * 현재 멤버 수
     *
     * @return 멤버 수
     */
    public int getMemberCount() {
        return members.size();
    }

    /**
     * 파티가 만석인지 확인
     *
     * @return 만석이면 true
     */
    public boolean isFull() {
        return members.size() >= maxMembers;
    }

    /**
     * 파티에 멤버를 추가할 수 있는지 확인
     *
     * @return 추가 가능하면 true
     */
    public boolean canAddMember() {
        return ! isFull();
    }

    /**
     * 멤버가 파티에 속해있는지 확인
     *
     * @param memberId 멤버 ID
     * @return 속해있으면 true
     */
    public boolean hasMember(UUID memberId) {
        return members.contains(memberId);
    }

    /**
     * 플레이어가 파티 리더인지 확인
     *
     * @param playerId 플레이어 ID
     * @return 리더이면 true
     */
    public boolean isLeader(UUID playerId) {
        return leaderId.equals(playerId);
    }

    /**
     * 파티가 던전에 진입 중인지 확인
     *
     * @return 진입 중이면 true
     */
    public boolean isInDungeon() {
        return currentInstanceId != null;
    }

    /**
     * 남은 모집 인원
     *
     * @return 남은 인원 수
     */
    public int getRemainingSlots() {
        return Math.max(0, maxMembers - members.size());
    }

    /**
     * 파티 해체 (모든 멤버 제거)
     */
    public void disband() {
        members.clear();
        leaderId = null;
        currentInstanceId = null;
    }

    /**
     * 리더 위임
     *
     * @param newLeaderId 새 리더 ID
     * @return 성공하면 true
     */
    public boolean promoteLeader(UUID newLeaderId) {
        if (! hasMember(newLeaderId)) {
            return false;
        }
        this.leaderId = newLeaderId;
        return true;
    }

    /**
     * 파티 생성 후 경과 시간 (분)
     *
     * @return 경과 시간
     */
    public long getAgeInMinutes() {
        return (System.currentTimeMillis() - createdTime) / 60000;
    }

    @Override
    public String toString() {
        return "Party{" +
                "partyId=" + partyId +
                ", leaderId=" + leaderId +
                ", members=" + members.size() +
                ", maxMembers=" + maxMembers +
                ", isOpen=" + isOpen +
                ", inDungeon=" + isInDungeon() +
                '}';
    }
}