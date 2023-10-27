package com.test.yolo.controllers;

import javax.validation.ConstraintViolation;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnOpen;
import javax.websocket.OnMessage;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.yolo.dto.BetRequestDTO;
import com.test.yolo.dto.BetResponseDTO;
import com.test.yolo.services.GameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
@ServerEndpoint(value = "/api/v1/socket/game")
public class GameWebSocketController {

    private final GameService gameService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session) {
        log.info("Connected: " + session.getId());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        try {
            System.out.println(message);
            BetRequestDTO request = objectMapper.readValue(message, BetRequestDTO.class);
            System.out.println(request);
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

            Set<ConstraintViolation<BetRequestDTO>> violations = validator.validate(request);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                        .collect(Collectors.joining(", "));
                session.getBasicRemote().sendText("{\"error\": \"" + errorMessage + "\"}");
                return;
            }

            CompletableFuture<ResponseEntity<BetResponseDTO>> future = gameService.calculateWinAsync(request.getBet(), request.getNumber())
                    .thenApply(win -> {
                        BetResponseDTO response = new BetResponseDTO();
                        response.setWin(win);
                        return ResponseEntity.ok(response);
                    })
                    .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));

            future.thenAccept(responseEntity -> {
                try {
                    String responseJson = objectMapper.writeValueAsString(responseEntity.getBody());
                    session.getBasicRemote().sendText(responseJson);
                } catch (IOException e) {
                   log.error("Error sending response: " + e.getMessage());
                }
            });

        } catch (IOException e) {
            log.error("Error parsing message: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session) {
        log.info("Closed: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.info("Error: " + throwable.getMessage());
    }
}
