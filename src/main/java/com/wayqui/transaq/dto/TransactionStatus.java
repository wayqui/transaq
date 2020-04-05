package com.wayqui.transaq.dto;

public enum TransactionStatus {
    PENDING("PENDING"),
    SETTLE("SETTLE"),
    FUTURE("SETTLE"),
    INVALID("INVALID");

    private String status;

    TransactionStatus(String status) {
        this.status = status;
    }

}
