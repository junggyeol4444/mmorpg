package com.multiverse.core.utils;

import com.multiverse.core.models.FusionStatus;
import com.multiverse.core.models.enums.FusionStage;

public class FusionUtil {

    public static FusionStage nextStage(FusionStage current) {
        switch (current) {
            case NONE:
                return FusionStage.INITIATED;
            case INITIATED:
                return FusionStage.IN_PROGRESS;
            case IN_PROGRESS:
                return FusionStage.COMPLETED;
            case COMPLETED:
                return FusionStage.REVERSED;
            case REVERSED:
            default:
                return FusionStage.NONE;
        }
    }

    public static boolean canAdvance(FusionStatus status) {
        return status != null
                && status.isFused()
                && status.getCurrentStage() < FusionStage.REVERSED.ordinal();
    }

    public static boolean isFusionActive(FusionStatus status) {
        return status != null && status.isFused() && status.getCurrentStage() > FusionStage.NONE.ordinal();
    }
}