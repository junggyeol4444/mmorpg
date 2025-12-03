package com.multiverse.death.commands;

import com.multiverse.death.DeathAndRebirthCore;
import com.multiverse.death.utils.MessageUtil;
import com.multiverse.death.utils.ConfigUtil;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandManager {

    private final DeathAndRebirthCore plugin;
    private final MessageUtil msg;
    private final ConfigUtil configUtil;

    public CommandManager(DeathAndRebirthCore plugin, MessageUtil msg, ConfigUtil configUtil) {
        this.plugin = plugin;
        this.msg = msg;
        this.configUtil = configUtil;
        registerCommands();
    }

    private void registerCommands() {
        PluginCommand deathCmd = plugin.getCommand("death");
        if (deathCmd != null) {
            deathCmd.setExecutor(new PlayerCommand(plugin, msg));
            deathCmd.setTabCompleter(new PlayerCommand(plugin, msg));
        }
        PluginCommand soulcoinCmd = plugin.getCommand("soulcoin");
        if (soulcoinCmd != null) {
            soulcoinCmd.setExecutor(new PlayerCommand(plugin, msg));
            soulcoinCmd.setTabCompleter(new PlayerCommand(plugin, msg));
        }
        // Admin command is sub of /death admin
        // Register as /death admin in PlayerCommand, but /death admin uses AdminCommand internally.
        // Alternatively, register /deathadmin for direct access
        PluginCommand deathAdminCmd = plugin.getCommand("deathadmin");
        if (deathAdminCmd != null) {
            deathAdminCmd.setExecutor(new AdminCommand(plugin, msg));
            deathAdminCmd.setTabCompleter(new AdminCommand(plugin, msg));
        }
    }
}