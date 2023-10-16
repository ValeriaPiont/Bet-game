package com.test.yolo.services;
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
        mockedNumberGenerator.when(() -> NumberGenerator.generateRandomNumber(100)).thenReturn(50);
        BigDecimal win = gameService.calculateWin(BigDecimal.valueOf(40.5), 60);
        Assertions.assertEquals(100.24, win.doubleValue(), 0.001);
    }

    @Test
    public void testNoWinCalculationWithLowerNumber() {
        mockedNumberGenerator.when(() -> NumberGenerator.generateRandomNumber(100)).thenReturn(70);
        BigDecimal win = gameService.calculateWin(BigDecimal.valueOf(40.5), 60);
        Assertions.assertEquals(0.0, win.doubleValue(), 0.001);
    }
}