package com.multiverse.death.models;

import com.multiverse.death.models.enums.NPCType;
import org.bukkit.Location;

/**
 * 명계에 등장하는 NPC 정보 모델
 */
public class NetherRealmNPC {
    private String id;
    private NPCType type;
    private String name;
    private Location location;
    private String dialog;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public NPCType getType() {
        return type;
    }
    public void setType(NPCType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDialog() {
        return dialog;
    }
    public void setDialog(String dialog) {
        this.dialog = dialog;
    }
}