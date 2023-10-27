package com.test.yolo.services;

import com.test.yolo.utils.NumberGenerator;
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
            // Special rule: When the player chooses MAX_NUMBER (100), they win based on a 50% chance.
            // This is represented by checking if the generated number is even. If it is even,
            // the win is calculated by multiplying the bet by the WIN_MULTIPLIER (99).
            // If the generated number is odd, the player wins nothing.
            // However, this interpretation of the business logic may be clarified with business analysts in case of real task.
            if (number == MAX_NUMBER) {
                return generatedNumber % 2 == 0 ? getCalculatedWinIfNumberIs100(bet) : BigDecimal.ZERO;
            }
            return getCalculatedWin(bet, number);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal getCalculatedWinIfNumberIs100(BigDecimal bet) {
        return bet.multiply(BigDecimal.valueOf(WIN_MULTIPLIER)).setScale(RESULT_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal getCalculatedWin(BigDecimal bet, int number) {
        BigDecimal divisor = BigDecimal.valueOf(MAX_NUMBER - number);
        BigDecimal multiplier = BigDecimal.valueOf(WIN_MULTIPLIER)
                .divide(divisor, SCALE_FOR_DIVISION, RoundingMode.HALF_UP);
        return bet.multiply(multiplier).setScale(RESULT_SCALE, RoundingMode.HALF_UP);
    }
}


