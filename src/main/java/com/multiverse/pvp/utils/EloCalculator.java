package com.multiverse.pvp.utils;

public class EloCalculator {

    // 기본 K-Factor
    private static final int DEFAULT_K_FACTOR = 32;

    /**
     * 레이팅 변화 계산
     * @param winnerRating 승자 레이팅
     * @param loserRating 패자 레이팅
     * @param kFactor K-Factor (변동 폭)
     * @return [승자 레이팅 변화, 패자 레이팅 변화]
     */
    public static int[] calculateRatingChange(int winnerRating, int loserRating, int kFactor) {
        // 예상 승률 계산
        double expectedWinner = getExpectedScore(winnerRating, loserRating);
        double expectedLoser = getExpectedScore(loserRating, winnerRating);

        // 레이팅 변화 계산
        int winnerChange = (int) Math.round(kFactor * (1.0 - expectedWinner));
        int loserChange = (int) Math.round(kFactor * (0.0 - expectedLoser));

        // 최소 변화량 보장
        if (winnerChange < 1) {
            winnerChange = 1;
        }
        if (loserChange > -1) {
            loserChange = -1;
        }

        return new int[]{winnerChange, loserChange};
    }

    /**
     * 기본 K-Factor로 레이팅 변화 계산
     */
    public static int[] calculateRatingChange(int winnerRating, int loserRating) {
        return calculateRatingChange(winnerRating, loserRating, DEFAULT_K_FACTOR);
    }

    /**
     * 예상 승률 계산
     * @param playerRating 플레이어 레이팅
     * @param opponentRating 상대 레이팅
     * @return 예상 승률 (0.0 ~ 1.0)
     */
    public static double getExpectedScore(int playerRating, int opponentRating) {
        return 1.0 / (1.0 + Math.pow(10, (opponentRating - playerRating) / 400.0));
    }

    /**
     * 레이팅 차이에 따른 동적 K-Factor 계산
     * @param rating 플레이어 레이팅
     * @param gamesPlayed 플레이한 게임 수
     * @return 동적 K-Factor
     */
    public static int getDynamicKFactor(int rating, int gamesPlayed) {
        // 신규 플레이어는 높은 K-Factor
        if (gamesPlayed < 30) {
            return 40;
        }

        // 고레이팅은 낮은 K-Factor
        if (rating >= 2400) {
            return 16;
        }

        // 기본 K-Factor
        return DEFAULT_K_FACTOR;
    }

    /**
     * 팀전 레이팅 변화 계산
     * @param winnerRatings 승리 팀 레이팅 배열
     * @param loserRatings 패배 팀 레이팅 배열
     * @param kFactor K-Factor
     * @return [승리 팀 평균 변화, 패배 팀 평균 변화]
     */
    public static int[] calculateTeamRatingChange(int[] winnerRatings, int[] loserRatings, int kFactor) {
        // 팀 평균 레이팅 계산
        int winnerAvg = calculateAverage(winnerRatings);
        int loserAvg = calculateAverage(loserRatings);

        return calculateRatingChange(winnerAvg, loserAvg, kFactor);
    }

    /**
     * 배열 평균 계산
     */
    private static int calculateAverage(int[] values) {
        if (values == null || values.length == 0) {
            return 1000;
        }

        int sum = 0;
        for (int value : values) {
            sum += value;
        }

        return sum / values.length;
    }

    /**
     * 무승부 레이팅 변화 계산
     * @param rating1 플레이어1 레이팅
     * @param rating2 플레이어2 레이팅
     * @param kFactor K-Factor
     * @return [플레이어1 변화, 플레이어2 변화]
     */
    public static int[] calculateDrawRatingChange(int rating1, int rating2, int kFactor) {
        double expected1 = getExpectedScore(rating1, rating2);
        double expected2 = getExpectedScore(rating2, rating1);

        // 무승부는 0. 5점
        int change1 = (int) Math.round(kFactor * (0.5 - expected1));
        int change2 = (int) Math.round(kFactor * (0.5 - expected2));

        return new int[]{change1, change2};
    }

    /**
     * 예상 승률 퍼센트 반환
     */
    public static String getWinProbabilityString(int playerRating, int opponentRating) {
        double expected = getExpectedScore(playerRating, opponentRating);
        return String.format("%.1f%%", expected * 100);
    }

    /**
     * 레이팅 차이에 따른 예상 레이팅 변화 미리보기
     * @param myRating 내 레이팅
     * @param opponentRating 상대 레이팅
     * @param kFactor K-Factor
     * @return [승리시 변화, 패배시 변화]
     */
    public static int[] previewRatingChange(int myRating, int opponentRating, int kFactor) {
        // 승리 시
        int[] winResult = calculateRatingChange(myRating, opponentRating, kFactor);
        
        // 패배 시
        int[] loseResult = calculateRatingChange(opponentRating, myRating, kFactor);

        return new int[]{winResult[0], loseResult[1]};
    }

    /**
     * 레이팅 차이에 따른 설명
     */
    public static String getRatingDifferenceDescription(int ratingDiff) {
        int absDiff = Math. abs(ratingDiff);

        if (absDiff < 50) {
            return "비슷한 실력";
        } else if (absDiff < 100) {
            return "약간의 실력 차이";
        } else if (absDiff < 200) {
            return "실력 차이 있음";
        } else if (absDiff < 400) {
            return "큰 실력 차이";
        } else {
            return "압도적 실력 차이";
        }
    }

    /**
     * 목표 레이팅까지 필요한 예상 승수 계산
     * @param currentRating 현재 레이팅
     * @param targetRating 목표 레이팅
     * @param averageOpponentRating 평균 상대 레이팅
     * @param kFactor K-Factor
     * @return 예상 필요 승수
     */
    public static int estimateWinsNeeded(int currentRating, int targetRating, int averageOpponentRating, int kFactor) {
        if (currentRating >= targetRating) {
            return 0;
        }

        int ratingNeeded = targetRating - currentRating;
        int[] change = calculateRatingChange(currentRating, averageOpponentRating, kFactor);
        
        if (change[0] <= 0) {
            return Integer.MAX_VALUE;
        }

        return (int) Math.ceil((double) ratingNeeded / change[0]);
    }

    /**
     * 연승/연패 보너스 계산
     * @param baseChange 기본 레이팅 변화
     * @param streak 연승/연패 수
     * @param isWin 승리 여부
     * @return 보너스 적용된 레이팅 변화
     */
    public static int applyStreakBonus(int baseChange, int streak, boolean isWin) {
        if (streak <= 1) {
            return baseChange;
        }

        // 연승 보너스:  연승 수에 따라 5%씩 증가 (최대 50%)
        double multiplier = 1.0 + Math.min(streak - 1, 10) * 0.05;

        if (isWin) {
            return (int) Math.round(baseChange * multiplier);
        } else {
            // 연패 시에는 보너스 없음 (페널티도 없음)
            return baseChange;
        }
    }
}