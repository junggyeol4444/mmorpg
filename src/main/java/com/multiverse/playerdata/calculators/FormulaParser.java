package com.multiverse.playerdata.calculators;

import com.multiverse.playerdata.models.PlayerStats;
import com.multiverse.playerdata.models.enums.StatType;

import java.util.Map;
import java.util.Stack;

/**
 * 수식 기반 계산 파서 (ex: "STR * 1.5 + INT * 2 + 50")
 * 기본 스탯에 따라 다이나믹 계산 가능
 */
public class FormulaParser {

    /**
     * 공식에 따라 계산 값을 반환
     */
    public static double parse(String formula, PlayerStats stats) {
        // 가장 단순한 구현: 변수 치환 후 eval
        String parsed = formula;
        for (Map.Entry<StatType, Integer> entry : stats.getBaseStats().entrySet()) {
            parsed = parsed.replaceAll("\\b" + entry.getKey().name() + "\\b",
                    String.valueOf(entry.getValue()));
        }
        // 숫자와 연산자만 남은 식 → 계산
        return evalSimple(parsed);
    }

    // 매우 단순화된 사칙연산 파서 (괄호 미지원)
    private static double evalSimple(String str) {
        try {
            return new Object() {
                int pos = -1, ch;

                void nextChar() { ch = (++pos < str.length()) ? str.charAt(pos) : -1; }

                double parse() {
                    nextChar();
                    double x = parseExpression();
                    if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                    return x;
                }

                // Grammar: expression = term | expression `+` term | expression `-` term
                double parseExpression() {
                    double x = parseTerm();
                    for (;;) {
                        if      (ch == '+') { nextChar(); x += parseTerm(); }
                        else if (ch == '-') { nextChar(); x -= parseTerm(); }
                        else return x;
                    }
                }

                // Grammar: term = factor | term `*` factor | term `/` factor
                double parseTerm() {
                    double x = parseFactor();
                    for (;;) {
                        if      (ch == '*') { nextChar(); x *= parseFactor(); }
                        else if (ch == '/') { nextChar(); x /= parseFactor(); }
                        else return x;
                    }
                }

                // Grammar: factor = `+` factor | `-` factor | number
                double parseFactor() {
                    if (ch == '+') { nextChar(); return parseFactor(); }
                    if (ch == '-') { nextChar(); return -parseFactor(); }

                    double x;
                    int startPos = this.pos;
                    if ((ch >= '0' && ch <= '9') || ch == '.') {
                        while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                        x = Double.parseDouble(str.substring(startPos, this.pos));
                        return x;
                    }
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }
            }.parse();
        } catch (Exception e) {
            return 0;
        }
    }
}