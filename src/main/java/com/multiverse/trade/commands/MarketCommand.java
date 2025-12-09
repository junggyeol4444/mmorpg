package com.multiverse.trade.commands;

import com.multiverse.trade.TradeCore;
import com.multiverse. trade.managers.MarketManager;
import com.multiverse. trade.managers.PriceTracker;
import com. multiverse.trade. models.MarketOrder;
import com. multiverse.trade. models.MarketPrice;
import com. multiverse.trade. models.OrderStatus;
import com.multiverse.trade.models.OrderType;
import com.multiverse.trade.utils.ItemUtil;
import com.multiverse.trade.utils.MessageUtil;
import com.multiverse.trade.utils.NumberUtil;
import org.bukkit.command.Command;
import org.bukkit. command.CommandExecutor;
import org. bukkit.command. CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit. inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java. util.List;
import java.util. UUID;
import java.util. stream.Collectors;

public class MarketCommand implements CommandExecutor, TabCompleter {

    private final TradeCore plugin;
    private final MarketManager marketManager;
    private final PriceTracker priceTracker;

    public MarketCommand(TradeCore plugin) {
        this. plugin = plugin;
        this.marketManager = plugin.getMarketManager();
        this.priceTracker = plugin.getPriceTracker();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil. color("&c플레이어만 사용할 수 있습니다."));
            return true;
        }

        Player player = (Player) sender;

        // 기능 활성화 확인
        if (!plugin.getConfig().getBoolean("market.enabled", true)) {
            MessageUtil.send(player, "general.feature-disabled");
            return true;
        }

        // 권한 확인
        if (!player.hasPermission("trade.market")) {
            MessageUtil.send(player, "general.no-permission");
            return true;
        }

        if (args.length == 0) {
            // 기본적으로 거래소 GUI 열기
            plugin.getGuiManager().openMarketGUI(player, 1);
            return true;
        }

        String subCommand = args[0]. toLowerCase();

        switch (subCommand) {
            case "sell":
            case "판매": 
                handleSell(player, args);
                break;
            case "buy": 
            case "구매":
                handleBuy(player, args);
                break;
            case "orders":
            case "주문":
                handleOrders(player);
                break;
            case "cancel":
            case "취소":
                handleCancel(player, args);
                break;
            case "price":
            case "시세":
                handlePrice(player, args);
                break;
            case "instant":
            case "즉시":
                handleInstant(player, args);
                break;
            case "trending":
            case "인기":
                handleTrending(player);
                break;
            case "history":
            case "기록":
                handleHistory(player);
                break;
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void handleSell(Player player, String[] args) {
        // /market sell <개당가격> [수량]
        if (args.length < 2) {
            player.sendMessage(MessageUtil.color("&c사용법: /market sell <개당가격> [수량]"));
            return;
        }

        ItemStack itemInHand = player. getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getType().isAir()) {
            player.sendMessage(MessageUtil. color("&c판매할 아이템을 손에 들고 있어야 합니다. "));
            return;
        }

        double pricePerUnit = NumberUtil.parseDouble(args[1], -1);
        if (pricePerUnit <= 0) {
            MessageUtil.send(player, "general.invalid-amount");
            return;
        }

        int amount = itemInHand.getAmount();
        if (args.length >= 3) {
            amount = NumberUtil.parseInt(args[2], itemInHand.getAmount());
            if (amount <= 0 || amount > itemInHand.getAmount()) {
                player.sendMessage(MessageUtil.color("&c올바른 수량을 입력하세요.  (보유:  " + itemInHand.getAmount() + "개)"));
                return;
            }
        }

        // 수수료 계산
        double feeRate = plugin.getConfig().getDouble("market.fees.sell-order", 2.0) / 100.0;
        double totalValue = pricePerUnit * amount;
        double fee = totalValue * feeRate;

        // 아이템 차감
        ItemStack sellItem = itemInHand.clone();
        sellItem.setAmount(amount);
        
        if (itemInHand.getAmount() == amount) {
            player.getInventory().setItemInMainHand(null);
        } else {
            itemInHand.setAmount(itemInHand.getAmount() - amount);
        }

        // 판매 주문 생성
        MarketOrder order = marketManager.createSellOrder(player, sellItem, amount, pricePerUnit);
        
        if (order != null) {
            String shortId = order. getOrderId().toString().substring(0, 8);
            MessageUtil.send(player, "market.order-created", "id", shortId);
            
            if (fee > 0) {
                player.sendMessage(MessageUtil.color("&7(판매 시 수수료 " + NumberUtil.format(fee) + " 차감 예정)"));
            }
        } else {
            // 실패 시 아이템 반환
            player.getInventory().addItem(sellItem);
            player.sendMessage(MessageUtil. color("&c주문 등록에 실패했습니다."));
        }
    }

    private void handleBuy(Player player, String[] args) {
        // /market buy <아이템> <개당가격> <수량>
        if (args.length < 4) {
            player.sendMessage(MessageUtil.color("&c사용법:  /market buy <아이템> <개당가격> <수량>"));
            return;
        }

        String itemName = args[1];
        double pricePerUnit = NumberUtil.parseDouble(args[2], -1);
        int amount = NumberUtil. parseInt(args[3], -1);

        if (pricePerUnit <= 0 || amount <= 0) {
            MessageUtil.send(player, "general.invalid-amount");
            return;
        }

        // 아이템 파싱
        ItemStack item = ItemUtil.parseItem(itemName);
        if (item == null) {
            player.sendMessage(MessageUtil.color("&c알 수 없는 아이템입니다:  " + itemName));
            return;
        }

        double totalCost = pricePerUnit * amount;

        // 돈 확인
        if (! plugin.getEconomy().has(player, totalCost)) {
            MessageUtil.send(player, "shop.not-enough-money");
            return;
        }

        // 돈 차감
        plugin. getEconomy().withdrawPlayer(player, totalCost);

        // 구매 주문 생성
        item.setAmount(amount);
        MarketOrder order = marketManager.createBuyOrder(player, item, amount, pricePerUnit);
        
        if (order != null) {
            String shortId = order. getOrderId().toString().substring(0, 8);
            MessageUtil.send(player, "market. order-created", "id", shortId);
            player.sendMessage(MessageUtil.color("&7(총 " + NumberUtil.format(totalCost) + " 예치)"));
        } else {
            // 실패 시 돈 반환
            plugin.getEconomy().depositPlayer(player, totalCost);
            player.sendMessage(MessageUtil.color("&c주문 등록에 실패했습니다."));
        }
    }

    private void handleOrders(Player player) {
        List<MarketOrder> orders = marketManager. getPlayerOrders(player);
        
        if (orders.isEmpty()) {
            player. sendMessage(MessageUtil.color("&c등록한 주문이 없습니다. "));
            return;
        }

        player.sendMessage(MessageUtil.color("&6===== 내 주문 목록 ====="));
        
        for (MarketOrder order : orders) {
            String shortId = order.getOrderId().toString().substring(0, 8);
            String type = order.getType() == OrderType. SELL ? "&c[판매]" :  "&a[구매]";
            String itemName = ItemUtil.getItemName(order.getItem());
            String status = order.getStatus().name();
            int remaining = order.getRemainingAmount();
            double price = order.getPricePerUnit();

            player.sendMessage(MessageUtil.color(type + " &7[" + shortId + "] &f" + itemName));
            player.sendMessage(MessageUtil. color("   &7가격: &a" + NumberUtil. format(price) + "/개 &7| 남은수량: &e" + remaining + "개 &7| 상태: " + status));
        }
    }

    private void handleCancel(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(MessageUtil.color("&c사용법:  /market cancel <주문ID>"));
            return;
        }

        String orderIdStr = args[1];
        MarketOrder order = findOrderByShortId(player, orderIdStr);
        
        if (order == null) {
            MessageUtil.send(player, "market.order-not-found");
            return;
        }

        // 소유자 확인
        if (!order.getPlayer().equals(player.getUniqueId()) && !player.hasPermission("trade. admin")) {
            MessageUtil.send(player, "market. order-not-owner");
            return;
        }

        // 주문 취소
        marketManager.cancelOrder(order. getOrderId());
        MessageUtil.send(player, "market.order-cancelled");
    }

    private void handlePrice(Player player, String[] args) {
        ItemStack item;
        
        if (args.length >= 2) {
            // 아이템 이름으로 조회
            item = ItemUtil.parseItem(args[1]);
            if (item == null) {
                player.sendMessage(MessageUtil.color("&c알 수 없는 아이템입니다:  " + args[1]));
                return;
            }
        } else {
            // 손에 든 아이템으로 조회
            item = player. getInventory().getItemInMainHand();
            if (item == null || item.getType().isAir()) {
                player.sendMessage(MessageUtil. color("&c아이템을 손에 들거나 아이템 이름을 입력하세요."));
                return;
            }
        }

        MarketPrice marketPrice = priceTracker.getMarketPrice(item);
        String itemName = ItemUtil. getItemName(item);

        player.sendMessage(MessageUtil.color("&6===== " + itemName + " 시세 ====="));
        player.sendMessage(MessageUtil. color("&7현재가: &a" + NumberUtil.format(marketPrice.getCurrentPrice())));
        player.sendMessage(MessageUtil.color("&7평균가 (7일): &a" + NumberUtil.format(marketPrice.getAveragePrice())));
        player.sendMessage(MessageUtil.color("&7최저 판매가: &a" + NumberUtil. format(marketPrice. getLowestSell())));
        player.sendMessage(MessageUtil.color("&7최고 구매가: &a" + NumberUtil. format(marketPrice. getHighestBuy())));
        player.sendMessage(MessageUtil.color("&7일일 거래량: &e" + marketPrice.getDailyVolume() + "개"));
        
        // 가격 변동 표시
        double priceChange = priceTracker.getPriceChange(item, 24);
        String changeColor = priceChange >= 0 ? "&a+" : "&c";
        player.sendMessage(MessageUtil. color("&724시간 변동: " + changeColor + NumberUtil. format(priceChange) + "%"));
    }

    private void handleInstant(Player player, String[] args) {
        // /market instant <sell|buy> [수량]
        if (args.length < 2) {
            player.sendMessage(MessageUtil.color("&c사용법: /market instant <sell|buy> [수량]"));
            return;
        }

        String type = args[1]. toLowerCase();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        
        if (itemInHand == null || itemInHand.getType().isAir()) {
            player.sendMessage(MessageUtil.color("&c아이템을 손에 들고 있어야 합니다."));
            return;
        }

        int amount = itemInHand.getAmount();
        if (args.length >= 3) {
            amount = NumberUtil.parseInt(args[2], itemInHand. getAmount());
        }

        if (type.equals("sell") || type.equals("판매")) {
            // 즉시 판매 - 가장 높은 구매 주문에 판매
            double totalEarned = marketManager.instantSell(player, itemInHand, amount);
            if (totalEarned > 0) {
                MessageUtil.send(player, "market.instant-sell", 
                    "amount", String.valueOf(amount),
                    "price", NumberUtil.format(totalEarned));
            } else {
                MessageUtil.send(player, "market.no-orders");
            }
        } else if (type.equals("buy") || type.equals("구매")) {
            // 즉시 구매 - 가장 낮은 판매 주문에서 구매
            double totalSpent = marketManager. instantBuy(player, itemInHand, amount);
            if (totalSpent > 0) {
                MessageUtil. send(player, "market.instant-buy",
                    "amount", String.valueOf(amount),
                    "price", NumberUtil.format(totalSpent));
            } else {
                MessageUtil.send(player, "market.no-orders");
            }
        } else {
            player.sendMessage(MessageUtil.color("&c사용법: /market instant <sell|buy> [수량]"));
        }
    }

    private void handleTrending(Player player) {
        List<MarketPrice> trending = priceTracker.getTrendingItems(10);
        
        if (trending.isEmpty()) {
            player.sendMessage(MessageUtil.color("&c거래 데이터가 부족합니다. "));
            return;
        }

        player.sendMessage(MessageUtil.color("&6===== 인기 아이템 TOP 10 ====="));
        int rank = 1;
        for (MarketPrice mp : trending) {
            String itemName = ItemUtil.getItemName(mp.getItem());
            String price = NumberUtil. format(mp.getCurrentPrice());
            int volume = mp.getDailyVolume();
            
            player. sendMessage(MessageUtil.color("&e" + rank + ".  &f" + itemName + " &7- &a" + price + " &7(거래량: " + volume + ")"));
            rank++;
        }
    }

    private void handleHistory(Player player) {
        List<MarketOrder> history = marketManager.getPlayerOrderHistory(player, 10);
        
        if (history.isEmpty()) {
            player.sendMessage(MessageUtil.color("&c거래 기록이 없습니다."));
            return;
        }

        player.sendMessage(MessageUtil.color("&6===== 최근 거래 기록 ====="));
        for (MarketOrder order : history) {
            String type = order.getType() == OrderType.SELL ? "&c[판매]" : "&a[구매]";
            String itemName = ItemUtil.getItemName(order.getItem());
            String status = order.getStatus().name();
            int amount = order.getAmount() - order.getRemainingAmount();
            double total = order.getPricePerUnit() * amount;

            player.sendMessage(MessageUtil.color(type + " &f" + itemName + " x" + amount + " &7= &a" + NumberUtil.format(total) + " &7(" + status + ")"));
        }
    }

    private MarketOrder findOrderByShortId(Player player, String shortId) {
        List<MarketOrder> orders = marketManager.getPlayerOrders(player);
        
        for (MarketOrder order : orders) {
            if (order.getOrderId().toString().startsWith(shortId)) {
                return order;
            }
        }
        
        try {
            UUID fullId = UUID.fromString(shortId);
            for (MarketOrder order : orders) {
                if (order. getOrderId().equals(fullId)) {
                    return order;
                }
            }
        } catch (IllegalArgumentException ignored) {}
        
        return null;
    }

    private void sendHelp(Player player) {
        player.sendMessage(MessageUtil.color("&6===== 거래소 도움말 ====="));
        player.sendMessage(MessageUtil. color("&e/market &7- 거래소 열기"));
        player.sendMessage(MessageUtil.color("&e/market sell <가격> [수량] &7- 판매 주문 등록"));
        player.sendMessage(MessageUtil.color("&e/market buy <아이템> <가격> <수량> &7- 구매 주문 등록"));
        player.sendMessage(MessageUtil.color("&e/market instant <sell|buy> [수량] &7- 즉시 거래"));
        player.sendMessage(MessageUtil.color("&e/market orders &7- 내 주문 목록"));
        player.sendMessage(MessageUtil.color("&e/market cancel <주문ID> &7- 주문 취소"));
        player.sendMessage(MessageUtil. color("&e/market price [아이템] &7- 시세 조회"));
        player.sendMessage(MessageUtil. color("&e/market trending &7- 인기 아이템"));
        player.sendMessage(MessageUtil. color("&e/market history &7- 거래 기록"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args. length == 1) {
            List<String> subCommands = Arrays.asList(
                "sell", "buy", "orders", "cancel", "price", "instant", "trending", "history"
            );
            String input = args[0].toLowerCase();
            completions = subCommands.stream()
                    .filter(s -> s.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        } else if (args. length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand. equals("instant")) {
                completions.addAll(Arrays.asList("sell", "buy"));
            } else if (subCommand.equals("sell")) {
                completions.add("<개당가격>");
            } else if (subCommand.equals("cancel")) {
                // 플레이어의 활성 주문 ID
                if (sender instanceof Player) {
                    List<MarketOrder> orders = marketManager. getPlayerOrders((Player) sender);
                    String input = args[1]. toLowerCase();
                    for (MarketOrder order : orders) {
                        String shortId = order.getOrderId().toString().substring(0, 8);
                        if (shortId. startsWith(input)) {
                            completions.add(shortId);
                        }
                    }
                }
            }
        }

        return completions;
    }
}