package com.multiverse.core.commands;

import com.multiverse.core.MultiverseCore;
import com.multiverse.core.managers.*;
import com.multiverse.core.models.Dimension;
import com.multiverse.core.models.Waypoint;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerCommand extends SubCommand {
    private final MultiverseCore plugin;

    public PlayerCommand(MultiverseCore plugin) {
        super("player", "multiverse.player");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c플레이어만 사용할 수 있습니다.");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("multiverse.player")) {
            sender.sendMessage(plugin.getMessageUtil().get("no-permission"));
            return true;
        }

        // 기본: "/dimension" 혹은 "/dim"
        if (args.length == 0) {
            showCurrentDimensionInfo(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "list":
                // /dim list: 이동 가능한 차원 목록 표시
                List<Dimension> dims = plugin.getDimensionManager().getAccessibleDimensions(player);
                sender.sendMessage("§e[이동 가능한 차원 목록]");
                for (Dimension d : dims) {
                    sender.sendMessage("§f- " + d.getName() + " (§e" + d.getId() + "§f) | 균형도: §a" + d.getBalanceValue());
                }
                break;
            case "tp":
                // /dim tp <차원ID>
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /dim tp <차원ID>");
                    return true;
                }
                String id = args[1];
                if (plugin.getTeleportManager().canTeleport(player, id)) {
                    plugin.getTeleportManager().teleportToDimension(player, id);
                    sender.sendMessage(plugin.getMessageUtil().get("teleport.success").replace("{dimension}", id));
                } else {
                    String deny = plugin.getTeleportManager().getTeleportDenyReason(player, id);
                    sender.sendMessage("§c" + deny);
                }
                break;
            case "map":
                // /dim map: 월드맵 GUI 열기
                plugin.getDimensionManager().openMapGUI(player);
                break;
            case "waypoint":
                // /dim waypoint <create|list|tp|delete> [이름]
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /dim waypoint <create|list|tp|delete> [이름]");
                    return true;
                }
                String wcmd = args[1].toLowerCase();
                switch (wcmd) {
                    case "create":
                        if (args.length < 3) {
                            sender.sendMessage("§c웨이포인트 이름을 입력하세요.");
                            return true;
                        }
                        String wpName = args[2];
                        if (!plugin.getWaypointManager().canCreateWaypoint(player)) {
                            sender.sendMessage(plugin.getMessageUtil().format("waypoint.limit-reached"));
                            return true;
                        }
                        Waypoint wp = plugin.getWaypointManager().createWaypoint(player, wpName);
                        sender.sendMessage(plugin.getMessageUtil().format("waypoint.created").replace("{name}", wpName));
                        break;
                    case "list":
                        plugin.getWaypointManager().openWaypointList(player);
                        break;
                    case "tp":
                        if (args.length < 3) {
                            sender.sendMessage("§c웨이포인트 이름을 입력하세요.");
                            return true;
                        }
                        wpName = args[2];
                        wp = plugin.getWaypointManager().getWaypoint(player, wpName);
                        if (wp == null) {
                            sender.sendMessage(plugin.getMessageUtil().format("waypoint.not-found").replace("{name}", wpName));
                            return true;
                        }
                        plugin.getWaypointManager().teleportToWaypoint(player, wp);
                        break;
                    case "delete":
                        if (args.length < 3) {
                            sender.sendMessage("§c웨이포인트 이름을 입력하세요.");
                            return true;
                        }
                        wpName = args[2];
                        boolean removed = plugin.getWaypointManager().deleteWaypoint(player, wpName);
                        sender.sendMessage(plugin.getMessageUtil().format(
                                removed ? "waypoint.removed" : "waypoint.not-found"
                        ).replace("{name}", wpName));
                        break;
                    default:
                        sender.sendMessage("§c잘못된 웨이포인트 명령어입니다.");
                        break;
                }
                break;
            case "info":
                // /dim info [차원ID]
                Dimension d;
                if (args.length == 2) {
                    String dimId = args[1];
                    d = plugin.getDimensionManager().getDimension(dimId);
                    if (d == null) {
                        sender.sendMessage(plugin.getMessageUtil().get("dimension.not-found"));
                        return true;
                    }
                } else {
                    String currDim = plugin.getDimensionManager().getPlayerCurrentDimension(player);
                    d = plugin.getDimensionManager().getDimension(currDim);
                    if (d == null) {
                        sender.sendMessage(plugin.getMessageUtil().get("dimension.not-found"));
                        return true;
                    }
                }
                sender.sendMessage("§e차원 정보: " + d.getId());
                sender.sendMessage("§f이름: " + d.getName() + " / 타입: " + d.getType());
                sender.sendMessage("§f월드: " + d.getWorldName() + " / 균형도: " + d.getBalanceValue() + "/100");
                sender.sendMessage("§f활성화: " + (d.isActive() ? "§aO" : "§cX") + " / 입장레벨: " + d.getLevelRequirement());
                break;
            default:
                showCurrentDimensionInfo(player);
        }
        return true;
    }

    private void showCurrentDimensionInfo(Player player) {
        String currDim = plugin.getDimensionManager().getPlayerCurrentDimension(player);
        Dimension d = plugin.getDimensionManager().getDimension(currDim);
        if (d == null) {
            player.sendMessage(plugin.getMessageUtil().get("dimension.not-found"));
            return;
        }
        player.sendMessage("§e[현재 차원 정보]");
        player.sendMessage("§f이름: " + d.getName() + " (§e" + d.getId() + "§f)");
        player.sendMessage("§f유형: " + d.getType() + " / 균형도: §a" + d.getBalanceValue() + "/100");
        player.sendMessage("§f활성화: " + (d.isActive() ? "§aO" : "§cX") + " / 입장레벨: " + d.getLevelRequirement());
    }
}