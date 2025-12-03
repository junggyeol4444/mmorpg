package com.multiverse.death.listeners;

import com.multiverse.death.managers.*;
import com.multiverse.death.models.RevivalQuest;
import com.multiverse.death.models.enums.QuestType;
import com.multiverse.death.utils.MessageUtil;
import com.multiverse.death.utils.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class RevivalQuestListener implements Listener {

    private final RevivalManager revivalManager;
    private final DeathManager deathManager;
    private final InsuranceManager insuranceManager;
    private final NetherRealmManager netherRealmManager;
    private final SoulCoinManager soulCoinManager;
    private final MessageUtil msg;
    private final ConfigUtil config;

    public RevivalQuestListener(RevivalManager revivalManager,
                               DeathManager deathManager,
                               InsuranceManager insuranceManager,
                               NetherRealmManager netherRealmManager,
                               SoulCoinManager soulCoinManager,
                               MessageUtil msg,
                               ConfigUtil config) {
        this.revivalManager = revivalManager;
        this.deathManager = deathManager;
        this.insuranceManager = insuranceManager;
        this.netherRealmManager = netherRealmManager;
        this.soulCoinManager = soulCoinManager;
        this.msg = msg;
        this.config = config;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        RevivalQuest quest = revivalManager.getQuest(player);
        if (quest == null || quest.isCompleted()) return;

        // 예시: 특정 블록 클릭 이벤트로 진행도 증가
        if (quest.getType() == QuestType.BLOCK_CLICK) {
            String block = event.getClickedBlock() != null ? event.getClickedBlock().getType().name() : "";
            revivalManager.updateQuestProgress(player, QuestType.BLOCK_CLICK, block);
            player.sendMessage(msg.p("&a블록 클릭 퀘스트 진행도 증가: " + block));
            checkComplete(player);
        }
    }

    @EventHandler
    public void onEntityPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        RevivalQuest quest = revivalManager.getQuest(player);
        if (quest == null || quest.isCompleted()) return;

        // 예시: 아이템 줍기 이벤트
        if (quest.getType() == QuestType.ITEM_PICKUP) {
            String item = event.getItem().getItemStack().getType().name();
            revivalManager.updateQuestProgress(player, QuestType.ITEM_PICKUP, item);
            player.sendMessage(msg.p("&a아이템 퀘스트 진행도 증가: " + item));
            checkComplete(player);
        }
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        RevivalQuest quest = revivalManager.getQuest(player);
        if (quest == null || quest.isCompleted()) return;

        // Advancement 완료 퀘스트 처리
        if (quest.getType() == QuestType.ADVANCEMENT) {
            String adv = event.getAdvancement().getKey().toString();
            revivalManager.updateQuestProgress(player, QuestType.ADVANCEMENT, adv);
            player.sendMessage(msg.p("&a업적 퀘스트 진행도 증가: " + adv));
            checkComplete(player);
        }
    }

    @EventHandler
    public void onCheckpointMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        RevivalQuest quest = revivalManager.getQuest(player);
        if (quest == null || quest.isCompleted()) return;

        // 이동 퀘스트 처리
        if (quest.getType() == QuestType.WALK_TO_CHECKPOINT) {
            // 예시: 특정 위치 근처 이동 감지
            // 구현 생략: 명계 체크포인트 위치 근처 도달 시 진행도 증가
        }
    }

    private void checkComplete(Player player) {
        if (revivalManager.isQuestCompleted(player)) {
            revivalManager.completeQuest(player);
            player.sendMessage(msg.g("revival.quest-complete"));
        }
    }
}