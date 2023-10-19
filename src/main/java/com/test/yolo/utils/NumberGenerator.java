package com.test.yolo.utils;

public final class NumberGenerator {

    private static final int LOWER_BOUND = 1;

    private NumberGenerator() {}

    public static int generateRandomNumber(int upperBound) {
        return (int) (Math.random() * (upperBound - LOWER_BOUND + 1)) + LOWER_BOUND;
    }
}