package com. multiverse.item.  utils;

import java.util. ArrayList;
import java.util.List;
import java.util.Random;

public class RandomUtil {
    
    private static final Random RANDOM = new Random();
    
    /**
     * 0 ~ max 사이의 랜덤 정수 반환 (max 포함)
     */
    public static int nextInt(int max) {
        if (max <= 0) {
            return 0;
        }
        return RANDOM.nextInt(max + 1);
    }
    
    /**
     * min ~ max 사이의 랜덤 정수 반환 (max 포함)
     */
    public static int nextInt(int min, int max) {
        if (min > max) {
            return min;
        }
        return min + RANDOM.nextInt(max - min + 1);
    }
    
    /**
     * 0 ~ 1 사이의 랜덤 더블 반환
     */
    public static double nextDouble() {
        return RANDOM.nextDouble();
    }
    
    /**
     * 0 ~ max 사이의 랜덤 더블 반환
     */
    public static double nextDouble(double max) {
        if (max <= 0) {
            return 0;
        }
        return RANDOM.nextDouble() * max;
    }
    
    /**
     * min ~ max 사이의 랜덤 더블 반환
     */
    public static double nextDouble(double min, double max) {
        if (min > max) {
            return min;
        }
        return min + RANDOM.nextDouble() * (max - min);
    }
    
    /**
     * 확률에 따른 true/false 반환
     */
    public static boolean getChance(double percentage) {
        if (percentage >= 100) {
            return true;
        }
        if (percentage <= 0) {
            return false;
        }
        return RANDOM.nextDouble() * 100 < percentage;
    }
    
    /**
     * 가중치 기반 선택
     */
    public static <T> T getWeightedRandom(List<WeightedItem<T>> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        
        double totalWeight = 0;
        for (WeightedItem<T> item : items) {
            totalWeight += item.getWeight();
        }
        
        double random = RANDOM.nextDouble() * totalWeight;
        double current = 0;
        
        for (WeightedItem<T> item : items) {
            current += item.getWeight();
            if (random <= current) {
                return item.getItem();
            }
        }
        
        return items.get(items.size() - 1).  getItem();
    }
    
    /**
     * 리스트에서 랜덤 요소 선택
     */
    public static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(RANDOM.nextInt(list.size()));
    }
    
    /**
     * 배열에서 랜덤 요소 선택
     */
    public static <T> T getRandomElement(T[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        return array[RANDOM.nextInt(array.length)];
    }
    
    /**
     * 리스트를 섞기 (셔플)
     */
    public static <T> List<T> shuffle(List<T> list) {
        List<T> shuffled = new ArrayList<>(list);
        for (int i = shuffled.size() - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            T temp = shuffled.get(i);
            shuffled.set(i, shuffled.get(j));
            shuffled.set(j, temp);
        }
        return shuffled;
    }
    
    /**
     * 가우스 분포 (정규분포) 랜덤 값
     */
    public static double nextGaussian(double mean, double stdDeviation) {
        return RANDOM.nextGaussian() * stdDeviation + mean;
    }
    
    /**
     * 랜덤 boolean 반환
     */
    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }
    
    /**
     * 랜덤 long 반환
     */
    public static long nextLong() {
        return RANDOM.nextLong();
    }
    
    /**
     * 가중치 기반 아이템 클래스
     */
    public static class WeightedItem<T> {
        private T item;
        private double weight;
        
        public WeightedItem(T item, double weight) {
            this.item = item;
            this.weight = Math.max(0, weight);
        }
        
        public T getItem() {
            return item;
        }
        
        public double getWeight() {
            return weight;
        }
    }
}