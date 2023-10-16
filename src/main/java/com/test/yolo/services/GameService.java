package com.test.yolo.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class GameService {

    private static final int MAX_NUMBER = 100;
    private static final double WIN_MULTIPLIER = 99.0;
    private static final int SCALE_FOR_DIVISION = 4;
    private static final int RESULT_SCALE = 2;

    public BigDecimal calculateWin(BigDecimal bet, int number) {
        int generatedNumber = NumberGenerator.generateRandomNumber(MAX_NUMBER);
        if (number > generatedNumber) {
            BigDecimal multiplier = BigDecimal.valueOf(WIN_MULTIPLIER)
                    .divide(BigDecimal.valueOf(MAX_NUMBER - number), SCALE_FOR_DIVISION, RoundingMode.HALF_UP);
            return bet.multiply(multiplier).setScale(RESULT_SCALE, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}


