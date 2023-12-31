package com.test.yolo.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class BetRequestDTO {
    @NotNull(message = "Bet amount cannot be null")
    @DecimalMin(message = "Bet must be greater than or equal to 0.01", value = "0.01")
    private BigDecimal bet;

    @NotNull(message = "Number cannot be null")
    @Min(value = 1, message = "Number must be between 1 and 100")
    @Max(value = 100, message = "Number must be between 1 and 100")
    private Integer number;
}
