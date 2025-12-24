package com.multiverse.guild.model;

import java.util.EnumSet;
import java.util.Set;

public class GuildRank {
    private String rankName;
    private int priority; // 1 = 최고 (길드장)
    private Set<GuildPermission> permissions;
    private double dailySalary;

    public GuildRank(String rankName, int priority, Set<GuildPermission> permissions, double dailySalary) {
        this.rankName = rankName;
        this.priority = priority;
        this.permissions = permissions;
        this.dailySalary = dailySalary;
    }

    public static GuildRank defaultMaster() {
        return new GuildRank("Guild Master", 1, EnumSet.allOf(GuildPermission.class), 1000.0);
    }

    public static GuildRank defaultOfficer() {
        return new GuildRank("Officer", 2, EnumSet.of(
                GuildPermission.INVITE_MEMBERS,
                GuildPermission.KICK_MEMBERS,
                GuildPermission.PROMOTE_MEMBERS,
                GuildPermission.DEMOTE_MEMBERS,
                GuildPermission.MANAGE_RANKS,
                GuildPermission.EDIT_INFO,
                GuildPermission.MANAGE_TERRITORY,
                GuildPermission.USE_TREASURY
        ), 500.0);
    }

    public static GuildRank defaultMember() {
        return new GuildRank("Member", 3, EnumSet.noneOf(GuildPermission.class), 0.0);
    }

    public String getRankName() { return rankName; }
    public void setRankName(String rankName) { this.rankName = rankName; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public Set<GuildPermission> getPermissions() { return permissions; }
    public void setPermissions(Set<GuildPermission> permissions) { this.permissions = permissions; }
    public double getDailySalary() { return dailySalary; }
    public void setDailySalary(double dailySalary) { this.dailySalary = dailySalary; }
}