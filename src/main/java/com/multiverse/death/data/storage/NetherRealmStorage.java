package com.multiverse.death.data.storage;

import com.multiverse.death.models.NetherRealmLocation;
import com.multiverse.death.models.NetherRealmNPC;
import com.multiverse.death.models.enums.LocationType;
import org.bukkit.Location;

import java.util.List;

/**
 * Optional storage layer for NetherRealm location and NPC data,
 * decouples internal logic from persistence details (YAML or DB)
 */
public interface NetherRealmStorage {

    NetherRealmLocation getNetherRealmLocation(LocationType type);
    void setNetherRealmLocation(LocationType type, Location location);
    List<NetherRealmLocation> getAllNetherRealmLocations();

    void spawnNPC(NetherRealmNPC npc);
    void removeNPC(String id);
    NetherRealmNPC getNPC(String id);
    List<NetherRealmNPC> getAllNPCs();
}