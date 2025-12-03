package com.multiverse.economy.listeners;

import com.multiverse.economy.events.TransactionEvent;
import com.multiverse.economy.models.BankAccount;
import com.multiverse.economy.models.Transaction;
import com.multiverse.economy.utils.MessageUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;

public class TransactionListener implements Listener {

    @EventHandler
    public void onTransaction(TransactionEvent event) {
        Transaction transaction = event.getTransaction();
        Player sender = event.getSender();
        Player receiver = event.getReceiver();

        // 거래 검증 로직: 예시로 잔액 부족, 일일 한도 등
        BankAccount senderAccount = transaction.getSenderAccount();
        BankAccount receiverAccount = transaction.getReceiverAccount();

        if (senderAccount.getBalance() < transaction.getAmount()) {
            MessageUtil.send(sender, "거래 실패: 잔액이 부족합니다.");
            event.setCancelled(true);
            return;
        }

        // 실제 거래 처리
        senderAccount.withdraw(transaction.getAmount());
        receiverAccount.deposit(transaction.getAmount());

        // 로그 기록 및 알림
        MessageUtil.send(sender, "거래 완료! " + receiver.getName() + "에게 " + transaction.getAmount() + "원을 보냈습니다.");
        MessageUtil.send(receiver, sender.getName() + "로부터 " + transaction.getAmount() + "원을 받았습니다.");

        // 필요에 따라 DB 저장, 통계 갱신 등
        // StatisticsStorage 등에서 처리 추가 가능
    }
}