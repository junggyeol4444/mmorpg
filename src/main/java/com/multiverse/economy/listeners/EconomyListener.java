package com.multiverse.economy.listeners;

import com.multiverse.economy.events.BalanceChangeEvent;
import com.multiverse.economy.events.TransactionEvent;
import com.multiverse.economy.models.BankAccount;
import com.multiverse.economy.utils.MessageUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;

public class EconomyListener implements Listener {

    @EventHandler
    public void onBalanceChange(BalanceChangeEvent event) {
        Player player = event.getPlayer();
        BankAccount account = event.getBankAccount();
        double newBalance = event.getNewBalance();

        // 한도 검증 로직 예시
        if (newBalance < 0) {
            MessageUtil.send(player, "잔액은 0원 미만이 될 수 없습니다.");
            event.setCancelled(true);
            return;
        }

        account.setBalance(newBalance);
        MessageUtil.send(player, "잔액이 변경되었습니다! 현재 잔액: " + newBalance + "원");

        // 통계 갱신 등 추가 로직 삽입 가능
    }

    @EventHandler
    public void onTransaction(TransactionEvent event) {
        Player sender = event.getSender();
        Player receiver = event.getReceiver();
        double amount = event.getTransaction().getAmount();

        MessageUtil.send(sender, "거래가 처리되었습니다: " + receiver.getName() + "에게 " + amount + "원 송금.");
        MessageUtil.send(receiver, sender.getName() + "로부터 " + amount + "원을 받았습니다.");

        // 서버 기록, 통계, 이벤트 후처리 등
    }
}