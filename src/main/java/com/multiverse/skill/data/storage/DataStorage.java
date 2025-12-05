package com.multiverse.skill.data. storage;

import java.util.*;

/**
 * 데이터 저장소 인터페이스
 */
public interface DataStorage {

    /**
     * 데이터 로드
     */
    <T> T load(String key, Class<T> type);

    /**
     * 데이터 저장
     */
    void save(String key, Object data);

    /**
     * 데이터 삭제
     */
    void delete(String key);

    /**
     * 데이터 존재 여부 확인
     */
    boolean exists(String key);

    /**
     * 모든 데이터 조회
     */
    <T> List<T> loadAll(String directory, Class<T> type);

    /**
     * 모든 데이터 저장
     */
    void saveAll(String directory, Map<String, Object> dataMap);

    /**
     * 저장소 초기화
     */
    void reload();

    /**
     * 저장소 닫기
     */
    void close();

    /**
     * 저장소 상태 확인
     */
    boolean isReady();
}