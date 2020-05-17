package com.wayqui.transaq.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "TR_TRANSACTION")
public class Transaction {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long Id;
    private String reference;
    @Column(name = "account_iban")
    private String iban;

    @Column(name = "transaction_date")
    private Instant date;
    private BigDecimal amount;
    private BigDecimal fee;
    private String description;

    @Override
    public String toString() {
        return "Transaction{" +
                "Id=" + Id +
                ", reference='" + reference + '\'' +
                ", iban='" + iban + '\'' +
                ", date=" + date +
                ", amount=" + amount +
                ", fee=" + fee +
                ", description='" + description + '\'' +
                '}';
    }
}
