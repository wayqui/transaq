package com.wayqui.transaq.dto;

public enum TransactionStatus {
    PENDING("PENDING"),
    SETTLED("SETTLED"),
    FUTURE("SETTLE"),
    INVALID("INVALID");

    private String status;

    TransactionStatus(String status) {
        this.status = status;
    }

}
