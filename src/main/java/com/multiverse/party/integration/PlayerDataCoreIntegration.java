package com.multiverse.party.integration;

import com.multiverse.party.PartyCore;
import org.bukkit.entity.Player;

/**
 * PlayerDataCore 플러그인 연동, 레벨/스탯/경험치 접근용
 */
public class PlayerDataCoreIntegration {

    private final PartyCore plugin;

    public PlayerDataCoreIntegration(PartyCore plugin) {
        this.plugin = plugin;
    }

    // PlayerDataCore에서 플레이어의 레벨 가져오기
    public int getPlayerLevel(Player player) {
        // 실제 연동 API 접근
        return 1; // 예시 (실제 PlayerDataCore API 호출 해야 함)
    }

    // PlayerDataCore의 경험치 추가
    public void addExperience(Player player, int exp) {
        // 실제 연동 API 호출
    }

    public void refreshPlayerProfile(Player player) {
        // 캐시 또는 외부 시스템에서 데이터 새로고침
    }
}