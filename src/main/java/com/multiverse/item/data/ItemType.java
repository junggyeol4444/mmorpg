package com.multiverse.item.data;

public enum ItemType {
    // 무기
    SWORD("검", "weapon", 1. 0),
    AXE("도끼", "weapon", 1.2),
    BOW("활", "weapon", 0.8),
    SPEAR("창", "weapon", 1.1),
    STAFF("지팡이", "weapon", 0.9),
    DAGGER("단검", "weapon", 0.7),
    
    // 방어구
    HELMET("투구", "armor", 0.5),
    CHESTPLATE("흉갑", "armor", 0.6),
    LEGGINGS("레깅스", "armor", 0.55),
    BOOTS("부츠", "armor", 0.45),
    
    // 악세서리
    RING("반지", "accessory", 0.3),
    NECKLACE("목걸이", "accessory", 0.35),
    BRACELET("팔찌", "accessory", 0.32),
    
    // 소비 아이템
    POTION("포션", "consumable", 0.0),
    SCROLL("스크롤", "consumable", 0.0),
    
    // 재료
    ORE("광석", "material", 0. 0),
    CRAFTING("제작재료", "material", 0. 0);
    
    private String koreanName;
    private String category;
    private double damageModifier;
    
    ItemType(String koreanName, String category, double damageModifier) {
        this.koreanName = koreanName;
        this. category = category;
        this. damageModifier = damageModifier;
    }
    
    /**
     * 한글 이름 반환
     */
    public String getKoreanName() {
        return koreanName;
    }
    
    /**
     * 카테고리 반환
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * 피해 배수 반환 (무기용)
     */
    public double getDamageModifier() {
        return damageModifier;
    }
    
    /**
     * 무기인지 확인
     */
    public boolean isWeapon() {
        return "weapon".equals(category);
    }
    
    /**
     * 방어구인지 확인
     */
    public boolean isArmor() {
        return "armor".equals(category);
    }
    
    /**
     * 악세서리인지 확인
     */
    public boolean isAccessory() {
        return "accessory".equals(category);
    }
    
    /**
     * 소비 아이템인지 확인
     */
    public boolean isConsumable() {
        return "consumable".equals(category);
    }
    
    /**
     * 재료인지 확인
     */
    public boolean isMaterial() {
        return "material".equals(category);
    }
    
    /**
     * 강화 가능 여부
     */
    public boolean canEnhance() {
        return isWeapon() || isArmor() || isAccessory();
    }
}