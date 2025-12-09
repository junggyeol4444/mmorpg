package com.multiverse.trade.commands;

import com. multiverse.trade. TradeCore;
import com. multiverse.trade. managers.AuctionManager;
import com.multiverse.trade.models. Auction;
import com.multiverse.trade.models.AuctionStatus;
import com. multiverse.trade. utils.MessageUtil;
import com.multiverse.trade.utils.NumberUtil;
import com.multiverse.trade.utils.TimeUtil;
import org.bukkit. Bukkit;
import org.bukkit. command.Command;
import org.bukkit. command.CommandExecutor;
import org. bukkit.command. CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit. inventory.ItemStack;

import java.util. ArrayList;
import java. util.Arrays;
import java. util.List;
import java.util. UUID;
import java.util. stream.Collectors;

public class AuctionCommand implements CommandExecutor, TabCompleter {

    private final TradeCore plugin;
    private final AuctionManager auctionManager;

    public AuctionCommand(TradeCore plugin) {
        this. plugin = plugin;
        this.auctionManager = plugin. getAuctionManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil. color("&c플레이어만 사용할 수 있습니다. "));
            return true;
        }

        Player player = (Player) sender;

        // 기능 활성화 확인
        if (! plugin.getConfig().getBoolean("auction.enabled", true)) {
            MessageUtil.send(player, "general.feature-disabled");
            return true;
        }

        // 권한 확인
        if (! player.hasPermission("trade.auction")) {
            MessageUtil.send(player, "general.no-permission");
            return true;
        }

        if (args.length == 0) {
            // 기본적으로 경매장 GUI 열기
            plugin.getGuiManager().openAuctionGUI(player, 1);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "list":
            case "목록":
                int page = 1;
                if (args.length >= 2) {
                    page = NumberUtil.parseInt(args[1], 1);
                }
                plugin.getGuiManager().openAuctionGUI(player, page);
                break;
            case "sell":
            case "판매":
            case "등록":
                handleSell(player, args);
                break;
            case "bid":
            case "입찰": 
                handleBid(player, args);
                break;
            case "buyout":
            case "즉시구매":
                handleBuyout(player, args);
                break;
            case "cancel":
            case "취소":
                handleCancel(player, args);
                break;
            case "my":
            case "내경매":
                handleMyAuctions(player);
                break;
            case "search":
            case "검색":
                if (args.length >= 2) {
                    String query = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    handleSearch(player, query);
                } else {
                    player.sendMessage(MessageUtil.color("&c사용법: /auction search <검색어>"));
                }
                break;
            case "info":
            case "정보": 
                if (args.length >= 2) {
                    handleInfo(player, args[1]);
                } else {
                    player.sendMessage(MessageUtil.color("&c사용법: /auction info <경매ID>"));
                }
                break;
            case "create":
            case "생성": 
                // 경매 생성 GUI 열기
                plugin.getGuiManager().openAuctionCreateGUI(player);
                break;
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void handleSell(Player player, String[] args) {
        // /auction sell <시작가> [즉시구매가] [시간(시간)]
        if (args.length < 2) {
            player.sendMessage(MessageUtil.color("&c사용법:  /auction sell <시작가> [즉시구매가] [시간]"));
            return;
        }

        // 손에 들고 있는 아이템 확인
        ItemStack itemInHand = player. getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getType().isAir()) {
            player.sendMessage(MessageUtil.color("&c경매에 등록할 아이템을 손에 들고 있어야 합니다. "));
            return;
        }

        // 최대 경매 개수 확인
        int maxAuctions = plugin.getConfig().getInt("auction.limits.max-active-per-player", 5);
        List<Auction> playerAuctions = auctionManager.getPlayerAuctions(player);
        long activeCount = playerAuctions.stream()
                .filter(a -> a.getStatus() == AuctionStatus.ACTIVE)
                .count();

        if (activeCount >= maxAuctions && ! player.hasPermission("trade.admin. bypass")) {
            MessageUtil.send(player, "auction.max-auctions", "max", String.valueOf(maxAuctions));
            return;
        }

        // 시작가 파싱
        double startingBid = NumberUtil.parseDouble(args[1], -1);
        if (startingBid <= 0) {
            MessageUtil.send(player, "general.invalid-amount");
            return;
        }

        // 즉시구매가 파싱 (옵션)
        double buyoutPrice = 0;
        if (args.length >= 3) {
            buyoutPrice = NumberUtil.parseDouble(args[2], 0);
            if (buyoutPrice > 0 && buyoutPrice <= startingBid) {
                player.sendMessage(MessageUtil.color("&c즉시구매가는 시작가보다 높아야 합니다."));
                return;
            }
        }

        // 경매 시간 파싱 (옵션, 기본 24시간)
        int duration = 24;
        if (args.length >= 4) {
            duration = NumberUtil.parseInt(args[3], 24);
            List<Integer> availableDurations = plugin.getConfig().getIntegerList("auction.durations. available");
            if (! availableDurations.contains(duration)) {
                player.sendMessage(MessageUtil.color("&c사용 가능한 경매 시간:  " + availableDurations));
                return;
            }
        }

        // 등록비 확인 및 차감
        double listingFee = plugin.getConfig().getDouble("auction.fees.listing-fee", 100.0);
        if (listingFee > 0) {
            if (! plugin.getEconomy().has(player, listingFee)) {
                MessageUtil.send(player, "shop.not-enough-money");
                return;
            }
            plugin.getEconomy().withdrawPlayer(player, listingFee);
            MessageUtil.send(player, "auction.listing-fee", "fee", NumberUtil.format(listingFee));
        }

        // 경매 생성
        ItemStack auctionItem = itemInHand. clone();
        player.getInventory().setItemInMainHand(null);

        Auction auction = auctionManager.createAuction(player, auctionItem, startingBid, buyoutPrice, duration);
        
        if (auction != null) {
            String shortId = auction.getAuctionId().toString().substring(0, 8);
            MessageUtil.send(player, "auction.listed", "id", shortId);
        } else {
            // 실패 시 아이템 반환
            player. getInventory().addItem(auctionItem);
            player.sendMessage(MessageUtil.color("&c경매 등록에 실패했습니다. "));
        }
    }

    private void handleBid(Player player, String[] args) {
        // /auction bid <경매ID> <금액>
        if (args.length < 3) {
            player.sendMessage(MessageUtil.color("&c사용법:  /auction bid <경매ID> <금액>"));
            return;
        }

        String auctionIdStr = args[1];
        double bidAmount = NumberUtil. parseDouble(args[2], -1);

        if (bidAmount <= 0) {
            MessageUtil. send(player, "general.invalid-amount");
            return;
        }

        // 경매 찾기
        Auction auction = findAuctionByShortId(auctionIdStr);
        if (auction == null) {
            MessageUtil.send(player, "auction. not-found");
            return;
        }

        // 자신의 경매인지 확인
        if (auction. getSeller().equals(player.getUniqueId())) {
            MessageUtil. send(player, "auction.own-auction");
            return;
        }

        // 경매 상태 확인
        if (auction. getStatus() != AuctionStatus. ACTIVE) {
            MessageUtil.send(player, "auction. already-ended");
            return;
        }

        // 최소 입찰가 확인
        double minBid = auction. getCurrentBid() + plugin.getConfig().getDouble("auction.bidding. min-increment", 100.0);
        if (bidAmount < minBid) {
            MessageUtil. send(player, "auction.bid-too-low", "min", NumberUtil.format(minBid));
            return;
        }

        // 돈 확인
        if (! plugin.getEconomy().has(player, bidAmount)) {
            MessageUtil. send(player, "shop.not-enough-money");
            return;
        }

        // 입찰 처리
        auctionManager.placeBid(player, auction. getAuctionId(), bidAmount);
        MessageUtil.send(player, "auction.bid-placed", "amount", NumberUtil.format(bidAmount));
    }

    private void handleBuyout(Player player, String[] args) {
        // /auction buyout <경매ID>
        if (args. length < 2) {
            player. sendMessage(MessageUtil.color("&c사용법: /auction buyout <경매ID>"));
            return;
        }

        String auctionIdStr = args[1];
        
        // 경매 찾기
        Auction auction = findAuctionByShortId(auctionIdStr);
        if (auction == null) {
            MessageUtil.send(player, "auction.not-found");
            return;
        }

        // 즉시구매가 확인
        if (auction.getBuyoutPrice() <= 0) {
            MessageUtil.send(player, "auction.no-buyout");
            return;
        }

        // 자신의 경매인지 확인
        if (auction.getSeller().equals(player.getUniqueId())) {
            MessageUtil.send(player, "auction.own-auction");
            return;
        }

        // 경매 상태 확인
        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            MessageUtil. send(player, "auction.already-ended");
            return;
        }

        // 돈 확인
        if (!plugin. getEconomy().has(player, auction.getBuyoutPrice())) {
            MessageUtil.send(player, "shop. not-enough-money");
            return;
        }

        // 즉시 구매 처리
        auctionManager.buyout(player, auction.getAuctionId());
        MessageUtil. send(player, "auction.buyout-success");
    }

    private void handleCancel(Player player, String[] args) {
        // /auction cancel <경매ID>
        if (args.length < 2) {
            player.sendMessage(MessageUtil.color("&c사용법:  /auction cancel <경매ID>"));
            return;
        }

        String auctionIdStr = args[1];
        
        // 경매 찾기
        Auction auction = findAuctionByShortId(auctionIdStr);
        if (auction == null) {
            MessageUtil.send(player, "auction.not-found");
            return;
        }

        // 소유자 확인
        if (! auction.getSeller().equals(player.getUniqueId()) && !player.hasPermission("trade. admin")) {
            MessageUtil.send(player, "auction.not-owner");
            return;
        }

        // 입찰자가 있으면 취소 불가 (관리자 제외)
        if (auction.getCurrentBidder() != null && !player.hasPermission("trade.admin")) {
            player.sendMessage(MessageUtil.color("&c이미 입찰자가 있어 취소할 수 없습니다."));
            return;
        }

        // 경매 취소
        auctionManager.cancelAuction(auction.getAuctionId());
        MessageUtil.send(player, "auction.cancelled");
    }

    private void handleMyAuctions(Player player) {
        List<Auction> myAuctions = auctionManager.getPlayerAuctions(player);
        
        if (myAuctions.isEmpty()) {
            player.sendMessage(MessageUtil.color("&c등록한 경매가 없습니다. "));
            return;
        }

        player.sendMessage(MessageUtil.color("&6===== 내 경매 목록 ====="));
        for (Auction auction : myAuctions) {
            String shortId = auction.getAuctionId().toString().substring(0, 8);
            String itemName = auction.getItem().hasItemMeta() && auction.getItem().getItemMeta().hasDisplayName()
                    ? auction.getItem().getItemMeta().getDisplayName()
                    : auction.getItem().getType().name();
            String status = auction.getStatus().name();
            String currentBid = NumberUtil.format(auction.getCurrentBid());
            String timeLeft = TimeUtil. formatDuration(auction.getEndTime() - System.currentTimeMillis());

            player.sendMessage(MessageUtil.color("&7[" + shortId + "] &f" + itemName));
            player.sendMessage(MessageUtil. color("   &7상태: &e" + status + " &7| 현재가: &a" + currentBid + " &7| 남은시간: &e" + timeLeft));
        }
    }

    private void handleSearch(Player player, String query) {
        List<Auction> results = auctionManager.searchAuctions(query);
        
        if (results.isEmpty()) {
            player.sendMessage(MessageUtil.color("&c'" + query + "'에 대한 검색 결과가 없습니다."));
            return;
        }

        plugin.getGuiManager().openAuctionSearchGUI(player, results, 1);
    }

    private void handleInfo(Player player, String auctionIdStr) {
        Auction auction = findAuctionByShortId(auctionIdStr);
        if (auction == null) {
            MessageUtil.send(player, "auction.not-found");
            return;
        }

        String itemName = auction.getItem().hasItemMeta() && auction.getItem().getItemMeta().hasDisplayName()
                ? auction.getItem().getItemMeta().getDisplayName()
                : auction.getItem().getType().name();
        String sellerName = Bukkit.getOfflinePlayer(auction. getSeller()).getName();
        String bidderName = auction.getCurrentBidder() != null 
                ? Bukkit.getOfflinePlayer(auction.getCurrentBidder()).getName() 
                : "없음";

        player.sendMessage(MessageUtil.color("&6===== 경매 정보 ====="));
        player.sendMessage(MessageUtil. color("&7아이템:  &f" + itemName + " x" + auction.getItem().getAmount()));
        player.sendMessage(MessageUtil.color("&7판매자: &f" + sellerName));
        player.sendMessage(MessageUtil. color("&7시작가: &a" + NumberUtil.format(auction.getStartingBid())));
        player.sendMessage(MessageUtil.color("&7현재가: &a" + NumberUtil. format(auction.getCurrentBid())));
        if (auction.getBuyoutPrice() > 0) {
            player.sendMessage(MessageUtil.color("&7즉시구매가: &a" + NumberUtil.format(auction.getBuyoutPrice())));
        }
        player. sendMessage(MessageUtil.color("&7현재 입찰자: &f" + bidderName));
        player.sendMessage(MessageUtil.color("&7입찰 횟수: &f" + auction.getBidHistory().size() + "회"));
        player.sendMessage(MessageUtil.color("&7남은 시간:  &e" + TimeUtil.formatDuration(auction.getEndTime() - System.currentTimeMillis())));
        player.sendMessage(MessageUtil.color("&7상태: &e" + auction.getStatus().name()));
    }

    private Auction findAuctionByShortId(String shortId) {
        List<Auction> allAuctions = auctionManager.getActiveAuctions();
        allAuctions.addAll(auctionManager.getAllAuctions());
        
        for (Auction auction : allAuctions) {
            if (auction.getAuctionId().toString().startsWith(shortId)) {
                return auction;
            }
        }
        
        // UUID 전체로도 시도
        try {
            UUID fullId = UUID.fromString(shortId);
            return auctionManager. getAuction(fullId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage(MessageUtil.color("&6===== 경매장 도움말 ====="));
        player.sendMessage(MessageUtil.color("&e/auction &7- 경매장 열기"));
        player.sendMessage(MessageUtil.color("&e/auction sell <시작가> [즉시구매가] [시간] &7- 경매 등록"));
        player.sendMessage(MessageUtil.color("&e/auction bid <경매ID> <금액> &7- 입찰"));
        player.sendMessage(MessageUtil. color("&e/auction buyout <경매ID> &7- 즉시 구매"));
        player.sendMessage(MessageUtil.color("&e/auction cancel <경매ID> &7- 경매 취소"));
        player.sendMessage(MessageUtil. color("&e/auction my &7- 내 경매 목록"));
        player.sendMessage(MessageUtil. color("&e/auction search <검색어> &7- 경매 검색"));
        player.sendMessage(MessageUtil. color("&e/auction info <경매ID> &7- 경매 정보"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args. length == 1) {
            List<String> subCommands = Arrays.asList(
                "list", "sell", "bid", "buyout", "cancel", "my", "search", "info", "create"
            );
            String input = args[0].toLowerCase();
            completions = subCommands.stream()
                    .filter(s -> s. toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("sell")) {
                completions.add("<시작가>");
            } else if (subCommand.equals("bid") || subCommand.equals("buyout") || 
                       subCommand.equals("cancel") || subCommand.equals("info")) {
                // 활성 경매 ID 제안
                List<Auction> activeAuctions = auctionManager.getActiveAuctions();
                String input = args[1].toLowerCase();
                for (Auction auction :  activeAuctions) {
                    String shortId = auction.getAuctionId().toString().substring(0, 8);
                    if (shortId.toLowerCase().startsWith(input)) {
                        completions.add(shortId);
                    }
                }
            }
        } else if (args. length == 3) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("sell")) {
                completions.add("[즉시구매가]");
            } else if (subCommand. equals("bid")) {
                completions.add("<입찰금액>");
            }
        } else if (args. length == 4 && args[0].equalsIgnoreCase("sell")) {
            List<Integer> durations = plugin.getConfig().getIntegerList("auction.durations.available");
            completions = durations.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        }

        return completions;
    }
}