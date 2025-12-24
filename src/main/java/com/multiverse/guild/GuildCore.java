package com.multiverse.guild;

import com.multiverse.guild.command.GuildAdminCommandExecutor;
import com.multiverse.guild.command.GuildCommandExecutor;
import com.multiverse.guild.listener.PlayerListener;
import com.multiverse.guild.listener.WarListener;
import com.multiverse.guild.manager.*;
import com.multiverse.guild.storage.*;
import com.multiverse.guild.task.AutoSaveTask;
import com.multiverse.guild.task.QuestResetTask;
import com.multiverse.guild.task.SalaryPayTask;
import com.multiverse.guild.util.Message;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public class GuildCore extends JavaPlugin {

    private static GuildCore instance;

    private Economy economy;

    private GuildManager guildManager;
    private GuildLevelManager guildLevelManager;
    private TerritoryManager territoryManager;
    private GuildSkillManager guildSkillManager;
    private GuildWarManager guildWarManager;
    private GuildTreasury guildTreasury;
    private GuildQuestManager guildQuestManager;
    private AllianceManager allianceManager;
    private GuildRanking guildRanking;
    private GuildFame guildFame;

    private YamlGuildStorage guildStorage;
    private YamlTerritoryStorage territoryStorage;
    private YamlWarStorage warStorage;
    private YamlPlayerStorage playerStorage;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        saveResource("messages.yml", false);

        Logger log = getLogger();
        Message.init(this);

        if (!setupEconomy()) {
            log.severe("Vault Economy not found. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Storage init
        guildStorage = new YamlGuildStorage(this);
        territoryStorage = new YamlTerritoryStorage(this);
        warStorage = new YamlWarStorage(this);
        playerStorage = new YamlPlayerStorage(this);

        // Managers
        guildLevelManager = new GuildLevelManager(this);
        territoryManager = new TerritoryManager(this);
        guildSkillManager = new GuildSkillManager(this);
        guildWarManager = new GuildWarManager(this);
        guildTreasury = new GuildTreasury(this);
        guildQuestManager = new GuildQuestManager(this);
        allianceManager = new AllianceManager(this);
        guildRanking = new GuildRanking(this);
        guildFame = new GuildFame(this);
        guildManager = new GuildManager(this);

        // Commands
        GuildCommandExecutor guildExec = new GuildCommandExecutor(this);
        Objects.requireNonNull(getCommand("guild")).setExecutor(guildExec);
        Objects.requireNonNull(getCommand("guild")).setTabCompleter(guildExec);
        // Admin executor shares /guild admin
        // Listeners
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new WarListener(this), this);

        // Tasks
        long autoSaveTicks = getConfig().getLong("data.auto-save-interval", 300) * 20L;
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new AutoSaveTask(this), autoSaveTicks, autoSaveTicks);

        long salaryTicks = Math.max(1, getConfig().getLong("treasury.salary.pay-interval", 86400)) * 20L;
        Bukkit.getScheduler().runTaskTimer(this, new SalaryPayTask(this), salaryTicks, salaryTicks);

        Bukkit.getScheduler().runTaskTimer(this, new QuestResetTask(this), 20L * 60L, 20L * 60L); // every minute

        log.info("GuildCore enabled.");
    }

    @Override
    public void onDisable() {
        new AutoSaveTask(this).run(); // flush data
        getLogger().info("GuildCore disabled.");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return economy != null;
    }

    public static GuildCore getInstance() { return instance; }
    public Economy getEconomy() { return economy; }

    public GuildManager getGuildManager() { return guildManager; }
    public GuildLevelManager getGuildLevelManager() { return guildLevelManager; }
    public TerritoryManager getTerritoryManager() { return territoryManager; }
    public GuildSkillManager getGuildSkillManager() { return guildSkillManager; }
    public GuildWarManager getGuildWarManager() { return guildWarManager; }
    public GuildTreasury getGuildTreasury() { return guildTreasury; }
    public GuildQuestManager getGuildQuestManager() { return guildQuestManager; }
    public AllianceManager getAllianceManager() { return allianceManager; }
    public GuildRanking getGuildRanking() { return guildRanking; }
    public GuildFame getGuildFame() { return guildFame; }

    public YamlGuildStorage getGuildStorage() { return guildStorage; }
    public YamlTerritoryStorage getTerritoryStorage() { return territoryStorage; }
    public YamlWarStorage getWarStorage() { return warStorage; }
    public YamlPlayerStorage getPlayerStorage() { return playerStorage; }
}