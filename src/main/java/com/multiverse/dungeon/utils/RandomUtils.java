package com.multiverse.dungeon.utils;

import java.util.List;
import java.util.Random;

/**
 * 랜덤 유틸리티
 */
public class RandomUtils {

    private static final Random random = new Random();

    /**
     * 0 ~ max 사이의 랜덤 정수 (max 포함)
     *
     * @param max 최대값
     * @return 랜덤 정수
     */
    public static int nextInt(int max) {
        return random. nextInt(max + 1);
    }

    /**
     * min ~ max 사이의 랜덤 정수 (둘 다 포함)
     *
     * @param min 최소값
     * @param max 최대값
     * @return 랜덤 정수
     */
    public static int nextIntRange(int min, int max) {
        if (min > max) {
            return nextIntRange(max, min);
        }
        return min + random.nextInt(max - min + 1);
    }

    /**
     * 0. 0 ~ 1.0 사이의 랜덤 실수
     *
     * @return 랜덤 실수
     */
    public static double nextDouble() {
        return random.nextDouble();
    }

    /**
     * min ~ max 사이의 랜덤 실수 (둘 다 포함)
     *
     * @param min 최소값
     * @param max 최대값
     * @return 랜덤 실수
     */
    public static double nextDoubleRange(double min, double max) {
        if (min > max) {
            return nextDoubleRange(max, min);
        }
        return min + (random.nextDouble() * (max - min));
    }

    /**
     * 불린 값 랜덤 생성
     *
     * @return 랜덤 불린
     */
    public static boolean nextBoolean() {
        return random.nextBoolean();
    }

    /**
     * 확률에 따른 불린 값 생성
     *
     * @param probability 확률 (0.0 ~ 1.0)
     * @return 확률에 따른 불린
     */
    public static boolean chance(double probability) {
        if (probability <= 0.0) {
            return false;
        }
        if (probability >= 1.0) {
            return true;
        }
        return random.nextDouble() < probability;
    }

    /**
     * 백분율 확률에 따른 불린 값 생성
     *
     * @param percentage 확률 (0 ~ 100)
     * @return 확률에 따른 불린
     */
    public static boolean chancePercent(double percentage) {
        return chance(percentage / 100.0);
    }

    /**
     * 리스트에서 랜덤 요소 선택
     *
     * @param list 선택할 리스트
     * @return 랜덤 요소, 리스트가 비어있으면 null
     */
    public static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(random.nextInt(list.size()));
    }

    /**
     * 배열에서 랜덤 요소 선택
     *
     * @param array 선택할 배열
     * @return 랜덤 요소, 배열이 비어있으면 null
     */
    public static <T> T getRandomElement(T[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        return array[random.nextInt(array. length)];
    }

    /**
     * 가중치가 있는 랜덤 선택
     *
     * @param weights 가중치 배열
     * @return 선택된 인덱스
     */
    public static int getWeightedRandom(double[] weights) {
        if (weights == null || weights.length == 0) {
            return -1;
        }

        double totalWeight = 0;
        for (double weight : weights) {
            totalWeight += weight;
        }

        double random = RandomUtils.random.nextDouble() * totalWeight;
        double current = 0;

        for (int i = 0; i < weights.length; i++) {
            current += weights[i];
            if (random < current) {
                return i;
            }
        }

        return weights.length - 1;
    }

    /**
     * UUID 문자열의 첫 8자 가져오기
     *
     * @return 랜덤 문자열 (8자)
     */
    public static String randomString8() {
        return java.util.UUID.randomUUID().  toString().substring(0, 8);
    }

    /**
     * 랜덤 색상 코드 생성
     *
     * @return 색상 코드 (예: #FF0000)
     */
    public static String randomColor() {
        return String.format("#%06X", random.nextInt(0xFFFFFF + 1));
    }

    /**
     * 셔플 (Fisher-Yates)
     *
     * @param list 셔플할 리스트
     */
    public static <T> void shuffle(List<T> list) {
        if (list == null || list.size() <= 1) {
            return;
        }

        for (int i = list.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            T temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }

    /**
     * 정규분포 랜덤 (가우스)
     *
     * @param mean 평균
     * @param stdDev 표준편차
     * @return 정규분포 랜덤 값
     */
    public static double nextGaussian(double mean, double stdDev) {
        return mean + (random.nextGaussian() * stdDev);
    }

    /**
     * 특정 범위의 정규분포 랜덤
     *
     * @param mean 평균
     * @param stdDev 표준편차
     * @param min 최소값
     * @param max 최대값
     * @return 범위 내의 정규분포 랜덤 값
     */
    public static double nextGaussianRange(double mean, double stdDev, double min, double max) {
        double value = nextGaussian(mean, stdDev);
        return Math.max(min, Math.min(max, value));
    }

    /**
     * 지수분포 랜덤
     *
     * @param lambda 람다
     * @return 지수분포 랜덤 값
     */
    public static double nextExponential(double lambda) {
        return -Math.log(1. 0 - random.nextDouble()) / lambda;
    }
}