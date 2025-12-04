package com.multiverse.quest.models;

/**
 * 선택 보상 데이터 모델
 * 플레이어가 여러 보상 중 하나를 선택할 수 있게 합니다.
 */
public class RewardChoice {
    private String choiceId;              // 고유 ID
    private String name;                  // 선택지 이름
    private String description;           // 선택지 설명
    private QuestReward reward;           // 선택 시 지급될 보상
    private int order;                    // 선택지 순서
    private boolean enabled;              // 활성화 여부

    /**
     * 기본 생성자
     */
    public RewardChoice() {
        this.enabled = true;
        this.order = 0;
        this.reward = new QuestReward();
    }

    /**
     * 전체 파라미터 생성자
     */
    public RewardChoice(String choiceId, String name, String description, QuestReward reward) {
        this();
        this.choiceId = choiceId;
        this. name = name;
        this. description = description;
        this. reward = reward;
    }

    // ============ Getters ============

    public String getChoiceId() {
        return choiceId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public QuestReward getReward() {
        return reward;
    }

    public int getOrder() {
        return order;
    }

    public boolean isEnabled() {
        return enabled;
    }

    // ============ Setters ============

    public void setChoiceId(String choiceId) {
        this.choiceId = choiceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReward(QuestReward reward) {
        if (reward != null) {
            this.reward = reward;
        }
    }

    public void setOrder(int order) {
        this.order = Math.max(order, 0);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // ============ Business Logic ============

    /**
     * 선택지가 유효한지 확인
     * @return 유효 여부
     */
    public boolean isValid() {
        return choiceId != null && ! choiceId.isEmpty() &&
               name != null && !name.isEmpty() &&
               reward != null && reward.hasRewards() &&
               enabled;
    }

    /**
     * 선택지 정보 요약
     * @return 요약 문자열
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6"). append(name).append("§r\n");
        
        if (description != null && ! description.isEmpty()) {
            sb.append("§7").append(description).append("\n");
        }
        
        sb.append("\n§e=== 보상 내용 ===§r\n");
        sb.append(reward.getSummary());
        
        return sb.toString();
    }

    /**
     * 선택지의 상세 정보 반환
     * @return 상세 정보 문자열
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb. append("§6=== 선택지: "). append(name).append(" ===§r\n");
        
        if (description != null && ! description.isEmpty()) {
            sb.append("§7설명: §f").append(description).append("\n");
        }
        
        sb.append("§7ID: §f").append(choiceId).append("\n");
        sb.append("§7상태: ").append(enabled ? "§a활성화" : "§c비활성화").append("\n");
        
        sb.append("\n§e=== 보상 정보 ===§r\n");
        sb.append(reward.getDetailedInfo());
        
        return sb.toString();
    }

    /**
     * 보상 가치 계산 (UI 정렬용)
     * 더 많은 보상을 주는 선택지가 더 높은 값을 반환합니다.
     * @return 가치 점수
     */
    public double getRewardValue() {
        double value = 0;
        
        // 경험치 (1경험치 = 0.1점)
        value += reward.getExperience() * 0.1;
        
        // 금액 (1골드 = 1점)
        value += reward.getMoney(). values().stream(). mapToDouble(Double::doubleValue).sum();
        
        // 아이템 (1아이템 = 50점)
        value += reward. getItems().size() * 50;
        
        // 스탯 포인트 (1포인트 = 100점)
        value += reward. getStatPoints() * 100;
        
        // 호감도 (1호감도 = 10점)
        value += reward.getReputation(). values().stream().mapToInt(Integer::intValue).sum() * 10;
        
        // 퀘스트 포인트 (1포인트 = 2점)
        value += reward. getQuestPoints() * 2;
        
        // 칭호 (500점)
        if (reward.getTitle() != null) {
            value += 500;
        }
        
        // 권한 (권한 1개 = 200점)
        value += reward.getPermissions().size() * 200;
        
        return value;
    }

    /**
     * 보상 비교 (더 가치 있는 보상이 큰 값)
     * @param other 비교 대상
     * @return 비교 결과
     */
    public int compareRewardValue(RewardChoice other) {
        if (other == null) return 1;
        return Double.compare(this.getRewardValue(), other.getRewardValue());
    }

    /**
     * 보상이 비어있는지 확인
     * @return 비어있는지 여부
     */
    public boolean isEmpty() {
        return reward == null || reward.isEmpty();
    }

    /**
     * 보상이 있는지 확인
     * @return 보상 여부
     */
    public boolean hasRewards() {
        return reward != null && reward.hasRewards();
    }

    /**
     * GUI에 표시할 아이콘 이름
     * @return 아이콘 이름
     */
    public String getIconName() {
        // 보상 가치에 따라 아이콘 결정
        double value = getRewardValue();
        
        if (value >= 500) {
            return "DIAMOND"; // 매우 좋음
        } else if (value >= 250) {
            return "GOLD_INGOT"; // 좋음
        } else if (value >= 100) {
            return "IRON_INGOT"; // 보통
        } else {
            return "COPPER_INGOT"; // 낮음
        }
    }

    /**
     * GUI에 표시할 색상 코드
     * @return 색상 코드
     */
    public String getColorCode() {
        double value = getRewardValue();
        
        if (value >= 500) {
            return "§b"; // 하늘색 (Legendary)
        } else if (value >= 250) {
            return "§d"; // 자주색 (Epic)
        } else if (value >= 100) {
            return "§6"; // 주황색 (Rare)
        } else {
            return "§f"; // 흰색 (Common)
        }
    }

    /**
     * 선택지 순위 등급 반환
     * @return 등급 (Common, Uncommon, Rare, Epic, Legendary)
     */
    public String getRarity() {
        double value = getRewardValue();
        
        if (value >= 500) {
            return "Legendary";
        } else if (value >= 250) {
            return "Epic";
        } else if (value >= 100) {
            return "Rare";
        } else if (value >= 50) {
            return "Uncommon";
        } else {
            return "Common";
        }
    }

    /**
     * 등급 이모지 반환
     * @return 이모지
     */
    public String getRarityEmoji() {
        switch (getRarity()) {
            case "Legendary":
                return "⭐⭐⭐";
            case "Epic":
                return "⭐⭐";
            case "Rare":
                return "⭐";
            case "Uncommon":
                return "✨";
            case "Common":
            default:
                return "•";
        }
    }

    /**
     * 복사본 생성
     * @return RewardChoice 복사본
     */
    public RewardChoice copy() {
        RewardChoice copy = new RewardChoice();
        copy.choiceId = this.choiceId;
        copy.name = this.name;
        copy.description = this.description;
        copy.reward = this.reward != null ? this.reward.copy() : new QuestReward();
        copy.order = this.order;
        copy.enabled = this.enabled;
        return copy;
    }

    /**
     * 문자열 표현
     * @return 문자열
     */
    @Override
    public String toString() {
        return String.format("RewardChoice{id=%s, name=%s, rarity=%s, value=%.1f}",
                choiceId, name, getRarity(), getRewardValue());
    }

    /**
     * 비교 메서드
     * @param o 비교 대상 객체
     * @return 같은지 여부
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        RewardChoice that = (RewardChoice) o;
        return choiceId != null && choiceId.equals(that.choiceId);
    }

    /**
     * 해시 코드
     * @return 해시 코드
     */
    @Override
    public int hashCode() {
        return choiceId != null ? choiceId.hashCode() : 0;
    }
}