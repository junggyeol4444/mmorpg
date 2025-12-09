package com.multiverse.party.listeners;

import com.multiverse.party.PartyCore;
import com.multiverse.party.models.Party;
import com.multiverse.party.models.enums.PartyRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.UUID;

public class CombatListener implements Listener {

    private final PartyCore plugin;

    public CombatListener(PartyCore plugin) {
        this.plugin = plugin;
    }

    // 플레이어가 데미지 입힐 때 통계 반영
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDealDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getDamager();
        Party party = plugin.getPartyManager().getPlayerParty(player);
        if (party == null) return;

        double damage = event.getFinalDamage();
        UUID playerUUID = player.getUniqueId();

        plugin.getPartyStatisticsManager().recordDamage(party, playerUUID, damage);
        // 필요시 기여도 Manager에도 기록
        plugin.getContributionManager().recordDamage(party, playerUUID, damage);
    }

    // 플레이어가 직접 치유할 때 통계 반영 (힐 스킬 등)
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerHeal(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Party party = plugin.getPartyManager().getPlayerParty(player);
        if (party == null) return;

        double heal = event.getAmount();
        UUID playerUUID = player.getUniqueId();

        plugin.getPartyStatisticsManager().recordHealing(party, playerUUID, heal);
        // 필요시 기여도 Manager에도 기록
        plugin.getContributionManager().recordHealing(party, playerUUID, heal);
    }
}