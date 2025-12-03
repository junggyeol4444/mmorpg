package com.multiverse.death.listeners;

import com.multiverse.death.managers.NetherRealmManager;
import com.multiverse.death.utils.MessageUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.Location;

public class PvPListener implements Listener {

    private final NetherRealmManager netherRealmManager;
    private final MessageUtil msg;
    private final org.bukkit.configuration.file.FileConfiguration config;

    public PvPListener(NetherRealmManager netherRealmManager, MessageUtil msg, org.bukkit.configuration.file.FileConfiguration config) {
        this.netherRealmManager = netherRealmManager;
        this.msg = msg;
        this.config = config;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity attacker = event.getDamager();

        if (!(victim instanceof Player) || !(attacker instanceof Player)) return;

        Player vPlayer = (Player) victim;
        Player aPlayer = (Player) attacker;

        Location vLoc = vPlayer.getLocation();
        Location aLoc = aPlayer.getLocation();

        if (netherRealmManager.isPvpDisabled(vLoc) || netherRealmManager.isPvpDisabled(aLoc)) {
            event.setCancelled(true);
            aPlayer.sendMessage(msg.g("nether-realm.pvp-disabled"));
        }
    }
}