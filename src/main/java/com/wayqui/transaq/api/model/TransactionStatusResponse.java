package com.wayqui.transaq.api.model;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionStatusResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String reference;
    private String status;
    private Double amount;
    private Double fee;
}
