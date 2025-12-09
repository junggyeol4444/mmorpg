package com.multiverse.party.integration;

import com.multiverse.party.PartyCore;
import org.bukkit.entity.Player;

/**
 * CombatCore 연동 - 전투력 정보 등 외부 시스템 접근용
 */
public class CombatCoreIntegration {

    private final PartyCore plugin;

    public CombatCoreIntegration(PartyCore plugin) {
        this.plugin = plugin;
    }

    public void refreshCombatStats(Player player) {
        // CombatCore에서 플레이어 전투력, 특성 등 정보 새로고침
    }
}