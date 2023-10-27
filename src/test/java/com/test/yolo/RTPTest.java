package com.test.yolo;

import com.test.yolo.dto.BetRequestDTO;
import com.test.yolo.dto.BetResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RTPTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final int TOTAL_ROUNDS = 1_000_000;
    private static final int THREAD_COUNT = 24;
    private static final BigDecimal BET_AMOUNT = BigDecimal.ONE;

    @Test
    public void shouldCalculateRTPForOneMillionGames() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        AtomicReference<BigDecimal> totalWon = new AtomicReference<>(BigDecimal.ZERO);
        AtomicInteger exceptionsCount = new AtomicInteger(0);

        for (int i = 1; i <= TOTAL_ROUNDS; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    System.out.println("Round: " + finalI);
                    BetRequestDTO request = new BetRequestDTO();
                    request.setBet(BET_AMOUNT);
                    int number = (int) (Math.random() * 100) + 1;
                    request.setNumber(number);
                    ResponseEntity<BetResponseDTO> responseEntity = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/game/bet", request, BetResponseDTO.class);
                    BigDecimal win = Objects.requireNonNull(responseEntity.getBody()).getWin();
                    totalWon.updateAndGet(current -> current.add(win));
                } catch (Exception e) {
                    exceptionsCount.incrementAndGet();
                    System.err.println("Exception in Round " + finalI + ": " + e.getMessage());
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);

        BigDecimal totalSpent = BET_AMOUNT.multiply(BigDecimal.valueOf(TOTAL_ROUNDS));
        BigDecimal rtp = (totalWon.get().divide(totalSpent, 4, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100));

        System.out.println("Total Spent: " + totalSpent);
        System.out.println("Total Won: " + totalWon);
        System.out.println("RTP: " + rtp + "%");
    }
}