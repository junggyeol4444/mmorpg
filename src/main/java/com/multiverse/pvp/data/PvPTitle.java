package com.multiverse. pvp.data;

import com.multiverse.pvp.enums.PvPTier;
import com.multiverse.pvp.enums.TitleCategory;

import java.util.HashMap;
import java. util.Map;

public class PvPTitle {

    private String titleId;
    private String displayName;
    private TitleCategory category;

    // 조건
    private int requiredKills;
    private int requiredWins;
    private int requiredStreak;
    private PvPTier requiredTier;
    private int requiredRank;
    private int requiredSeason;

    // 효과/보너스
    private Map<String, Double> bonuses;

    // 표시
    private String prefix;
    private String suffix;
    private String color;

    // 희귀도
    private TitleRarity rarity;

    // 설명
    private String description;

    // 잠금 해제 조건 설명
    private String unlockDescription;

    // 활성화 여부
    private boolean enabled;

    public PvPTitle(String titleId, String displayName, TitleCategory category) {
        this.titleId = titleId;
        this.displayName = displayName;
        this.category = category;

        this.requiredKills = 0;
        this. requiredWins = 0;
        this.requiredStreak = 0;
        this.requiredTier = null;
        this. requiredRank = 0;
        this.requiredSeason = 0;

        this.bonuses = new HashMap<>();
        this.prefix = "";
        this.suffix = "";
        this.color = "&f";
        this. rarity = TitleRarity.COMMON;
        this. description = "";
        this.unlockDescription = "";
        this.enabled = true;
    }

    // ==================== Getters ====================

    public String getTitleId() {
        return titleId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public TitleCategory getCategory() {
        return category;
    }

    public int getRequiredKills() {
        return requiredKills;
    }

    public int getRequiredWins() {
        return requiredWins;
    }

    public int getRequiredStreak() {
        return requiredStreak;
    }

    public PvPTier getRequiredTier() {
        return requiredTier;
    }

    public int getRequiredRank() {
        return requiredRank;
    }

    public int getRequiredSeason() {
        return requiredSeason;
    }

    public Map<String, Double> getBonuses() {
        return bonuses;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getColor() {
        return color;
    }

    public TitleRarity getRarity() {
        return rarity;
    }

    public String getDescription() {
        return description;
    }

    public String getUnlockDescription() {
        return unlockDescription;
    }

    public boolean isEnabled() {
        return enabled;
    }

    // ==================== Setters ====================

    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setCategory(TitleCategory category) {
        this.category = category;
    }

    public void setRequiredKills(int requiredKills) {
        this.requiredKills = requiredKills;
    }

    public void setRequiredWins(int requiredWins) {
        this.requiredWins = requiredWins;
    }

    public void setRequiredStreak(int requiredStreak) {
        this. requiredStreak = requiredStreak;
    }

    public void setRequiredTier(PvPTier requiredTier) {
        this.requiredTier = requiredTier;
    }

    public void setRequiredRank(int requiredRank) {
        this.requiredRank = requiredRank;
    }

    public void setRequiredSeason(int requiredSeason) {
        this.requiredSeason = requiredSeason;
    }

    public void setBonuses(Map<String, Double> bonuses) {
        this.bonuses = bonuses;
    }

    public void setPrefix(String prefix) {
        this. prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setColor(String color) {
        this. color = color;
    }

    public void setRarity(TitleRarity rarity) {
        this.rarity = rarity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUnlockDescription(String unlockDescription) {
        this.unlockDescription = unlockDescription;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 보너스 추가
     */
    public void addBonus(String bonusType, double value) {
        bonuses.put(bonusType, value);
    }

    /**
     * 보너스 조회
     */
    public double getBonus(String bonusType) {
        return bonuses.getOrDefault(bonusType, 0.0);
    }

    /**
     * 보너스 존재 여부
     */
    public boolean hasBonus(String bonusType) {
        return bonuses.containsKey(bonusType);
    }

    /**
     * 킬 조건 충족 여부
     */
    public boolean meetsKillRequirement(int playerKills) {
        return requiredKills <= 0 || playerKills >= requiredKills;
    }

    /**
     * 승리 조건 충족 여부
     */
    public boolean meetsWinRequirement(int playerWins) {
        return requiredWins <= 0 || playerWins >= requiredWins;
    }

    /**
     * 스트릭 조건 충족 여부
     */
    public boolean meetsStreakRequirement(int playerStreak) {
        return requiredStreak <= 0 || playerStreak >= requiredStreak;
    }

    /**
     * 티어 조건 충족 여부
     */
    public boolean meetsTierRequirement(PvPTier playerTier) {
        if (requiredTier == null) {
            return true;
        }
        return playerTier. ordinal() >= requiredTier.ordinal();
    }

    /**
     * 랭크 조건 충족 여부
     */
    public boolean meetsRankRequirement(int playerRank) {
        return requiredRank <= 0 || playerRank <= requiredRank;
    }

    /**
     * 모든 조건 충족 여부
     */
    public boolean meetsAllRequirements(int kills, int wins, int streak, PvPTier tier, int rank) {
        return meetsKillRequirement(kills) &&
               meetsWinRequirement(wins) &&
               meetsStreakRequirement(streak) &&
               meetsTierRequirement(tier) &&
               meetsRankRequirement(rank);
    }

    /**
     * 진행률 계산 (0.0 ~ 1.0)
     */
    public double getProgress(int kills, int wins, int streak) {
        double progress = 0.0;
        int conditions = 0;

        if (requiredKills > 0) {
            progress += Math.min(1.0, (double) kills / requiredKills);
            conditions++;
        }

        if (requiredWins > 0) {
            progress += Math.min(1.0, (double) wins / requiredWins);
            conditions++;
        }

        if (requiredStreak > 0) {
            progress += Math. min(1.0, (double) streak / requiredStreak);
            conditions++;
        }

        if (conditions == 0) {
            return 1.0;
        }

        return progress / conditions;
    }

    /**
     * 포맷된 이름 반환
     */
    public String getFormattedName() {
        return color + displayName;
    }

    /**
     * 플레이어 이름에 적용할 포맷
     */
    public String formatPlayerName(String playerName) {
        StringBuilder sb = new StringBuilder();
        
        if (! prefix.isEmpty()) {
            sb.append(prefix).append(" ");
        }
        
        sb.append(playerName);
        
        if (!suffix.isEmpty()) {
            sb.append(" ").append(suffix);
        }
        
        return sb.toString();
    }

    /**
     * 복사본 생성
     */
    public PvPTitle clone() {
        PvPTitle clone = new PvPTitle(this.titleId, this.displayName, this.category);
        clone.requiredKills = this.requiredKills;
        clone. requiredWins = this.requiredWins;
        clone.requiredStreak = this.requiredStreak;
        clone.requiredTier = this.requiredTier;
        clone.requiredRank = this. requiredRank;
        clone.requiredSeason = this.requiredSeason;
        clone. bonuses = new HashMap<>(this.bonuses);
        clone.prefix = this. prefix;
        clone.suffix = this. suffix;
        clone.color = this. color;
        clone.rarity = this.rarity;
        clone.description = this.description;
        clone.unlockDescription = this. unlockDescription;
        clone.enabled = this.enabled;
        return clone;
    }

    /**
     * 타이틀 희귀도 Enum
     */
    public enum TitleRarity {
        COMMON("&f", "일반"),
        UNCOMMON("&a", "고급"),
        RARE("&9", "희귀"),
        EPIC("&5", "영웅"),
        LEGENDARY("&6", "전설"),
        MYTHIC("&d", "신화");

        private final String color;
        private final String displayName;

        TitleRarity(String color, String displayName) {
            this.color = color;
            this.displayName = displayName;
        }

        public String getColor() {
            return color;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getFormattedName() {
            return color + displayName;
        }
    }

    /**
     * 빌더 패턴
     */
    public static Builder builder(String titleId) {
        return new Builder(titleId);
    }

    public static class Builder {
        private final PvPTitle title;

        public Builder(String titleId) {
            this.title = new PvPTitle(titleId, titleId, TitleCategory.KILLS);
        }

        public Builder displayName(String displayName) {
            title.setDisplayName(displayName);
            return this;
        }

        public Builder category(TitleCategory category) {
            title.setCategory(category);
            return this;
        }

        public Builder requiredKills(int kills) {
            title.setRequiredKills(kills);
            return this;
        }

        public Builder requiredWins(int wins) {
            title.setRequiredWins(wins);
            return this;
        }

        public Builder requiredStreak(int streak) {
            title.setRequiredStreak(streak);
            return this;
        }

        public Builder requiredTier(PvPTier tier) {
            title.setRequiredTier(tier);
            return this;
        }

        public Builder requiredRank(int rank) {
            title.setRequiredRank(rank);
            return this;
        }

        public Builder bonus(String type, double value) {
            title.addBonus(type, value);
            return this;
        }

        public Builder prefix(String prefix) {
            title.setPrefix(prefix);
            return this;
        }

        public Builder suffix(String suffix) {
            title.setSuffix(suffix);
            return this;
        }

        public Builder color(String color) {
            title.setColor(color);
            return this;
        }

        public Builder rarity(TitleRarity rarity) {
            title.setRarity(rarity);
            return this;
        }

        public Builder description(String description) {
            title. setDescription(description);
            return this;
        }

        public Builder unlockDescription(String unlockDescription) {
            title.setUnlockDescription(unlockDescription);
            return this;
        }

        public PvPTitle build() {
            return title;
        }
    }

    @Override
    public String toString() {
        return "PvPTitle{" +
                "titleId='" + titleId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", category=" + category +
                ", rarity=" + rarity +
                '}';
    }
}