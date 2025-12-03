package com.multiverse.death.commands;

import com.multiverse.death.DeathAndRebirthCore;
import com.multiverse.death.managers.*;
import com.multiverse.death.models.enums.*;
import com.multiverse.death.models.Insurance;
import com.multiverse.death.models.NetherRealmLocation;
import com.multiverse.death.models.NetherRealmNPC;
import com.multiverse.death.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminCommand implements TabExecutor {

    private final DeathAndRebirthCore plugin;
    private final DeathManager deathManager;
    private final RevivalManager revivalManager;
    private final InsuranceManager insuranceManager;
    private final SoulCoinManager soulCoinManager;
    private final NetherRealmManager netherRealmManager;
    private final NPCManager npcManager;
    private final MessageUtil msg;

    public AdminCommand(DeathAndRebirthCore plugin, MessageUtil msg) {
        this.plugin = plugin;
        this.deathManager = plugin.getDeathManager();
        this.revivalManager = plugin.getRevivalManager();
        this.insuranceManager = plugin.getInsuranceManager();
        this.soulCoinManager = plugin.getSoulCoinManager();
        this.netherRealmManager = plugin.getNetherRealmManager();
        this.npcManager = plugin.getNPCManager();
        this.msg = msg;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("death.admin")) {
            sender.sendMessage(msg.p("&c권한이 없습니다."));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        // 명령어 분기
        switch (args[0].toLowerCase()) {
            case "setlocation":
                handleSetLocation(sender, args);
                break;

            case "tp":
                handleTeleport(sender, args);
                break;

            case "revive":
                handleRevive(sender, args);
                break;

            case "insurance":
                handleInsurance(sender, args);
                break;

            case "soulcoin":
                handleSoulcoin(sender, args);
                break;

            case "spawnnpc":
                handleSpawnNPC(sender, args);
                break;

            case "removenpc":
                handleRemoveNPC(sender, args);
                break;

            case "stats":
                handleStats(sender);
                break;

            case "reload":
                plugin.reloadConfig();
                sender.sendMessage(msg.p("&a설정을 다시 불러왔습니다."));
                break;

            default:
                sendHelp(sender);
                break;
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(msg.p("&c[관리자 명령어]"));
        sender.sendMessage(msg.p("/death admin setlocation <타입>"));
        sender.sendMessage(msg.p("/death admin tp <플레이어> <타입>"));
        sender.sendMessage(msg.p("/death admin revive <플레이어>"));
        sender.sendMessage(msg.p("/death admin insurance give <플레이어> <타입>"));
        sender.sendMessage(msg.p("/death admin soulcoin <플레이어> <add|remove|set> <금액>"));
        sender.sendMessage(msg.p("/death admin spawnNPC <타입> <이름>"));
        sender.sendMessage(msg.p("/death admin removeNPC <ID>"));
        sender.sendMessage(msg.p("/death admin stats"));
        sender.sendMessage(msg.p("/death admin reload"));
    }

    private void handleSetLocation(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.p("&c이 명령어는 플레이어만 사용 가능합니다."));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(msg.p("&c타입을 입력해주세요: spawn, temple, bank, market, checkpoint, plaza"));
            return;
        }
        Player player = (Player) sender;
        String typeStr = args[1].toUpperCase();
        LocationType type;
        try {
            type = LocationType.valueOf(typeStr);
        } catch(Exception e) {
            sender.sendMessage(msg.p("&c잘못된 타입입니다."));
            return;
        }
        netherRealmManager.setLocation(type, player.getLocation());
        sender.sendMessage(msg.p("&a명계 위치 " + type + " 설정 완료."));
    }

    private void handleTeleport(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(msg.p("&c사용법: /death admin tp <플레이어> <타입>"));
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(msg.p("&c플레이어를 찾을 수 없습니다."));
            return;
        }
        LocationType type;
        try {
            type = LocationType.valueOf(args[2].toUpperCase());
        } catch(Exception e) {
            sender.sendMessage(msg.p("&c잘못된 타입입니다."));
            return;
        }
        netherRealmManager.teleportToNetherRealm(target, type);
        sender.sendMessage(msg.p("&a" + target.getName() + " 님을 명계 " + type + "로 이동시켰습니다."));
    }

    private void handleRevive(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(msg.p("&c사용법: /death admin revive <플레이어>"));
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(msg.p("&c플레이어를 찾을 수 없습니다."));
            return;
        }
        revivalManager.revivePlayer(target, true); // 강제 부활 (관리자용)
        sender.sendMessage(msg.p("&a플레이어를 강제로 부활시켰습니다."));
    }

    private void handleInsurance(CommandSender sender, String[] args) {
        if (args.length < 4 || !"give".equalsIgnoreCase(args[1])) {
            sender.sendMessage(msg.p("&c사용법: /death admin insurance give <플레이어> <타입>"));
            return;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(msg.p("&c플레이어를 찾을 수 없습니다."));
            return;
        }
        InsuranceType type;
        try {
            type = InsuranceType.valueOf(args[3].toUpperCase());
        } catch(Exception e) {
            sender.sendMessage(msg.p("&c잘못된 보험 타입입니다."));
            return;
        }
        insuranceManager.giveInsurance(target, type);
        sender.sendMessage(msg.p("&a" + target.getName() + "에게 " + type.name() + " 보험을 지급했습니다."));
    }

    private void handleSoulcoin(CommandSender sender, String[] args) {
        if (args.length < 5) {
            sender.sendMessage(msg.p("&c사용법: /death admin soulcoin <플레이어> <add|remove|set> <금액>"));
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(msg.p("&c플레이어를 찾을 수 없습니다."));
            return;
        }
        String action = args[2].toLowerCase();
        double amount;
        try {
            amount = Double.parseDouble(args[3]);
        } catch(Exception e) {
            sender.sendMessage(msg.p("&c숫자로 금액을 입력해주세요."));
            return;
        }
        switch (action) {
            case "add":
                soulCoinManager.addBalance(target, amount, "관리자 지급");
                sender.sendMessage(msg.p("&a" + target.getName() + "에게 " + amount + " SC 지급."));
                break;
            case "remove":
                soulCoinManager.removeBalance(target, amount, "관리자 차감");
                sender.sendMessage(msg.p("&a" + target.getName() + "에서 " + amount + " SC 차감."));
                break;
            case "set":
                soulCoinManager.setBalance(target, amount);
                sender.sendMessage(msg.p("&a" + target.getName() + " SC를 " + amount + "으로 설정."));
                break;
            default:
                sender.sendMessage(msg.p("&cadd, remove, set 중에서 선택해주세요."));
        }
    }

    private void handleSpawnNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.p("&c플레이어만 사용 가능합니다."));
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(msg.p("&c사용법: /death admin spawnNPC <타입> <이름>"));
            return;
        }
        Player player = (Player) sender;
        NPCType type;
        try {
            type = NPCType.valueOf(args[1].toUpperCase());
        } catch(Exception e) {
            sender.sendMessage(msg.p("&c잘못된 NPC 타입입니다."));
            return;
        }
        String name = args[2];
        Location location = player.getLocation();
        NetherRealmNPC npc = npcManager.createNPC(type, name, location);
        npcManager.spawnNPC(npc);
        sender.sendMessage(msg.p("&aNPC 생성됨: " + npc.getName() + " [" + type.name() + "]"));
    }

    private void handleRemoveNPC(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(msg.p("&c사용법: /death admin removeNPC <ID>"));
            return;
        }
        String id = args[1];
        npcManager.removeNPC(id);
        sender.sendMessage(msg.p("&aNPC를 제거했습니다: " + id));
    }

    private void handleStats(CommandSender sender) {
        sender.sendMessage(msg.p("&6[DeathAndRebirthCore 전체 통계]"));
        double totalCirc = soulCoinManager.getTotalCirculation();
        double totalBurn = soulCoinManager.getTotalBurned();
        double totalEarned = soulCoinManager.getTotalEarned();
        double totalSpent = soulCoinManager.getTotalSpent();
        sender.sendMessage(msg.p("&e총 유통량: " + totalCirc + " SC"));
        sender.sendMessage(msg.p("&e총 소각량: " + totalBurn + " SC"));
        sender.sendMessage(msg.p("&e총 획득량: " + totalEarned + " SC"));
        sender.sendMessage(msg.p("&e총 지출량: " + totalSpent + " SC"));
        sender.sendMessage(msg.p("&7-------------------------------"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> comp = new ArrayList<>();
        if (args.length == 1) {
            comp.add("setlocation");
            comp.add("tp");
            comp.add("revive");
            comp.add("insurance");
            comp.add("soulcoin");
            comp.add("spawnNPC");
            comp.add("removeNPC");
            comp.add("stats");
            comp.add("reload");
        } else if (args.length == 2 && "setlocation".equalsIgnoreCase(args[0])) {
            for (LocationType type : LocationType.values()) comp.add(type.name().toLowerCase());
        } else if (args.length == 2 && "insurance".equalsIgnoreCase(args[0])) {
            comp.add("give");
        } else if (args.length == 3 && "insurance".equalsIgnoreCase(args[0])) {
            for (InsuranceType type : InsuranceType.values()) comp.add(type.name().toLowerCase());
        }
        return comp;
    }

}