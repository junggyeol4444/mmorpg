package com.multiverse.pet.model. breeding;

import java.util.*;

/**
 * 펫 교배 데이터 클래스
 * 펫 교배의 진행 상태와 결과를 정의
 */
public class PetBreeding {

    // 기본 정보
    private UUID breedingId;
    private UUID ownerId;

    // 부모 펫
    private UUID parent1Id;
    private UUID parent2Id;
    private String parent1SpeciesId;
    private String parent2SpeciesId;

    // 진행 상태
    private BreedingStatus status;
    private long startTime;
    private long endTime;
    private long duration;          // 밀리초

    // 결과
    private UUID offspringId;
    private String offspringSpeciesId;
    private boolean isMutation;
    private String mutationType;

    // 유전 정보
    private PetGenetics resultGenetics;

    // 비용
    private double goldCost;
    private List<ItemCost> itemCosts;

    // 추가 정보
    private int attemptCount;       // 교배 시도 횟수
    private String failReason;      // 실패 이유

    /**
     * 기본 생성자
     */
    public PetBreeding() {
        this.breedingId = UUID.randomUUID();
        this.status = BreedingStatus.IN_PROGRESS;
        this.itemCosts = new ArrayList<>();
        this.attemptCount = 0;
        this. isMutation = false;
    }

    /**
     * 전체 생성자
     */
    public PetBreeding(UUID ownerId, UUID parent1Id, UUID parent2Id, long duration) {
        this();
        this. ownerId = ownerId;
        this.parent1Id = parent1Id;
        this.parent2Id = parent2Id;
        this. duration = duration;
        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + duration;
    }

    // ===== 진행 상태 관련 메서드 =====

    /**
     * 교배 완료 여부 확인
     */
    public boolean isCompleted() {
        return status == BreedingStatus.COMPLETED;
    }

    /**
     * 교배 진행 중 여부 확인
     */
    public boolean isInProgress() {
        return status == BreedingStatus.IN_PROGRESS;
    }

    /**
     * 교배 취소됨 여부 확인
     */
    public boolean isCancelled() {
        return status == BreedingStatus.CANCELLED;
    }

    /**
     * 교배 실패 여부 확인
     */
    public boolean isFailed() {
        return status == BreedingStatus. FAILED;
    }

    /**
     * 교배 시간 완료 여부 확인
     */
    public boolean isTimeCompleted() {
        return System.currentTimeMillis() >= endTime;
    }

    /**
     * 남은 시간 (밀리초)
     */
    public long getRemainingTime() {
        if (status != BreedingStatus.IN_PROGRESS) return 0;
        long remaining = endTime - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    /**
     * 남은 시간 (초)
     */
    public int getRemainingSeconds() {
        return (int) (getRemainingTime() / 1000);
    }

    /**
     * 남은 시간 포맷팅
     */
    public String getRemainingTimeFormatted() {
        long remaining = getRemainingTime();
        if (remaining <= 0) return "완료";

        long seconds = remaining / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if (hours > 0) {
            return String.format("%d시간 %d분", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d분 %d초", minutes, seconds % 60);
        } else {
            return String. format("%d초", seconds);
        }
    }

    /**
     * 진행률 (0-100)
     */
    public double getProgress() {
        if (duration <= 0) return 100;
        long elapsed = System.currentTimeMillis() - startTime;
        return Math.min(100, (elapsed / (double) duration) * 100);
    }

    /**
     * 교배 완료 처리
     */
    public void complete(UUID offspringId, PetGenetics genetics) {
        this.status = BreedingStatus.COMPLETED;
        this.offspringId = offspringId;
        this.resultGenetics = genetics;
        this.isMutation = genetics != null && genetics.isMutant();
        if (this.isMutation) {
            this. mutationType = genetics. getMutationType();
        }
    }

    /**
     * 교배 취소 처리
     */
    public void cancel() {
        this.status = BreedingStatus.CANCELLED;
    }

    /**
     * 교배 실패 처리
     */
    public void fail(String reason) {
        this.status = BreedingStatus.FAILED;
        this.failReason = reason;
    }

    // ===== 시도 횟수 관련 =====

    /**
     * 시도 횟수 증가
     */
    public void incrementAttempt() {
        this.attemptCount++;
    }

    /**
     * 최대 시도 횟수 도달 여부
     *
     * @param maxAttempts 최대 시도 횟수
     */
    public boolean hasReachedMaxAttempts(int maxAttempts) {
        return attemptCount >= maxAttempts;
    }

    // ===== 비용 관련 =====

    /**
     * 아이템 비용 추가
     */
    public void addItemCost(String itemId, int amount) {
        itemCosts.add(new ItemCost(itemId, amount));
    }

    /**
     * 총 비용 계산 가능 (외부에서 사용)
     */
    public double getTotalGoldCost() {
        return goldCost;
    }

    /**
     * 아이템 비용 목록 반환
     */
    public List<ItemCost> getItemCosts() {
        return Collections.unmodifiableList(itemCosts);
    }

    // ===== 정보 표시 =====

    /**
     * 교배 정보 요약
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("교배 ID: ").append(breedingId).append("\n");
        sb.append("상태: ").append(status. getDisplayName()).append("\n");
        sb.append("부모1:  ").append(parent1SpeciesId).append("\n");
        sb.append("부모2: ").append(parent2SpeciesId).append("\n");

        if (status == BreedingStatus.IN_PROGRESS) {
            sb.append("남은 시간:  ").append(getRemainingTimeFormatted()).append("\n");
            sb.append("진행률: ").append(String.format("%.1f%%", getProgress())).append("\n");
        } else if (status == BreedingStatus.COMPLETED) {
            sb.append("자손:  ").append(offspringSpeciesId).append("\n");
            if (isMutation) {
                sb. append("변이:  ").append(mutationType).append("\n");
            }
        } else if (status == BreedingStatus.FAILED) {
            sb.append("실패 이유: ").append(failReason).append("\n");
        }

        return sb.toString();
    }

    // ===== Getter/Setter =====

    public UUID getBreedingId() {
        return breedingId;
    }

    public void setBreedingId(UUID breedingId) {
        this.breedingId = breedingId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public UUID getParent1Id() {
        return parent1Id;
    }

    public void setParent1Id(UUID parent1Id) {
        this.parent1Id = parent1Id;
    }

    public UUID getParent2Id() {
        return parent2Id;
    }

    public void setParent2Id(UUID parent2Id) {
        this. parent2Id = parent2Id;
    }

    public String getParent1SpeciesId() {
        return parent1SpeciesId;
    }

    public void setParent1SpeciesId(String parent1SpeciesId) {
        this.parent1SpeciesId = parent1SpeciesId;
    }

    public String getParent2SpeciesId() {
        return parent2SpeciesId;
    }

    public void setParent2SpeciesId(String parent2SpeciesId) {
        this.parent2SpeciesId = parent2SpeciesId;
    }

    public BreedingStatus getStatus() {
        return status;
    }

    public void setStatus(BreedingStatus status) {
        this. status = status;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
        this.endTime = startTime + duration;
    }

    public UUID getOffspringId() {
        return offspringId;
    }

    public void setOffspringId(UUID offspringId) {
        this.offspringId = offspringId;
    }

    public String getOffspringSpeciesId() {
        return offspringSpeciesId;
    }

    public void setOffspringSpeciesId(String offspringSpeciesId) {
        this. offspringSpeciesId = offspringSpeciesId;
    }

    public boolean isMutation() {
        return isMutation;
    }

    public void setMutation(boolean mutation) {
        isMutation = mutation;
    }

    public String getMutationType() {
        return mutationType;
    }

    public void setMutationType(String mutationType) {
        this.mutationType = mutationType;
    }

    public PetGenetics getResultGenetics() {
        return resultGenetics;
    }

    public void setResultGenetics(PetGenetics resultGenetics) {
        this.resultGenetics = resultGenetics;
    }

    public double getGoldCost() {
        return goldCost;
    }

    public void setGoldCost(double goldCost) {
        this.goldCost = goldCost;
    }

    public void setItemCosts(List<ItemCost> itemCosts) {
        this. itemCosts = itemCosts;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetBreeding that = (PetBreeding) o;
        return Objects.equals(breedingId, that.breedingId);
    }

    @Override
    public int hashCode() {
        return Objects. hash(breedingId);
    }

    @Override
    public String toString() {
        return "PetBreeding{" +
                "breedingId=" + breedingId +
                ", parent1Id=" + parent1Id +
                ", parent2Id=" + parent2Id +
                ", status=" + status +
                ", progress=" + String.format("%.1f%%", getProgress()) +
                '}';
    }

    // ===== 내부 클래스 =====

    /**
     * 아이템 비용
     */
    public static class ItemCost {
        private String itemId;
        private int amount;

        public ItemCost() {}

        public ItemCost(String itemId, int amount) {
            this.itemId = itemId;
            this.amount = amount;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }
}