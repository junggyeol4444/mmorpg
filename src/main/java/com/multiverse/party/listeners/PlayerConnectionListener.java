package com.multiverse.party.listeners;

import com.multiverse.party.PartyCore;
import com.multiverse.party.models.Party;
import com.multiverse.party.models.PlayerPartyData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerConnectionListener implements Listener {

    private final PartyCore plugin;

    public PlayerConnectionListener(PartyCore plugin) {
        this.plugin = plugin;
    }

    // 플레이어 접속 시 파티 정보 복구 및 버프 적용
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerPartyData playerData = plugin.getDataManager().loadPlayerData(playerUUID);
            if (playerData == null) {
                playerData = new PlayerPartyData();
                playerData.setPlayerUUID(playerUUID);
                playerData.setPlayerName(player.getName());
                plugin.getDataManager().savePlayerData(playerUUID, playerData);
            } else {
                if (!player.getName().equals(playerData.getPlayerName())) {
                    playerData.setPlayerName(player.getName());
                    plugin.getDataManager().savePlayerData(playerUUID, playerData);
                }
            }
            Bukkit.getScheduler().runTask(plugin, () -> restorePartyOnJoin(player, playerData));
        });
    }

    private void restorePartyOnJoin(Player player, PlayerPartyData playerData) {
        if (playerData.getCurrentParty() == null) return;
        Party party = plugin.getPartyManager().getParty(playerData.getCurrentParty());
        if (party == null || !party.getMembers().contains(player.getUniqueId())) {
            playerData.setCurrentParty(null);
            plugin.getDataManager().savePlayerData(player.getUniqueId(), playerData);
            return;
        }
        plugin.getPartyBuffManager().applyBuffsToPlayer(player, party);
        plugin.getPartyChatManager().sendNotification(party,
                plugin.getMessageUtil().getMessage("party.member-online", "%player%", player.getName()));
        player.sendMessage(plugin.getMessageUtil().getMessage("party.welcome-back",
                "%party%", party.getPartyName() != null ? party.getPartyName() : "파티"));
    }

    // 플레이어 퇴장 시 데이터 처리, GUI 제거, 버프 제거
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        plugin.getGuiManager().closeGUI(player);
        plugin.getPartyInviteManager().removeAllInvites(player);
        plugin.getPartyFinder().removeFromQueue(player);

        Party party = plugin.getPartyManager().getPlayerParty(player);
        if (party == null) return;

        // 설정에 따라 파티 탈퇴 처리/리더 위임
        boolean leaveOnQuit = plugin.getConfig().getBoolean("party.leave-on-quit", false);
        int offlineTimeout = plugin.getConfig().getInt("party.offline-timeout", 0);

        if (leaveOnQuit) {
            plugin.getPartyManager().removeMember(party, playerUUID);
        } else if (offlineTimeout > 0) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (Bukkit.getPlayer(playerUUID) == null) {
                    Party currentParty = plugin.getPartyManager().getPlayerParty(playerUUID);
                    if (currentParty != null) {
                        plugin.getPartyManager().removeMember(currentParty, playerUUID);
                    }
                }
            }, offlineTimeout * 20L);
        } else {
            plugin.getPartyChatManager().sendNotification(party,
                    plugin.getMessageUtil().getMessage("party.member-offline", "%player%", player.getName()));
        }
        plugin.getBuffListener().removeAttributeModifiers(player);
        plugin.getBuffListener().removePotionEffects(player);
    }
}