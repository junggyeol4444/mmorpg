package com.multiverse.trade.managers;

import com.multiverse.trade.TradeCore;
import com.multiverse. trade.events.MarketOrderCreateEvent;
import com.multiverse.trade.models.MarketOrder;
import com.multiverse. trade.models.OrderStatus;
import com. multiverse.trade. models.OrderType;
import com.multiverse.trade.utils.ItemUtil;
import com. multiverse.trade. utils.MessageUtil;
import com.multiverse.trade.utils.NumberUtil;
import org. bukkit.Bukkit;
import org.bukkit. OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util. stream.Collectors;

public class MarketManager {

    private final TradeCore plugin;
    private final Map<UUID, MarketOrder> orders = new ConcurrentHashMap<>();
    private final List<MarketOrder> orderHistory = Collections.synchronizedList(new ArrayList<>());

    public MarketManager(TradeCore plugin) {
        this.plugin = plugin;
        loadOrders();
    }

    private void loadOrders() {
        Map<UUID, MarketOrder> loadedOrders = plugin.getMarketDataManager().getAllOrders();
        orders.putAll(loadedOrders);
    }

    public MarketOrder createSellOrder(Player player, ItemStack item, int amount, double pricePerUnit) {
        UUID orderId = UUID.randomUUID();

        MarketOrder order = new MarketOrder();
        order.setOrderId(orderId);
        order.setPlayer(player.getUniqueId());
        order.setType(OrderType. SELL);
        order.setItem(item.clone());
        order.setAmount(amount);
        order.setRemainingAmount(amount);
        order.setPricePerUnit(pricePerUnit);
        order.setCreateTime(System.currentTimeMillis());

        int expiryDays = plugin.getConfig().getInt("market.order-expiry", 7);
        order.setExpiryTime(System.currentTimeMillis() + (expiryDays * 24L * 60L * 60L * 1000L));
        order.setStatus(OrderStatus. ACTIVE);

        MarketOrderCreateEvent event = new MarketOrderCreateEvent(order, player);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return null;
        }

        orders.put(orderId, order);
        plugin.getMarketDataManager().saveOrder(order);

        if (plugin.getConfig().getBoolean("market. matching. auto-match", true)) {
            tryMatchOrder(order);
        }

        return order;
    }

    public MarketOrder createBuyOrder(Player player, ItemStack item, int amount, double pricePerUnit) {
        UUID orderId = UUID.randomUUID();

        MarketOrder order = new MarketOrder();
        order.setOrderId(orderId);
        order.setPlayer(player.getUniqueId());
        order.setType(OrderType. BUY);
        order.setItem(item.clone());
        order.setAmount(amount);
        order.setRemainingAmount(amount);
        order.setPricePerUnit(pricePerUnit);
        order.setCreateTime(System.currentTimeMillis());

        int expiryDays = plugin.getConfig().getInt("market. order-expiry", 7);
        order.setExpiryTime(System. currentTimeMillis() + (expiryDays * 24L * 60L * 60L * 1000L));
        order.setStatus(OrderStatus. ACTIVE);

        MarketOrderCreateEvent event = new MarketOrderCreateEvent(order, player);
        Bukkit.getPluginManager().callEvent(event);

        if (event. isCancelled()) {
            return null;
        }

        orders.put(orderId, order);
        plugin.getMarketDataManager().saveOrder(order);

        if (plugin.getConfig().getBoolean("market. matching.auto-match", true)) {
            tryMatchOrder(order);
        }

        return order;
    }

    public List<MarketOrder> getActiveOrders(ItemStack item) {
        String itemKey = ItemUtil.getItemKey(item);
        return orders.values().stream()
                .filter(o -> o.getStatus() == OrderStatus.ACTIVE || o.getStatus() == OrderStatus.PARTIAL)
                .filter(o -> ItemUtil.getItemKey(o.getItem()).equals(itemKey))
                .collect(Collectors.toList());
    }

    public List<MarketOrder> getPlayerOrders(Player player) {
        return orders.values().stream()
                .filter(o -> o.getPlayer().equals(player. getUniqueId()))
                .filter(o -> o. getStatus() == OrderStatus.ACTIVE || o.getStatus() == OrderStatus. PARTIAL)
                .collect(Collectors. toList());
    }

    public List<MarketOrder> getPlayerOrderHistory(Player player, int limit) {
        return orderHistory.stream()
                .filter(o -> o. getPlayer().equals(player.getUniqueId()))
                .sorted(Comparator. comparingLong(MarketOrder::getCreateTime).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public void cancelOrder(UUID orderId) {
        MarketOrder order = orders.get(orderId);
        if (order == null) {
            return;
        }

        if (order.getType() == OrderType.SELL) {
            ItemStack returnItem = order.getItem().clone();
            returnItem.setAmount(order.getRemainingAmount());

            Player player = Bukkit.getPlayer(order.getPlayer());
            if (player != null && player.isOnline()) {
                player.getInventory().addItem(returnItem);
            } else {
                plugin.getMailManager().sendSystemMail(
                    order. getPlayer(),
                    "거래소 주문 취소",
                    "판매 주문이 취소되어 아이템이 반환됩니다.",
                    Collections.singletonList(returnItem),
                    0, false, 0
                );
            }
        } else {
            double refund = order.getPricePerUnit() * order.getRemainingAmount();
            plugin.getEconomy().depositPlayer(Bukkit. getOfflinePlayer(order.getPlayer()), refund);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orders.remove(orderId);
        orderHistory.add(order);
        plugin.getMarketDataManager().saveOrder(order);
    }

    public double instantSell(Player player, ItemStack item, int amount) {
        String itemKey = ItemUtil.getItemKey(item);

        List<MarketOrder> buyOrders = orders.values().stream()
                .filter(o -> o.getType() == OrderType.BUY)
                .filter(o -> o.getStatus() == OrderStatus.ACTIVE || o.getStatus() == OrderStatus. PARTIAL)
                .filter(o -> ItemUtil.getItemKey(o.getItem()).equals(itemKey))
                .sorted(Comparator. comparingDouble(MarketOrder:: getPricePerUnit).reversed())
                .collect(Collectors. toList());

        if (buyOrders.isEmpty()) {
            return 0;
        }

        int remainingAmount = amount;
        double totalEarned = 0;

        for (MarketOrder buyOrder : buyOrders) {
            if (remainingAmount <= 0) {
                break;
            }

            int tradeAmount = Math. min(remainingAmount, buyOrder.getRemainingAmount());
            double tradeValue = tradeAmount * buyOrder.getPricePerUnit();

            double feeRate = plugin.getConfig().getDouble("market.fees.instant-trade", 3.0) / 100.0;
            double fee = tradeValue * feeRate;
            double sellerAmount = tradeValue - fee;

            plugin. getEconomy().depositPlayer(player, sellerAmount);

            if (fee > 0) {
                plugin.getTransactionFeeManager().distributeFee(fee);
            }

            buyOrder.setRemainingAmount(buyOrder.getRemainingAmount() - tradeAmount);
            if (buyOrder.getRemainingAmount() <= 0) {
                buyOrder.setStatus(OrderStatus.FILLED);
                orders.remove(buyOrder.getOrderId());
                orderHistory. add(buyOrder);
            } else {
                buyOrder.setStatus(OrderStatus.PARTIAL);
            }

            Player buyer = Bukkit.getPlayer(buyOrder.getPlayer());
            if (buyer != null && buyer. isOnline()) {
                ItemStack boughtItem = item.clone();
                boughtItem.setAmount(tradeAmount);
                buyer.getInventory().addItem(boughtItem);
            } else {
                ItemStack boughtItem = item.clone();
                boughtItem. setAmount(tradeAmount);
                plugin.getMailManager().sendSystemMail(
                    buyOrder.getPlayer(),
                    "거래소 구매 완료",
                    "구매 주문이 체결되었습니다.",
                    Collections.singletonList(boughtItem),
                    0, false, 0
                );
            }

            ItemStack sellItem = item.clone();
            sellItem.setAmount(tradeAmount);
            player.getInventory().removeItem(sellItem);

            plugin.getPriceTracker().recordTransaction(item, buyOrder.getPricePerUnit(), tradeAmount);

            remainingAmount -= tradeAmount;
            totalEarned += sellerAmount;

            plugin.getMarketDataManager().saveOrder(buyOrder);
        }

        return totalEarned;
    }

    public double instantBuy(Player player, ItemStack item, int amount) {
        String itemKey = ItemUtil.getItemKey(item);

        List<MarketOrder> sellOrders = orders.values().stream()
                .filter(o -> o. getType() == OrderType.SELL)
                .filter(o -> o.getStatus() == OrderStatus.ACTIVE || o.getStatus() == OrderStatus.PARTIAL)
                .filter(o -> ItemUtil.getItemKey(o.getItem()).equals(itemKey))
                .sorted(Comparator.comparingDouble(MarketOrder::getPricePerUnit))
                .collect(Collectors.toList());

        if (sellOrders.isEmpty()) {
            return 0;
        }

        int remainingAmount = amount;
        double totalSpent = 0;

        for (MarketOrder sellOrder :  sellOrders) {
            if (remainingAmount <= 0) {
                break;
            }

            int tradeAmount = Math.min(remainingAmount, sellOrder.getRemainingAmount());
            double tradeValue = tradeAmount * sellOrder.getPricePerUnit();

            double feeRate = plugin.getConfig().getDouble("market.fees.instant-trade", 3.0) / 100.0;
            double fee = tradeValue * feeRate;

            if (! plugin.getEconomy().has(player, tradeValue)) {
                break;
            }

            plugin.getEconomy().withdrawPlayer(player, tradeValue);

            double sellerAmount = tradeValue - fee;
            plugin.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(sellOrder.getPlayer()), sellerAmount);

            if (fee > 0) {
                plugin.getTransactionFeeManager().distributeFee(fee);
            }

            sellOrder.setRemainingAmount(sellOrder.getRemainingAmount() - tradeAmount);
            if (sellOrder. getRemainingAmount() <= 0) {
                sellOrder. setStatus(OrderStatus.FILLED);
                orders.remove(sellOrder.getOrderId());
                orderHistory.add(sellOrder);
            } else {
                sellOrder.setStatus(OrderStatus.PARTIAL);
            }

            ItemStack boughtItem = sellOrder.getItem().clone();
            boughtItem.setAmount(tradeAmount);
            player.getInventory().addItem(boughtItem);

            plugin. getPriceTracker().recordTransaction(item, sellOrder.getPricePerUnit(), tradeAmount);

            remainingAmount -= tradeAmount;
            totalSpent += tradeValue;

            plugin.getMarketDataManager().saveOrder(sellOrder);
        }

        return totalSpent;
    }

    public void matchOrders() {
        Map<String, List<MarketOrder>> sellOrdersByItem = new HashMap<>();
        Map<String, List<MarketOrder>> buyOrdersByItem = new HashMap<>();

        for (MarketOrder order :  orders.values()) {
            if (order. getStatus() != OrderStatus.ACTIVE && order.getStatus() != OrderStatus. PARTIAL) {
                continue;
            }

            String itemKey = ItemUtil.getItemKey(order. getItem());

            if (order.getType() == OrderType.SELL) {
                sellOrdersByItem.computeIfAbsent(itemKey, k -> new ArrayList<>()).add(order);
            } else {
                buyOrdersByItem.computeIfAbsent(itemKey, k -> new ArrayList<>()).add(order);
            }
        }

        for (String itemKey : sellOrdersByItem.keySet()) {
            List<MarketOrder> sellOrders = sellOrdersByItem.get(itemKey);
            List<MarketOrder> buyOrders = buyOrdersByItem. get(itemKey);

            if (buyOrders == null || buyOrders.isEmpty()) {
                continue;
            }

            sellOrders.sort(Comparator.comparingDouble(MarketOrder::getPricePerUnit));
            buyOrders.sort(Comparator. comparingDouble(MarketOrder::getPricePerUnit).reversed());

            for (MarketOrder sellOrder : sellOrders) {
                for (MarketOrder buyOrder : buyOrders) {
                    if (sellOrder. getRemainingAmount() <= 0) {
                        break;
                    }

                    if (buyOrder.getRemainingAmount() <= 0) {
                        continue;
                    }

                    if (sellOrder.getPricePerUnit() <= buyOrder.getPricePerUnit()) {
                        int matchAmount = Math.min(sellOrder.getRemainingAmount(), buyOrder.getRemainingAmount());
                        executeMatch(sellOrder, buyOrder, matchAmount);
                    }
                }
            }
        }
    }

    public void executeMatch(MarketOrder sellOrder, MarketOrder buyOrder, int amount) {
        double tradePrice = (sellOrder.getPricePerUnit() + buyOrder.getPricePerUnit()) / 2.0;
        double totalValue = tradePrice * amount;

        double feeRate = plugin.getConfig().getDouble("market.fees.sell-order", 2.0) / 100.0;
        double fee = totalValue * feeRate;
        double sellerAmount = totalValue - fee;

        plugin. getEconomy().depositPlayer(Bukkit.getOfflinePlayer(sellOrder. getPlayer()), sellerAmount);

        if (fee > 0) {
            plugin.getTransactionFeeManager().distributeFee(fee);
        }

        sellOrder.setRemainingAmount(sellOrder.getRemainingAmount() - amount);
        buyOrder.setRemainingAmount(buyOrder.getRemainingAmount() - amount);

        if (sellOrder.getRemainingAmount() <= 0) {
            sellOrder.setStatus(OrderStatus.FILLED);
            orders.remove(sellOrder.getOrderId());
            orderHistory.add(sellOrder);
        } else {
            sellOrder.setStatus(OrderStatus. PARTIAL);
        }

        if (buyOrder.getRemainingAmount() <= 0) {
            buyOrder.setStatus(OrderStatus. FILLED);
            orders.remove(buyOrder.getOrderId());
            orderHistory.add(buyOrder);
        } else {
            buyOrder.setStatus(OrderStatus.PARTIAL);
        }

        ItemStack tradedItem = sellOrder.getItem().clone();
        tradedItem.setAmount(amount);

        Player buyer = Bukkit. getPlayer(buyOrder.getPlayer());
        if (buyer != null && buyer.isOnline()) {
            buyer.getInventory().addItem(tradedItem);
            MessageUtil.send(buyer, "market.order-filled");
        } else {
            plugin.getMailManager().sendSystemMail(
                buyOrder. getPlayer(),
                "거래소 주문 체결",
                "구매 주문이 체결되었습니다.",
                Collections.singletonList(tradedItem),
                0, false, 0
            );
        }

        Player seller = Bukkit.getPlayer(sellOrder.getPlayer());
        if (seller != null && seller. isOnline()) {
            MessageUtil.send(seller, "market.order-filled");
        }

        plugin.getPriceTracker().recordTransaction(sellOrder.getItem(), tradePrice, amount);

        plugin.getMarketDataManager().saveOrder(sellOrder);
        plugin.getMarketDataManager().saveOrder(buyOrder);
    }

    private void tryMatchOrder(MarketOrder newOrder) {
        String itemKey = ItemUtil.getItemKey(newOrder.getItem());

        if (newOrder.getType() == OrderType. SELL) {
            List<MarketOrder> buyOrders = orders. values().stream()
                    .filter(o -> o.getType() == OrderType.BUY)
                    .filter(o -> o. getStatus() == OrderStatus.ACTIVE || o.getStatus() == OrderStatus. PARTIAL)
                    .filter(o -> ItemUtil.getItemKey(o.getItem()).equals(itemKey))
                    .filter(o -> o. getPricePerUnit() >= newOrder.getPricePerUnit())
                    .sorted(Comparator. comparingDouble(MarketOrder::getPricePerUnit).reversed())
                    .collect(Collectors.toList());

            for (MarketOrder buyOrder : buyOrders) {
                if (newOrder.getRemainingAmount() <= 0) {
                    break;
                }
                int matchAmount = Math. min(newOrder. getRemainingAmount(), buyOrder.getRemainingAmount());
                executeMatch(newOrder, buyOrder, matchAmount);
            }
        } else {
            List<MarketOrder> sellOrders = orders.values().stream()
                    .filter(o -> o.getType() == OrderType. SELL)
                    .filter(o -> o.getStatus() == OrderStatus. ACTIVE || o.getStatus() == OrderStatus.PARTIAL)
                    .filter(o -> ItemUtil. getItemKey(o.getItem()).equals(itemKey))
                    .filter(o -> o.getPricePerUnit() <= newOrder.getPricePerUnit())
                    .sorted(Comparator. comparingDouble(MarketOrder::getPricePerUnit))
                    .collect(Collectors.toList());

            for (MarketOrder sellOrder : sellOrders) {
                if (newOrder.getRemainingAmount() <= 0) {
                    break;
                }
                int matchAmount = Math.min(newOrder.getRemainingAmount(), sellOrder.getRemainingAmount());
                executeMatch(sellOrder, newOrder, matchAmount);
            }
        }
    }

    public void checkExpiredOrders() {
        long now = System. currentTimeMillis();
        List<UUID> expiredOrderIds = new ArrayList<>();

        for (MarketOrder order : orders.values()) {
            if (order. getExpiryTime() <= now && 
                (order.getStatus() == OrderStatus. ACTIVE || order.getStatus() == OrderStatus.PARTIAL)) {
                expiredOrderIds.add(order.getOrderId());
            }
        }

        for (UUID orderId : expiredOrderIds) {
            MarketOrder order = orders.get(orderId);
            order.setStatus(OrderStatus.EXPIRED);

            if (order.getType() == OrderType.SELL && order.getRemainingAmount() > 0) {
                ItemStack returnItem = order.getItem().clone();
                returnItem.setAmount(order.getRemainingAmount());
                plugin.getMailManager().sendSystemMail(
                    order.getPlayer(),
                    "거래소 주문 만료",
                    "판매 주문이 만료되어 아이템이 반환됩니다.",
                    Collections.singletonList(returnItem),
                    0, false, 0
                );
            } else if (order. getType() == OrderType.BUY && order.getRemainingAmount() > 0) {
                double refund = order. getPricePerUnit() * order.getRemainingAmount();
                plugin.getEconomy().depositPlayer(Bukkit. getOfflinePlayer(order.getPlayer()), refund);
            }

            orders.remove(orderId);
            orderHistory.add(order);
            plugin. getMarketDataManager().saveOrder(order);
        }
    }

    public void saveAllOrders() {
        for (MarketOrder order : orders.values()) {
            plugin.getMarketDataManager().saveOrder(order);
        }
    }
}