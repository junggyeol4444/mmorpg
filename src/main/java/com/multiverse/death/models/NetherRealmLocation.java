package com.multiverse.death.models;

import com.multiverse.death.models.enums.LocationType;
import org.bukkit.Location;

/**
 * 명계의 특정 위치 데이터
 */
public class NetherRealmLocation {
    private LocationType type;
    private Location location;

    public LocationType getType() {
        return type;
    }
    public void setType(LocationType type) {
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }
}