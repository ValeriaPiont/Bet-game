package com.test.yolo.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CompletableFuture;

@Service
public class GameService {

    private static final int MAX_NUMBER = 100;
    private static final double WIN_MULTIPLIER = 99.0;
    private static final int SCALE_FOR_DIVISION = 4;
    private static final int RESULT_SCALE = 2;

    @Async
    public CompletableFuture<BigDecimal> calculateWinAsync(BigDecimal bet, int number) {
        return CompletableFuture.completedFuture(calculateWin(bet, number));
    }

    public BigDecimal calculateWin(BigDecimal bet, int number) {
        int generatedNumber = NumberGenerator.generateRandomNumber(MAX_NUMBER);
        if (number > generatedNumber) {
            // The purpose is to calculate the win based on the chosen number,
            // then when number is 100, the player is essentially choosing a win.
            // So, win should be user's bet times the multiplier, which is 99.
            // However, this interpretation of the business logic may be clarified with business analysts in case of real task.
            if (number == MAX_NUMBER) {
                return getMaxWin(bet);
            }
            return getCalculatedWin(bet, number);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal getMaxWin(BigDecimal bet) {
        return bet.multiply(BigDecimal.valueOf(WIN_MULTIPLIER)).setScale(RESULT_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal getCalculatedWin(BigDecimal bet, int number) {
        BigDecimal divisor = BigDecimal.valueOf(MAX_NUMBER - number);
        BigDecimal multiplier = BigDecimal.valueOf(WIN_MULTIPLIER)
                .divide(divisor, SCALE_FOR_DIVISION, RoundingMode.HALF_UP);

        return bet.multiply(multiplier).setScale(RESULT_SCALE, RoundingMode.HALF_UP);
    }
}


