package com.multiverse.item. exceptions;

/**
 * 아이템 시스템 기본 예외 클래스
 */
public class ItemException extends Exception {
    
    /**
     * 기본 생성자
     */
    public ItemException() {
        super();
    }
    
    /**
     * 메시지를 포함한 생성자
     */
    public ItemException(String message) {
        super(message);
    }
    
    /**
     * 원인을 포함한 생성자
     */
    public ItemException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 원인만 포함한 생성자
     */
    public ItemException(Throwable cause) {
        super(cause);
    }
}