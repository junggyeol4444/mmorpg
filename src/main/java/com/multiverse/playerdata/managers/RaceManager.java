package com.multiverse.playerdata.managers;

import com.multiverse.playerdata.PlayerDataCore;
import com.multiverse.playerdata.models.Race;
import com.multiverse.playerdata.models.enums.RaceType;
import com.multiverse.playerdata.models.enums.StatType;
import com.multiverse.playerdata.data.DataManager;
import com.multiverse.playerdata.utils.ConfigUtil;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RaceManager {

    private final PlayerDataCore plugin;
    private final DataManager dataManager;
    private final ConfigUtil configUtil;
    private final Map<String, Race> races;

    public RaceManager(PlayerDataCore plugin, DataManager dataManager, ConfigUtil configUtil) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.configUtil = configUtil;
        this.races = new LinkedHashMap<>();
        loadRaces();
    }

    public void reloadConfig() {
        races.clear();
        loadRaces();
    }

    private void loadRaces() {
        Map<String, Object> yamlData = dataManager.loadRaceData();
        for (Map.Entry<String, Object> entry : yamlData.entrySet()) {
            String id = entry.getKey();
            Map<String, Object> data = (Map<String, Object>) entry.getValue();
            Race race = Race.fromYaml(id, data);
            races.put(id, race);
        }
    }

    public Race getRace(String id) {
        return races.get(id);
    }

    public boolean existsRace(String id) {
        return races.containsKey(id);
    }

    public List<Race> getAllRaces() {
        return new ArrayList<>(races.values());
    }

    public List<Race> getBasicRaces() {
        List<Race> result = new ArrayList<>();
        for (Race race : races.values()) {
            if (race.getType() == RaceType.BASIC) result.add(race);
        }
        return result;
    }

    public List<Race> getSpecialRaces() {
        List<Race> result = new ArrayList<>();
        for (Race race : races.values()) {
            if (race.getType() == RaceType.SPECIAL) result.add(race);
        }
        return result;
    }

    public Race getRandomRace() {
        double basicRate = configUtil.getDouble("race.random-spawn.basic-rate", 99.5);
        double specialRate = configUtil.getDouble("race.random-spawn.special-rate", 0.5);

        double rand = ThreadLocalRandom.current().nextDouble(0, 100.0);
        List<Race> possible;
        if (rand < basicRate) {
            possible = getBasicRaces();
        } else {
            possible = getSpecialRaces();
        }
        if (possible.isEmpty()) return races.get("human");
        return possible.get(ThreadLocalRandom.current().nextInt(possible.size()));
    }

    // 플레이어 종족 관리
    public Race getPlayerRace(Player player) {
        String raceId = dataManager.getPlayerRaceId(player.getUniqueId());
        return getRace(raceId);
    }

    public void setPlayerRace(Player player, Race race) {
        Race oldRace = getPlayerRace(player);
        dataManager.setPlayerRaceId(player.getUniqueId(), race.getId());
        removeRaceEffects(player, oldRace);
        applyRaceEffects(player);
    }

    // 종족 효과 적용
    public void applyRaceEffects(Player player) {
        Race race = getPlayerRace(player);
        if (race == null) return;
        // 포션/버프 등 실제 게임 버프 적용
        plugin.getStatsManager().applyRaceStatBonus(player, race.getStatBonus());
        // 특수 능력, 버프 등 추가 구현
        // ...
    }

    public void removeRaceEffects(Player player) {
        Race oldRace = getPlayerRace(player);
        removeRaceEffects(player, oldRace);
    }

    private void removeRaceEffects(Player player, Race race) {
        if (race == null) return;
        // 종족 버프/효과 제거 구현 필요
        plugin.getStatsManager().removeRaceStatBonus(player, race.getStatBonus());
        // 포션 등 추가 구현
        // ...
    }
}