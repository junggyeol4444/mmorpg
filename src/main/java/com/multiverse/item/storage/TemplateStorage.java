package com.multiverse.item. storage;

import com.multiverse.item.data.CustomItem;
import java.util. HashMap;
import java.util. Map;

public class TemplateStorage {
    
    private Map<String, CustomItem> templates;
    private YAMLStorage storage;
    
    /**
     * 기본 생성자
     */
    public TemplateStorage(YAMLStorage storage) {
        this.storage = storage;
        this.templates = new HashMap<>();
        loadAllTemplates();
    }
    
    /**
     * 모든 템플릿 로드
     */
    private void loadAllTemplates() {
        if (! storage.contains("templates")) {
            return;
        }
        
        Map<String, Object> templateData = storage.getSection("templates");
        for (String templateId : templateData.keySet()) {
            // 나중에 ItemManager와 연동하여 CustomItem으로 변환
            plugin.getLogger().info("템플릿 로드: " + templateId);
        }
    }
    
    /**
     * 템플릿 저장
     */
    public void saveTemplate(String templateId, CustomItem template) {
        templates.put(templateId, template);
        
        String path = "templates." + templateId;
        storage.set(path + ".name", template.getName());
        storage.set(path + ".type", template.getType(). toString());
        storage.set(path + ".rarity", template.getRarity(). toString());
        storage.set(path + ".base-stats", template.getBaseStats());
        storage.set(path + ".description", template.getDescription());
    }
    
    /**
     * 템플릿 로드
     */
    public CustomItem loadTemplate(String templateId) {
        if (!storage.contains("templates." + templateId)) {
            return null;
        }
        
        return templates.get(templateId);
    }
    
    /**
     * 템플릿 삭제
     */
    public void deleteTemplate(String templateId) {
        templates.remove(templateId);
        storage.remove("templates." + templateId);
    }
    
    /**
     * 모든 템플릿 조회
     */
    public Map<String, CustomItem> getAllTemplates() {
        return new HashMap<>(templates);
    }
    
    /**
     * 템플릿 존재 여부 확인
     */
    public boolean existsTemplate(String templateId) {
        return storage.contains("templates." + templateId);
    }
    
    /**
     * 템플릿 개수
     */
    public int getTemplateCount() {
        return templates.size();
    }
    
    /**
     * 템플릿 복사
     */
    public CustomItem copyTemplate(String templateId) {
        CustomItem template = loadTemplate(templateId);
        if (template == null) {
            return null;
        }
        
        // 템플릿 복사
        return template;
    }
    
    /**
     * 저장소 초기화
     */
    public void clear() {
        templates.clear();
        storage.remove("templates");
    }
    
    /**
     * 템플릿 다시 로드
     */
    public void reload() {
        clear();
        loadAllTemplates();
    }
}