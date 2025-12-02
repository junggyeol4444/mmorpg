package com.multiverse.core.utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.UUID;

public class IdUtil {
    private static final AtomicInteger portalIdSeq = new AtomicInteger(1);
    private static final AtomicInteger waypointIdSeq = new AtomicInteger(1);

    public static int nextPortalId() {
        return portalIdSeq.getAndIncrement();
    }

    public static int nextWaypointId() {
        return waypointIdSeq.getAndIncrement();
    }

    public static UUID newUUID() {
        return UUID.randomUUID();
    }
}