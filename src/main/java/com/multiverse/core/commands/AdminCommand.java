package com.multiverse.core.commands;

import com.multiverse.core.MultiverseCore;
import com.multiverse.core.managers.*;
import com.multiverse.core.models.enums.DimensionType;
import com.multiverse.core.models.enums.PortalType;
import com.multiverse.core.models.Dimension;
import com.multiverse.core.models.FusionStatus;
import com.multiverse.core.models.Portal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand extends SubCommand {
    private final MultiverseCore plugin;

    public AdminCommand(MultiverseCore plugin) {
        super("admin", "multiverse.admin");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("multiverse.admin")) {
            sender.sendMessage(plugin.getMessageUtil().get("no-permission"));
            return true;
        }

        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "create":
                // /mdim create <차원ID> <월드이름> <타입>
                if (args.length < 4) {
                    sender.sendMessage("§c사용법: /mdim create <차원ID> <월드이름> <타입>");
                    return true;
                }
                String id = args[1];
                String worldName = args[2];
                String typeStr = args[3].toUpperCase();
                DimensionType type;
                try {
                    type = DimensionType.valueOf(typeStr);
                } catch (Exception e) {
                    sender.sendMessage("§c타입은 MAIN/UPPER/LOWER/HUB 중 하나여야 합니다.");
                    return true;
                }
                Dimension dim = new Dimension(id, plugin.getConfigUtil().getDimensionDisplayName(id), worldName, type, 50, 1.0, true, 0, null);
                plugin.getDimensionManager().createDimension(dim);
                sender.sendMessage("§a차원 " + id + " 생성됨.");
                break;
            case "delete":
                // /mdim delete <차원ID>
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /mdim delete <차원ID>");
                    return true;
                }
                id = args[1];
                plugin.getDimensionManager().deleteDimension(id);
                sender.sendMessage("§c차원 " + id + " 비활성화됨.");
                break;
            case "activate":
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /mdim activate <차원ID>");
                    return true;
                }
                id = args[1];
                plugin.getDimensionManager().activateDimension(id);
                sender.sendMessage("§a차원 " + id + " 활성화됨.");
                break;
            case "deactivate":
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /mdim deactivate <차원ID>");
                    return true;
                }
                id = args[1];
                plugin.getDimensionManager().deactivateDimension(id);
                sender.sendMessage("§c차원 " + id + " 비활성화됨.");
                break;
            case "balance":
                // /mdim balance <차원ID> [set|add] [값]
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /mdim balance <차원ID> [set|add] [값]");
                    return true;
                }
                id = args[1];
                if (args.length == 2) {
                    int val = plugin.getBalanceManager().getBalance(id);
                    sender.sendMessage("§e차원 " + id + " 균형도: §b" + val + "/100");
                } else if (args.length == 4) {
                    String op = args[2];
                    int value;
                    try {
                        value = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§c값은 숫자여야 합니다.");
                        return true;
                    }
                    if ("set".equalsIgnoreCase(op)) {
                        plugin.getBalanceManager().setBalance(id, value);
                        sender.sendMessage("§e차원 " + id + " 균형도가 " + value + "으로 설정됨.");
                    } else if ("add".equalsIgnoreCase(op)) {
                        plugin.getBalanceManager().adjustBalance(id, value, "관리자 명령어");
                        int after = plugin.getBalanceManager().getBalance(id);
                        sender.sendMessage("§e차원 " + id + " 균형도가 " + after + "로 조정됨.");
                    }
                } else {
                    sender.sendMessage("§c사용법: /mdim balance <차원ID> [set|add] [값]");
                }
                break;
            case "tp":
                // /mdim tp <플레이어> <차원ID>
                if (args.length < 3) {
                    sender.sendMessage("§c사용법: /mdim tp <플레이어> <차원ID>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                id = args[2];
                if (target == null) {
                    sender.sendMessage("§c플레이어를 찾을 수 없습니다.");
                    return true;
                }
                plugin.getTeleportManager().teleportToDimension(target, id, true);
                sender.sendMessage("§a" + target.getName() + " 님을 " + id + " 차원으로 이동시켰습니다.");
                break;
            case "portal":
                // /mdim portal create <이름> <시작차원> <도착차원> [비용]
                if (args.length >= 2) {
                    String op = args[1].toLowerCase();
                    if ("create".equals(op)) {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage("§c플레이어만 사용 가능합니다.");
                            return true;
                        }
                        if (args.length < 5) {
                            sender.sendMessage("§c사용법: /mdim portal create <이름> <시작차원> <도착차원> [비용]");
                            return true;
                        }
                        String name = args[2];
                        String from = args[3];
                        String to = args[4];
                        int cost = args.length >= 6 ? Integer.parseInt(args[5]) : 100;
                        Player p = (Player) sender;
                        Location loc = p.getLocation();
                        Portal portal = plugin.getPortalManager().createPortal(name, from, to, loc, PortalType.FIXED, cost);
                        sender.sendMessage("§a포탈 " + name + " 생성됨.");
                    } else if ("remove".equals(op)) {
                        if (args.length < 3) {
                            sender.sendMessage("§c사용법: /mdim portal remove <이름>");
                            return true;
                        }
                        String name = args[2];
                        plugin.getPortalManager().deletePortal(name);
                        sender.sendMessage("§c포탈 " + name + " 제거됨.");
                    } else if ("list".equals(op)) {
                        for (Portal portal : plugin.getPortalManager().getAllPortals()) {
                            sender.sendMessage("§e" + portal.getName() + ": " + portal.getFromDimension() + " → " + portal.getToDimension());
                        }
                    }
                }
                break;
            case "fusion":
                // /mdim fusion start/stop/stage <단계>
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /mdim fusion <start|stop|stage>");
                    return true;
                }
                String op = args[1].toLowerCase();
                if ("start".equals(op)) {
                    plugin.getFusionManager().startFusion();
                    sender.sendMessage("§a차원 융합이 시작되었습니다.");
                } else if ("stop".equals(op)) {
                    plugin.getFusionManager().stopFusion();
                    sender.sendMessage("§c차원 융합이 중지되었습니다.");
                } else if ("stage".equals(op)) {
                    if (args.length < 3) {
                        sender.sendMessage("§c사용법: /mdim fusion stage <단계(0~4)>");
                        return true;
                    }
                    int stage = Integer.parseInt(args[2]);
                    plugin.getFusionManager().setStage(stage);
                    sender.sendMessage("§e융합 단계가 " + stage + "으로 변경됨.");
                }
                break;
            case "reload":
                plugin.getDataManager().save();
                plugin.reloadConfig();
                plugin.getDataManager().reload();
                sender.sendMessage("§a데이터 및 설정이 리로드되었습니다.");
                break;
            case "info":
                // /mdim info <차원ID>
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /mdim info <차원ID>");
                    return true;
                }
                id = args[1];
                Dimension d = plugin.getDimensionManager().getDimension(id);
                if (d == null) {
                    sender.sendMessage("§c차원을 찾을 수 없습니다: " + id);
                } else {
                    sender.sendMessage("§e차원 정보: " + id);
                    sender.sendMessage("§f이름: " + d.getName() + " / 타입: " + d.getType());
                    sender.sendMessage("§f월드: " + d.getWorldName() + " / 균형도: " + d.getBalanceValue() + "/100");
                    sender.sendMessage("§f활성화: " + (d.isActive() ? "§aO" : "§cX") + " / 입장레벨: " + d.getLevelRequirement());
                }
                break;
            default:
                showHelp(sender);
                break;
        }
        return true;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage("§e[관리자 명령어 도움말]");
        sender.sendMessage("§a/mdim create <차원ID> <월드이름> <타입>");
        sender.sendMessage("§c/mdim delete <차원ID>");
        sender.sendMessage("§e/mdim activate <차원ID> / deactivate <차원ID>");
        sender.sendMessage("§e/mdim balance <차원ID> [set|add] [값]");
        sender.sendMessage("§e/mdim tp <플레이어> <차원ID>");
        sender.sendMessage("§d/mdim portal create <이름> <시작차원> <도착차원> [비용]");
        sender.sendMessage("§d/mdim portal remove <이름> / list");
        sender.sendMessage("§6/mdim fusion start|stop|stage <단계>");
        sender.sendMessage("§7/mdim reload");
        sender.sendMessage("§7/mdim info <차원ID>");
    }
}