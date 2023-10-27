package com.test.yolo.controllers;

import static org.mockito.Mockito.*;

import javax.websocket.Session;
import javax.websocket.RemoteEndpoint;

import com.test.yolo.dto.BetRequestDTO;
import com.test.yolo.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class GameWebSocketControllerTest {

    @Mock
    private GameService gameService;

    @Mock
    private Session session;

    @Mock
    private RemoteEndpoint.Basic basicRemote;

    @InjectMocks
    private GameWebSocketController gameWebSocketController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(session.getBasicRemote()).thenReturn(basicRemote);
    }

    private static Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of(66L, 6, 200L, "{\"bet\": 66, \"number\": 6}", "{\"win\":200}", true),
                Arguments.of(-66L, 6, 200L, "{\"bet\": -66, \"number\": 6}", "{\"error\": \"bet: Bet must be greater than or equal to 0.01\"}", false),
                Arguments.of(66L, -6, 200L, "{\"bet\": 66, \"number\": -6}", "{\"error\": \"number: Number must be between 1 and 100\"}", false)
        );
    }

    @ParameterizedTest
    @MethodSource("testData")
    public void shouldPlaceBetAndReturnResponse(long bet, int number, long win,
                   String incomingMessage, String responseMessage, boolean shouldVerify) throws IOException {

        BigDecimal mockWinAmount = BigDecimal.valueOf(win);
        BetRequestDTO mockRequest = new BetRequestDTO();
        mockRequest.setBet(BigDecimal.valueOf(bet));
        mockRequest.setNumber(number);

        when(gameService.calculateWinAsync(mockRequest.getBet(), mockRequest.getNumber()))
                .thenReturn(CompletableFuture.completedFuture(mockWinAmount));

        gameWebSocketController.onMessage(session, incomingMessage);

        if (shouldVerify) {
            verify(gameService).calculateWinAsync(mockRequest.getBet(), mockRequest.getNumber());
        } else {
            verify(gameService, never()).calculateWinAsync(mockRequest.getBet(), mockRequest.getNumber());
        }

        verify(session.getBasicRemote()).sendText(responseMessage);
    }


    @Test
    public void shouldReturnErrorMessageForNullBetAmount() throws IOException {
        BigDecimal bet = null;
        int number = 6;
        long win = 200;
        String incomingMessage = "{\"bet\": " + bet + ", \"number\":" + number + "}";
        String responseMessage = "{\"error\": \"bet: Bet amount cannot be null\"}";
        BigDecimal mockWinAmount = BigDecimal.valueOf(win);

        when(gameService.calculateWinAsync(bet, number))
                .thenReturn(CompletableFuture.completedFuture(mockWinAmount));

        gameWebSocketController.onMessage(session, incomingMessage);

        verify(gameService, never()).calculateWinAsync(bet, number);
        verify(session.getBasicRemote()).sendText(responseMessage);
    }

    @Test
    public void shouldReturnErrorMessageForNullNumber() throws IOException {
        long bet = 66;
        Integer number = null;
        long win = 200;
        String incomingMessage = "{\"bet\": " + bet + ", \"number\":" + number + "}";
        String responseMessage = "{\"error\": \"number: Number cannot be null\"}";
        BigDecimal mockWinAmount = BigDecimal.valueOf(win);

        when(gameService.calculateWinAsync(BigDecimal.valueOf(bet), number))
                .thenReturn(CompletableFuture.completedFuture(mockWinAmount));

        gameWebSocketController.onMessage(session, incomingMessage);

        verify(gameService, never()).calculateWinAsync(BigDecimal.valueOf(bet), number);
        verify(session.getBasicRemote()).sendText(responseMessage);
    }
}
