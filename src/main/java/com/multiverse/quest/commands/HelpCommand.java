package com.multiverse.quest.commands;

import com.multiverse.quest.managers.QuestDataManager;
import org.bukkit.command.Command;
import org.bukkit. command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit. Bukkit;
import java.util.*;

/**
 * 도움말 명령어
 * 플레이어에게 퀨스트 시스템에 대한 전체 도움말을 제공합니다.
 */
public class HelpCommand implements CommandExecutor {
    private final QuestDataManager questDataManager;

    /**
     * 생성자
     * @param questDataManager 퀨스트 데이터 관리자
     */
    public HelpCommand(QuestDataManager questDataManager) {
        this.questDataManager = questDataManager;
    }

    // ============ Command Execution ============

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            showMainHelp(player);
            return true;
        }

        String topic = args[0].toLowerCase();

        switch (topic) {
            case "quest":
                showQuestHelp(player);
                break;
            case "admin":
                showAdminHelp(player);
                break;
            case "player":
                showPlayerHelp(player);
                break;
            case "report":
                showReportHelp(player);
                break;
            case "stats":
                showStatsHelp(player);
                break;
            case "getting-started":
                showGettingStarted(player);
                break;
            case "faq":
                showFAQ(player);
                break;
            case "about":
                showAbout(player);
                break;
            default:
                player.sendMessage("§c알 수 없는 도움말 주제입니다: " + topic);
                showMainHelp(player);
                return true;
        }

        return true;
    }

    // ============ Help Sections ============

    /**
     * 메인 도움말
     */
    private void showMainHelp(Player player) {
        player.sendMessage("§6════════════════════════════════════════════════════════════════§r");
        player.sendMessage("§6                    QuestCore 도움말                           §r");
        player.sendMessage("§6════════════════════════════════════════════════════════════════§r");

        player.sendMessage("\n§e【 기본 명령어 】§r");
        player.sendMessage("§f/quest §7- 퀨스트 기본 명령어");
        player.sendMessage("§f/pquest §7- 플레이어 개인 설정 명령어");
        player.sendMessage("§f/stats §7- 통계 조회 명령어");
        player. sendMessage("§f/report §7- 문제 보고 명령어");
        player.sendMessage("§f/questadmin §7- 관리자 명령어");

        player.sendMessage("\n§e【 도움말 주제 】§r");
        player.sendMessage("§f/help quest §7- 퀨스트 명령어 상세 설명");
        player.sendMessage("§f/help player §7- 플레이어 명령어 상세 설명");
        player.sendMessage("§f/help stats §7- 통계 명령어 상세 설명");
        player.sendMessage("§f/help report §7- 보고 명령어 상세 설명");
        player. sendMessage("§f/help admin §7- 관리자 명령어 상세 설명 (관리자만)");

        player.sendMessage("\n§e【 초보자 가이드 】§r");
        player.sendMessage("§f/help getting-started §7- 빠른 시작 가이드");
        player.sendMessage("§f/help faq §7- 자주 묻는 질문");
        player.sendMessage("§f/help about §7- 플러그인 정보");

        player.sendMessage("\n§6════════════════════════════════════════════════════════════════§r");
    }

    /**
     * 퀨스트 명령어 도움말
     */
    private void showQuestHelp(Player player) {
        player.sendMessage("§6=== 퀨스트 명령어 (/quest) ===§r\n");

        player.sendMessage("§e【 주요 명령어 】§r");
        player.sendMessage("§f/quest list §7- 수락 가능한 퀨스트 목록 조회");
        player.sendMessage("§f/quest info <ID> §7- 특정 퀨스트 상세 정보 조회");
        player.sendMessage("§f/quest accept <ID> §7- 퀨스트 수락");
        player.sendMessage("§f/quest complete <ID> §7- 퀨스트 완료");
        player.sendMessage("§f/quest abandon <ID> §7- 퀨스트 포기");

        player.sendMessage("\n§e【 진행도 관련 】§r");
        player.sendMessage("§f/quest progress §7- 진행 중인 퀨스트 목록");
        player.sendMessage("§f/quest track <ID> §7- 퀨스트 추적 시작");
        player.sendMessage("§f/quest untrack §7- 퀨스트 추적 중지");

        player.sendMessage("\n§e【 정보 조회 】§r");
        player.sendMessage("§f/quest status §7- 퀨스트 통계 조회");
        player.sendMessage("§f/quest reward <ID> §7- 보상 정보 미리보기");

        player.sendMessage("\n§7💡 팁: /quest로 도움말을 볼 수 있습니다.\n");
    }

    /**
     * 플레이어 명령어 도움말
     */
    private void showPlayerHelp(Player player) {
        player.sendMessage("§6=== 플레이어 명령어 (/pquest) ===§r\n");

        player.sendMessage("§e【 개인 설정 】§r");
        player.sendMessage("§f/pquest settings §7- 현재 설정 보기");
        player.sendMessage("§f/pquest settings <설정> <true|false> §7- 설정 변경");
        player.sendMessage("  §7옵션: sound, title, actionbar, chat");

        player.sendMessage("\n§e【 알림 설정 】§r");
        player.sendMessage("§f/pquest notification on §7- 모든 알림 활성화");
        player.sendMessage("§f/pquest notification off §7- 모든 알림 비활성화");
        player.sendMessage("§f/pquest notification reset §7- 알림 설정 초기화");

        player.sendMessage("\n§e【 추적기 설정 】§r");
        player.sendMessage("§f/pquest tracker <위치> §7- 추적기 위치 변경");
        player.sendMessage("  §7위치: TOP_LEFT, TOP_CENTER, TOP_RIGHT");
        player.sendMessage("  §7     CENTER, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT");

        player.sendMessage("\n§e【 즐겨찾기 】§r");
        player.sendMessage("§f/pquest favorite add <ID> §7- 퀨스트 즐겨찾기 추가");
        player.sendMessage("§f/pquest favorite remove <ID> §7- 즐겨찾기 제거");
        player.sendMessage("§f/pquest favorite clear §7- 모든 즐겨찾기 제거");

        player.sendMessage("\n§e【 정보 조회 】§r");
        player.sendMessage("§f/pquest history §7- 완료한 퀨스트 이력");
        player.sendMessage("§f/pquest daily §7- 일일 퀨스트 정보");
        player.sendMessage("§f/pquest weekly §7- 주간 퀨스트 정보");

        player.sendMessage("\n§7💡 팁: /pquest로 도움말을 볼 수 있습니다.\n");
    }

    /**
     * 통계 명령어 도움말
     */
    private void showStatsHelp(Player player) {
        player.sendMessage("§6=== 통계 명령어 (/stats) ===§r\n");

        player.sendMessage("§e【 개인 통계 】§r");
        player.sendMessage("§f/stats §7- 개인 통계 조회");
        player.sendMessage("§f/stats personal §7- 개인 상세 통계");
        player.sendMessage("§f/stats daily §7- 일일 퀨스트 통계");
        player.sendMessage("§f/stats weekly §7- 주간 퀨스트 통계");

        player.sendMessage("\n§e【 전체 통계 】§r");
        player.sendMessage("§f/stats quest §7- 퀨스트 통계");
        player.sendMessage("§f/stats chain §7- 체인 통계");
        player.sendMessage("§f/stats reward §7- 보상 통계");

        player.sendMessage("\n§e【 순위 (관리자) 】§r");
        player.sendMessage("§f/stats top completed §7- 완료 순위");
        player.sendMessage("§f/stats top rewards §7- 보상 순위");
        player.sendMessage("§f/stats top level §7- 레벨 순위");

        player.sendMessage("\n§7💡 팁: /stats로 개인 통계를 바로 볼 수 있습니다.\n");
    }

    /**
     * 보고 명령어 도움말
     */
    private void showReportHelp(Player player) {
        player.sendMessage("§6=== 보고 명령어 (/report) ===§r\n");

        player.sendMessage("§e【 보고 제출 】§r");
        player.sendMessage("§f/report bug <설명> §7- 버그 보고");
        player.sendMessage("§f/report suggestion <설명> §7- 건의사항 제출");
        player.sendMessage("§f/report issue <설명> §7- 문제 보고");

        player.sendMessage("\n§e【 보고 관리 】§r");
        player.sendMessage("§f/report check <ID> §7- 보고 상태 확인");
        player.sendMessage("§f/report list §7- 내 보고 목록");

        player.sendMessage("\n§7💡 팁: 버그나 문제를 발견하면 /report로 관리자에게 알려주세요!\n");
    }

    /**
     * 관리자 명령어 도움말
     */
    private void showAdminHelp(Player player) {
        if (! player.hasPermission("questcore.admin")) {
            player. sendMessage("§c관리자 권한이 없습니다.");
            return;
        }

        player.sendMessage("§6=== 관리자 명령어 (/questadmin) ===§r\n");

        player.sendMessage("§e【 퀨스트 관리 】§r");
        player. sendMessage("§f/questadmin create <ID> <이름> §7- 퀨스트 생성");
        player.sendMessage("§f/questadmin delete <ID> §7- 퀨스트 삭제");
        player.sendMessage("§f/questadmin edit <ID> <설정> <값> §7- 퀨스트 수정");
        player.sendMessage("§f/questadmin list §7- 전체 퀨스트 목록");

        player.sendMessage("\n§e【 플레이어 관리 】§r");
        player.sendMessage("§f/questadmin player <이름> info §7- 플레이어 정보");
        player.sendMessage("§f/questadmin player <이름> reset §7- 플레이어 데이터 초기화");
        player.sendMessage("§f/questadmin player <이름> complete <ID> §7- 퀨스트 완료 처리");
        player.sendMessage("§f/questadmin give <이름> <ID> §7- 플레이어에게 퀨스트 지급");

        player.sendMessage("\n§e【 시스템 관리 】§r");
        player.sendMessage("§f/questadmin reload §7- 데이터 다시 로드");
        player.sendMessage("§f/questadmin reset all §7- 모든 퀨스트 초기화");
        player.sendMessage("§f/questadmin reset daily §7- 일일 퀨스트 초기화");
        player. sendMessage("§f/questadmin reset weekly §7- 주간 퀨스트 초기화");

        player. sendMessage("\n§e【 통계 】§r");
        player.sendMessage("§f/questadmin stats §7- 전체 통계");
        player.sendMessage("§f/questadmin status §7- 시스템 상태");

        player.sendMessage("\n§7💡 팁: 관리자만 이 명령어를 사용할 수 있습니다.\n");
    }

    /**
     * 빠른 시작 가이드
     */
    private void showGettingStarted(Player player) {
        player. sendMessage("§6════════════════════════════════════════════════════════════════§r");
        player.sendMessage("§6                    빠른 시작 가이드                           §r");
        player.sendMessage("§6════════════════════════════════════════════════════════════════§r");

        player.sendMessage("\n§e【 1단계: 퀨스트 찾기 】§r");
        player.sendMessage("§f/quest list§r를 입력하여 수락 가능한 퀨스트를 확인하세요.");

        player.sendMessage("\n§e【 2단계: 퀨스트 정보 조회 】§r");
        player.sendMessage("§f/quest info <퀨스트ID>§r로 상세 정보와 보상을 확인하세요.");

        player.sendMessage("\n§e【 3단계: 퀨스트 수락 】§r");
        player.sendMessage("§f/quest accept <퀨스트ID>§r로 퀨스트를 수락하세요.");

        player.sendMessage("\n§e【 4단계: 진행도 확인 】§r");
        player.sendMessage("§f/quest progress§r로 진행 중인 퀨스트를 확인하세요.");
        player.sendMessage("§f/quest track <퀨스트ID>§r로 특정 퀨스트를 추적하세요.");

        player.sendMessage("\n§e【 5단계: 퀨스트 완료 】§r");
        player. sendMessage("목표를 모두 완료한 후 §f/quest complete <퀨스트ID>§r로 완료하세요.");

        player.sendMessage("\n§e【 보상 받기 】§r");
        player.sendMessage("퀨스트 완료 시 경험치, 돈, 아이템 등의 보상을 받습니다.");
        player.sendMessage("§f/quest reward <퀨스트ID>§r로 미리 확인할 수 있습니다.");

        player.sendMessage("\n§e【 추가 팁 】§r");
        player.sendMessage("§7• /stats로 통계를 확인하세요");
        player.sendMessage("§7• /pquest settings로 알림을 개인화하세요");
        player.sendMessage("§7• 버그나 건의사항은 /report로 제출하세요");

        player.sendMessage("\n§6════════════════════════════════════════════════════════════════§r\n");
    }

    /**
     * 자주 묻는 질문
     */
    private void showFAQ(Player player) {
        player.sendMessage("§6════════════════════════════════════════════════════════════════§r");
        player.sendMessage("§6                    자주 묻는 질문 (FAQ)                     §r");
        player.sendMessage("§6════════════════════════════════════════════════════════════════§r");

        player.sendMessage("\n§e Q: 퀨스트를 포기하면 어떻게 되나요?§r");
        player.sendMessage("§7 A: /quest abandon <ID>로 퀨스트를 포기할 수 있습니다.");
        player.sendMessage("    포기한 퀨스트는 다시 수락할 수 있습니다.");

        player. sendMessage("\n§e Q: 퀨스트 진행도를 초기화할 수 있나요?§r");
        player.sendMessage("§7 A: 플레이어는 포기 후 다시 수락하면 됩니다.");
        player. sendMessage("    관리자는 /questadmin player <이름> reset로 초기화할 수 있습니다.");

        player.sendMessage("\n§e Q: 일일/주간 퀨스트는 언제 초기화되나요?§r");
        player.sendMessage("§7 A: 일일 퀨스트는 매일 자정에, 주간 퀨스트는 매주 월요일에 초기화됩니다.");

        player.sendMessage("\n§e Q: 보상을 못 받으면? §r");
        player.sendMessage("§7 A: 인벤토리 공간이 부족할 수 있습니다.");
        player.sendMessage("    인벤토리를 정리한 후 다시 시도하세요.");

        player.sendMessage("\n§e Q: 체인 퀨스트란? §r");
        player.sendMessage("§7 A: 일련의 퀨스트를 순서대로 완료하는 퀨스트입니다.");
        player.sendMessage("    이전 퀨스트를 완료해야 다음 퀨스트를 수락할 수 있습니다.");

        player. sendMessage("\n§6════════════════════════════════════════════════════════════════§r\n");
    }

    /**
     * 플러그인 정보
     */
    private void showAbout(Player player) {
        player.sendMessage("§6════════════════════════════════════════════════════════════════§r");
        player.sendMessage("§6                        QuestCore v1.0                       §r");
        player.sendMessage("§6════════════════════════════════════════════════════════════════§r");

        player.sendMessage("\n§e【 플러그인 정보 】§r");
        player.sendMessage("§7이름: QuestCore");
        player.sendMessage("§7버전: 1.0. 0");
        player.sendMessage("§7설명: 포괄적인 퀨스트 시스템 플러그인");

        player.sendMessage("\n§e【 주요 기능 】§r");
        player.sendMessage("§7• 일반/일일/주간 퀨스트");
        player.sendMessage("§7• 퀨스트 체인");
        player.sendMessage("§7• 목표 기반 진행도 추적");
        player.sendMessage("§7• 다양한 보상 시스템");
        player.sendMessage("§7• 플레이어 통계");
        player.sendMessage("§7• NPC 상호작용");

        player.sendMessage("\n§e【 지원 명령어 】§r");
        player.sendMessage("§7• /quest - 기본 퀨스트 명령어");
        player.sendMessage("§7• /pquest - 플레이어 개인 설정");
        player.sendMessage("§7• /stats - 통계 조회");
        player.sendMessage("§7• /report - 문제 보고");
        player.sendMessage("§7• /questadmin - 관리자 명령어");

        player.sendMessage("\n§e【 더 알아보기 】§r");
        player.sendMessage("§f/help getting-started§7 - 빠른 시작 가이드");
        player.sendMessage("§f/help faq§7 - 자주 묻는 질문");

        player.sendMessage("\n§6════════════════════════════════════════════════════════════════§r\n");
    }

    // ============ Getters ============

    /**
     * 데이터 관리자 반환
     */
    public QuestDataManager getQuestDataManager() {
        return questDataManager;
    }
}