package com.multiverse.core.commands;

import com.multiverse.core.MultiverseCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor {
    private final MultiverseCore plugin;
    private final List<SubCommand> subCommands = new ArrayList<>();

    public CommandManager(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    public void register(SubCommand subCommand) {
        subCommands.add(subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 관리자 명령어와 플레이어 명령어 구분
        if ("mdim".equalsIgnoreCase(label) || "multidim".equalsIgnoreCase(label) || "multiversedim".equalsIgnoreCase(label)) {
            // 관리자 서브커맨드 실행
            for (SubCommand sub : subCommands) {
                if (sub.getName().equals("admin")) {
                    return sub.execute(sender, command, label, args);
                }
            }
        } else if ("dimension".equalsIgnoreCase(label) || "dim".equalsIgnoreCase(label)) {
            // 플레이어 서브커맨드 실행
            for (SubCommand sub : subCommands) {
                if (sub.getName().equals("player")) {
                    return sub.execute(sender, command, label, args);
                }
            }
        }
        sender.sendMessage("§c명령어를 확인해주세요.");
        return true;
    }
}