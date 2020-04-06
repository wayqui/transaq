package com.wayqui.transaq.api.model;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatusResponse {
    private String reference;
    private String status;
    private Double amount;
    private Double fee;
}
