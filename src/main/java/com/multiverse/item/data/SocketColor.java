package com.multiverse.item.data;

public enum SocketColor {
    RED("빨강", "&c", "공격 관련"),
    BLUE("파랑", "&9", "방어 관련"),
    GREEN("초록", "&a", "회복 관련"),
    YELLOW("노랑", "&e", "특수 관련"),
    PURPLE("보라", "&5", "마법 관련"),
    WHITE("흰색", "&f", "모든 관련");
    
    private String koreanName;
    private String colorCode;
    private String category;
    
    SocketColor(String koreanName, String colorCode, String category) {
        this.koreanName = koreanName;
        this.colorCode = colorCode;
        this. category = category;
    }
    
    /**
     * 한글 이름 반환
     */
    public String getKoreanName() {
        return koreanName;
    }
    
    /**
     * 색상 코드 반환
     */
    public String getColorCode() {
        return colorCode;
    }
    
    /**
     * 카테고리 반환
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * 소켓 색상과 보석이 호환되는지 확인
     */
    public boolean isCompatible(SocketColor gemColor) {
        // 흰색 소켓은 모든 보석과 호환
        if (this == WHITE) {
            return true;
        }
        
        // 같은 색상이면 호환
        return this == gemColor;
    }
    
    /**
     * 소켓 색상 설명 반환
     */
    public String getDescription() {
        switch (this) {
            case RED:
                return "공격력 관련 보석을 장착할 수 있습니다.";
            case BLUE:
                return "방어력 관련 보석을 장착할 수 있습니다.";
            case GREEN:
                return "회복 관련 보석을 장착할 수 있습니다.";
            case YELLOW:
                return "특수 효과 보석을 장착할 수 있습니다.";
            case PURPLE:
                return "마법 관련 보석을 장착할 수 있습니다.";
            case WHITE:
                return "모든 색상의 보석을 장착할 수 있습니다. ";
            default:
                return "알 수 없는 소켓입니다.";
        }
    }
}