package com.multiverse.npcai.commands;

import com.multiverse.npcai.NPCAICore;
import com.multiverse.npcai.managers.*;
import com.multiverse.npcai.models.NPCData;
import com.multiverse.npcai.models.Reputation;
import com.multiverse.npcai.models.enums.ReputationLevel;
import com.multiverse.npcai.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * 플레이어용 NPC 명령어 구현
 * 권한: npcai.player.*
 */
public class NPCCommand implements CommandExecutor {

    private final NPCAICore plugin;
    private final NPCManager npcManager;
    private final ReputationManager reputationManager;
    private final SkillTrainerManager skillTrainerManager;
    private final MessageUtil msg;

    public NPCCommand(NPCAICore plugin) {
        this.plugin = plugin;
        this.npcManager = plugin.getNPCManager();
        this.reputationManager = plugin.getReputationManager();
        this.skillTrainerManager = plugin.getSkillTrainerManager();
        this.msg = new MessageUtil(plugin.getConfigUtil());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.prefix("&c플레이어만 사용 가능합니다."));
            return true;
        }
        Player player = (Player) sender;

        if (args.length < 1) {
            sender.sendMessage(msg.prefix("&e사용법: /npc <info|reputation|gift|skills> ..."));
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {

            case "info": { // /npc info <npcId>
                if (!player.hasPermission("npcai.player.info")) {
                    msg.msg(player, "&c권한이 없습니다."); break;
                }
                if (args.length < 2) {
                    msg.msg(player, "&c/npc info <npcId>"); break;
                }
                int npcId = Integer.parseInt(args[1]);
                NPCData npc = npcManager.getNPC(npcId);
                if (npc == null) {
                    msg.msg(player, "&cNPC를 찾을 수 없습니다."); break;
                }
                msg.msg(player, "&e[NPC 정보]");
                msg.msg(player, "&7이름: &f" + npc.getName());
                msg.msg(player, "&7타입: &f" + npc.getType());
                msg.msg(player, "&7위치: &f" + npc.getLocation().toString());
                msg.msg(player, "&7호감도: &f" + reputationManager.getPoints(player, npcId));
                break;
            }

            case "reputation": { // /npc reputation [<npcId>]
                if (!player.hasPermission("npcai.player.reputation")) {
                    msg.msg(player, "&c권한이 없습니다."); break;
                }
                if (args.length == 1) {
                    List<Reputation> reps = reputationManager.getAllReputations(player);
                    msg.msg(player, "&e[호감도 목록]");
                    if (reps.isEmpty()) { msg.msg(player, "&7NPC 데이터가 없습니다."); break; }
                    for (Reputation rep : reps) {
                        NPCData npc = npcManager.getNPC(rep.getNpcId());
                        String npcName = npc != null ? npc.getName() : "ID " + rep.getNpcId();
                        msg.msg(player, "&e" + npcName + " &7: &f" + rep.getPoints() + " (" + rep.getLevel().name() + ")");
                    }
                } else {
                    int npcId = Integer.parseInt(args[1]);
                    Reputation rep = reputationManager.getReputation(player, npcId);
                    if (rep == null) {
                        msg.msg(player, "&c해당 NPC와의 호감도 정보가 없습니다."); break;
                    }
                    NPCData npc = npcManager.getNPC(npcId);
                    String npcName = npc != null ? npc.getName() : "ID " + npcId;
                    msg.msg(player, "&e" + npcName + " : &a" + rep.getPoints() + " (" + rep.getLevel().name() + ")");
                }
                break;
            }

            case "gift": { // /npc gift <npcId>
                if (!player.hasPermission("npcai.player.gift")) {
                    msg.msg(player, "&c권한이 없습니다."); break;
                }
                if (args.length < 2) { msg.msg(player, "&c/npc gift <npcId>"); break; }
                int npcId = Integer.parseInt(args[1]);
                NPCData npc = npcManager.getNPC(npcId);
                if (npc == null) { msg.msg(player, "&cNPC를 찾을 수 없습니다."); break; }
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item == null || item.getType().isAir()) {
                    msg.msg(player, "&c선물할 아이템을 손에 들어주세요."); break;
                }
                int reputationChange = reputationManager.calcGiftReputation(item);
                if (reputationChange <= 0) {
                    msg.msg(player, "&c이 아이템은 선물 효과가 없습니다."); break;
                }
                reputationManager.addPoints(player, npcId, reputationChange, "선물");
                player.getInventory().setItemInMainHand(null); // 선물 제거
                msg.msg(player, "&a선물 성공! 호감도 +" + reputationChange);
                break;
            }

            case "skills": { // /npc skills
                if (!player.hasPermission("npcai.player.skills")) {
                    msg.msg(player, "&c권한이 없습니다."); break;
                }
                List<String> currentSkills = skillTrainerManager.getLearningSkills(player);
                List<String> completedSkills = skillTrainerManager.getCompletedSkills(player);
                msg.msg(player, "&e[학습 중인 스킬]");
                for (String s : currentSkills) {
                    msg.msg(player, "&7- &b" + s);
                }
                msg.msg(player, "&e[완료한 스킬]");
                for (String s : completedSkills) {
                    msg.msg(player, "&7- &a" + s);
                }
                break;
            }

            default:
                msg.msg(player, "&c알 수 없는 명령어.");
                break;
        }

        return true;
    }
}