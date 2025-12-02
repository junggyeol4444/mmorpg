package com.multiverse.playerdata.managers;

import com.multiverse.playerdata.PlayerDataCore;
import com.multiverse.playerdata.models.Evolution;
import com.multiverse.playerdata.models.enums.EvolutionType;
import com.multiverse.playerdata.data.DataManager;
import com.multiverse.playerdata.utils.ConfigUtil;
import org.bukkit.entity.Player;

import java.util.*;

public class EvolutionManager {

    private final PlayerDataCore plugin;
    private final DataManager dataManager;
    private final ConfigUtil configUtil;
    private final Map<String, Evolution> evolutions;

    public EvolutionManager(PlayerDataCore plugin, DataManager dataManager, ConfigUtil configUtil) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.configUtil = configUtil;
        this.evolutions = new LinkedHashMap<>();
        loadEvolutions();
    }

    public void reloadConfig() {
        evolutions.clear();
        loadEvolutions();
    }

    private void loadEvolutions() {
        Map<String, Object> yamlData = dataManager.loadEvolutionData();
        for (Map.Entry<String, Object> entry : yamlData.entrySet()) {
            String id = entry.getKey();
            Map<String, Object> data = (Map<String, Object>) entry.getValue();
            Evolution evo = Evolution.fromYaml(id, data);
            evolutions.put(id, evo);
        }
    }

    public Evolution getEvolution(String id) {
        return evolutions.get(id);
    }

    public boolean existsEvolution(String id) {
        return evolutions.containsKey(id);
    }

    public List<Evolution> getAllEvolutions() {
        return new ArrayList<>(evolutions.values());
    }

    // 해당 플레이어가 가능한 모든 진화 반환
    public List<Evolution> getAvailableEvolutions(Player player) {
        List<Evolution> available = new ArrayList<>();
        for (Evolution e : evolutions.values()) {
            if (canEvolve(player, e)) {
                available.add(e);
            }
        }
        return available;
    }

    public boolean canEvolve(Player player, Evolution evolution) {
        Map<String, Boolean> reqs = checkRequirements(player, evolution);
        for (boolean b : reqs.values()) {
            if (!b) return false;
        }
        EvolutionType type = evolution.getType();
        String currentRaceId = dataManager.getPlayerRaceId(player.getUniqueId());
        if (!evolution.getFromRaceId().equals(currentRaceId)) return false;
        return true;
    }

    public void evolvePlayer(Player player, Evolution evolution) {
        if (!canEvolve(player, evolution)) return;
        dataManager.addPlayerEvolutionHistory(player.getUniqueId(), evolution.getFromRaceId(), evolution.getToRaceId());
        dataManager.setPlayerRaceId(player.getUniqueId(), evolution.getToRaceId());
        plugin.getRaceManager().applyRaceEffects(player);
        plugin.getStatsManager().applyStatEffects(player);

        // 브로드캐스트, 이벤트 등 추가 카운트
        if (configUtil.getBoolean("evolution.notification.broadcast", true)) {
            String msg = configUtil.replaceVariables("evolution.notification.message",
                    player.getName(), evolution.getName());
            player.getServer().broadcastMessage(msg);
        }
    }

    public boolean isReversible(Evolution evolution) {
        return evolution.isReversible();
    }

    public Map<String, Boolean> checkRequirements(Player player, Evolution evolution) {
        Map<String, Boolean> result = new LinkedHashMap<>();
        Map<String, Object> requirements = evolution.getRequirements();
        for (String key : requirements.keySet()) {
            Object value = requirements.get(key);
            boolean satisfied = checkRequirement(player, key, value);
            result.put(key, satisfied);
        }
        return result;
    }

    private boolean checkRequirement(Player player, String key, Object value) {
        // 스탯/퀘스트/기타 요구 조건
        switch (key) {
            case "level":
                int requiredLvl = (value instanceof Number) ? ((Number) value).intValue() : Integer.parseInt(value.toString());
                int playerLvl = plugin.getStatsManager().getLevel(player);
                return playerLvl >= requiredLvl;
            case "spirit_affinity":
            case "demon_energy":
            case "celestial_power":
            case "immortal_qi":
            case "yokai_energy":
            case "soul_power":
                double statVal = plugin.getStatsManager().getSpecialStat(player, key);
                return statVal >= ((Number) value).doubleValue();
            case "corruption_level":
                double val = plugin.getStatsManager().getSpecialStat(player, key);
                return val >= ((Number) value).doubleValue();
            case "quest":
                return plugin.getPlayerDataManager().hasPlayerCompletedQuest(player, value.toString());
            // 원하는 요구 조건 추가
            default:
                return false;
        }
    }

    public String getRequirementText(Evolution evolution) {
        Map<String, Object> requirements = evolution.getRequirements();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> e : requirements.entrySet()) {
            sb.append(e.getKey()).append(": ").append(e.getValue()).append(", ");
        }
        return sb.toString();
    }
}