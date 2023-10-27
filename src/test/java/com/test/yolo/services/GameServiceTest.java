package com.test.yolo.services;
import com.test.yolo.utils.NumberGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
public class GameServiceTest {

    @InjectMocks
    private GameService gameService;

    private MockedStatic<NumberGenerator> mockedNumberGenerator;

    private final int UPPER_BOUND = 100;

    @BeforeEach
    public void setUp() {
        mockedNumberGenerator = Mockito.mockStatic(NumberGenerator.class);
    }

    @AfterEach
    public void tearDown() {
        mockedNumberGenerator.close();
    }

    @Test
    public void testWinCalculationWithGreaterNumber() {
        mockNumberGenerator(50);
        BigDecimal win = gameService.calculateWin(BigDecimal.valueOf(40.5), 60);
        Assertions.assertEquals(100.24, win.doubleValue(), 0.001);
    }

    @Test
    public void testNoWinCalculationWithLowerNumber() {
        mockNumberGenerator(70);
        BigDecimal win = gameService.calculateWin(BigDecimal.valueOf(40.5), 60);
        Assertions.assertEquals(0.0, win.doubleValue(), 0.001);
    }

    @Test
    public void testNoWinCalculationWithEqualNumber() {
        mockNumberGenerator(60);
        BigDecimal win = gameService.calculateWin(BigDecimal.valueOf(40.5), 60);
        Assertions.assertEquals(0.0, win.doubleValue(), 0.001);
    }

    @Test
    public void testWinCalculationWithMaximumNumberAndOddGeneratedNumber() {
        mockNumberGenerator(60);
        BigDecimal win = gameService.calculateWin(BigDecimal.valueOf(5.5), 100);
        Assertions.assertEquals(544.5, win.doubleValue(), 0.001);
    }

    @Test
    public void testNoWinCalculationWithMaximumNumberAndEvenGeneratedNumber() {
        mockNumberGenerator(61);
        BigDecimal win = gameService.calculateWin(BigDecimal.valueOf(40.5), 100);
        Assertions.assertEquals(0.0, win.doubleValue(), 0.001);
    }

    private void mockNumberGenerator(int returnedValue) {
        mockedNumberGenerator.when(() -> NumberGenerator.generateRandomNumber(UPPER_BOUND)).thenReturn(returnedValue);
    }
}