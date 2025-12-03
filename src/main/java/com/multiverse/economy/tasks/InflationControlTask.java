package com.multiverse.economy.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import com.multiverse.economy.models.InflationControl;
import com.multiverse.economy.utils.ConfigUtil;

public class InflationControlTask extends BukkitRunnable {

    private final InflationControl control;

    public InflationControlTask(InflationControl control) {
        this.control = control;
    }

    @Override
    public void run() {
        if (control.isActive() && control.getInflationRate() > control.getControlThreshold()) {
            ConfigUtil.applyInflationControl(control.getCurrencyId());
        }
    }
}