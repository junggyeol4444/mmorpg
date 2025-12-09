package com. multiverse.trade. commands;

import com.multiverse.trade.TradeCore;
import com.multiverse.trade.managers.PlayerShopManager;
import com.multiverse.trade.models. PlayerShop;
import com.multiverse.trade.utils.MessageUtil;
import com.multiverse.trade.utils.NumberUtil;
import org.bukkit. Bukkit;
import org.bukkit. command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit. entity.Player;

import java.util. ArrayList;
import java.util.Arrays;
import java. util.List;
import java.util. UUID;
import java.util.stream. Collectors;

public class ShopCommand implements CommandExecutor, TabCompleter {

    private final TradeCore plugin;
    private final PlayerShopManager shopManager;

    public ShopCommand(TradeCore plugin) {
        this. plugin = plugin;
        this.shopManager = plugin.getPlayerShopManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.color("&c플레이어만 사용할 수 있습니다. "));
            return true;
        }

        Player player = (Player) sender;

        // 기능 활성화 확인
        if (!plugin.getConfig().getBoolean("player-shops.enabled", true)) {
            MessageUtil.send(player, "general.feature-disabled");
            return true;
        }

        // 권한 확인
        if (!player. hasPermission("trade.shop")) {
            MessageUtil.send(player, "general.no-permission");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
            case "생성":
                if (args.length >= 2) {
                    String shopName = String.join(" ", Arrays.copyOfRange(args, 1, args. length));
                    handleCreate(player, shopName);
                } else {
                    player.sendMessage(MessageUtil.color("&c사용법: /shop create <상점이름>"));
                }
                break;
            case "manage":
            case "관리": 
                handleManage(player);
                break;
            case "visit":
            case "방문":
                if (args.length >= 2) {
                    handleVisit(player, args[1]);
                } else {
                    player.sendMessage(MessageUtil. color("&c사용법: /shop visit <플레이어>"));
                }
                break;
            case "list":
            case "목록":
                int page = 1;
                if (args. length >= 2) {
                    page = NumberUtil.parseInt(args[1], 1);
                }
                handleList(player, page);
                break;
            case "delete":
            case "삭제":
                if (args.length >= 2) {
                    handleDelete(player, args[1]);
                } else {
                    handleDeleteOwn(player);
                }
                break;
            case "open":
            case "열기":
                handleOpen(player);
                break;
            case "close": 
            case "닫기":
                handleClose(player);
                break;
            case "setprice":
            case "가격설정":
                if (args.length >= 3) {
                    int slot = NumberUtil.parseInt(args[1], -1);
                    double price = NumberUtil. parseDouble(args[2], -1);
                    handleSetPrice(player, slot, price);
                } else {
                    player.sendMessage(MessageUtil.color("&c사용법: /shop setprice <슬롯> <가격>"));
                }
                break;
            case "setstock":
            case "재고설정": 
                if (args.length >= 3) {
                    int slot = NumberUtil.parseInt(args[1], -1);
                    int stock = NumberUtil.parseInt(args[2], -1);
                    handleSetStock(player, slot, stock);
                } else {
                    player.sendMessage(MessageUtil.color("&c사용법:  /shop setstock <슬롯> <재고>"));
                }
                break;
            case "search":
            case "검색":
                if (args.length >= 2) {
                    String query = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    handleSearch(player, query);
                } else {
                    player.sendMessage(MessageUtil.color("&c사용법:  /shop search <검색어>"));
                }
                break;
            case "info":
            case "정보":
                handleInfo(player);
                break;
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void handleCreate(Player player, String shopName) {
        // 최대 상점 개수 확인
        int maxShops = plugin.getConfig().getInt("player-shops.limits.max-shops-per-player", 3);
        List<PlayerShop> playerShops = shopManager.getPlayerShops(player);
        
        if (playerShops.size() >= maxShops && ! player.hasPermission("trade.admin. bypass")) {
            MessageUtil.send(player, "shop. max-shops", "max", String.valueOf(maxShops));
            return;
        }

        // 생성 비용 확인
        double creationCost = plugin.getConfig().getDouble("player-shops.fees.creation-cost", 10000.0);
        if (creationCost > 0) {
            if (!plugin.getEconomy().has(player, creationCost)) {
                MessageUtil.send(player, "shop.not-enough-money");
                return;
            }
        }

        // 상점 생성
        PlayerShop shop = shopManager.createShop(player, player.getLocation(), shopName);
        
        if (shop != null) {
            // 비용 차감
            if (creationCost > 0) {
                plugin.getEconomy().withdrawPlayer(player, creationCost);
                MessageUtil.send(player, "shop. creation-cost", "cost", NumberUtil.format(creationCost));
            }
            MessageUtil.send(player, "shop.created", "name", shopName);
            
            // 상점 관리 GUI 열기
            plugin.getGuiManager().openShopManageGUI(player, shop);
        }
    }

    private void handleManage(Player player) {
        List<PlayerShop> shops = shopManager.getPlayerShops(player);
        
        if (shops.isEmpty()) {
            player.sendMessage(MessageUtil.color("&c보유한 상점이 없습니다.  /shop create <이름>으로 생성하세요."));
            return;
        }

        if (shops.size() == 1) {
            // 상점이 하나면 바로 관리 GUI 열기
            plugin.getGuiManager().openShopManageGUI(player, shops.get(0));
        } else {
            // 여러 개면 선택 GUI 열기
            plugin.getGuiManager().openShopSelectGUI(player, shops);
        }
    }

    private void handleVisit(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        UUID targetUUID;
        
        if (target != null) {
            targetUUID = target.getUniqueId();
        } else {
            // 오프라인 플레이어 처리
            targetUUID = Bukkit.getOfflinePlayer(targetName).getUniqueId();
        }

        List<PlayerShop> shops = shopManager. getPlayerShopsByUUID(targetUUID);
        
        if (shops. isEmpty()) {
            player.sendMessage(MessageUtil.color("&c해당 플레이어의 상점이 없습니다. "));
            return;
        }

        // 열린 상점만 필터링
        List<PlayerShop> openShops = shops.stream()
                .filter(PlayerShop::isOpen)
                .collect(Collectors.toList());

        if (openShops. isEmpty()) {
            MessageUtil.send(player, "shop.shop-closed");
            return;
        }

        if (openShops. size() == 1) {
            plugin.getGuiManager().openShopGUI(player, openShops.get(0));
        } else {
            plugin.getGuiManager().openShopSelectGUI(player, openShops);
        }
    }

    private void handleList(Player player, int page) {
        List<PlayerShop> allShops = shopManager.getAllShops();
        List<PlayerShop> openShops = allShops.stream()
                .filter(PlayerShop::isOpen)
                .collect(Collectors.toList());

        if (openShops. isEmpty()) {
            player.sendMessage(MessageUtil.color("&c현재 열린 상점이 없습니다. "));
            return;
        }

        plugin.getGuiManager().openShopListGUI(player, openShops, page);
    }

    private void handleDelete(Player player, String shopIdOrIndex) {
        List<PlayerShop> shops = shopManager. getPlayerShops(player);
        
        if (shops. isEmpty()) {
            player.sendMessage(MessageUtil.color("&c보유한 상점이 없습니다. "));
            return;
        }

        // 인덱스로 삭제 시도
        int index = NumberUtil.parseInt(shopIdOrIndex, -1);
        if (index >= 1 && index <= shops.size()) {
            PlayerShop shop = shops.get(index - 1);
            shopManager.deleteShop(shop. getShopId());
            MessageUtil.send(player, "shop.deleted");
            return;
        }

        // UUID로 삭제 시도
        try {
            UUID shopId = UUID.fromString(shopIdOrIndex);
            PlayerShop shop = shopManager.getShop(shopId);
            if (shop != null && shop.getOwner().equals(player. getUniqueId())) {
                shopManager.deleteShop(shopId);
                MessageUtil. send(player, "shop.deleted");
            } else {
                MessageUtil.send(player, "shop.not-owner");
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage(MessageUtil.color("&c올바른 상점 번호 또는 ID가 아닙니다."));
        }
    }

    private void handleDeleteOwn(Player player) {
        List<PlayerShop> shops = shopManager. getPlayerShops(player);
        
        if (shops. isEmpty()) {
            player.sendMessage(MessageUtil.color("&c보유한 상점이 없습니다. "));
            return;
        }

        if (shops.size() == 1) {
            shopManager.deleteShop(shops.get(0).getShopId());
            MessageUtil.send(player, "shop.deleted");
        } else {
            player.sendMessage(MessageUtil.color("&c여러 상점을 보유 중입니다.  /shop delete <번호>로 지정하세요."));
            for (int i = 0; i < shops.size(); i++) {
                player.sendMessage(MessageUtil.color("&7" + (i + 1) + ".  &f" + shops.get(i).getShopName()));
            }
        }
    }

    private void handleOpen(Player player) {
        List<PlayerShop> shops = shopManager.getPlayerShops(player);
        
        if (shops.isEmpty()) {
            player. sendMessage(MessageUtil.color("&c보유한 상점이 없습니다."));
            return;
        }

        for (PlayerShop shop : shops) {
            shop.setOpen(true);
        }
        player.sendMessage(MessageUtil.color("&a모든 상점을 열었습니다."));
    }

    private void handleClose(Player player) {
        List<PlayerShop> shops = shopManager. getPlayerShops(player);
        
        if (shops. isEmpty()) {
            player.sendMessage(MessageUtil.color("&c보유한 상점이 없습니다. "));
            return;
        }

        for (PlayerShop shop : shops) {
            shop.setOpen(false);
        }
        player.sendMessage(MessageUtil.color("&c모든 상점을 닫았습니다."));
    }

    private void handleSetPrice(Player player, int slot, double price) {
        if (slot < 0 || price < 0) {
            player.sendMessage(MessageUtil. color("&c올바른 슬롯 번호와 가격을 입력하세요."));
            return;
        }

        List<PlayerShop> shops = shopManager.getPlayerShops(player);
        if (shops.isEmpty()) {
            player. sendMessage(MessageUtil.color("&c보유한 상점이 없습니다."));
            return;
        }

        PlayerShop shop = shops. get(0); // 첫 번째 상점
        shopManager.updatePrice(shop, slot, price);
        MessageUtil.send(player, "shop.price-updated", "price", NumberUtil.format(price));
    }

    private void handleSetStock(Player player, int slot, int stock) {
        if (slot < 0 || stock < 0) {
            player.sendMessage(MessageUtil. color("&c올바른 슬롯 번호와 재고를 입력하세요."));
            return;
        }

        List<PlayerShop> shops = shopManager.getPlayerShops(player);
        if (shops.isEmpty()) {
            player.sendMessage(MessageUtil.color("&c보유한 상점이 없습니다."));
            return;
        }

        PlayerShop shop = shops.get(0);
        shopManager. updateStock(shop, slot, stock);
        MessageUtil.send(player, "shop.stock-updated", "stock", String.valueOf(stock));
    }

    private void handleSearch(Player player, String query) {
        List<PlayerShop> results = shopManager.searchShops(query);
        
        if (results.isEmpty()) {
            player.sendMessage(MessageUtil.color("&c'" + query + "'에 대한 검색 결과가 없습니다."));
            return;
        }

        plugin.getGuiManager().openShopListGUI(player, results, 1);
    }

    private void handleInfo(Player player) {
        List<PlayerShop> shops = shopManager.getPlayerShops(player);
        
        player.sendMessage(MessageUtil.color("&6===== 내 상점 정보 ====="));
        
        if (shops. isEmpty()) {
            player.sendMessage(MessageUtil.color("&7보유한 상점이 없습니다."));
            return;
        }

        for (int i = 0; i < shops.size(); i++) {
            PlayerShop shop = shops.get(i);
            String status = shop.isOpen() ? "&a[영업중]" : "&c[휴업중]";
            player.sendMessage(MessageUtil.color("&e" + (i + 1) + ".  " + shop.getShopName() + " " + status));
            player.sendMessage(MessageUtil.color("   &7총 판매액: &f" + NumberUtil.format(shop.getTotalSales())));
            player.sendMessage(MessageUtil.color("   &7총 주문수: &f" + shop.getTotalOrders() + "건"));
            player. sendMessage(MessageUtil.color("   &7등록 상품:  &f" + shop.getItems().size() + "개"));
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage(MessageUtil.color("&6===== 개인 상점 도움말 ====="));
        player.sendMessage(MessageUtil. color("&e/shop create <이름> &7- 상점 생성"));
        player.sendMessage(MessageUtil. color("&e/shop manage &7- 상점 관리"));
        player.sendMessage(MessageUtil.color("&e/shop visit <플레이어> &7- 상점 방문"));
        player.sendMessage(MessageUtil.color("&e/shop list [페이지] &7- 상점 목록"));
        player.sendMessage(MessageUtil.color("&e/shop search <검색어> &7- 상점 검색"));
        player.sendMessage(MessageUtil. color("&e/shop open &7- 상점 열기"));
        player.sendMessage(MessageUtil.color("&e/shop close &7- 상점 닫기"));
        player.sendMessage(MessageUtil. color("&e/shop delete [번호] &7- 상점 삭제"));
        player.sendMessage(MessageUtil.color("&e/shop info &7- 상점 정보"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args. length == 1) {
            List<String> subCommands = Arrays.asList(
                "create", "manage", "visit", "list", "delete", 
                "open", "close", "setprice", "setstock", "search", "info"
            );
            String input = args[0]. toLowerCase();
            completions = subCommands.stream()
                    .filter(s -> s.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        } else if (args. length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("visit")) {
                String input = args[1].toLowerCase();
                completions = Bukkit.getOnlinePlayers().stream()
                        .map(Player:: getName)
                        .filter(s -> s.toLowerCase().startsWith(input))
                        .collect(Collectors.toList());
            }
        }

        return completions;
    }
}