package com.multiverse.quest.models;

import com.multiverse.quest.models. enums.NotificationType;
import java.util.*;

/**
 * 퀘스트 알림 데이터 모델
 * 플레이어에게 표시할 퀘스트 알림을 정의합니다.
 */
public class QuestNotification {
    private String notificationId;                   // 고유 ID
    private NotificationType type;                   // 알림 타입
    private String questId;                          // 연관된 퀘스트 ID
    private String message;                          // 알림 메시지
    private String subtitle;                         // 서브 메시지
    
    private boolean showTitle;                       // 타이틀바에 표시
    private boolean showActionBar;                   // 액션바에 표시
    private boolean showChat;                        // 채팅에 표시
    private boolean playSound;                       // 소리 재생
    private boolean showParticles;                   // 파티클 표시
    
    private String soundType;                        // 소리 타입
    private float soundVolume;                       // 소리 볼륨 (0.0~1.0)
    private float soundPitch;                        // 소리 피치 (0.5~2.0)
    
    private int duration;                            // 표시 지속 시간 (틱)
    private int[] fadeTimes;                         // [fadeIn, stay, fadeOut] (틱)
    
    private long createdTime;                        // 생성 시간
    private long expireTime;                         // 만료 시간 (0이면 무제한)
    
    private Map<String, Object> metadata;            // 추가 데이터
    private boolean sent;                            // 전송 여부

    /**
     * 기본 생성자
     */
    public QuestNotification() {
        this.metadata = new HashMap<>();
        this. showTitle = true;
        this. showActionBar = true;
        this.showChat = true;
        this.playSound = true;
        this.showParticles = false;
        this.soundVolume = 1.0f;
        this.soundPitch = 1.0f;
        this.duration = 80; // 4초
        this.fadeTimes = new int[]{10, 80, 10};
        this.createdTime = System.currentTimeMillis();
        this.expireTime = 0;
        this.sent = false;
    }

    /**
     * 전체 파라미터 생성자
     */
    public QuestNotification(String notificationId, NotificationType type, String questId, String message) {
        this();
        this.notificationId = notificationId;
        this. type = type;
        this. questId = questId;
        this.message = message;
    }

    // ============ Getters ============

    public String getNotificationId() {
        return notificationId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getQuestId() {
        return questId;
    }

    public String getMessage() {
        return message;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public boolean isShowActionBar() {
        return showActionBar;
    }

    public boolean isShowChat() {
        return showChat;
    }

    public boolean isPlaySound() {
        return playSound;
    }

    public boolean isShowParticles() {
        return showParticles;
    }

    public String getSoundType() {
        return soundType;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public float getSoundPitch() {
        return soundPitch;
    }

    public int getDuration() {
        return duration;
    }

    public int[] getFadeTimes() {
        return fadeTimes != null ? fadeTimes. clone() : null;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }

    public boolean isSent() {
        return sent;
    }

    // ============ Setters ============

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public void setType(NotificationType type) {
        if (type != null) {
            this.type = type;
        }
    }

    public void setQuestId(String questId) {
        this.questId = questId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public void setShowActionBar(boolean showActionBar) {
        this.showActionBar = showActionBar;
    }

    public void setShowChat(boolean showChat) {
        this.showChat = showChat;
    }

    public void setPlaySound(boolean playSound) {
        this.playSound = playSound;
    }

    public void setShowParticles(boolean showParticles) {
        this.showParticles = showParticles;
    }

    public void setSoundType(String soundType) {
        this.soundType = soundType;
    }

    public void setSoundVolume(float soundVolume) {
        this.soundVolume = Math.max(0.0f, Math.min(soundVolume, 1.0f));
    }

    public void setSoundPitch(float soundPitch) {
        this.soundPitch = Math.max(0.5f, Math.min(soundPitch, 2.0f));
    }

    public void setDuration(int duration) {
        this.duration = Math.max(duration, 1);
    }

    public void setFadeTimes(int fadeIn, int stay, int fadeOut) {
        this.fadeTimes = new int[]{
            Math.max(fadeIn, 0),
            Math.max(stay, 1),
            Math.max(fadeOut, 0)
        };
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = Math.max(expireTime, 0);
    }

    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    // ============ Display Configuration ============

    /**
     * 타이틀바 표시 설정
     */
    public void applyTitleConfig() {
        showTitle = true;
        showActionBar = false;
        showChat = false;
    }

    /**
     * 액션바 표시 설정
     */
    public void applyActionBarConfig() {
        showTitle = false;
        showActionBar = true;
        showChat = false;
    }

    /**
     * 채팅 표시 설정
     */
    public void applyChatConfig() {
        showTitle = false;
        showActionBar = false;
        showChat = true;
    }

    /**
     * 모든 표시 활성화
     */
    public void enableAllDisplay() {
        showTitle = true;
        showActionBar = true;
        showChat = true;
    }

    /**
     * 모든 표시 비활성화
     */
    public void disableAllDisplay() {
        showTitle = false;
        showActionBar = false;
        showChat = false;
    }

    /**
     * 사운드 설정
     */
    public void applySoundConfig(String soundType, float volume, float pitch) {
        this.soundType = soundType;
        setSoundVolume(volume);
        setSoundPitch(pitch);
    }

    /**
     * 기본 사운드 설정 적용
     */
    public void applyDefaultSound() {
        if (type != null) {
            this.soundType = type.getDefaultSoundType();
            this.soundVolume = 1.0f;
            this.soundPitch = 1. 0f;
        }
    }

    /**
     * 유형별 기본 설정 적용
     */
    public void applyTypeDefaults() {
        if (type != null) {
            showTitle = type.shouldShowTitle();
            showActionBar = type.shouldShowActionBar();
            showChat = type.shouldShowChat();
            playSound = type.shouldPlaySound();
            soundType = type.getDefaultSoundType();
            duration = type.getDefaultDuration();
            fadeTimes = type.getDefaultFadeTimes();
        }
    }

    // ============ Business Logic ============

    /**
     * 알림이 유효한지 확인
     */
    public boolean isValid() {
        return notificationId != null && ! notificationId.isEmpty() &&
               type != null &&
               message != null && !message.isEmpty() &&
               (showTitle || showActionBar || showChat);
    }

    /**
     * 알림이 만료되었는지 확인
     */
    public boolean isExpired() {
        if (expireTime == 0) return false;
        return System.currentTimeMillis() >= expireTime;
    }

    /**
     * 알림이 표시되어야 하는지 확인
     */
    public boolean shouldDisplay() {
        return isValid() && !isExpired();
    }

    /**
     * 알림이 어떤 방식으로 표시되는지 확인
     */
    public int getDisplayMethodCount() {
        int count = 0;
        if (showTitle) count++;
        if (showActionBar) count++;
        if (showChat) count++;
        return count;
    }

    /**
     * 사운드가 재생되어야 하는지 확인
     */
    public boolean shouldPlaySound() {
        return playSound && soundType != null && ! soundType.isEmpty();
    }

    /**
     * 우선순위 반환
     */
    public int getPriority() {
        return type != null ? type.getPriority() : 5;
    }

    /**
     * 긴급 알림인지 확인
     */
    public boolean isUrgent() {
        return type != null && type.isUrgent();
    }

    /**
     * 긍정적 알림인지 확인
     */
    public boolean isPositive() {
        return type != null && type.isPositive();
    }

    /**
     * 알림 색상 코드 반환
     */
    public String getColorCode() {
        if (type != null) {
            return type.getColorCode();
        }
        return "§f";
    }

    /**
     * 포맷된 메시지 반환
     */
    public String getFormattedMessage() {
        return getColorCode() + message;
    }

    /**
     * 알림 요약 반환
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb. append(getFormattedMessage()). append("§r\n");
        
        if (subtitle != null && !subtitle.isEmpty()) {
            sb.append("§7"). append(subtitle).append("\n");
        }
        
        sb.append("§7타입: ").append(type != null ? type.getFormattedName() : "알 수 없음"). append("\n");
        
        if (questId != null && !questId. isEmpty()) {
            sb.append("§7퀘스트: §f").append(questId).append("\n");
        }
        
        return sb.toString();
    }

    /**
     * 상세 정보 반환
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb. append("§6=== 알림 상세 정보 ===§r\n");
        sb.append("§7ID: §f").append(notificationId). append("\n");
        sb. append("§7타입: ").append(type != null ? type.getFormattedName() : "알 수 없음"). append("\n");
        sb. append("§7메시지: §f").append(message).append("\n");
        
        if (subtitle != null && !subtitle.isEmpty()) {
            sb.append("§7서브: §f").append(subtitle).append("\n");
        }
        
        if (questId != null && !questId.isEmpty()) {
            sb.append("§7퀨스트: §f").append(questId).append("\n");
        }
        
        sb.append("\n§e=== 표시 설정 ===§r\n");
        sb.append("§7타이틀: ").append(showTitle ?  "§a표시" : "§c숨김").append("\n");
        sb.append("§7액션바: ").append(showActionBar ? "§a표시" : "§c숨김").append("\n");
        sb.append("§7채팅: ").append(showChat ? "§a표시" : "§c숨김").append("\n");
        
        sb.append("\n§e=== 사운드 설정 ===§r\n");
        sb.append("§7재생: ").append(playSound ? "§a재생" : "§c미재생").append("\n");
        
        if (playSound && soundType != null) {
            sb.append("§7타입: §f").append(soundType).append("\n");
            sb.append("§7볼륨: §f").append(String.format("%.1f", soundVolume)).append("\n");
            sb.append("§7피치: §f"). append(String.format("%.1f", soundPitch)).append("\n");
        }
        
        sb.append("\n§e=== 표시 시간 ===§r\n");
        sb.append("§7지속 시간: §f").append(duration).append(" 틱\n");
        sb.append("§7페이드 인: §f").append(fadeTimes[0]).append(" 틱\n");
        sb.append("§7유지: §f").append(fadeTimes[1]).append(" 틱\n");
        sb.append("§7페이드 아웃: §f").append(fadeTimes[2]).append(" 틱\n");
        
        return sb.toString();
    }

    /**
     * 비교 메서드 (우선순위 순)
     */
    public int comparePriority(QuestNotification other) {
        if (other == null) return 1;
        return Integer.compare(other.getPriority(), this.getPriority()); // 큰 우선순위가 먼저
    }

    /**
     * 복사본 생성
     */
    public QuestNotification copy() {
        QuestNotification copy = new QuestNotification();
        copy.notificationId = this.notificationId;
        copy.type = this.type;
        copy.questId = this.questId;
        copy.message = this.message;
        copy.subtitle = this.subtitle;
        copy.showTitle = this.showTitle;
        copy.showActionBar = this.showActionBar;
        copy.showChat = this.showChat;
        copy.playSound = this. playSound;
        copy.showParticles = this.showParticles;
        copy.soundType = this.soundType;
        copy.soundVolume = this.soundVolume;
        copy.soundPitch = this.soundPitch;
        copy.duration = this.duration;
        copy.fadeTimes = this.fadeTimes != null ? this.fadeTimes. clone() : null;
        copy.createdTime = this.createdTime;
        copy.expireTime = this.expireTime;
        copy.metadata = new HashMap<>(this.metadata);
        copy.sent = this.sent;
        return copy;
    }

    /**
     * 메타데이터 조회
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    /**
     * 메타데이터 조회 (기본값)
     */
    public Object getMetadata(String key, Object defaultValue) {
        return metadata. getOrDefault(key, defaultValue);
    }

    /**
     * 문자열 표현
     */
    @Override
    public String toString() {
        return String.format("QuestNotification{id=%s, type=%s, quest=%s, sent=%s}",
                notificationId, type, questId, sent);
    }

    /**
     * 비교 메서드
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        QuestNotification that = (QuestNotification) o;
        return notificationId != null && notificationId.equals(that.notificationId);
    }

    /**
     * 해시 코드
     */
    @Override
    public int hashCode() {
        return notificationId != null ? notificationId. hashCode() : 0;
    }
}