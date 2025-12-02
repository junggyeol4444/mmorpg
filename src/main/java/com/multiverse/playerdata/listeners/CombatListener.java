package com.multiverse.playerdata.listeners;

import com.multiverse.playerdata.PlayerDataCore;
import com.multiverse.playerdata.managers.StatsManager;
import com.multiverse.playerdata.managers.RaceManager;
import com.multiverse.playerdata.models.enums.StatType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class CombatListener implements Listener {

    private final PlayerDataCore plugin;
    private final StatsManager statsManager;
    private final RaceManager raceManager;

    public CombatListener(PlayerDataCore plugin) {
        this.plugin = plugin;
        this.statsManager = plugin.getStatsManager();
        this.raceManager = plugin.getRaceManager();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();

        // 스탯 기반 데미지 계산
        int str = statsManager.getTotalStat(player, StatType.STR);
        double damage = event.getDamage() + (str * 0.5);

        // 크리티컬 체크
        int dex = statsManager.getTotalStat(player, StatType.DEX);
        int luk = statsManager.getTotalStat(player, StatType.LUK);
        double critChance = 5.0 + (dex * 0.05) + (luk * 0.1);

        if (Math.random() * 100 < critChance) {
            damage *= 2.0;
            player.sendMessage("§e§l크리티컬!");
        }

        event.setDamage(damage);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        // 회피 체크
        int dex = statsManager.getTotalStat(player, StatType.DEX);
        double dodgeChance = Math.min(dex * 0.1, 75.0);

        if (Math.random() * 100 < dodgeChance) {
            event.setCancelled(true);
            player.sendMessage("§a§l회피!");
            return;
        }

        // 저항 적용
        int res = statsManager.getTotalStat(player, StatType.RES);
        double resistance = res * 0.002;
        double damage = event.getDamage() * (1 - resistance);
        event.setDamage(damage);
    }
}