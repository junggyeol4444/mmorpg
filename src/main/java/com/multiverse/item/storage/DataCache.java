package com.multiverse.item.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataCache {
    
    private ConcurrentHashMap<String, CacheEntry<? >> cache;
    private long defaultTTL; // Time To Live (밀리초)
    
    /**
     * 기본 생성자
     */
    public DataCache(long defaultTTLMillis) {
        this.cache = new ConcurrentHashMap<>();
        this.defaultTTL = defaultTTLMillis;
    }
    
    /**
     * 데이터 캐시 저장
     */
    public <T> void put(String key, T value) {
        put(key, value, defaultTTL);
    }
    
    /**
     * 데이터 캐시 저장 (TTL 지정)
     */
    public <T> void put(String key, T value, long ttlMillis) {
        long expiryTime = System.currentTimeMillis() + ttlMillis;
        cache.put(key, new CacheEntry<>(value, expiryTime));
    }
    
    /**
     * 데이터 캐시 조회
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        CacheEntry<? > entry = cache.get(key);
        
        if (entry == null) {
            return null;
        }
        
        // 만료 시간 확인
        if (System. currentTimeMillis() > entry. expiryTime) {
            cache.remove(key);
            return null;
        }
        
        return (T) entry. value;
    }
    
    /**
     * 데이터 캐시 존재 여부 확인
     */
    public boolean containsKey(String key) {
        return get(key) != null;
    }
    
    /**
     * 데이터 캐시 삭제
     */
    public void remove(String key) {
        cache.remove(key);
    }
    
    /**
     * 모든 캐시 데이터 조회
     */
    public Map<String, Object> getAll() {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, CacheEntry<?>> entry : cache.entrySet()) {
            if (System.currentTimeMillis() <= entry.getValue().  expiryTime) {
                result.put(entry.getKey(), entry.getValue().value);
            }
        }
        return result;
    }
    
    /**
     * 전체 캐시 초기화
     */
    public void clear() {
        cache. clear();
    }
    
    /**
     * 만료된 캐시 정리
     */
    public void cleanup() {
        long currentTime = System.currentTimeMillis();
        cache.entrySet().removeIf(entry -> currentTime > entry. getValue().expiryTime);
    }
    
    /**
     * 캐시 크기
     */
    public int size() {
        cleanup(); // 정리 후 반환
        return cache.size();
    }
    
    /**
     * 캐시 통계 출력
     */
    public String getStats() {
        cleanup();
        return "Cache Size: " + cache.size() + ", Memory: " + (cache.size() * 8) + " bytes";
    }
    
    /**
     * 캐시 엔트리 클래스
     */
    private static class CacheEntry<T> {
        T value;
        long expiryTime;
        
        CacheEntry(T value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }
    }
}