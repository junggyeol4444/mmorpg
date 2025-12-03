package com.multiverse.death.api;

import com.multiverse.death.models.DeathRecord;
import com.multiverse.death.models.RevivalQuest;
import org.bukkit.entity.Player;
import java.util.UUID;

/**
 * DeathAndRebirth 외부 API 제공 클래스
 */
public class DeathAndRebirthAPI {

    private static DeathAndRebirthAPI instance;

    public static DeathAndRebirthAPI getInstance() {
        if (instance == null) {
            instance = new DeathAndRebirthAPI();
        }
        return instance;
    }

    public DeathRecord getDeathRecord(Player player) {
        // 실제 DB나 메모리에서 DeathRecord 객체 반환
        return DeathAndRebirthAPICore.getDeathRecord(player.getUniqueId());
    }

    public RevivalQuest getActiveRevivalQuest(Player player) {
        // 실제 DB나 메모리에서 RevivalQuest 객체 반환
        return DeathAndRebirthAPICore.getRevivalQuest(player.getUniqueId());
    }

    public boolean revivePlayer(Player player) {
        // 실제 부활 처리 로직
        return DeathAndRebirthAPICore.revivePlayer(player);
    }

    public boolean paySoulCoin(Player player, int amount) {
        // 실제 소울 코인 지불 로직
        return DeathAndRebirthAPICore.paySoulCoin(player, amount);
    }
}