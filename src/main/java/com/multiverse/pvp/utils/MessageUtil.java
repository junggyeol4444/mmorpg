package com.multiverse. pvp.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java. util.List;
import java.util. regex. Matcher;
import java.util.regex. Pattern;

public class MessageUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    /**
     * 색상 코드 변환
     */
    public static String colorize(String message) {
        if (message == null) {
            return "";
        }

        // HEX 색상 지원 (&#RRGGBB)
        Matcher matcher = HEX_PATTERN. matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher. find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement. append("§").append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }
        matcher.appendTail(buffer);

        // 기본 색상 코드 변환
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    /**
     * 색상 코드 제거
     */
    public static String stripColor(String message) {
        if (message == null) {
            return "";
        }
        return ChatColor.stripColor(colorize(message));
    }

    /**
     * 플레이어에게 메시지 전송
     */
    public static void sendMessage(Player player, String message) {
        if (player == null || message == null) {
            return;
        }
        player.sendMessage(colorize(message));
    }

    /**
     * 플레이어에게 메시지 전송 (프리픽스 포함)
     */
    public static void sendMessage(Player player, String prefix, String message) {
        if (player == null || message == null) {
            return;
        }
        player.sendMessage(colorize(prefix + message));
    }

    /**
     * 여러 줄 메시지 전송
     */
    public static void sendMessages(Player player, List<String> messages) {
        if (player == null || messages == null) {
            return;
        }
        for (String message :  messages) {
            sendMessage(player, message);
        }
    }

    /**
     * 여러 줄 메시지 전송 (배열)
     */
    public static void sendMessages(Player player, String...  messages) {
        if (player == null || messages == null) {
            return;
        }
        for (String message :  messages) {
            sendMessage(player, message);
        }
    }

    /**
     * 액션바 메시지 전송
     */
    public static void sendActionBar(Player player, String message) {
        if (player == null || message == null) {
            return;
        }
        player.spigot().sendMessage(
                net.md_5.bungee.api. ChatMessageType.ACTION_BAR,
                net.md_5.bungee.api.chat.TextComponent.fromLegacyText(colorize(message))[0]
        );
    }

    /**
     * 타이틀 전송
     */
    public static void sendTitle(Player player, String title, String subtitle) {
        sendTitle(player, title, subtitle, 10, 70, 20);
    }

    /**
     * 타이틀 전송 (시간 지정)
     */
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player == null) {
            return;
        }
        player.sendTitle(
                title != null ? colorize(title) : "",
                subtitle != null ?  colorize(subtitle) : "",
                fadeIn, stay, fadeOut
        );
    }

    /**
     * 문자열 리스트 색상 변환
     */
    public static List<String> colorize(List<String> messages) {
        if (messages == null) {
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();
        for (String message : messages) {
            result.add(colorize(message));
        }
        return result;
    }

    /**
     * 중앙 정렬 메시지
     */
    public static String centerMessage(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int CENTER_PX = 154;
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo. SPACE. getLength() + 1;
        int compensated = 0;

        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return sb.toString() + message;
    }

    /**
     * 진행률 바 생성
     */
    public static String createProgressBar(double progress, int length, String filledColor, String emptyColor, char filledChar, char emptyChar) {
        int filled = (int) (progress * length);

        StringBuilder sb = new StringBuilder();
        sb.append(filledColor);
        for (int i = 0; i < filled; i++) {
            sb. append(filledChar);
        }
        sb.append(emptyColor);
        for (int i = filled; i < length; i++) {
            sb.append(emptyChar);
        }

        return sb.toString();
    }

    /**
     * 기본 진행률 바
     */
    public static String createProgressBar(double progress) {
        return createProgressBar(progress, 10, "&a", "&7", '█', '█');
    }

    /**
     * 숫자 포맷팅
     */
    public static String formatNumber(long number) {
        if (number >= 1000000000) {
            return String.format("%.1fB", number / 1000000000.0);
        } else if (number >= 1000000) {
            return String. format("%.1fM", number / 1000000.0);
        } else if (number >= 1000) {
            return String.format("%.1fK", number / 1000.0);
        } else {
            return String.valueOf(number);
        }
    }

    /**
     * 시간 포맷팅 (초 -> 읽기 쉬운 형식)
     */
    public static String formatTime(long seconds) {
        if (seconds < 60) {
            return seconds + "초";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            long secs = seconds % 60;
            return minutes + "분 " + secs + "초";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            return hours + "시간 " + minutes + "분";
        } else {
            long days = seconds / 86400;
            long hours = (seconds % 86400) / 3600;
            return days + "일 " + hours + "시간";
        }
    }

    /**
     * 폰트 정보 (중앙 정렬용)
     */
    public enum DefaultFontInfo {
        A('A', 5), B('B', 5), C('C', 5), D('D', 5), E('E', 5),
        F('F', 5), G('G', 5), H('H', 5), I('I', 3), J('J', 5),
        K('K', 5), L('L', 5), M('M', 5), N('N', 5), O('O', 5),
        P('P', 5), Q('Q', 5), R('R', 5), S('S', 5), T('T', 5),
        U('U', 5), V('V', 5), W('W', 5), X('X', 5), Y('Y', 5),
        Z('Z', 5),
        a('a', 5), b('b', 5), c('c', 5), d('d', 5), e('e', 5),
        f('f', 4), g('g', 5), h('h', 5), i('i', 1), j('j', 5),
        k('k', 4), l('l', 1), m('m', 5), n('n', 5), o('o', 5),
        p('p', 5), q('q', 5), r('r', 5), s('s', 5), t('t', 4),
        u('u', 5), v('v', 5), w('w', 5), x('x', 5), y('y', 5),
        z('z', 5),
        NUM_1('1', 5), NUM_2('2', 5), NUM_3('3', 5), NUM_4('4', 5),
        NUM_5('5', 5), NUM_6('6', 5), NUM_7('7', 5), NUM_8('8', 5),
        NUM_9('9', 5), NUM_0('0', 5),
        EXCLAMATION_POINT('!', 1), AT_SYMBOL('@', 6), HASH('#', 5),
        DOLLAR_SIGN('$', 5), PERCENT('%', 5), CARET('^', 5),
        AMPERSAND('&', 5), ASTERISK('*', 5), LEFT_PARENTHESIS('(', 4),
        RIGHT_PARENTHESIS(')', 4), MINUS('-', 5), UNDERSCORE('_', 5),
        PLUS_SIGN('+', 5), EQUALS_SIGN('=', 5), LEFT_BRACKET('[', 3),
        RIGHT_BRACKET(']', 3), LEFT_BRACE('{', 4), RIGHT_BRACE('}', 4),
        COLON(':', 1), SEMICOLON(';', 1), DOUBLE_QUOTE('"', 3),
        SINGLE_QUOTE('\'', 1), LESS_THAN('<', 4), GREATER_THAN('>', 4),
        QUESTION_MARK('? ', 5), SLASH('/', 5), BACKSLASH('\\', 5),
        PIPE('|', 1), TILDE('~', 5), TICK('`', 2), PERIOD('.', 1),
        COMMA(',', 1), SPACE(' ', 3),
        DEFAULT('a', 4);

        private final char character;
        private final int length;

        DefaultFontInfo(char character, int length) {
            this.character = character;
            this.length = length;
        }

        public char getCharacter() {
            return character;
        }

        public int getLength() {
            return length;
        }

        public int getBoldLength() {
            return length + (this == SPACE ? 0 : 1);
        }

        public static DefaultFontInfo getDefaultFontInfo(char c) {
            for (DefaultFontInfo dFI : values()) {
                if (dFI. character == c) {
                    return dFI;
                }
            }
            return DEFAULT;
        }
    }
}