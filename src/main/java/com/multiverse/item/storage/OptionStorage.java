package com.multiverse.item.storage;

import com.multiverse.item.data.ItemOption;
import java.util. HashMap;
import java.util. Map;

public class OptionStorage {
    
    private Map<String, ItemOption> options;
    private YAMLStorage storage;
    
    /**
     * 기본 생성자
     */
    public OptionStorage(YAMLStorage storage) {
        this.storage = storage;
        this.options = new HashMap<>();
        loadAllOptions();
    }
    
    /**
     * 모든 옵션 로드
     */
    private void loadAllOptions() {
        if (!storage.contains("options")) {
            return;
        }
        
        Map<String, Object> optionData = storage.getSection("options");
        for (String optionId : optionData.keySet()) {
            // 나중에 OptionManager와 연동하여 ItemOption으로 변환
        }
    }
    
    /**
     * 옵션 저장
     */
    public void saveOption(String optionId, ItemOption option) {
        options.put(optionId, option);
        
        String path = "options." + optionId;
        storage.set(path + ".name", option.getName());
        storage.set(path + ". type", option.getType(). toString());
        storage.set(path + ".value", option.getValue());
        storage.set(path + ".trigger", option.getTrigger(). toString());
        storage.set(path + ".percentage", option.isPercentage());
    }
    
    /**
     * 옵션 로드
     */
    public ItemOption loadOption(String optionId) {
        if (!storage.contains("options." + optionId)) {
            return null;
        }
        
        return options.get(optionId);
    }
    
    /**
     * 옵션 삭제
     */
    public void deleteOption(String optionId) {
        options.remove(optionId);
        storage.remove("options." + optionId);
    }
    
    /**
     * 모든 옵션 조회
     */
    public Map<String, ItemOption> getAllOptions() {
        return new HashMap<>(options);
    }
    
    /**
     * 옵션 존재 여부 확인
     */
    public boolean existsOption(String optionId) {
        return storage.contains("options." + optionId);
    }
    
    /**
     * 옵션 개수
     */
    public int getOptionCount() {
        return options.size();
    }
    
    /**
     * 유형별 옵션 조회
     */
    public Map<String, ItemOption> getOptionsByType(String type) {
        Map<String, ItemOption> result = new HashMap<>();
        for (Map.Entry<String, ItemOption> entry : options.entrySet()) {
            if (entry. getValue().getType().toString().equalsIgnoreCase(type)) {
                result.put(entry. getKey(), entry.getValue());
            }
        }
        return result;
    }
    
    /**
     * 트리거별 옵션 조회
     */
    public Map<String, ItemOption> getOptionsByTrigger(String trigger) {
        Map<String, ItemOption> result = new HashMap<>();
        for (Map.Entry<String, ItemOption> entry : options.entrySet()) {
            if (entry.getValue().getTrigger().toString().equalsIgnoreCase(trigger)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
    
    /**
     * 저장소 초기화
     */
    public void clear() {
        options.clear();
        storage.remove("options");
    }
    
    /**
     * 옵션 다시 로드
     */
    public void reload() {
        clear();
        loadAllOptions();
    }
}