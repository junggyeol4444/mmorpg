package com.multiverse.party.listeners;

import com.multiverse.party.PartyCore;
import com.multiverse.party.models.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class IntegrationListener implements Listener {

    private final PartyCore plugin;

    public IntegrationListener(PartyCore plugin) {
        this.plugin = plugin;
    }

    // 플레이어 접속 시 모든 외부 플러그인 연동 초기화/캐시
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // PlaceholderAPI 자동 플래그 적용/언로드
        if (plugin.getIntegrationManager().isPlaceholderAPIEnabled()) {
            plugin.getIntegrationManager().getPlaceholderAPIHook().registerPlayerPlaceholders(player);
        }

        // Vault 권한/화폐 연동
        if (plugin.getIntegrationManager().isVaultEnabled()) {
            plugin.getIntegrationManager().getVaultHook().refreshPlayerData(player);
        }

        // PlayerDataCore 레벨/스탯 연동
        if (plugin.getIntegrationManager().isPlayerDataCoreEnabled()) {
            plugin.getIntegrationManager().getPlayerDataCoreIntegration().refreshPlayerProfile(player);
        }

        // CombatCore 전투력 자동 적용 (예시)
        if (plugin.getIntegrationManager().isCombatCoreEnabled()) {
            plugin.getIntegrationManager().getCombatCoreIntegration().refreshCombatStats(player);
        }
    }

    // 플레이어 퇴장 시 외부 연동 해제 또는 캐시 정리
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.getIntegrationManager().isPlaceholderAPIEnabled()) {
            plugin.getIntegrationManager().getPlaceholderAPIHook().unregisterPlayerPlaceholders(player);
        }

        if (plugin.getIntegrationManager().isVaultEnabled()) {
            plugin.getIntegrationManager().getVaultHook().unloadPlayerCache(player.getUniqueId());
        }
    }

    // 플레이어 월드 이동 시 파티 버프, 외부 효과 연동
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        Party party = plugin.getPartyManager().getPlayerParty(player);
        if (party != null) {
            plugin.getPartyBuffManager().applyBuffsToPlayer(player, party);
        }
    }

    // 월드 로드 시 특수 외부 연동 등 초기화(예: MythicMobs, 던전코어 등)
    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldLoad(WorldLoadEvent event) {
        if (plugin.getIntegrationManager().isMythicMobsEnabled()) {
            plugin.getIntegrationManager().getMythicMobsIntegration().loadWorld(event.getWorld());
        }
        if (plugin.getIntegrationManager().isDungeonCoreEnabled()) {
            plugin.getIntegrationManager().getDungeonCoreIntegration().loadWorld(event.getWorld());
        }
    }
}