package com.wayqui.transaq.api.model;

import com.wayqui.transaq.conf.validator.NotZero;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    private String reference;

    @NotNull(message = "account_iban cannot be null")
    @NotBlank(message = "account_iban cannot be empty")
    private String account_iban;

    private OffsetDateTime date;

    /**
     * ASSUMPTION: Since it's not mentioned but it's obvious that a transaction with an
     * amount of zero should not be allowed
     */
    @NotNull(message = "amount cannot be null")
    @NotZero(message = "amount cannot be zero")
    private BigDecimal amount;

    @Positive(message = "fee must be positive")
    private BigDecimal fee;

    private String description;
}
