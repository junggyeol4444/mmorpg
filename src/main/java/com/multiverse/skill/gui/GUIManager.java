package com.multiverse.skill.gui;

import com.multiverse.skill.SkillCore;
import com.multiverse.skill.managers.SkillManager;
import com.multiverse.skill.data.models. SkillPreset;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * GUI 매니저
 */
public class GUIManager implements Listener {

    private final SkillCore plugin;
    private final SkillManager skillManager;
    private final SkillTreeGUI skillTreeGUI;
    private final SkillListGUI skillListGUI;
    private final SkillPresetGUI skillPresetGUI;

    public GUIManager(SkillCore plugin, SkillManager skillManager) {
        this. plugin = plugin;
        this. skillManager = skillManager;
        this.skillTreeGUI = new SkillTreeGUI(plugin, skillManager);
        this.skillListGUI = new SkillListGUI(plugin, skillManager);
        this.skillPresetGUI = new SkillPresetGUI(plugin, skillManager);
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * 스킬 트리 GUI 열기
     */
    public void openSkillTree(Player player, String treeId) {
        if (player == null) {
            return;
        }
        skillTreeGUI.openSkillTreeGUI(player, treeId);
    }

    /**
     * 스킬 목록 GUI 열기
     */
    public void openSkillList(Player player) {
        if (player == null) {
            return;
        }
        skillListGUI. openSkillListGUI(player);
    }

    /**
     * 프리셋 GUI 열기
     */
    public void openPresetGUI(Player player) {
        if (player == null) {
            return;
        }
        skillPresetGUI.openPresetGUI(player);
    }

    /**
     * 프리셋 호트바 GUI 열기
     */
    public void openPresetHotbar(Player player, SkillPreset preset) {
        if (player == null || preset == null) {
            return;
        }
        skillPresetGUI. openPresetHotbarGUI(player, preset);
    }

    /**
     * 인벤토리 클릭 이벤트
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        // GUI 타입별 처리
        if (title.contains("스킬 트리")) {
            handleSkillTreeClick(event, player);
        } else if (title.contains("습득한 스킬")) {
            handleSkillListClick(event, player);
        } else if (title.contains("스킬 프리셋")) {
            handlePresetClick(event, player);
        } else if (title.contains("호트바")) {
            handleHotbarClick(event, player);
        }
    }

    /**
     * 스킬 트리 클릭 처리
     */
    private void handleSkillTreeClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);
        // 스킬 습득 로직 구현
    }

    /**
     * 스킬 목록 클릭 처리
     */
    private void handleSkillListClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);
        // 스킬 정보 조회 로직 구현
    }

    /**
     * 프리셋 클릭 처리
     */
    private void handlePresetClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);
        // 프리셋 변경 로직 구현
    }

    /**
     * 호트바 클릭 처리
     */
    private void handleHotbarClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);
        // 호트바 스킬 변경 로직 구현
    }
}