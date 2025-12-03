package com.multiverse.economy.api;

import com.multiverse.economy.models.InflationControl;

public class InflationAPI {

    public static boolean applyInflationControl(InflationControl control) {
        if (control.isActive() && control.getInflationRate() > control.getControlThreshold()) {
            // Implement the adjustment logic here
            return true;
        }
        return false;
    }

    // Additional inflation logic as needed
}