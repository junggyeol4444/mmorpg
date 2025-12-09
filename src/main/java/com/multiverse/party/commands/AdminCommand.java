package com.multiverse.party.commands;

import com. multiverse.party. PartyCore;
import com. multiverse.party. models.Party;
import com.multiverse. party.models.PartyStatistics;
import org.bukkit. Bukkit;
import org.bukkit. Location;
import org. bukkit.command. Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit. command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java. util.Arrays;
import java. util.Date;
import java.util. List;
import java.util.UUID;

public class AdminCommand implements CommandExecutor, TabCompleter {

    private final PartyCore plugin;

    public AdminCommand(PartyCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (! sender.hasPermission("party. admin")) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("general.no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendAdminHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

        switch (subCommand) {
            case "reload":
                return handleReload(sender, subArgs);
            case "disband":
                return handleDisband(sender, subArgs);
            case "info":
                return handleInfo(sender, subArgs);
            case "list":
                return handleList(sender, subArgs);
            case "setlevel": 
                return handleSetLevel(sender, subArgs);
            case "addexp":
                return handleAddExp(sender, subArgs);
            case "kick":
                return handleKick(sender, subArgs);
            case "forceadd":
                return handleForceAdd(sender, subArgs);
            case "teleport":
            case "tp": 
                return handleTeleport(sender, subArgs);
            case "resetstats":
                return handleResetStats(sender, subArgs);
            case "save":
                return handleSave(sender, subArgs);
            case "backup":
                return handleBackup(sender, subArgs);
            default:
                sendAdminHelp(sender);
                return true;
        }
    }

    private boolean handleReload(CommandSender sender, String[] args) {
        long startTime = System.currentTimeMillis();
        
        try {
            plugin.reloadPlugin();
            
            long endTime = System. currentTimeMillis();
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.reload-success",
                    "%time%", String.valueOf(endTime - startTime)));
        } catch (Exception e) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.reload-failed",
                    "%error%", e. getMessage()));
            plugin.getLogger().severe("플러그인 리로드 실패: " + e. getMessage());
            e.printStackTrace();
        }

        return true;
    }

    private boolean handleDisband(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.usage.disband"));
            return true;
        }

        Party party = findParty(args[0]);
        
        if (party == null) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.party-not-found",
                    "%party%", args[0]));
            return true;
        }

        String partyName = party. getPartyName() != null ? party. getPartyName() : party.getPartyId().toString().substring(0, 8);

        plugin.getPartyChatManager().sendNotification(party,
                plugin.getMessageUtil().getMessage("admin.party-disbanded-notify"));

        plugin.getPartyManager().disbandPartyByAdmin(party);
        
        sender. sendMessage(plugin. getMessageUtil().getMessage("admin.party-disbanded",
                "%party%", partyName));

        return true;
    }

    private boolean handleInfo(CommandSender sender, String[] args) {
        if (args. length < 1) {
            sender. sendMessage(plugin. getMessageUtil().getMessage("admin.usage. info"));
            return true;
        }

        Party party = findParty(args[0]);
        
        if (party == null) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.party-not-found",
                    "%party%", args[0]));
            return true;
        }

        sender.sendMessage("§8§m----------------------------------------");
        sender.sendMessage("§6§l파티 관리자 정보");
        sender.sendMessage("§8§m----------------------------------------");
        
        sender.sendMessage("§7ID: §f" + party. getPartyId().toString());
        sender.sendMessage("§7이름: §f" + (party.getPartyName() != null ? party.getPartyName() : "없음"));
        
        Player leader = Bukkit.getPlayer(party.getLeaderId());
        String leaderName = leader != null ? leader.getName() : plugin.getPartyManager().getOfflinePlayerName(party.getLeaderId());
        sender.sendMessage("§7리더: §f" + leaderName + " (" + party.getLeaderId().toString().substring(0, 8) + ")");
        
        sender.sendMessage("§7멤버: §f" + party.getMembers().size() + "/" + party. getMaxMembers());
        for (UUID memberUUID : party. getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            String memberName = member != null ? member.getName() : plugin.getPartyManager().getOfflinePlayerName(memberUUID);
            String role = plugin.getPartyRoleManager().getRole(party, memberUUID).name();
            String status = member != null ? "§a온라인" : "§c오프라인";
            sender.sendMessage("  §8- §f" + memberName + " §7[" + role + "] " + status);
        }
        
        sender.sendMessage("§7파티 레벨: §f" + plugin.getPartyLevelManager().getPartyLevel(party));
        sender.sendMessage("§7파티 경험치: §f" + plugin.getPartyLevelManager().getPartyExp(party));
        
        sender.sendMessage("§7공개 설정: §f" + party.getPrivacy().name());
        sender.sendMessage("§7아이템 분배: §f" + party.getLootDistribution().name());
        sender.sendMessage("§7경험치 분배: §f" + party.getExpDistribution().name());
        
        PartyStatistics stats = plugin.getPartyStatisticsManager().getStatistics(party);
        sender.sendMessage("§7몬스터 처치: §f" + stats.getMonstersKilled());
        sender.sendMessage("§7보스 처치: §f" + stats. getBossesKilled());
        sender.sendMessage("§7던전 클리어: §f" + stats.getDungeonsCompleted());
        
        sender. sendMessage("§7생성 시간:  §f" + new Date(party.getCreatedTime()));
        
        sender.sendMessage("§8§m----------------------------------------");

        return true;
    }

    private boolean handleList(CommandSender sender, String[] args) {
        List<Party> parties = plugin.getPartyManager().getAllParties();
        
        if (parties.isEmpty()) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.no-parties"));
            return true;
        }

        int page = 1;
        if (args. length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }

        int perPage = 10;
        int totalPages = (int) Math.ceil((double) parties.size() / perPage);
        page = Math.max(1, Math.min(page, totalPages));

        int startIndex = (page - 1) * perPage;
        int endIndex = Math. min(startIndex + perPage, parties. size());

        sender.sendMessage("§8§m----------------------------------------");
        sender.sendMessage("§6§l파티 목록 §7(페이지 " + page + "/" + totalPages + ", 총 " + parties.size() + "개)");
        sender.sendMessage("§8§m----------------------------------------");

        for (int i = startIndex; i < endIndex; i++) {
            Party party = parties.get(i);
            String partyName = party.getPartyName() != null ? party.getPartyName() : "이름 없음";
            String id = party.getPartyId().toString().substring(0, 8);
            int members = party. getMembers().size();
            int maxMembers = party. getMaxMembers();
            int level = plugin.getPartyLevelManager().getPartyLevel(party);
            
            sender.sendMessage("§7" + (i + 1) + ". §f" + partyName + " §7(" + id + ") - " +
                    members + "/" + maxMembers + "명 Lv." + level);
        }

        if (totalPages > 1) {
            sender.sendMessage("§7/party admin list <페이지> 로 다른 페이지 확인");
        }
        sender.sendMessage("§8§m----------------------------------------");

        return true;
    }

    private boolean handleSetLevel(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.usage.setlevel"));
            return true;
        }

        Party party = findParty(args[0]);
        
        if (party == null) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.party-not-found",
                    "%party%", args[0]));
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.invalid-number"));
            return true;
        }

        int maxLevel = plugin.getConfig().getInt("party-level.max-level", 50);
        if (level < 1 || level > maxLevel) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.invalid-level",
                    "%min%", "1",
                    "%max%", String.valueOf(maxLevel)));
            return true;
        }

        plugin.getPartyLevelManager().setPartyLevel(party, level);
        
        String partyName = party. getPartyName() != null ? party. getPartyName() : party.getPartyId().toString().substring(0, 8);
        sender.sendMessage(plugin.getMessageUtil().getMessage("admin.level-set",
                "%party%", partyName,
                "%level%", String.valueOf(level)));

        return true;
    }

    private boolean handleAddExp(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.usage.addexp"));
            return true;
        }

        Party party = findParty(args[0]);
        
        if (party == null) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.party-not-found",
                    "%party%", args[0]));
            return true;
        }

        long amount;
        try {
            amount = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            sender. sendMessage(plugin. getMessageUtil().getMessage("admin.invalid-number"));
            return true;
        }

        if (amount <= 0) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.invalid-exp-amount"));
            return true;
        }

        plugin.getPartyLevelManager().addPartyExp(party, amount);
        
        String partyName = party.getPartyName() != null ? party.getPartyName() : party.getPartyId().toString().substring(0, 8);
        sender.sendMessage(plugin.getMessageUtil().getMessage("admin.exp-added",
                "%party%", partyName,
                "%amount%", String.valueOf(amount)));

        return true;
    }

    private boolean handleKick(CommandSender sender, String[] args) {
        if (args. length < 2) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.usage.kick"));
            return true;
        }

        Party party = findParty(args[0]);
        
        if (party == null) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.party-not-found",
                    "%party%", args[0]));
            return true;
        }

        Player target = Bukkit. getPlayer(args[1]);
        UUID targetUUID;
        String targetName;
        
        if (target != null) {
            targetUUID = target.getUniqueId();
            targetName = target.getName();
        } else {
            targetUUID = plugin.getPartyManager().getOfflinePlayerUUID(args[1]);
            targetName = args[1];
            
            if (targetUUID == null) {
                sender. sendMessage(plugin. getMessageUtil().getMessage("general.player-not-found",
                        "%player%", args[1]));
                return true;
            }
        }

        if (! party.getMembers().contains(targetUUID)) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("party.target-not-in-party",
                    "%player%", targetName));
            return true;
        }

        plugin.getPartyManager().removeMember(party, targetUUID);
        
        if (target != null) {
            target.sendMessage(plugin.getMessageUtil().getMessage("admin.you-were-kicked-admin"));
        }
        
        sender.sendMessage(plugin.getMessageUtil().getMessage("admin.player-kicked",
                "%player%", targetName));

        return true;
    }

    private boolean handleForceAdd(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.usage.forceadd"));
            return true;
        }

        Party party = findParty(args[0]);
        
        if (party == null) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.party-not-found",
                    "%party%", args[0]));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        
        if (target == null) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("general. player-not-found",
                    "%player%", args[1]));
            return true;
        }

        if (plugin.getPartyManager().isInParty(target)) {
            Party currentParty = plugin. getPartyManager().getPlayerParty(target);
            plugin.getPartyManager().removeMember(currentParty, target.getUniqueId());
        }

        boolean success = plugin.getPartyManager().addMember(party, target);
        
        if (success) {
            target.sendMessage(plugin.getMessageUtil().getMessage("admin.you-were-added-admin"));
            
            String partyName = party.getPartyName() != null ? party.getPartyName() : party.getPartyId().toString().substring(0, 8);
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.player-added",
                    "%player%", target.getName(),
                    "%party%", partyName));
        } else {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.add-failed"));
        }

        return true;
    }

    private boolean handleTeleport(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("general.player-only"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.usage.teleport"));
            return true;
        }

        Party party = findParty(args[0]);
        
        if (party == null) {
            sender. sendMessage(plugin. getMessageUtil().getMessage("admin.party-not-found",
                    "%party%", args[0]));
            return true;
        }

        Player admin = (Player) sender;
        Player leader = Bukkit. getPlayer(party. getLeaderId());
        
        if (leader != null) {
            admin.teleport(leader. getLocation());
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.teleported",
                    "%player%", leader.getName()));
        } else {
            for (UUID memberUUID : party.getMembers()) {
                Player member = Bukkit. getPlayer(memberUUID);
                if (member != null) {
                    admin.teleport(member.getLocation());
                    sender.sendMessage(plugin.getMessageUtil().getMessage("admin.teleported",
                            "%player%", member.getName()));
                    return true;
                }
            }
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.no-online-members"));
        }

        return true;
    }

    private boolean handleResetStats(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.usage.resetstats"));
            return true;
        }

        Party party = findParty(args[0]);
        
        if (party == null) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.party-not-found",
                    "%party%", args[0]));
            return true;
        }

        plugin.getPartyStatisticsManager().resetStatistics(party);
        
        String partyName = party.getPartyName() != null ? party.getPartyName() : party.getPartyId().toString().substring(0, 8);
        sender.sendMessage(plugin.getMessageUtil().getMessage("admin.stats-reset",
                "%party%", partyName));

        return true;
    }

    private boolean handleSave(CommandSender sender, String[] args) {
        long startTime = System. currentTimeMillis();
        
        try {
            plugin. getDataManager().saveAllParties();
            plugin.getDataManager().saveAllPlayerData();
            plugin.getDataManager().saveAllListings();
            
            long endTime = System. currentTimeMillis();
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.save-success",
                    "%time%", String. valueOf(endTime - startTime)));
        } catch (Exception e) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.save-failed",
                    "%error%", e.getMessage()));
            plugin.getLogger().severe("데이터 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    private boolean handleBackup(CommandSender sender, String[] args) {
        long startTime = System.currentTimeMillis();
        
        try {
            plugin.getDataManager().saveAllParties();
            plugin.getDataManager().saveAllPlayerData();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String timestamp = sdf. format(new Date());
            
            File backupDir = new File(plugin.getDataFolder(), "backups/" + timestamp);
            backupDir.mkdirs();
            
            File partiesDir = new File(plugin.getDataFolder(), "parties");
            File playersDir = new File(plugin.getDataFolder(), "players");
            
            if (partiesDir.exists()) {
                copyDirectory(partiesDir, new File(backupDir, "parties"));
            }
            
            if (playersDir.exists()) {
                copyDirectory(playersDir, new File(backupDir, "players"));
            }
            
            File configFile = new File(plugin.getDataFolder(), "config.yml");
            if (configFile.exists()) {
                Files. copy(configFile. toPath(), new File(backupDir, "config.yml").toPath(), StandardCopyOption. REPLACE_EXISTING);
            }
            
            cleanOldBackups();
            
            long endTime = System. currentTimeMillis();
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.backup-success",
                    "%time%", String. valueOf(endTime - startTime),
                    "%path%", backupDir.getPath()));
        } catch (Exception e) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("admin.backup-failed",
                    "%error%", e. getMessage()));
            plugin.getLogger().severe("백업 실패: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    private void copyDirectory(File source, File target) throws IOException {
        if (! target.exists()) {
            target.mkdirs();
        }
        
        File[] files = source. listFiles();
        if (files != null) {
            for (File file : files) {
                File targetFile = new File(target, file. getName());
                if (file.isDirectory()) {
                    copyDirectory(file, targetFile);
                } else {
                    Files.copy(file. toPath(), targetFile.toPath(), StandardCopyOption. REPLACE_EXISTING);
                }
            }
        }
    }

    private void cleanOldBackups() {
        int maxBackups = plugin.getConfig().getInt("data. max-backups", 10);
        File backupsDir = new File(plugin.getDataFolder(), "backups");
        
        if (! backupsDir. exists()) return;
        
        File[] backups = backupsDir.listFiles(File:: isDirectory);
        if (backups == null || backups.length <= maxBackups) return;
        
        Arrays.sort(backups, (a, b) -> Long.compare(a.lastModified(), b.lastModified()));
        
        int toDelete = backups.length - maxBackups;
        for (int i = 0; i < toDelete; i++) {
            deleteDirectory(backups[i]);
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory. listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    private Party findParty(String identifier) {
        try {
            UUID partyId = UUID. fromString(identifier);
            Party party = plugin.getPartyManager().getParty(partyId);
            if (party != null) return party;
        } catch (IllegalArgumentException ignored) {}
        
        Party party = plugin.getPartyManager().getPartyByName(identifier);
        if (party != null) return party;
        
        Player player = Bukkit. getPlayer(identifier);
        if (player != null) {
            return plugin.getPartyManager().getPlayerParty(player);
        }
        
        UUID playerUUID = plugin. getPartyManager().getOfflinePlayerUUID(identifier);
        if (playerUUID != null) {
            return plugin.getPartyManager().getPlayerParty(playerUUID);
        }
        
        return null;
    }

    private void sendAdminHelp(CommandSender sender) {
        sender.sendMessage("§8§m----------------------------------------");
        sender.sendMessage("§6§lPartyCore 관리자 명령어");
        sender.sendMessage("§8§m----------------------------------------");
        sender.sendMessage("§e/party admin reload §7- 플러그인 리로드");
        sender.sendMessage("§e/party admin disband <파티> §7- 파티 강제 해체");
        sender.sendMessage("§e/party admin info <파티> §7- 파티 상세 정보");
        sender.sendMessage("§e/party admin list [페이지] §7- 모든 파티 목록");
        sender.sendMessage("§e/party admin setlevel <파티> <레벨> §7- 파티 레벨 설정");
        sender.sendMessage("§e/party admin addexp <파티> <경험치> §7- 파티 경험치 추가");
        sender.sendMessage("§e/party admin kick <파티> <플레이어> §7- 강제 추방");
        sender.sendMessage("§e/party admin forceadd <파티> <플레이어> §7- 강제 가입");
        sender.sendMessage("§e/party admin tp <파티> §7- 파티원에게 텔레포트");
        sender.sendMessage("§e/party admin resetstats <파티> §7- 통계 초기화");
        sender.sendMessage("§e/party admin save §7- 데이터 즉시 저장");
        sender.sendMessage("§e/party admin backup §7- 데이터 백업");
        sender.sendMessage("§8§m----------------------------------------");
        sender.sendMessage("§7<파티>:  파티ID, 파티이름, 또는 멤버 이름");
        sender.sendMessage("§8§m----------------------------------------");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (! sender.hasPermission("party. admin")) {
            return completions;
        }

        if (args. length == 1) {
            completions.addAll(Arrays.asList(
                    "reload", "disband", "info", "list", "setlevel", "addexp",
                    "kick", "forceadd", "tp", "teleport", "resetstats", "save", "backup"
            ));
            return filterCompletions(completions, args[0]);
        }

        if (args.length >= 2) {
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "disband":
                case "info":
                case "setlevel":
                case "addexp": 
                case "kick":
                case "tp":
                case "teleport":
                case "resetstats":
                    if (args.length == 2) {
                        for (Party party : plugin.getPartyManager().getAllParties()) {
                            if (party.getPartyName() != null) {
                                completions.add(party.getPartyName().replace(" ", "_"));
                            }
                            completions.add(party.getPartyId().toString().substring(0, 8));
                        }
                    }
                    break;
                    
                case "forceadd": 
                    if (args.length == 2) {
                        for (Party party :  plugin.getPartyManager().getAllParties()) {
                            if (party.getPartyName() != null) {
                                completions.add(party.getPartyName().replace(" ", "_"));
                            }
                        }
                    } else if (args. length == 3) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            completions.add(player.getName());
                        }
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
}