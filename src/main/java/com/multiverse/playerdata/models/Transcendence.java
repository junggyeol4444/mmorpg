package com.multiverse.playerdata.models;

import com.multiverse.playerdata.models.enums.TranscendentPower;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class Transcendence {

    private final UUID uuid;
    private final Set<TranscendentPower> unlockedPowers;
    private TranscendentPower selectedPower;
    private int transcendLevel;

    public Transcendence(UUID uuid, Set<TranscendentPower> unlockedPowers,
                         TranscendentPower selectedPower, int transcendLevel) {
        this.uuid = uuid;
        this.unlockedPowers = unlockedPowers != null ? unlockedPowers : new HashSet<>();
        this.selectedPower = selectedPower;
        this.transcendLevel = transcendLevel;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Set<TranscendentPower> getUnlockedPowers() {
        return new HashSet<>(unlockedPowers);
    }

    public void unlockPower(TranscendentPower power) {
        unlockedPowers.add(power);
    }

    public TranscendentPower getSelectedPower() {
        return selectedPower;
    }

    public void setSelectedPower(TranscendentPower selectedPower) {
        this.selectedPower = selectedPower;
    }

    public int getTranscendLevel() {
        return transcendLevel;
    }

    public void setTranscendLevel(int transcendLevel) {
        this.transcendLevel = transcendLevel;
    }

    // YAML 로드
    public static Transcendence fromYaml(UUID uuid, YamlConfiguration yaml) {
        Set<TranscendentPower> powers = new HashSet<>();
        List<String> powerList = yaml.getStringList("transcendence.powers");
        for (String pname : powerList) {
            try {
                powers.add(TranscendentPower.valueOf(pname.toUpperCase()));
            } catch (Exception e) {}
        }
        TranscendentPower selected = null;
        String sel = yaml.getString("transcendence.selected", null);
        if (sel != null) {
            try {
                selected = TranscendentPower.valueOf(sel.toUpperCase());
            } catch (Exception e) {}
        }
        int tlevel = yaml.getInt("transcendence.level", 1);
        return new Transcendence(uuid, powers, selected, tlevel);
    }

    // YAML 저장
    public void toYaml(YamlConfiguration yaml) {
        List<String> powers = new ArrayList<>();
        for (TranscendentPower p : unlockedPowers) {
            powers.add(p.name());
        }
        yaml.set("transcendence.powers", powers);
        yaml.set("transcendence.selected", selectedPower != null ? selectedPower.name() : null);
        yaml.set("transcendence.level", transcendLevel);
    }
}