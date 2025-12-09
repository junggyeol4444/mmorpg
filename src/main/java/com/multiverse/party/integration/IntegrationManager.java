package com.multiverse.party.managers;

import com.multiverse.party.PartyCore;
import org.bukkit.entity.Player;

/**
 * 외부 플러그인 및 타 시스템 연동 관리 (PlaceholderAPI, Vault, MythicMobs, 등)
 */
public class IntegrationManager {

    private final PartyCore plugin;
    // 각 연동 모듈별 활성 여부/핸들러는 실제 구현체 필드

    public IntegrationManager(PartyCore plugin) {
        this.plugin = plugin;
    }

    // PlaceholderAPI 연동 여부 및 핸들러
    public boolean isPlaceholderAPIEnabled() {
        // 실제 연동 체크 구현
        return plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
    }
    public PlaceholderAPIHook getPlaceholderAPIHook() {
        // 실제 연동 객체 반환
        return new PlaceholderAPIHook(plugin);
    }

    // Vault 연동 여부 및 핸들러
    public boolean isVaultEnabled() {
        return plugin.getServer().getPluginManager().getPlugin("Vault") != null;
    }
    public VaultHook getVaultHook() {
        return new VaultHook(plugin);
    }

    // PlayerDataCore 연동 여부 및 핸들러
    public boolean isPlayerDataCoreEnabled() {
        return plugin.getServer().getPluginManager().getPlugin("PlayerDataCore") != null;
    }
    public PlayerDataCoreIntegration getPlayerDataCoreIntegration() {
        return new PlayerDataCoreIntegration(plugin);
    }

    // CombatCore 연동
    public boolean isCombatCoreEnabled() {
        return plugin.getServer().getPluginManager().getPlugin("CombatCore") != null;
    }
    public CombatCoreIntegration getCombatCoreIntegration() {
        return new CombatCoreIntegration(plugin);
    }

    // MythicMobs 연동
    public boolean isMythicMobsEnabled() {
        return plugin.getServer().getPluginManager().getPlugin("MythicMobs") != null;
    }
    public MythicMobsIntegration getMythicMobsIntegration() {
        return new MythicMobsIntegration(plugin);
    }

    // DungeonCore 연동
    public boolean isDungeonCoreEnabled() {
        return plugin.getServer().getPluginManager().getPlugin("DungeonCore") != null;
    }
    public DungeonCoreIntegration getDungeonCoreIntegration() {
        return new DungeonCoreIntegration(plugin);
    }

    // 플레이어 대상 외부 연동 초기화 등
    public void hookForPlayer(Player player) {
        if (isPlaceholderAPIEnabled()) {
            getPlaceholderAPIHook().registerPlayerPlaceholders(player);
        }
        if (isVaultEnabled()) {
            getVaultHook().refreshPlayerData(player);
        }
    }
}