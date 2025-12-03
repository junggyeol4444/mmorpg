package com.multiverse.death.utils;

import org.bukkit.entity.Player;

/**
 * 소울 코인(가상 화폐) 관련 유틸리티
 */
public class EconomyUtil {

    // 플레이어 소울 코인 잔액 확인
    public static int getSoulCoin(Player player) {
        return player.getPersistentDataContainer().getOrDefault(DeathKeys.SOUL_COIN, 0);
    }

    // 소울 코인 지급
    public static void addSoulCoin(Player player, int amount) {
        int current = getSoulCoin(player);
        setSoulCoin(player, current + amount);
    }

    // 소울 코인 차감 (잔액 부족 시 false 반환)
    public static boolean deductSoulCoin(Player player, int amount) {
        int current = getSoulCoin(player);
        if (current < amount) {
            return false;
        }
        setSoulCoin(player, current - amount);
        return true;
    }

    // 소울 코인 설정
    public static void setSoulCoin(Player player, int amount) {
        player.getPersistentDataContainer().set(DeathKeys.SOUL_COIN, amount);
    }
}