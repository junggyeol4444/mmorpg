package com.multiverse.core.managers;

import com.multiverse.core.MultiverseCore;
import com.multiverse.core.models.Dimension;
import com.multiverse.core.models.enums.DimensionType;
import com.multiverse.core.data.YAMLDataManager;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.*;

public class DimensionManager {
    private final MultiverseCore plugin;
    private final YAMLDataManager dataManager;

    // 차원 데이터 캐싱
    private final Map<String, Dimension> dimensionMap = new HashMap<>();

    public DimensionManager(MultiverseCore plugin, YAMLDataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        loadDimensions();
    }

    // 차원 CRUD
    public void createDimension(Dimension dimension) {
        dimensionMap.put(dimension.getId(), dimension);
        saveDimensions();
    }

    public Dimension getDimension(String id) {
        return dimensionMap.get(id);
    }

    public List<Dimension> getAllDimensions() {
        return new ArrayList<>(dimensionMap.values());
    }

    public void updateDimension(Dimension dimension) {
        dimensionMap.put(dimension.getId(), dimension);
        saveDimensions();
    }

    public void deleteDimension(String id) {
        Dimension dim = dimensionMap.get(id);
        if (dim != null) {
            dim.setActive(false);
            saveDimensions();
        }
    }

    // 차원 활성화
    public void activateDimension(String id) {
        Dimension dim = dimensionMap.get(id);
        if (dim != null) {
            dim.setActive(true);
            saveDimensions();
        }
    }

    public void deactivateDimension(String id) {
        Dimension dim = dimensionMap.get(id);
        if (dim != null) {
            dim.setActive(false);
            saveDimensions();
        }
    }

    public boolean isDimensionActive(String id) {
        Dimension dim = dimensionMap.get(id);
        return dim != null && dim.isActive();
    }

    // Multiverse 연동 (stub, 실제 구현 필요)
    public void loadDimensionWorld(String id) {
        Dimension dim = getDimension(id);
        if (dim != null && Bukkit.getWorld(dim.getWorldName()) == null) {
            // 실제 Multiverse API를 이용해서 월드 로드
            // 예: MultiverseCore.getMVWorldManager().loadWorld(...)
        }
    }

    public void unloadDimensionWorld(String id) {
        Dimension dim = getDimension(id);
        if (dim != null) {
            // 실제 Multiverse API를 이용해서 월드 언로드
        }
    }

    // 플레이어가 위치한 차원 id
    public String getPlayerCurrentDimension(Player player) {
        String world = player.getWorld().getName();
        for (Dimension dim : dimensionMap.values()) {
            if (dim.getWorldName().equalsIgnoreCase(world)) {
                return dim.getId();
            }
        }
        return null;
    }

    // 플레이어가 이동 가능한 차원 리스트
    public List<Dimension> getAccessibleDimensions(Player player) {
        List<Dimension> result = new ArrayList<>();
        int level = player.getLevel();
        for (Dimension dim : dimensionMap.values()) {
            if (dim.isActive() && level >= dim.getLevelRequirement()) {
                result.add(dim);
            }
        }
        return result;
    }

    // 월드명으로 차원 id 가져오기 (몬스터 스폰 등에서 사용)
    public String getWorldDimension(String worldName) {
        for (Dimension dim : dimensionMap.values()) {
            if (dim.getWorldName().equalsIgnoreCase(worldName)) {
                return dim.getId();
            }
        }
        return null;
    }

    // 월드맵 GUI 열기 (stub)
    public void openMapGUI(Player player) {
        // 실제 MapGUI 클래스에서 구현 필요
        player.sendMessage("§e[월드맵 GUI] 미구현");
    }

    // LOAD & SAVE
    public void loadDimensions() {
        List<Dimension> loaded = dataManager.loadDimensions();
        dimensionMap.clear();
        for (Dimension d : loaded) {
            dimensionMap.put(d.getId(), d);
        }
    }

    public void saveDimensions() {
        dataManager.saveDimensions(new ArrayList<>(dimensionMap.values()));
    }
}