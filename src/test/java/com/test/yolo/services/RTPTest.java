package com.test.yolo.services;

import com.test.yolo.dto.BetRequestDTO;
import com.test.yolo.dto.BetResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RTPTest {

    @Autowired
    private Environment env;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final int TOTAL_ROUNDS = 1_000_000;
    private static final int THREAD_COUNT = 24;
    private static final BigDecimal BET_AMOUNT = BigDecimal.ONE;

    @Test
    public void testRTP() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        AtomicReference<BigDecimal> totalWon = new AtomicReference<>(BigDecimal.ZERO);

        int port = getServerPort();

        for (int i = 0; i < TOTAL_ROUNDS; i++) {
            executorService.execute(() -> {
                BetRequestDTO request = new BetRequestDTO();
                request.setBet(BET_AMOUNT);
                int number = (int) (Math.random() * 100) + 1;
                request.setNumber(number);
                ResponseEntity<BetResponseDTO> responseEntity = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/game/bet", request, BetResponseDTO.class);
                BigDecimal win = Objects.requireNonNull(responseEntity.getBody()).getWin();
                System.out.println(win.doubleValue());
                totalWon.updateAndGet(current -> current.add(win));
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

    private int getServerPort() {
        String portProperty = env.getProperty("local.server.port");

        if (portProperty == null || portProperty.isEmpty()) {
            throw new IllegalStateException("Port property is not set!");
        }

        try {
            return Integer.parseInt(portProperty);
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("The port property is not a valid integer: " + portProperty, ex);
        }
    }
}