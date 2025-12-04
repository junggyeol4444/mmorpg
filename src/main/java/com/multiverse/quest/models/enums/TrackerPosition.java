package com.multiverse.quest.models.enums;

/**
 * 퀘스트 추적 UI 위치 열거형
 * 화면에 표시되는 퀘스트 추적기의 위치를 정의합니다.
 */
public enum TrackerPosition {
    TOP_RIGHT("우측 상단", "화면 우측 상단에 표시", 0. 98f, 0.05f),
    TOP_LEFT("좌측 상단", "화면 좌측 상단에 표시", 0. 02f, 0.05f),
    BOTTOM_RIGHT("우측 하단", "화면 우측 하단에 표시", 0.98f, 0.95f),
    BOTTOM_LEFT("좌측 하단", "화면 좌측 하단에 표시", 0. 02f, 0.95f),
    CENTER("중앙", "화면 중앙에 표시", 0.5f, 0.5f);

    private final String displayName;
    private final String description;
    private final float anchorX;  // 0.0 ~ 1.0 (좌에서 우)
    private final float anchorY;  // 0.0 ~ 1.0 (상에서 하)

    /**
     * TrackerPosition 생성자
     * @param displayName 표시명
     * @param description 설명
     * @param anchorX X 앵커 위치
     * @param anchorY Y 앵커 위치
     */
    TrackerPosition(String displayName, String description, float anchorX, float anchorY) {
        this. displayName = displayName;
        this.description = description;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
    }

    /**
     * 표시명 반환
     * @return 표시명
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 설명 반환
     * @return 설명
     */
    public String getDescription() {
        return description;
    }

    /**
     * X 앵커 위치 반환 (0.0 ~ 1.0)
     * @return X 앵커 위치
     */
    public float getAnchorX() {
        return anchorX;
    }

    /**
     * Y 앵커 위치 반환 (0.0 ~ 1.0)
     * @return Y 앵커 위치
     */
    public float getAnchorY() {
        return anchorY;
    }

    /**
     * 화면 픽셀 좌표로 변환 (1920x1080 기준)
     * @param screenWidth 화면 너비
     * @param screenHeight 화면 높이
     * @return [x, y] 좌표 배열
     */
    public int[] getPixelCoordinates(int screenWidth, int screenHeight) {
        int x = Math.round(screenWidth * anchorX);
        int y = Math.round(screenHeight * anchorY);
        return new int[]{x, y};
    }

    /**
     * 기본 픽셀 오프셋 반환 (UI 크기 고려)
     * @return [offsetX, offsetY] 오프셋 배열
     */
    public int[] getDefaultOffset() {
        switch (this) {
            case TOP_RIGHT:
                return new int[]{-250, 10};  // 너비 250px, 상단에서 10px
            case TOP_LEFT:
                return new int[]{10, 10};
            case BOTTOM_RIGHT:
                return new int[]{-250, -300}; // 높이 300px
            case BOTTOM_LEFT:
                return new int[]{10, -300};
            case CENTER:
                return new int[]{-125, -150}; // 중앙 정렬
            default:
                return new int[]{0, 0};
        }
    }

    /**
     * 문자열로부터 TrackerPosition 찾기
     * @param name 이름
     * @return TrackerPosition (없으면 TOP_RIGHT)
     */
    public static TrackerPosition fromString(String name) {
        try {
            return TrackerPosition.valueOf(name. toUpperCase());
        } catch (IllegalArgumentException e) {
            return TOP_RIGHT; // 기본값
        }
    }

    /**
     * 모든 위치 반환
     * @return TrackerPosition 배열
     */
    public static TrackerPosition[] getAllPositions() {
        return TrackerPosition.values();
    }

    /**
     * 우측 위치인지 확인
     * @return 우측 위치 여부
     */
    public boolean isRight() {
        return this == TOP_RIGHT || this == BOTTOM_RIGHT;
    }

    /**
     * 좌측 위치인지 확인
     * @return 좌측 위치 여부
     */
    public boolean isLeft() {
        return this == TOP_LEFT || this == BOTTOM_LEFT;
    }

    /**
     * 상단 위치인지 확인
     * @return 상단 위치 여부
     */
    public boolean isTop() {
        return this == TOP_RIGHT || this == TOP_LEFT;
    }

    /**
     * 하단 위치인지 확인
     * @return 하단 위치 여부
     */
    public boolean isBottom() {
        return this == BOTTOM_RIGHT || this == BOTTOM_LEFT;
    }

    /**
     * 중앙 위치인지 확인
     * @return 중앙 위치 여부
     */
    public boolean isCenter() {
        return this == CENTER;
    }

    /**
     * 다음 위치 반환 (순환)
     * @return 다음 위치
     */
    public TrackerPosition getNext() {
        switch (this) {
            case TOP_RIGHT:
                return TOP_LEFT;
            case TOP_LEFT:
                return BOTTOM_LEFT;
            case BOTTOM_LEFT:
                return BOTTOM_RIGHT;
            case BOTTOM_RIGHT:
                return CENTER;
            case CENTER:
                return TOP_RIGHT;
            default:
                return TOP_RIGHT;
        }
    }

    /**
     * 이전 위치 반환 (순환)
     * @return 이전 위치
     */
    public TrackerPosition getPrevious() {
        switch (this) {
            case TOP_RIGHT:
                return CENTER;
            case CENTER:
                return BOTTOM_RIGHT;
            case BOTTOM_RIGHT:
                return BOTTOM_LEFT;
            case BOTTOM_LEFT:
                return TOP_LEFT;
            case TOP_LEFT:
                return TOP_RIGHT;
            default:
                return TOP_RIGHT;
        }
    }

    /**
     * 정렬 방식 반환 (텍스트 정렬 용)
     * @return 정렬 방식 ("LEFT", "CENTER", "RIGHT")
     */
    public String getAlignment() {
        if (isRight()) {
            return "RIGHT";
        } else if (isLeft()) {
            return "LEFT";
        } else {
            return "CENTER";
        }
    }

    /**
     * UI 요소의 수평 정렬 앵커 반환
     * @return 정렬 앵커 (0.0 ~ 1. 0)
     */
    public float getHorizontalAnchor() {
        if (isRight()) {
            return 1.0f;
        } else if (isLeft()) {
            return 0.0f;
        } else {
            return 0.5f;
        }
    }

    /**
     * UI 요소의 수직 정렬 앵커 반환
     * @return 정렬 앵커 (0.0 ~ 1.0)
     */
    public float getVerticalAnchor() {
        if (isTop()) {
            return 0.0f;
        } else if (isBottom()) {
            return 1.0f;
        } else {
            return 0.5f;
        }
    }

    /**
     * 콘솔 로그에 표시할 좌표 문자열
     * @return 좌표 문자열
     */
    public String toCoordinateString() {
        return String.format("[%. 2f, %.2f]", anchorX, anchorY);
    }
}