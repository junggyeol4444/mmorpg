package com.multiverse.party.listeners;

import com. multiverse.party. PartyCore;
import com.  multiverse. party. models.Party;
import org.bukkit.  Bukkit;
import org.bukkit.  entity.Player;
import org.bukkit.  event.EventHandler;
import org.bukkit. event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.  util.Map;
import java.util.  UUID;
import java.util. concurrent.ConcurrentHashMap;

public class ChatListener implements Listener {

    private final PartyCore plugin;
    private final Map<UUID, ChatMode> playerChatMode;
    private final Map<UUID, ChatInputHandler> pendingInputs;

    public ChatListener(PartyCore plugin) {
        this.  plugin = plugin;
        this. playerChatMode = new ConcurrentHashMap<>();
        this.pendingInputs = new ConcurrentHashMap<>();
    }

    // ==================== 채팅 이벤트 처리 ====================
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // 입력 대기 상태 확인 (파티 이름 변경 등)
        ChatInputHandler inputHandler = pendingInputs.remove(player.getUniqueId());
        if (inputHandler != null) {
            event.setCancelled(true);
            
            // 메인 스레드에서 처리
            Bukkit.getScheduler().runTask(plugin, () -> {
                inputHandler.handle(player, message);
            });
            return;
        }

        // 파티 채팅 모드 확인
        ChatMode mode = playerChatMode.get(player.getUniqueId());
        if (mode == ChatMode.PARTY) {
            event.setCancelled(true);
            
            Party party = plugin.getPartyManager().getPlayerParty(player);
            if (party == null) {
                setPlayerChatMode(player, ChatMode. NORMAL);
                player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
                return;
            }

            // 메인 스레드에서 파티 채팅 전송
            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.getPartyChatManager().sendPartyMessage(player, message);
            });
            return;
        }

        // 파티 채팅 접두사 확인
        if (plugin.getConfig().getBoolean("chat. prefix-enabled", true)) {
            String prefix = plugin.getConfig().getString("chat.prefix", "@p ");
            
            if (message. startsWith(prefix)) {
                event.setCancelled(true);
                
                Party party = plugin.getPartyManager().getPlayerParty(player);
                if (party == null) {
                    player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
                    return;
                }

                String partyMessage = message.substring(prefix.length());
                
                Bukkit.getScheduler().runTask(plugin, () -> {
                    plugin. getPartyChatManager().sendPartyMessage(player, partyMessage);
                });
            }
        }
    }

    // ==================== 플레이어 퇴장 시 정리 ====================
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        playerChatMode.remove(playerUUID);
        pendingInputs.remove(playerUUID);
    }

    // ==================== 채팅 모드 관리 ====================
    public void setPlayerChatMode(Player player, ChatMode mode) {
        if (mode == ChatMode.NORMAL) {
            playerChatMode.remove(player.getUniqueId());
        } else {
            playerChatMode.put(player. getUniqueId(), mode);
        }
    }

    public ChatMode getPlayerChatMode(Player player) {
        return playerChatMode.getOrDefault(player. getUniqueId(), ChatMode.NORMAL);
    }

    public void togglePartyChatMode(Player player) {
        ChatMode currentMode = getPlayerChatMode(player);
        
        if (currentMode == ChatMode. PARTY) {
            setPlayerChatMode(player, ChatMode.NORMAL);
            player.sendMessage(plugin.getMessageUtil().getMessage("chat.mode-normal"));
        } else {
            Party party = plugin.getPartyManager().getPlayerParty(player);
            if (party == null) {
                player.sendMessage(plugin.getMessageUtil().getMessage("party.not-in-party"));
                return;
            }
            
            setPlayerChatMode(player, ChatMode.PARTY);
            player.sendMessage(plugin.getMessageUtil().getMessage("chat.mode-party"));
        }
    }

    // ==================== 채팅 입력 대기 ====================
    public void waitForInput(Player player, ChatInputHandler handler, String prompt) {
        waitForInput(player, handler, prompt, 60);
    }

    public void waitForInput(Player player, ChatInputHandler handler, String prompt, int timeoutSeconds) {
        // 이전 대기 취소
        pendingInputs.remove(player.getUniqueId());

        // 프롬프트 메시지 전송
        player.sendMessage(prompt);
        player.sendMessage(plugin.getMessageUtil().getMessage("chat.input-hint"));

        // 핸들러 등록
        pendingInputs.put(player.getUniqueId(), handler);

        // 타임아웃 설정
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ChatInputHandler removed = pendingInputs.remove(player. getUniqueId());
            if (removed != null) {
                player.sendMessage(plugin.getMessageUtil().getMessage("chat.input-timeout"));
            }
        }, timeoutSeconds * 20L);
    }

    public void cancelInput(Player player) {
        ChatInputHandler removed = pendingInputs.remove(player. getUniqueId());
        if (removed != null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("chat.input-cancelled"));
        }
    }

    public boolean hasPendingInput(Player player) {
        return pendingInputs.containsKey(player. getUniqueId());
    }

    // ==================== 파티 이름 변경 입력 ====================
    public void promptPartyNameChange(Player player, Party party) {
        waitForInput(player, (p, input) -> {
            // 취소 확인
            if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("취소")) {
                p.sendMessage(plugin.getMessageUtil().getMessage("settings.name-change-cancelled"));
                return;
            }

            // 이름 유효성 검사
            if (input.length() > 32) {
                p.sendMessage(plugin.getMessageUtil().getMessage("party.name-too-long"));
                return;
            }

            if (input.length() < 2) {
                p. sendMessage(plugin. getMessageUtil().getMessage("party.name-too-short"));
                return;
            }

            // 중복 확인
            if (plugin.getPartyManager().isPartyNameTaken(input)) {
                p. sendMessage(plugin. getMessageUtil().getMessage("party.name-taken"));
                return;
            }

            // 이름 변경
            String oldName = party.getPartyName();
            party.setPartyName(input);
            plugin.getDataManager().saveParty(party);

            p.sendMessage(plugin.getMessageUtil().getMessage("settings.name-changed",
                    "%name%", input));

            // 파티원들에게 알림
            plugin.getPartyChatManager().sendNotification(party,
                    plugin.getMessageUtil().getMessage("settings.name-changed-notify",
                            "%player%", p.getName(),
                            "%old%", oldName != null ? oldName :  "없음",
                            "%new%", input));

        }, plugin.getMessageUtil().getMessage("settings.enter-party-name"));
    }

    // ==================== 모집 공고 작성 입력 ====================
    public void promptListingDescription(Player player, Party party) {
        waitForInput(player, (p, input) -> {
            if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("취소")) {
                p.sendMessage(plugin.getMessageUtil().getMessage("finder.listing-cancelled"));
                return;
            }

            // 길이 제한
            if (input. length() > 200) {
                input = input.substring(0, 200);
            }

            // 모집 공고 생성/업데이트
            plugin.getPartyFinder().createOrUpdateListing(party, input);
            
            p.sendMessage(plugin.getMessageUtil().getMessage("finder.listing-created"));

        }, plugin.getMessageUtil().getMessage("finder.enter-description"), 120);
    }

    // ==================== 채팅 모드 열거형 ====================
    public enum ChatMode {
        NORMAL,
        PARTY
    }

    // ==================== 채팅 입력 핸들러 인터페이스 ====================
    @FunctionalInterface
    public interface ChatInputHandler {
        void handle(Player player, String input);
    }
}