package com.multiverse. skill.listeners;

import com. multiverse.skill.SkillCore;
import com.multiverse.skill.managers.SkillManager;
import com.multiverse.skill.managers.SkillCastManager;
import com.multiverse.skill.data.models.*;
import com.multiverse.skill.data.enums.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org. bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit. event.player.PlayerInteractEvent;
import org.bukkit. inventory.ItemStack;

/**
 * 스킬 사용 리스너
 */
public class SkillUseListener implements Listener {

    private final SkillCore plugin;
    private final SkillManager skillManager;
    private final SkillCastManager castManager;

    public SkillUseListener(SkillCore plugin, SkillManager skillManager, SkillCastManager castManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
        this.castManager = castManager;
    }

    /**
     * 플레이어 상호작용 이벤트 (스킬 사용)
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (player == null || action == null) {
            return;
        }

        // 좌클릭 또는 우클릭만 처리
        if (action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK && 
            action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // 플레이어 스킬 데이터 조회
        PlayerSkillData skillData = skillManager.getPlayerSkillData(player.getUniqueId());
        if (skillData == null) {
            return;
        }

        // 활성 프리셋의 스킬 조회
        SkillPreset preset = skillData.getActivePreset();
        if (preset == null) {
            return;
        }

        // 손에 들고 있는 아이템 확인
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType(). isAir()) {
            return;
        }

        // 아이템의 커스텀 데이터에서 스킬 ID 추출
        String skillId = getSkillIdFromItem(item);
        if (skillId == null || skillId.isEmpty()) {
            return;
        }

        // 스킬 조회
        Skill skill = skillManager.getSkill(skillId);
        if (skill == null) {
            return;
        }

        // 습득한 스킬 확인
        LearnedSkill learnedSkill = skillData.getSkill(skillId);
        if (learnedSkill == null) {
            player.sendMessage("§c이 스킬을 습득하지 않았습니다!");
            return;
        }

        // 스킬 타입 확인
        if (skill.getType() != SkillType.ACTIVE) {
            player.sendMessage("§c활성 스킬만 사용할 수 있습니다!");
            return;
        }

        // 스킬 캐스팅 시작
        try {
            castManager.castSkill(player, skill, learnedSkill);
        } catch (Exception e) {
            player.sendMessage("§c스킬 사용 중 오류가 발생했습니다!");
            plugin.getLogger().warning("스킬 캐스팅 실패: " + skillId);
            e.printStackTrace();
        }

        event.setCancelled(true);
    }

    /**
     * 아이템에서 스킬 ID 추출
     */
    private String getSkillIdFromItem(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return null;
        }

        // 아이템 메타에서 커스텀 데이터 추출
        // 실제 구현에서는 NBT 태그나 PersistentDataContainer 사용
        String displayName = item.getItemMeta().getDisplayName();
        
        // 임시 구현: 아이템 이름에서 스킬 ID 추출
        if (displayName != null && displayName.contains("[")) {
            int start = displayName.indexOf("[") + 1;
            int end = displayName.indexOf("]");
            if (start > 0 && end > start) {
                return displayName.substring(start, end);
            }
        }

        return null;
    }
}