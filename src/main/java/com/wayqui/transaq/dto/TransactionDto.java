package com.wayqui.transaq.dto;

import lombok.*;

import java.time.Instant;
import java.util.Objects;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private String reference;
    private String iban;
    private Instant date;
    private Double amount;
    private Double fee;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionDto that = (TransactionDto) o;
        return Objects.equals(reference, that.reference) &&
                Objects.equals(iban, that.iban) &&
                Objects.equals(date, that.date) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(fee, that.fee) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference, iban, date, amount, fee, description);
    }
}
