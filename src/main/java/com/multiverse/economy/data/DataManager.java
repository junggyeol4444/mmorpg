package com.multiverse.economy.data;

import java.util.Map;

public interface DataManager {

    Map<String, Object> loadYaml(String path);

    void saveYaml(String path, Map<String, Object> data);

    void backup(String folder);

    void autoSave();

    void startAutoSaveTask(int intervalSeconds);

    void stopAutoSaveTask();
}