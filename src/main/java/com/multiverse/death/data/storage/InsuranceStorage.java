package com.multiverse.death.data.storage;

import com.multiverse.death.models.Insurance;
import org.bukkit.entity.Player;

/**
 * Optional storage layer for Insurance data,
 * decouples main engine from persistence details (YAML/MySQL, etc.)
 */
public interface InsuranceStorage {
    Insurance getPlayerInsurance(Player player);
    void savePlayerInsurance(Player player, Insurance insurance);
}