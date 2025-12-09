package com.multiverse.party.commands;

import com. multiverse.party. PartyCore;
import org.bukkit.command.Command;
import org.bukkit. command.CommandExecutor;
import org. bukkit.command. CommandSender;
import org.bukkit.command. PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util. ArrayList;
import java. util.Arrays;
import java. util.HashMap;
import java. util.List;
import java.util. Map;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final PartyCore plugin;
    private final Map<String, SubCommand> subCommands;
    private final PartyCommand partyCommand;
    private final PartyChatCommand partyChatCommand;
    private final AdminCommand adminCommand;
    
    public CommandManager(PartyCore plugin) {
        this. plugin = plugin;
        this.subCommands = new HashMap<>();
        this.partyCommand = new PartyCommand(plugin);
        this.partyChatCommand = new PartyChatCommand(plugin);
        this.adminCommand = new AdminCommand(plugin);
        
        registerSubCommands();
    }
    
    public void registerCommands() {
        PluginCommand partyCmd = plugin. getCommand("party");
        if (partyCmd != null) {
            partyCmd.setExecutor(this);
            partyCmd. setTabCompleter(this);
        }
        
        PluginCommand pCmd = plugin.getCommand("p");
        if (pCmd != null) {
            pCmd.setExecutor(partyChatCommand);
            pCmd.setTabCompleter(partyChatCommand);
        }
    }
    
    private void registerSubCommands() {
        // 파티 생성/관리
        subCommands. put("create", new SubCommand("create", "party. player.create", "파티를 생성합니다.", "[이름]"));
        subCommands.put("disband", new SubCommand("disband", "party.player.create", "파티를 해체합니다.", ""));
        
        // 초대/가입
        subCommands.put("invite", new SubCommand("invite", "party. player.invite", "플레이어를 초대합니다.", "<플레이어>"));
        subCommands.put("accept", new SubCommand("accept", "party. player.join", "초대를 수락합니다.", ""));
        subCommands.put("decline", new SubCommand("decline", "party. player.join", "초대를 거절합니다.", ""));
        subCommands.put("join", new SubCommand("join", "party. player.join", "공개 파티에 참가합니다.", "<파티이름>"));
        
        // 탈퇴/추방
        subCommands.put("leave", new SubCommand("leave", "party. player.create", "파티를 떠납니다.", ""));
        subCommands. put("kick", new SubCommand("kick", "party. player.invite", "멤버를 추방합니다.", "<플레이어>"));
        
        // 역할 관리
        subCommands.put("promote", new SubCommand("promote", "party. player.invite", "멤버를 부리더로 승격합니다.", "<플레이어>"));
        subCommands.put("demote", new SubCommand("demote", "party.player. invite", "부리더를 일반 멤버로 강등합니다.", "<플레이어>"));
        subCommands.put("transfer", new SubCommand("transfer", "party.player.create", "리더 권한을 위임합니다.", "<플레이어>"));
        
        // 정보
        subCommands. put("info", new SubCommand("info", "party. player.create", "파티 정보를 확인합니다.", ""));
        subCommands.put("list", new SubCommand("list", "party. player.create", "파티 멤버 목록을 확인합니다.", ""));
        subCommands.put("stats", new SubCommand("stats", "party. player.create", "파티 통계를 확인합니다.", ""));
        
        // 설정
        subCommands.put("settings", new SubCommand("settings", "party. player.create", "파티 설정을 엽니다.", ""));
        subCommands.put("privacy", new SubCommand("privacy", "party. player.create", "파티 공개 설정을 변경합니다.", "<public|invite|private>"));
        subCommands.put("loot", new SubCommand("loot", "party.player.create", "아이템 분배 방식을 변경합니다.", "<free|round|need|master>"));
        subCommands.put("exp", new SubCommand("exp", "party.player.create", "경험치 분배 방식을 변경합니다.", "<equal|level|contribution>"));
        
        // 채팅
        subCommands.put("chat", new SubCommand("chat", "party.player.chat", "파티 채팅을 보냅니다.", "<메시지>"));
        subCommands. put("announce", new SubCommand("announce", "party. player.chat", "파티 공지를 보냅니다.", "<메시지>"));
        
        // 파티 찾기
        subCommands.put("finder", new SubCommand("finder", "party. player.create", "파티 찾기를 엽니다.", ""));
        subCommands.put("search", new SubCommand("search", "party. player.create", "파티를 검색합니다.", "[검색어]"));
        subCommands.put("queue", new SubCommand("queue", "party. player.create", "자동 매칭 대기열에 참가합니다.", "<던전ID>"));
        subCommands.put("cancelqueue", new SubCommand("cancelqueue", "party.player.create", "매칭 대기를 취소합니다.", ""));
        
        // 스킬
        subCommands.put("skill", new SubCommand("skill", "party.player.create", "파티 스킬을 사용합니다.", "<스킬ID>"));
        subCommands.put("skills", new SubCommand("skills", "party. player.create", "파티 스킬 목록을 확인합니다.", ""));
        
        // GUI
        subCommands.put("menu", new SubCommand("menu", "party. player.create", "파티 메뉴를 엽니다.", ""));
        
        // 관리자
        subCommands. put("admin", new SubCommand("admin", "party.admin", "관리자 명령어", "<reload|disband|info>"));
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length > 0 && args[0].equalsIgnoreCase("admin")) {
                return adminCommand.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
            }
            sender.sendMessage(plugin.getMessageUtil().getMessage("general.player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (! plugin.getConfig().getBoolean("party.enabled", true)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("general.disabled"));
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        
        String subCommandName = args[0].toLowerCase();
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        
        switch (subCommandName) {
            case "create":
                return partyCommand.handleCreate(player, subArgs);
            case "disband": 
                return partyCommand.handleDisband(player, subArgs);
            case "invite":
                return partyCommand.handleInvite(player, subArgs);
            case "accept": 
                return partyCommand.handleAccept(player, subArgs);
            case "decline":
                return partyCommand.handleDecline(player, subArgs);
            case "join":
                return partyCommand. handleJoin(player, subArgs);
            case "leave": 
                return partyCommand.handleLeave(player, subArgs);
            case "kick":
                return partyCommand.handleKick(player, subArgs);
            case "promote": 
                return partyCommand.handlePromote(player, subArgs);
            case "demote":
                return partyCommand.handleDemote(player, subArgs);
            case "transfer": 
                return partyCommand.handleTransfer(player, subArgs);
            case "info":
                return partyCommand.handleInfo(player, subArgs);
            case "list":
                return partyCommand. handleList(player, subArgs);
            case "stats": 
                return partyCommand.handleStats(player, subArgs);
            case "settings": 
                return partyCommand.handleSettings(player, subArgs);
            case "privacy":
                return partyCommand.handlePrivacy(player, subArgs);
            case "loot": 
                return partyCommand.handleLoot(player, subArgs);
            case "exp":
                return partyCommand.handleExp(player, subArgs);
            case "chat":
                return partyCommand.handleChat(player, subArgs);
            case "announce":
                return partyCommand. handleAnnounce(player, subArgs);
            case "finder":
                return partyCommand.handleFinder(player, subArgs);
            case "search": 
                return partyCommand.handleSearch(player, subArgs);
            case "queue": 
                return partyCommand.handleQueue(player, subArgs);
            case "cancelqueue":
                return partyCommand.handleCancelQueue(player, subArgs);
            case "skill": 
                return partyCommand.handleSkill(player, subArgs);
            case "skills":
                return partyCommand.handleSkills(player, subArgs);
            case "menu": 
                return partyCommand.handleMenu(player, subArgs);
            case "admin": 
                if (! player.hasPermission("party.admin")) {
                    player. sendMessage(plugin. getMessageUtil().getMessage("general.no-permission"));
                    return true;
                }
                return adminCommand.onCommand(player, command, label, subArgs);
            case "help":
            default:
                sendHelpMessage(player);
                return true;
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (!(sender instanceof Player)) {
            if (args.length == 1) {
                completions.add("admin");
            }
            return filterCompletions(completions, args[args.length - 1]);
        }
        
        Player player = (Player) sender;
        
        if (args.length == 1) {
            for (Map.Entry<String, SubCommand> entry :  subCommands.entrySet()) {
                if (player.hasPermission(entry. getValue().getPermission())) {
                    completions.add(entry.getKey());
                }
            }
            return filterCompletions(completions, args[0]);
        }
        
        if (args.length >= 2) {
            String subCommand = args[0].toLowerCase();
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            
            switch (subCommand) {
                case "invite":
                case "kick":
                case "promote":
                case "demote": 
                case "transfer": 
                    return getOnlinePlayerCompletions(player, subArgs[subArgs.length - 1]);
                    
                case "privacy":
                    if (args.length == 2) {
                        completions.addAll(Arrays.asList("public", "invite", "private"));
                    }
                    break;
                    
                case "loot":
                    if (args.length == 2) {
                        completions. addAll(Arrays. asList("free", "round", "need", "master"));
                    }
                    break;
                    
                case "exp":
                    if (args.length == 2) {
                        completions.addAll(Arrays.asList("equal", "level", "contribution"));
                    }
                    break;
                    
                case "skill":
                    if (args.length == 2) {
                        completions. addAll(getAvailableSkillCompletions(player));
                    }
                    break;
                    
                case "admin":
                    return adminCommand.onTabComplete(sender, command, alias, subArgs);
                    
                case "join":
                case "search":
                    if (args.length == 2) {
                        completions. addAll(getPublicPartyCompletions());
                    }
                    break;
                    
                case "queue":
                    if (args.length == 2) {
                        completions.addAll(getDungeonCompletions());
                    }
                    break;
            }
            
            return filterCompletions(completions, args[args.length - 1]);
        }
        
        return completions;
    }
    
    private List<String> filterCompletions(List<String> completions, String prefix) {
        List<String> filtered = new ArrayList<>();
        String lowerPrefix = prefix.toLowerCase();
        
        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(lowerPrefix)) {
                filtered.add(completion);
            }
        }
        
        return filtered;
    }
    
    private List<String> getOnlinePlayerCompletions(Player excludePlayer, String prefix) {
        List<String> completions = new ArrayList<>();
        
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (! player.equals(excludePlayer)) {
                completions.add(player.getName());
            }
        }
        
        return filterCompletions(completions, prefix);
    }
    
    private List<String> getAvailableSkillCompletions(Player player) {
        List<String> completions = new ArrayList<>();
        
        var party = plugin.getPartyManager().getPlayerParty(player);
        if (party != null) {
            var partyLevel = party.getPartyLevel();
            if (partyLevel != null) {
                completions.addAll(partyLevel.getLearnedSkills());
            }
        }
        
        return completions;
    }
    
    private List<String> getPublicPartyCompletions() {
        List<String> completions = new ArrayList<>();
        
        for (var party : plugin.getPartyFinder().getPublicParties()) {
            String name = party.getPartyName();
            if (name != null && ! name.isEmpty()) {
                completions.add(name. replace(" ", "_"));
            }
        }
        
        return completions;
    }
    
    private List<String> getDungeonCompletions() {
        List<String> completions = new ArrayList<>();
        
        if (plugin.getIntegrationManager().isDungeonCoreEnabled()) {
            completions.addAll(plugin.getIntegrationManager().getDungeonCoreIntegration().getAvailableDungeons());
        } else {
            completions.addAll(Arrays.asList("dungeon1", "dungeon2", "dungeon3"));
        }
        
        return completions;
    }
    
    private void sendHelpMessage(Player player) {
        player.sendMessage(plugin.getMessageUtil().getMessage("help.header"));
        
        for (Map.Entry<String, SubCommand> entry : subCommands.entrySet()) {
            SubCommand subCommand = entry.getValue();
            
            if (player.hasPermission(subCommand.getPermission())) {
                String usage = "/party " + subCommand.getName();
                if (! subCommand.getArgs().isEmpty()) {
                    usage += " " + subCommand.getArgs();
                }
                
                player.sendMessage(plugin.getMessageUtil().getMessage("help.command-format",
                        "%command%", usage,
                        "%description%", subCommand. getDescription()));
            }
        }
        
        player.sendMessage(plugin.getMessageUtil().getMessage("help.footer"));
    }
    
    public Map<String, SubCommand> getSubCommands() {
        return subCommands;
    }
    
    public static class SubCommand {
        private final String name;
        private final String permission;
        private final String description;
        private final String args;
        
        public SubCommand(String name, String permission, String description, String args) {
            this.name = name;
            this. permission = permission;
            this.description = description;
            this.args = args;
        }
        
        public String getName() {
            return name;
        }
        
        public String getPermission() {
            return permission;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getArgs() {
            return args;
        }
    }
}