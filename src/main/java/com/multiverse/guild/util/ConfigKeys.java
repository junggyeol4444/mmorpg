package com.multiverse.guild.util;

public final class ConfigKeys {
    private ConfigKeys() {}

    public static final String PREFIX = "messages.prefix";

    // data
    public static final String AUTO_SAVE = "data.auto-save-interval";

    // guild creation
    public static final String CREATE_ENABLED = "guild-creation.enabled";
    public static final String CREATE_COST = "guild-creation.cost";

    // treasury
    public static final String SALARY_ENABLED = "treasury.salary.enabled";
    public static final String SALARY_INTERVAL = "treasury.salary.pay-interval";

    // quests
    public static final String QUESTS_ENABLED = "quests.enabled";
    public static final String QUESTS_DAILY_RESET = "quests.daily.reset-time";
    public static final String QUESTS_WEEKLY_DAY = "quests.weekly.reset-day";
}