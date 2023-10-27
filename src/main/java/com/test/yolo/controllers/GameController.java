package com.test.yolo.controllers;

import com.test.yolo.dto.BetRequestDTO;
import com.test.yolo.dto.BetResponseDTO;
import com.test.yolo.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/game", produces = MediaType.APPLICATION_JSON_VALUE)
public class GameController {

    private final GameService gameService;

    @PostMapping(value = "/bet", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Place a bet in the game")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Bet placed successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BetResponseDTO.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred")
    })
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