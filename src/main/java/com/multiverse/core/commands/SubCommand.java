package com.multiverse.core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class SubCommand {
    private final String name;
    private final String permission;

    public SubCommand(String name, String permission) {
        this.name = name;
        this.permission = permission;
    }

    public String getName() {
        return name;
    }
    public String getPermission() {
        return permission;
    }

    public abstract boolean execute(CommandSender sender, Command command, String label, String[] args);
}