package com.multiverse.npcai.commands;

import com.multiverse.npcai.NPCAICore;
import com.multiverse.npcai.managers.*;
import com.multiverse.npcai.models.enums.*;
import com.multiverse.npcai.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

/**
 * NPCAI 관리자 명령어 전체 구현
 * 권한: npcai.admin.*
 */
public class AdminCommand implements CommandExecutor {

    private final NPCAICore plugin;
    private final NPCManager npcManager;
    private final ReputationManager reputationManager;
    private final DialogueManager dialogueManager;
    private final ShopManager shopManager;
    private final SkillTrainerManager skillTrainerManager;
    private final AIBehaviorManager aiBehaviorManager;
    private final MessageUtil msg;

    public AdminCommand(NPCAICore plugin) {
        this.plugin = plugin;
        this.npcManager = plugin.getNPCManager();
        this.reputationManager = plugin.getReputationManager();
        this.dialogueManager = plugin.getDialogueManager();
        this.shopManager = plugin.getShopManager();
        this.skillTrainerManager = plugin.getSkillTrainerManager();
        this.aiBehaviorManager = plugin.getAIBehaviorManager();
        this.msg = new MessageUtil(plugin.getConfigUtil());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(msg.prefix("&c사용법: /npcai admin <subcommand> ..."));
            return false;
        }
        if (!(sender.hasPermission("npcai.admin"))) {
            sender.sendMessage(msg.prefix("&c관리자 권한이 필요합니다."));
            return true;
        }

        String sub = args[0].toLowerCase();
        try {
            switch (sub) {

                case "create": { // /npcai admin create <타입> <이름>
                    if (!(sender instanceof Player)) { msg.msg(sender, "&c플레이어만 사용 가능"); break; }
                    if (args.length < 3) { msg.msg(sender, "&c/npcai admin create <타입> <이름>"); break; }
                    NPCType type = safeNPCType(args[1]);
                    String name = args[2];
                    Location loc = ((Player) sender).getLocation();
                    npcManager.createNPC(name, type, loc);
                    msg.msg(sender, "&aNPC가 생성되었습니다: &e" + name);
                    break;
                }

                case "remove": { // /npcai admin remove <npcId>
                    if (args.length < 2) { msg.msg(sender, "&c/npcai admin remove <npcId>"); break; }
                    int npcId = Integer.parseInt(args[1]);
                    npcManager.removeNPC(npcId);
                    msg.msg(sender, "&cNPC ID " + npcId + " 제거 완료");
                    break;
                }

                case "settype": { // /npcai admin settype <npcId> <타입>
                    if (args.length < 3) { msg.msg(sender, "&c/npcai admin settype <npcId> <타입>"); break; }
                    int npcId = Integer.parseInt(args[1]);
                    NPCType type = safeNPCType(args[2]);
                    npcManager.setNPCType(npcId, type);
                    msg.msg(sender, "&aNPC 타입 변경: " + npcId + " → " + type);
                    break;
                }

                case "setdialogue": { // /npcai admin setdialogue <npcId> <파일경로>
                    if (args.length < 3) { msg.msg(sender, "&c/npcai admin setdialogue <npcId> <파일경로>"); break; }
                    int npcId = Integer.parseInt(args[1]);
                    String file = args[2];
                    dialogueManager.loadDialogueTree(npcId, file);
                    msg.msg(sender, "&a대화 트리 로드 완료. (" + file + ")");
                    break;
                }

                case "setbehavior": { // /npcai admin setbehavior <npcId> <행동>
                    if (args.length < 3) { msg.msg(sender, "&c/npcai admin setbehavior <npcId> <행동>"); break; }
                    int npcId = Integer.parseInt(args[1]);
                    BehaviorType behavior = safeBehaviorType(args[2]);
                    aiBehaviorManager.setBehavior(npcId, aiBehaviorManager.defaultBehavior(behavior));
                    msg.msg(sender, "&aNPC 행동 패턴 설정 완료: " + behavior.name());
                    break;
                }

                case "setpatrol": { // /npcai admin setpatrol <npcId>
                    if (!(sender instanceof Player)) { msg.msg(sender, "&c플레이어만 사용 가능"); break; }
                    if (args.length < 2) { msg.msg(sender, "&c/npcai admin setpatrol <npcId>"); break; }
                    int npcId = Integer.parseInt(args[1]);
                    Location loc = ((Player) sender).getLocation();
                    aiBehaviorManager.addPatrolPoint(npcId, loc);
                    msg.msg(sender, "&a순찰 경로에 현재 위치가 추가됨.");
                    break;
                }

                case "reputation": { // /npcai admin reputation <플레이어> <npcId> <set|add> <값>
                    if (args.length < 5) { msg.msg(sender, "&c사용법: /npcai admin reputation <플레이어> <npcId> <set|add> <값>"); break; }
                    Player p = Bukkit.getPlayer(args[1]);
                    if (p == null) { msg.msg(sender, "&c플레이어를 찾을 수 없음: " + args[1]); break; }
                    int npcId = Integer.parseInt(args[2]);
                    String op = args[3];
                    int value = Integer.parseInt(args[4]);
                    if (op.equalsIgnoreCase("set")) {
                        reputationManager.setPoints(p, npcId, value);
                        msg.msg(sender, "&a호감도 설정 완료: " + p.getName() + " - NPC " + npcId + " → " + value);
                    } else if (op.equalsIgnoreCase("add")) {
                        reputationManager.addPoints(p, npcId, value, "관리자 조정");
                        msg.msg(sender, "&a호감도 추가: " + p.getName() + " - NPC " + npcId + " + " + value);
                    } else {
                        msg.msg(sender, "&cset 또는 add 입력 필요");
                    }
                    break;
                }

                case "shop": { // 하위 shop 명령어
                    if (args.length < 2) { msg.msg(sender, "상점 명령어: create, additem"); break; }
                    String shopCmd = args[1].toLowerCase();
                    switch (shopCmd) {
                        case "create": { // /npcai admin shop create <npcId> <상점타입>
                            if (args.length < 4) { msg.msg(sender, "&c/npcai admin shop create <npcId> <상점타입>"); break; }
                            int npcId = Integer.parseInt(args[2]);
                            ShopType type = safeShopType(args[3]);
                            shopManager.createShop(npcId, Bukkit.getPlayer(sender.getName()).getName() + "의 상점", type);
                            msg.msg(sender, "&a상점 생성 완료! " + type.name());
                            break;
                        }
                        case "additem": { // /npcai admin shop additem <shopId> <아이템> <구매가> <판매가>
                            if (args.length < 6) { msg.msg(sender, "&c/npcai admin shop additem <shopId> <아이템> <구매가> <판매가>"); break; }
                            String shopId = args[2];
                            Material mat = Material.matchMaterial(args[3]);
                            if (mat == null) { msg.msg(sender, "&c아이템 이름 오류: " + args[3]); break; }
                            double buy = Double.parseDouble(args[4]);
                            double sell = Double.parseDouble(args[5]);
                            ItemStack item = new ItemStack(mat);
                            shopManager.addItem(shopId, shopManager.makeShopItem(item, buy, sell));
                            msg.msg(sender, "&a상점에 아이템 추가 완료: " + mat);
                            break;
                        }
                        default: msg.msg(sender, "&c알 수 없는 상점 명령어"); break;
                    }
                    break;
                }

                case "trainer": { // /npcai admin trainer create/addskill ...
                    if (args.length < 2) { msg.msg(sender, "트레이너 명령어: create, addskill"); break; }
                    String tcmd = args[1].toLowerCase();
                    switch (tcmd) {
                        case "create": { // /npcai admin trainer create <npcId> <트레이너타입>
                            if (args.length < 4) { msg.msg(sender, "&c/npcai admin trainer create <npcId> <트레이너타입>"); break; }
                            int npcId = Integer.parseInt(args[2]);
                            TrainerType tt = safeTrainerType(args[3]);
                            skillTrainerManager.createTrainer(npcId, tt);
                            msg.msg(sender, "&a트레이너 생성 완료! " + tt.name());
                            break;
                        }
                        case "addskill": { // /npcai admin trainer addskill <npcId> <스킬ID> <비용> <조건>
                            if (args.length < 6) { msg.msg(sender, "&c/npcai admin trainer addskill <npcId> <스킬ID> <비용> <조건>"); break; }
                            int npcId = Integer.parseInt(args[2]);
                            String skillId = args[3];
                            double cost = Double.parseDouble(args[4]);
                            String cond = args[5];
                            skillTrainerManager.addSkill(npcId, skillTrainerManager.makeSkill(skillId, cost, cond));
                            msg.msg(sender, "&a스킬 등록 완료: " + skillId);
                            break;
                        }
                        default: msg.msg(sender, "&c알 수 없는 트레이너 명령어"); break;
                    }
                    break;
                }

                case "reload": { // /npcai admin reload
                    plugin.reloadConfig();
                    plugin.getConfigUtil().reload();
                    plugin.getDataManager().reload();
                    msg.msg(sender, "&aNPCAICore 설정, 데이터 리로드 완료");
                    break;
                }

                default: msg.msg(sender, "&c알 수 없는 서브 명령어: " + args[0]); break;
            }
        } catch (Exception e) {
            sender.sendMessage(msg.prefix("&c오류 발생: " + e.getMessage()));
            e.printStackTrace();
        }
        return true;
    }

    // 편의: 타입 변환
    private NPCType safeNPCType(String type) {
        try { return NPCType.valueOf(type.toUpperCase()); }
        catch (Exception e) { return NPCType.CITIZEN; }
    }
    private BehaviorType safeBehaviorType(String btype) {
        try { return BehaviorType.valueOf(btype.toUpperCase()); }
        catch (Exception e) { return BehaviorType.STATIC; }
    }
    private ShopType safeShopType(String stype) {
        try { return ShopType.valueOf(stype.toUpperCase()); }
        catch (Exception e) { return ShopType.GENERAL; }
    }
    private TrainerType safeTrainerType(String ttype) {
        try { return TrainerType.valueOf(ttype.toUpperCase()); }
        catch (Exception e) { return TrainerType.GENERAL; }
    }
}