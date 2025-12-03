package com.multiverse.economy.managers;

import com.multiverse.economy.EconomyCore;
import com.multiverse.economy.models.*;
import com.multiverse.economy.models.enums.TransactionType;
import com.multiverse.economy.models.enums.RequestStatus;
import com.multiverse.economy.utils.ConfigUtil;
import com.multiverse.economy.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PaymentManager {

    private final EconomyCore plugin;
    private final ConfigUtil config;
    private final CurrencyManager currencyManager;
    private final TaxManager taxManager;
    private final StatisticsManager statisticsManager;
    private final BankManager bankManager;
    private final MessageUtil msg;

    // 요청 및 수표 임시 저장(실제론 EconomyDataManager에 위임)
    private final Map<UUID, PaymentRequest> paymentRequests = new HashMap<>();
    private final Map<UUID, Check> checks = new HashMap<>();

    public PaymentManager(EconomyCore plugin,
                         ConfigUtil config,
                         CurrencyManager currencyManager,
                         TaxManager taxManager,
                         StatisticsManager statisticsManager,
                         BankManager bankManager,
                         MessageUtil msg
    ) {
        this.plugin = plugin;
        this.config = config;
        this.currencyManager = currencyManager;
        this.taxManager = taxManager;
        this.statisticsManager = statisticsManager;
        this.bankManager = bankManager;
        this.msg = msg;
    }

    // 직접 송금
    public void sendMoney(Player from, Player to, String currencyId, double amount) {
        if (!canSendMoney(from, currencyId, amount)) throw new IllegalArgumentException("잔액 부족");
        double tax = taxManager.shouldApplyTax(TransactionType.PLAYER_TRANSFER)
                ? taxManager.calculateTax(TransactionType.PLAYER_TRANSFER, amount) : 0.0;
        double net = amount - tax;

        currencyManager.removeBalance(from, currencyId, amount);
        currencyManager.addBalance(to, currencyId, net);

        statisticsManager.recordTransaction(new Transaction(
                UUID.randomUUID(),
                System.currentTimeMillis(),
                TransactionType.PLAYER_TRANSFER,
                from.getUniqueId(), to.getUniqueId(),
                currencyId, amount, tax, net, "송금"));

        if (tax > 0) taxManager.collectTax(new Transaction(
                UUID.randomUUID(),
                System.currentTimeMillis(),
                TransactionType.PLAYER_TRANSFER,
                from.getUniqueId(), to.getUniqueId(),
                currencyId, amount, tax, net, "송금"));
    }

    public boolean canSendMoney(Player from, String currencyId, double amount) {
        return currencyManager.hasEnough(from, currencyId, amount);
    }

    // 지불 요청
    public void createRequest(Player from, Player to, String currencyId, double amount, String msgText) {
        PaymentRequest request = new PaymentRequest(UUID.randomUUID(), from.getUniqueId(), to.getUniqueId(),
                currencyId, amount, msgText, System.currentTimeMillis(),
                System.currentTimeMillis() + config.getInt("payment.request.expiry-hours", 24) * 3600000L, RequestStatus.PENDING);
        paymentRequests.put(request.getRequestId(), request);
        // 데이터 저장
    }

    public void acceptRequest(Player player, UUID requestId) {
        PaymentRequest req = paymentRequests.get(requestId);
        if (req == null || req.getStatus() != RequestStatus.PENDING) throw new IllegalArgumentException("요청 없음/유효하지 않음");
        Player from = plugin.getServer().getPlayer(req.getFromPlayer());
        Player to = plugin.getServer().getPlayer(req.getToPlayer());
        if (from == null || to == null) throw new IllegalArgumentException("플레이어 오류");
        sendMoney(from, to, req.getCurrencyId(), req.getAmount());
        req.setStatus(RequestStatus.ACCEPTED);
    }

    public void declineRequest(Player player, UUID requestId) {
        PaymentRequest req = paymentRequests.get(requestId);
        if (req == null || req.getStatus() != RequestStatus.PENDING) throw new IllegalArgumentException("요청 없음/유효하지 않음");
        req.setStatus(RequestStatus.DECLINED);
    }

    public List<PaymentRequest> getPendingRequests(Player player) {
        UUID uuid = player.getUniqueId();
        List<PaymentRequest> result = new ArrayList<>();
        for (PaymentRequest req : paymentRequests.values()) {
            if (req.getToPlayer().equals(uuid) && req.getStatus() == RequestStatus.PENDING) {
                result.add(req);
            }
        }
        return result;
    }

    // 수표
    public ItemStack createCheck(Player issuer, String currencyId, double amount) {
        // 실제 ItemStack 생성(커스텀 네임, 로어, NBT... 생략)
        Check check = new Check(UUID.randomUUID(), issuer.getUniqueId(), currencyId, amount,
                System.currentTimeMillis(), false, null, 0);
        checks.put(check.getCheckId(), check);
        // 실제 ItemStack 구현 필요(서버 환경에 따라)
        return new ItemStack(org.bukkit.Material.PAPER, 1);
    }

    public void redeemCheck(Player player, ItemStack checkItem) {
        // 실제에서는 ItemStack에서 UUID, 금액 등 추출하여 검증 후 금액 지급
        UUID checkId = extractCheckId(checkItem);
        Check check = checks.get(checkId);
        if (check == null || check.isRedeemed()) throw new IllegalArgumentException("유효하지 않은 수표");
        currencyManager.addBalance(player, check.getCurrencyId(), check.getAmount());
        check.setRedeemed(true);
        check.setRedeemedBy(player.getUniqueId());
        check.setRedeemDate(System.currentTimeMillis());
    }

    public boolean isValidCheck(ItemStack item) {
        UUID id = extractCheckId(item);
        Check check = checks.get(id);
        return check != null && !check.isRedeemed();
    }

    public UUID extractCheckId(ItemStack item) {
        // 실제로는 ItemStack의 NBT, 이름, 로어 등에서 UUID 추출
        return UUID.randomUUID(); // 임시
    }
}