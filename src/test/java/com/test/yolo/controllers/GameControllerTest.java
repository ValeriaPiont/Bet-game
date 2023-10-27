package com.test.yolo.controllers;

import com.test.yolo.dto.BetRequestDTO;
import com.test.yolo.dto.BetResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldPlaceBetAndReturnWinAmount() {
        String url = "http://localhost:" + port + "/api/v1/game/bet";
        BetRequestDTO request = new BetRequestDTO();
        request.setBet(BigDecimal.valueOf(100));
        request.setNumber(50);

        ResponseEntity<BetResponseDTO> responseEntity = restTemplate.postForEntity(url, request, BetResponseDTO.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        BetResponseDTO responseBody = responseEntity.getBody();

        assertNotNull(responseBody);
        assertNotNull(responseBody.getWin());
    }

    @Test
    public void shouldReturnBadRequestForInvalidBetAmount() {
        String url = "http://localhost:" + port + "/api/v1/game/bet";
        BetRequestDTO request = new BetRequestDTO();
        request.setBet(BigDecimal.valueOf(0.001));
        request.setNumber(50);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        String expectedResponse = "{\"bet\":\"Bet must be greater than or equal to 0.01\"}";
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    public void shouldReturnBadRequestForInvalidNumber() {
        String url = "http://localhost:" + port + "/api/v1/game/bet";
        BetRequestDTO request = new BetRequestDTO();
        request.setBet(BigDecimal.valueOf(100));
        request.setNumber(101);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        String expectedResponse = "{\"number\":\"Number must be between 1 and 100\"}";
        assertEquals(expectedResponse, responseEntity.getBody());    }

    @Test
    public void shouldReturnBadRequestForNullBetAmount() {
        String url = "http://localhost:" + port + "/api/v1/game/bet";
        BetRequestDTO request = new BetRequestDTO();
        request.setNumber(50);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        String expectedResponse = "{\"bet\":\"Bet amount cannot be null\"}";
        assertEquals(expectedResponse, responseEntity.getBody());    }

    @Test
    public void shouldReturnBadRequestForNullNumber() {
        String url = "http://localhost:" + port + "/api/v1/game/bet";
        BetRequestDTO request = new BetRequestDTO();
        request.setBet(BigDecimal.valueOf(100));

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        String expectedResponse = "{\"number\":\"Number cannot be null\"}";
        assertEquals(expectedResponse, responseEntity.getBody());    }

    @Test
    public void shouldPlaceBetWithMaxValues() {
        String url = "http://localhost:" + port + "/api/v1/game/bet";
        BetRequestDTO request = new BetRequestDTO();
        request.setBet(new BigDecimal("100000000000"));
        request.setNumber(90);

        ResponseEntity<BetResponseDTO> responseEntity = restTemplate.postForEntity(url, request, BetResponseDTO.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }
}