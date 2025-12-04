package com.multiverse.dungeon. managers;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse. dungeon.data.model. Dungeon;
import com.multiverse.dungeon.data.model.DungeonDataManager;

import java.util.*;

/**
 * 던전 관리 매니저
 * 던전 정보 로드, 저장, 조회
 */
public class DungeonManager {

    private final DungeonCore plugin;
    private final DungeonDataManager dataManager;
    private final Map<String, Dungeon> dungeons; // dungeonId -> Dungeon

    /**
     * 생성자
     */
    public DungeonManager(DungeonCore plugin, DungeonDataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.dungeons = new HashMap<>();
        
        loadAllDungeons();
    }

    /**
     * 모든 던전 로드
     */
    private void loadAllDungeons() {
        try {
            var loadedDungeons = dataManager. loadAllDungeons();
            dungeons.putAll(loadedDungeons);
            
            plugin.getLogger().info("✅ " + dungeons.size() + "개의 던전이 로드되었습니다.");
        } catch (Exception e) {
            plugin.getLogger().severe("❌ 던전 로드 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 던전 ID로 던전 조회
     *
     * @param dungeonId 던전 ID
     * @return 던전 객체, 없으면 null
     */
    public Dungeon getDungeon(String dungeonId) {
        return dungeons.get(dungeonId);
    }

    /**
     * 모든 던전 조회
     *
     * @return 던전 목록
     */
    public List<Dungeon> getAllDungeons() {
        return new ArrayList<>(dungeons. values());
    }

    /**
     * 활성화된 던전만 조회
     *
     * @return 활성화된 던전 목록
     */
    public List<Dungeon> getEnabledDungeons() {
        return dungeons.values().stream()
            .filter(Dungeon::isEnabled)
            .toList();
    }

    /**
     * 던전 추가
     *
     * @param dungeon 추가할 던전
     * @return 성공하면 true
     */
    public boolean addDungeon(Dungeon dungeon) {
        if (dungeon == null || dungeons.containsKey(dungeon.getDungeonId())) {
            return false;
        }

        dungeons.put(dungeon.getDungeonId(), dungeon);
        
        try {
            dataManager.saveDungeon(dungeon);
            plugin.getLogger().info("✅ 던전 '" + dungeon.getName() + "'이(가) 추가되었습니다.");
            return true;
        } catch (Exception e) {
            plugin.getLogger(). severe("❌ 던전 저장 실패: " + e.getMessage());
            dungeons.remove(dungeon.getDungeonId());
            return false;
        }
    }

    /**
     * 던전 업데이트
     *
     * @param dungeon 업데이트할 던전
     * @return 성공하면 true
     */
    public boolean updateDungeon(Dungeon dungeon) {
        if (dungeon == null || ! dungeons.containsKey(dungeon.getDungeonId())) {
            return false;
        }

        dungeons.put(dungeon.getDungeonId(), dungeon);
        
        try {
            dataManager.saveDungeon(dungeon);
            plugin.getLogger().info("✅ 던전 '" + dungeon.getName() + "'이(가) 업데이트되었습니다.");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("❌ 던전 업데이트 실패: " + e.getMessage());
            return false;
        }
    }

    /**
     * 던전 삭제
     *
     * @param dungeonId 던전 ID
     * @return 성공하면 true
     */
    public boolean deleteDungeon(String dungeonId) {
        if (!dungeons. containsKey(dungeonId)) {
            return false;
        }

        var dungeon = dungeons.remove(dungeonId);
        
        try {
            dataManager.deleteDungeon(dungeonId);
            plugin.getLogger().info("✅ 던전 '" + dungeon.getName() + "'이(가) 삭제되었습니다.");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("❌ 던전 삭제 실패: " + e.getMessage());
            dungeons.put(dungeonId, dungeon);
            return false;
        }
    }

    /**
     * 던전 활성화
     *
     * @param dungeonId 던전 ID
     * @return 성공하면 true
     */
    public boolean enableDungeon(String dungeonId) {
        var dungeon = dungeons.get(dungeonId);
        if (dungeon == null) {
            return false;
        }

        dungeon.setEnabled(true);
        return updateDungeon(dungeon);
    }

    /**
     * 던전 비활성화
     *
     * @param dungeonId 던전 ID
     * @return 성공하면 true
     */
    public boolean disableDungeon(String dungeonId) {
        var dungeon = dungeons. get(dungeonId);
        if (dungeon == null) {
            return false;
        }

        dungeon.setEnabled(false);
        return updateDungeon(dungeon);
    }

    /**
     * 던전 존재 여부
     *
     * @param dungeonId 던전 ID
     * @return 존재하면 true
     */
    public boolean hasDungeon(String dungeonId) {
        return dungeons. containsKey(dungeonId);
    }

    /**
     * 총 던전 개수
     *
     * @return 던전 개수
     */
    public int getDungeonCount() {
        return dungeons.size();
    }

    /**
     * 활성화된 던전 개수
     *
     * @return 활성화된 던전 개수
     */
    public int getEnabledDungeonCount() {
        return (int) dungeons.values().stream()
            .filter(Dungeon::isEnabled)
            . count();
    }

    /**
     * 모든 던전 저장
     */
    public void saveAllDungeons() {
        try {
            for (var dungeon : dungeons. values()) {
                dataManager. saveDungeon(dungeon);
            }
            plugin.getLogger().info("✅ 모든 던전이 저장되었습니다.");
        } catch (Exception e) {
            plugin.getLogger().severe("❌ 던전 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 모든 던전 재로드
     */
    public void reloadDungeons() {
        dungeons.clear();
        loadAllDungeons();
    }
}