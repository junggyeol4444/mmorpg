package com.multiverse.guild.model;

public class TerritorySettings {
    private boolean pvpEnabled;
    private boolean mobSpawn;
    private boolean blockBreak;
    private boolean publicAccess;

    public TerritorySettings(boolean pvpEnabled, boolean mobSpawn, boolean blockBreak, boolean publicAccess) {
        this.pvpEnabled = pvpEnabled;
        this.mobSpawn = mobSpawn;
        this.blockBreak = blockBreak;
        this.publicAccess = publicAccess;
    }

    public boolean isPvpEnabled() { return pvpEnabled; }
    public void setPvpEnabled(boolean pvpEnabled) { this.pvpEnabled = pvpEnabled; }
    public boolean isMobSpawn() { return mobSpawn; }
    public void setMobSpawn(boolean mobSpawn) { this.mobSpawn = mobSpawn; }
    public boolean isBlockBreak() { return blockBreak; }
    public void setBlockBreak(boolean blockBreak) { this.blockBreak = blockBreak; }
    public boolean isPublicAccess() { return publicAccess; }
    public void setPublicAccess(boolean publicAccess) { this.publicAccess = publicAccess; }
}