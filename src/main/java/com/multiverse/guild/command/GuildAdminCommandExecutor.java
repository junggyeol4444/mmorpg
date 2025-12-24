package com.multiverse.guild.command;

import com.multiverse.guild.GuildCore;
import com.multiverse.guild.manager.GuildManager;
import com.multiverse.guild.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GuildAdminCommandExecutor implements CommandExecutor {

    private final GuildCore plugin;
    private final GuildManager guildManager;

    public GuildAdminCommandExecutor(GuildCore plugin) {
        this.plugin = plugin;
        this.guildManager = plugin.getGuildManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("guild.admin")) {
            sender.sendMessage(Message.prefixed("&c권한이 없습니다."));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(Message.prefixed("&e/guild admin reload"));
            return true;
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "reload" -> {
                plugin.reloadConfig();
                sender.sendMessage(Message.prefixed("&aconfig.yml reloaded."));
            }
            default -> sender.sendMessage(Message.prefixed("&cUnknown admin subcommand."));
        }
        return true;
    }
}