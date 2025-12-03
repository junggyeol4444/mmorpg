package com.multiverse.death.data.storage;

import com.multiverse.death.models.DeathRecord;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Optional storage layer for DeathRecord data,
 * allows for easy migration to other storage engines (MySQL, Redis, etc.)
 */
public interface DeathStorage {

    void saveDeathRecord(Player player, DeathRecord record);

    DeathRecord getLastDeathRecord(Player player);

    List<DeathRecord> getDeathHistory(Player player, int limit);

    int getDeathCount(Player player);

    void clearDeathLocation(Player player);
}