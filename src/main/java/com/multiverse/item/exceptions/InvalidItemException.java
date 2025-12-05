package com.multiverse.item.exceptions;

/**
 * 잘못된 아이템 예외
 */
public class InvalidItemException extends ItemException {
    
    /**
     * 기본 생성자
     */
    public InvalidItemException() {
        super("잘못된 아이템입니다.");
    }
    
    /**
     * 메시지를 포함한 생성자
     */
    public InvalidItemException(String message) {
        super(message);
    }
    
    /**
     * 아이템 ID를 포함한 생성자
     */
    public InvalidItemException(String itemId, String reason) {
        super("아이템 '" + itemId + "'이 잘못되었습니다. 이유: " + reason);
    }
    
    /**
     * 원인을 포함한 생성자
     */
    public InvalidItemException(String message, Throwable cause) {
        super(message, cause);
    }
}