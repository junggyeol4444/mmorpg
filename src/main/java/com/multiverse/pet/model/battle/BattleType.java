package com.multiverse.pet.model. battle;

/**
 * 대결 타입 열거형
 * 펫 대결의 종류를 정의
 */
public enum BattleType {

    /**
     * 친선전
     * - 순위 영향 없음
     * - 보상 감소
     * - 자유로운 대결
     */
    FRIENDLY("친선전", "&a", "순위에 영향이 없는 자유로운 대결입니다.",
             false, 0.5, 0, 50),

    /**
     * 랭킹전
     * - 순위 영향 있음
     * - 일반 보상
     * - 레이팅 변동
     */
    RANKED("랭킹전", "&e", "순위에 영향을 주는 경쟁 대결입니다.",
           true, 1.0, 25, 100),

    /**
     * 토너먼트
     * - 대회 형식
     * - 높은 보상
     * - 특별 보상
     */
    TOURNAMENT("토너먼트", "&6", "토너먼트 대회 대결입니다.",
               true, 2.0, 50, 200),

    /**
     * 연습전
     * - AI 대결
     * - 보상 없음
     * - 연습 목적
     */
    PRACTICE("연습전", "&7", "AI와의 연습 대결입니다.",
             false, 0.25, 0, 25),

    /**
     * 도전전
     * - 특정 조건 도전
     * - 조건 충족 시 보상
     * - 일일 제한
     */
    CHALLENGE("도전전", "&b", "특별 조건을 달성하는 도전 대결입니다.",
              false, 1.5, 0, 150),

    /**
     * 길드전
     * - 길드 대항전
     * - 길드 포인트 획득
     * - 팀 보상
     */
    GUILD("길드전", "&5", "길드 간의 대항 대결입니다.",
          true, 1.5, 30, 120),

    /**
     * 시즌전
     * - 시즌 한정
     * - 시즌 보상
     * - 특별 랭킹
     */
    SEASONAL("시즌전", "&c", "시즌 한정 특별 대결입니다.",
             true, 2.5, 40, 250);

    private final String displayName;
    private final String colorCode;
    private final String description;
    private final boolean affectsRanking;
    private final double rewardMultiplier;
    private final int baseRatingChange;
    private final int baseExpReward;

    /**
     * BattleType 생성자
     *
     * @param displayName 표시 이름
     * @param colorCode 색상 코드
     * @param description 설명
     * @param affectsRanking 랭킹 영향 여부
     * @param rewardMultiplier 보상 배율
     * @param baseRatingChange 기본 레이팅 변동
     * @param baseExpReward 기본 경험치 보상
     */
    BattleType(String displayName, String colorCode, String description,
               boolean affectsRanking, double rewardMultiplier,
               int baseRatingChange, int baseExpReward) {
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.description = description;
        this.affectsRanking = affectsRanking;
        this. rewardMultiplier = rewardMultiplier;
        this.baseRatingChange = baseRatingChange;
        this. baseExpReward = baseExpReward;
    }

    /**
     * 표시 이름 반환
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 색상 코드 반환
     */
    public String getColorCode() {
        return colorCode;
    }

    /**
     * 색상 적용된 이름 반환
     */
    public String getColoredName() {
        return colorCode + displayName;
    }

    /**
     * 설명 반환
     */
    public String getDescription() {
        return description;
    }

    /**
     * 랭킹 영향 여부 반환
     */
    public boolean isAffectsRanking() {
        return affectsRanking;
    }

    /**
     * 보상 배율 반환
     */
    public double getRewardMultiplier() {
        return rewardMultiplier;
    }

    /**
     * 기본 레이팅 변동 반환
     */
    public int getBaseRatingChange() {
        return baseRatingChange;
    }

    /**
     * 기본 경험치 보상 반환
     */
    public int getBaseExpReward() {
        return baseExpReward;
    }

    /**
     * 친선전인지 확인
     */
    public boolean isFriendly() {
        return this == FRIENDLY;
    }

    /**
     * 랭킹전인지 확인
     */
    public boolean isRanked() {
        return this == RANKED;
    }

    /**
     * 토너먼트인지 확인
     */
    public boolean isTournament() {
        return this == TOURNAMENT;
    }

    /**
     * 연습전인지 확인
     */
    public boolean isPractice() {
        return this == PRACTICE;
    }

    /**
     * 도전전인지 확인
     */
    public boolean isChallenge() {
        return this == CHALLENGE;
    }

    /**
     * 길드전인지 확인
     */
    public boolean isGuild() {
        return this == GUILD;
    }

    /**
     * 시즌전인지 확인
     */
    public boolean isSeasonal() {
        return this == SEASONAL;
    }

    /**
     * AI 대결인지 확인
     */
    public boolean isAIBattle() {
        return this == PRACTICE;
    }

    /**
     * PvP 대결인지 확인
     */
    public boolean isPvP() {
        return this != PRACTICE;
    }

    /**
     * 경쟁 대결인지 확인
     */
    public boolean isCompetitive() {
        return this == RANKED || this == TOURNAMENT || 
               this == GUILD || this == SEASONAL;
    }

    /**
     * 일일 제한 여부
     */
    public boolean hasDailyLimit() {
        return this == CHALLENGE || this == RANKED;
    }

    /**
     * 일일 제한 횟수 반환
     */
    public int getDailyLimit() {
        switch (this) {
            case RANKED:
                return 20;
            case CHALLENGE:
                return 3;
            case TOURNAMENT:
                return -1; // 토너먼트는 별도 관리
            default:
                return -1; // 무제한
        }
    }

    /**
     * 입장료 반환
     */
    public double getEntryFee() {
        switch (this) {
            case RANKED:
                return 100;
            case TOURNAMENT:
                return 500;
            case CHALLENGE:
                return 200;
            case GUILD:
                return 150;
            case SEASONAL: 
                return 300;
            default: 
                return 0;
        }
    }

    /**
     * 최소 펫 레벨 반환
     */
    public int getMinPetLevel() {
        switch (this) {
            case RANKED:
                return 10;
            case TOURNAMENT:
                return 20;
            case CHALLENGE:
                return 15;
            case GUILD:
                return 15;
            case SEASONAL:
                return 25;
            default: 
                return 1;
        }
    }

    /**
     * 턴 제한 시간 반환 (초)
     */
    public int getTurnTimeLimit() {
        switch (this) {
            case RANKED:
                return 30;
            case TOURNAMENT:
                return 45;
            case CHALLENGE:
                return 60;
            case PRACTICE:
                return 120; // 연습은 넉넉하게
            default:
                return 30;
        }
    }

    /**
     * 최대 턴 수 반환
     */
    public int getMaxTurns() {
        switch (this) {
            case RANKED:
                return 50;
            case TOURNAMENT:
                return 100;
            case CHALLENGE: 
                return 30;
            case PRACTICE:
                return 999;
            default: 
                return 50;
        }
    }

    /**
     * 관전 허용 여부
     */
    public boolean allowsSpectators() {
        switch (this) {
            case TOURNAMENT:
                return true;
            case GUILD:
                return true;
            case SEASONAL:
                return true;
            case RANKED:
                return true;
            default:
                return false;
        }
    }

    /**
     * 대결 아이콘 Material 반환
     */
    public String getIconMaterial() {
        switch (this) {
            case FRIENDLY:
                return "WOODEN_SWORD";
            case RANKED: 
                return "IRON_SWORD";
            case TOURNAMENT: 
                return "DIAMOND_SWORD";
            case PRACTICE:
                return "STICK";
            case CHALLENGE:
                return "GOLDEN_SWORD";
            case GUILD: 
                return "NETHERITE_SWORD";
            case SEASONAL: 
                return "TRIDENT";
            default:
                return "IRON_SWORD";
        }
    }

    /**
     * 승리 메시지 반환
     */
    public String getWinMessage() {
        switch (this) {
            case FRIENDLY: 
                return "&a친선 대결에서 승리했습니다!";
            case RANKED:
                return "&e랭킹 대결에서 승리했습니다!  레이팅이 상승합니다! ";
            case TOURNAMENT:
                return "&6토너먼트 대결에서 승리했습니다!  다음 라운드로 진출합니다!";
            case PRACTICE:
                return "&7연습 대결을 완료했습니다. ";
            case CHALLENGE:
                return "&b도전에 성공했습니다!  보상을 획득합니다! ";
            case GUILD:
                return "&5길드전에서 승리했습니다!  길드 포인트를 획득합니다!";
            case SEASONAL: 
                return "&c시즌 대결에서 승리했습니다! 시즌 점수를 획득합니다!";
            default: 
                return "&a대결에서 승리했습니다!";
        }
    }

    /**
     * 패배 메시지 반환
     */
    public String getLoseMessage() {
        switch (this) {
            case FRIENDLY:
                return "&c친선 대결에서 패배했습니다. ";
            case RANKED:
                return "&c랭킹 대결에서 패배했습니다.  레이팅이 하락합니다.";
            case TOURNAMENT: 
                return "&c토너먼트에서 탈락했습니다.";
            case PRACTICE: 
                return "&7연습 대결이 종료되었습니다.";
            case CHALLENGE: 
                return "&c도전에 실패했습니다. ";
            case GUILD:
                return "&c길드전에서 패배했습니다.";
            case SEASONAL: 
                return "&c시즌 대결에서 패배했습니다.";
            default:
                return "&c대결에서 패배했습니다.";
        }
    }

    /**
     * 문자열로 BattleType 찾기
     *
     * @param name 이름
     * @return BattleType 또는 null
     */
    public static BattleType fromString(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        String upperName = name.toUpperCase().trim();

        // 영어 이름으로 찾기
        try {
            return BattleType.valueOf(upperName);
        } catch (IllegalArgumentException ignored) {
        }

        // 한글 이름으로 찾기
        for (BattleType type :  values()) {
            if (type.getDisplayName().equals(name)) {
                return type;
            }
        }

        return null;
    }

    /**
     * 기본 대결 타입 반환
     */
    public static BattleType getDefault() {
        return FRIENDLY;
    }

    /**
     * 모든 대결 타입의 표시 이름 목록 반환
     */
    public static String[] getDisplayNames() {
        BattleType[] types = values();
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].getDisplayName();
        }
        return names;
    }

    /**
     * PvP 대결 타입 목록 반환
     */
    public static BattleType[] getPvPTypes() {
        return new BattleType[]{FRIENDLY, RANKED, TOURNAMENT, CHALLENGE, GUILD, SEASONAL};
    }

    /**
     * 경쟁 대결 타입 목록 반환
     */
    public static BattleType[] getCompetitiveTypes() {
        return new BattleType[]{RANKED, TOURNAMENT, GUILD, SEASONAL};
    }
}