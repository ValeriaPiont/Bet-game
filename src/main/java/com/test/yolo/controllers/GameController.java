package com.test.yolo.controllers;

import com.test.yolo.dto.BetRequestDTO;
import com.test.yolo.dto.BetResponseDTO;
import com.test.yolo.services.GameService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/game")
public class GameController {

    private final GameService gameService;

    @PostMapping("/bet")
    public CompletableFuture<ResponseEntity<BetResponseDTO>> placeBet(@RequestBody @Valid BetRequestDTO request) {
        return gameService.calculateWinAsync(request.getBet(), request.getNumber())
                .thenApply(win -> {
                    BetResponseDTO response = new BetResponseDTO();
                    response.setWin(win);
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
    }
}