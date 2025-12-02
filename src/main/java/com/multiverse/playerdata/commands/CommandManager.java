package com.multiverse.playerdata.commands;

import com.multiverse.playerdata.PlayerDataCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final PlayerDataCore plugin;
    private final AdminCommand adminCommand;
    private final PlayerCommand playerCommand;

    public CommandManager(PlayerDataCore plugin) {
        this.plugin = plugin;
        this.adminCommand = new AdminCommand(plugin);
        this.playerCommand = new PlayerCommand(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /pdata admin 하위 명령 처리
        if (args.length > 0 && args[0].equalsIgnoreCase("admin")) {
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            return adminCommand.onCommand(sender, command, label, subArgs);
        } else {
            // 플레이어 명령어
            return playerCommand.onCommand(sender, command, label, args);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> base = new ArrayList<>(Arrays.asList(
                    "info", "stats", "race", "evolution", "transcend"
            ));
            // 관리자 권한이면 admin 추가
            if (sender.hasPermission("playerdata.admin")) base.add("admin");
            return base.stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("admin")) {
            return Arrays.asList(
                    "view", "setrace", "setstat", "addpoints", "evolve", "transcend",
                    "save", "backup", "restore", "reload", "migrate"
            ).stream().filter(s -> s.startsWith(args[1].toLowerCase())).collect(Collectors.toList());
        }
        // 하위 명령어 자동완성 (간략 예시, 더 상세하게 추가 가능)
        return Collections.emptyList();
    }
}