package com.multiverse.death.gui;

import com.multiverse.death.models.RevivalQuest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.Bukkit;

/**
 * 부활 관련 GUI를 생성 및 관리하는 클래스
 */
public class RevivalGUI {

    public static Inventory createQuestProgressGUI(Player player, RevivalQuest quest) {
        Inventory gui = Bukkit.createInventory(null, 27, "부활 퀘스트 진행");
        // TODO: 퀘스트 진행 상황에 따라 아이템을 채워넣는다.
        // 예: quest.getType(), quest.getProgress(), quest.getGoal() 등을 활용
        return gui;
    }

    public static void openQuestProgress(Player player, RevivalQuest quest) {
        Inventory gui = createQuestProgressGUI(player, quest);
        player.openInventory(gui);
    }
}