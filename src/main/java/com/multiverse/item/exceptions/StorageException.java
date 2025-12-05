package com.multiverse.item.exceptions;

/**
 * 저장 예외
 */
public class StorageException extends ItemException {
    
    private String storageType;
    private String operation;
    
    /**
     * 기본 생성자
     */
    public StorageException() {
        super("저장소 오류가 발생했습니다.");
    }
    
    /**
     * 메시지를 포함한 생성자
     */
    public StorageException(String message) {
        super(message);
    }
    
    /**
     * 저장소 타입과 작업을 포함한 생성자
     */
    public StorageException(String storageType, String operation) {
        super(storageType + " 저장소의 " + operation + " 작업 중 오류가 발생했습니다.");
        this. storageType = storageType;
        this.operation = operation;
    }
    
    /**
     * 저장소 타입, 작업, 상세 메시지를 포함한 생성자
     */
    public StorageException(String storageType, String operation, String detail) {
        super(storageType + " 저장소의 " + operation + " 작업 중 오류가 발생했습니다. 상세: " + detail);
        this.storageType = storageType;
        this.operation = operation;
    }
    
    /**
     * 원인을 포함한 생성자
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 저장소 타입 반환
     */
    public String getStorageType() {
        return storageType;
    }
    
    /**
     * 작업 반환
     */
    public String getOperation() {
        return operation;
    }
}