package com.multiverse.death.listeners;

import com.multiverse.death.DeathAndRebirthCore;
import com.multiverse.death.managers.DeathManager;
import com.multiverse.death.managers.InsuranceManager;
import com.multiverse.death.managers.NetherRealmManager;
import com.multiverse.death.models.DeathRecord;
import com.multiverse.death.models.Insurance;
import com.multiverse.death.models.enums.DeathCause;
import com.multiverse.death.models.enums.InsuranceType;
import com.multiverse.death.utils.MessageUtil;
import com.multiverse.death.events.PlayerDeathProcessEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private final DeathAndRebirthCore plugin;
    private final DeathManager deathManager;
    private final InsuranceManager insuranceManager;
    private final NetherRealmManager netherRealmManager;
    private final MessageUtil msg;
    private final org.bukkit.configuration.file.FileConfiguration config;

    public DeathListener(DeathAndRebirthCore plugin,
                        DeathManager deathManager,
                        InsuranceManager insuranceManager,
                        NetherRealmManager netherRealmManager,
                        MessageUtil msg,
                        org.bukkit.configuration.file.FileConfiguration config) {
        this.plugin = plugin;
        this.deathManager = deathManager;
        this.insuranceManager = insuranceManager;
        this.netherRealmManager = netherRealmManager;
        this.msg = msg;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // 사망 기록 생성
        DeathRecord record = new DeathRecord();
        record.setPlayerUUID(player.getUniqueId());
        record.setDeathTime(System.currentTimeMillis());
        record.setDimension(deathManager.getCurrentDimension(player));
        record.setDeathLocation(player.getLocation());
        record.setCause(deathManager.detectDeathCause(event, player));

        Insurance insurance = insuranceManager.getInsurance(player);
        boolean hasInsurance = insurance != null && insurance.isActive();
        record.setHasInsurance(hasInsurance);

        if (hasInsurance) {
            record.setInsuranceType(insurance.getType());
            // 보험 혜택 적용 (패널티 없음)
            record.setExpLost(0);
            record.setMoneyLost(0);
            record.setDroppedItems(new java.util.ArrayList<>());
            event.setKeepInventory(true);
            event.setKeepLevel(true);
            event.getDrops().clear();
            event.setDroppedExp(0);
        } else {
            // 일반 패널티 적용
            deathManager.applyDeathPenalty(player, record, event);
        }

        // 커스텀 이벤트 발생
        PlayerDeathProcessEvent deathProcessEvent = new PlayerDeathProcessEvent(player, record);
        Bukkit.getPluginManager().callEvent(deathProcessEvent);

        if (deathProcessEvent.isCancelled()) return;

        // 사망 기록 저장
        deathManager.recordDeath(player, record);

        // 명계로 강제 텔레포트 (딜레이)
        int delay = config.getInt("nether-realm.teleport.delay") * 20;
        Bukkit.getScheduler().runTaskLater(plugin,
            () -> {
                netherRealmManager.teleportToSpawn(player);
                player.sendMessage(msg.g("nether-realm.welcome"));
            }, delay);
    }

    @EventHandler
    public void onPlayerRespawn(org.bukkit.event.player.PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (config.getBoolean("nether-realm.respawn.force-nether-spawn")) {
            Location netherSpawn = netherRealmManager.getLocation(com.multiverse.death.models.enums.LocationType.SPAWN);
            if (netherSpawn != null) {
                event.setRespawnLocation(netherSpawn);
            }
        }
    }
}