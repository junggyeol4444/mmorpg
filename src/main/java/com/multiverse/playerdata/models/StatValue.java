package com.multiverse.playerdata.models;

import com.multiverse.playerdata.models.enums.StatType;

/**
 * 단일 스탯의 값과 증감 등 변동 처리용 클래스
 */
public class StatValue {

    private final StatType type;
    private int value;
    private int delta; // 증감치, 예: 버프/디버프 등

    public StatValue(StatType type, int value) {
        this(type, value, 0);
    }

    public StatValue(StatType type, int value, int delta) {
        this.type = type;
        this.value = value;
        this.delta = delta;
    }

    public StatType getType() {
        return type;
    }

    public int getValue() {
        return value + delta;
    }

    public int getRawValue() {
        return value;
    }

    public int getDelta() {
        return delta;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    public void add(int amount) {
        this.value += amount;
    }

    public void addDelta(int amount) {
        this.delta += amount;
    }
}