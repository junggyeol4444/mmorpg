package com. multiverse.combat. commands;

import org.bukkit.command.CommandMap;
import org.bukkit. command. PluginCommand;
import com.multiverse.combat.CombatCore;
import java.lang.reflect.Field;

/**
 * 명령어 관리 클래스
 * 모든 플러그인 명령어를 등록하고 관리합니다.
 */
public class CommandManager {
    
    private final CombatCore plugin;
    private AdminCommand adminCommand;
    private CombatCommand combatCommand;
    
    /**
     * CommandManager 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public CommandManager(CombatCore plugin) {
        this. plugin = plugin;
        this. adminCommand = new AdminCommand(plugin);
        this.combatCommand = new CombatCommand(plugin);
    }
    
    /**
     * 모든 명령어 등록
     */
    public void register() {
        try {
            // plugin.yml에서 등록된 명령어 가져오기
            PluginCommand combatCmd = plugin.getCommand("combat");
            
            if (combatCmd != null) {
                combatCmd.setExecutor((sender, cmd, label, args) -> {
                    if (args.length > 0 && args[0].equalsIgnoreCase("admin")) {
                        return adminCommand.onCommand(sender, cmd, label, args);
                    } else {
                        return combatCommand.onCommand(sender, cmd, label, args);
                    }
                });
                
                combatCmd.setTabCompleter((sender, cmd, label, args) -> {
                    return getTabCompletions(sender, args);
                });
            }
            
            plugin.getLogger().info("✓ 명령어 등록 완료");
            
        } catch (Exception e) {
            plugin.getLogger(). severe("명령어 등록 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 탭 완성 제공
     */
    private java.util.List<String> getTabCompletions(org.bukkit.command.CommandSender sender, String[] args) {
        java.util.List<String> completions = new java.util.ArrayList<>();
        
        if (args.length == 1) {
            completions.add("skills");
            completions.add("skill");
            completions.add("stats");
            completions.add("pvp");
            completions.add("ranking");
            
            if (sender. hasPermission("combat.admin")) {
                completions.add("admin");
            }
        } else if (args.length > 1 && args[0].equalsIgnoreCase("admin")) {
            if (args.length == 2) {
                completions.add("skill");
                completions.add("resetcooldowns");
                completions.add("effect");
                completions.add("cleareffects");
                completions.add("pvp");
                completions.add("reload");
            } else if (args.length == 3 && args[1].equalsIgnoreCase("skill")) {
                completions.add("give");
                completions.add("setlevel");
            }
        } else if (args.length > 1 && args[0].equalsIgnoreCase("skill")) {
            if (args.length == 2) {
                completions.add("info");
                completions.add("bind");
            }
        } else if (args.length > 1 && args[0].equalsIgnoreCase("pvp")) {
            completions.add("on");
            completions.add("off");
        }
        
        return completions;
    }
}