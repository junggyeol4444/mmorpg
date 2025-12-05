package com.multiverse.skill.listeners;

import com.multiverse.skill.SkillCore;
import com.multiverse. skill.managers. SkillManager;
import com.multiverse. skill.data.models.PlayerSkillData;
import com.multiverse. skill.data.storage.PlayerDataLoader;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event. Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event. player.PlayerQuitEvent;

/**
 * 플레이어 입장/퇴장 리스너
 */
public class PlayerJoinListener implements Listener {

    private final SkillCore plugin;
    private final SkillManager skillManager;
    private final PlayerDataLoader playerDataLoader;

    public PlayerJoinListener(SkillCore plugin, SkillManager skillManager, PlayerDataLoader playerDataLoader) {
        this.plugin = plugin;
        this.skillManager = skillManager;
        this.playerDataLoader = playerDataLoader;
    }

    /**
     * 플레이어 입장 이벤트
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if (player == null) {
            return;
        }

        // 플레이어 데이터 로드
        PlayerSkillData skillData = playerDataLoader.loadPlayerData(player. getUniqueId());
        
        if (skillData == null) {
            skillData = new PlayerSkillData(player.getUniqueId(), player.getName());
            // 새로운 플레이어 초기화
            initializeNewPlayer(skillData);
        }

        // 플레이어 데이터를 메모리에 등록
        skillManager.registerPlayerData(player. getUniqueId(), skillData);

        // 플레이어에게 환영 메시지 전송
        sendWelcomeMessage(player);

        plugin.getLogger().info("✅ 플레이어 입장: " + player.getName() + " (UUID: " + player.getUniqueId() + ")");
    }

    /**
     * 새로운 플레이어 초기화
     */
    private void initializeNewPlayer(PlayerSkillData skillData) {
        // 초기 스킬 포인트 지급
        skillData.setTotalSkillPoints(10);
        skillData.setAvailableSkillPoints(10);
        skillData.setUsedSkillPoints(0);

        // 기본 프리셋 생성
        skillData.addPreset(skillManager.createDefaultPreset());

        plugin.getLogger().info("✅ 새 플레이어 초기화: " + skillData.getPlayerName());
    }

    /**
     * 플레이어 환영 메시지
     */
    private void sendWelcomeMessage(Player player) {
        player.sendMessage("§b========== 스킬 시스템 ==========");
        player.sendMessage("§a/skill help - 명령어 도움말");
        player.sendMessage("§a/skill list - 스킬 목록");
        player.sendMessage("§a/skill tree - 스킬 트리 확인");
        player.sendMessage("§b==================================");
    }

    /**
     * 플레이어 퇴장 이벤트
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        if (player == null) {
            return;
        }

        // 플레이어 데이터 저장
        savePlayerData(player);

        // 메모리에서 플레이어 데이터 제거
        skillManager.unregisterPlayerData(player.getUniqueId());

        plugin.getLogger().info("✅ 플레이어 퇴장: " + player. getName());
    }

    /**
     * 플레이어 데이터 저장
     */
    private void savePlayerData(Player player) {
        try {
            PlayerSkillData skillData = skillManager.getPlayerSkillData(player.getUniqueId());
            
            if (skillData != null) {
                playerDataLoader.savePlayerData(player.getUniqueId());
            }
        } catch (Exception e) {
            plugin. getLogger().warning("플레이어 데이터 저장 실패: " + player.getName());
            e.printStackTrace();
        }
    }
}