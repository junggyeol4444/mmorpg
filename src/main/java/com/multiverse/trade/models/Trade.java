package com.multiverse.trade.models;

import org.bukkit. Bukkit;
import org.bukkit. entity.Player;
import org.bukkit. inventory.ItemStack;

import java.util. ArrayList;
import java. util.List;
import java.util. UUID;

public class Trade {

    private UUID tradeId;
    private UUID player1;
    private UUID player2;
    
    private List<ItemStack> player1Items;
    private List<ItemStack> player2Items;
    
    private String currency;
    private double player1Money;
    private double player2Money;
    
    private TradeStatus status;
    private boolean player1Ready;
    private boolean player2Ready;
    private long confirmStartTime;
    
    private long startTime;
    private long endTime;
    
    private double taxAmount;

    public Trade() {
        this.player1Items = new ArrayList<>();
        this.player2Items = new ArrayList<>();
        this.currency = "default";
        this. player1Money = 0;
        this.player2Money = 0;
        this. status = TradeStatus.REQUESTED;
        this. player1Ready = false;
        this. player2Ready = false;
        this. confirmStartTime = 0;
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this. taxAmount = 0;
    }

    public Trade(UUID tradeId, UUID player1, UUID player2) {
        this();
        this.tradeId = tradeId;
        this.player1 = player1;
        this.player2 = player2;
    }

    public UUID getTradeId() {
        return tradeId;
    }

    public void setTradeId(UUID tradeId) {
        this. tradeId = tradeId;
    }

    public UUID getPlayer1() {
        return player1;
    }

    public void setPlayer1(UUID player1) {
        this.player1 = player1;
    }

    public UUID getPlayer2() {
        return player2;
    }

    public void setPlayer2(UUID player2) {
        this.player2 = player2;
    }

    public List<ItemStack> getPlayer1Items() {
        return player1Items;
    }

    public void setPlayer1Items(List<ItemStack> player1Items) {
        this.player1Items = player1Items;
    }

    public List<ItemStack> getPlayer2Items() {
        return player2Items;
    }

    public void setPlayer2Items(List<ItemStack> player2Items) {
        this.player2Items = player2Items;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getPlayer1Money() {
        return player1Money;
    }

    public void setPlayer1Money(double player1Money) {
        this.player1Money = player1Money;
    }

    public double getPlayer2Money() {
        return player2Money;
    }

    public void setPlayer2Money(double player2Money) {
        this.player2Money = player2Money;
    }

    public TradeStatus getStatus() {
        return status;
    }

    public void setStatus(TradeStatus status) {
        this. status = status;
    }

    public boolean isPlayer1Ready() {
        return player1Ready;
    }

    public void setPlayer1Ready(boolean player1Ready) {
        this.player1Ready = player1Ready;
    }

    public boolean isPlayer2Ready() {
        return player2Ready;
    }

    public void setPlayer2Ready(boolean player2Ready) {
        this.player2Ready = player2Ready;
    }

    public long getConfirmStartTime() {
        return confirmStartTime;
    }

    public void setConfirmStartTime(long confirmStartTime) {
        this.confirmStartTime = confirmStartTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Player getOtherPlayer(Player player) {
        if (player. getUniqueId().equals(player1)) {
            return Bukkit.getPlayer(player2);
        } else if (player.getUniqueId().equals(player2)) {
            return Bukkit.getPlayer(player1);
        }
        return null;
    }

    public UUID getOtherPlayerUUID(UUID playerId) {
        if (playerId.equals(player1)) {
            return player2;
        } else if (playerId.equals(player2)) {
            return player1;
        }
        return null;
    }

    public boolean isParticipant(UUID playerId) {
        return playerId.equals(player1) || playerId.equals(player2);
    }

    public List<ItemStack> getItemsFor(UUID playerId) {
        if (playerId.equals(player1)) {
            return player1Items;
        } else if (playerId.equals(player2)) {
            return player2Items;
        }
        return new ArrayList<>();
    }

    public double getMoneyFor(UUID playerId) {
        if (playerId. equals(player1)) {
            return player1Money;
        } else if (playerId.equals(player2)) {
            return player2Money;
        }
        return 0;
    }

    public boolean isReady(UUID playerId) {
        if (playerId.equals(player1)) {
            return player1Ready;
        } else if (playerId.equals(player2)) {
            return player2Ready;
        }
        return false;
    }

    public void setReady(UUID playerId, boolean ready) {
        if (playerId.equals(player1)) {
            player1Ready = ready;
        } else if (playerId.equals(player2)) {
            player2Ready = ready;
        }
    }

    public boolean areBothReady() {
        return player1Ready && player2Ready;
    }

    public long getDuration() {
        if (endTime > 0) {
            return endTime - startTime;
        }
        return System.currentTimeMillis() - startTime;
    }
}