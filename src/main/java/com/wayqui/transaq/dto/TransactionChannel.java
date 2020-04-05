package com.wayqui.transaq.dto;

public enum TransactionChannel {
    CLIENT ("CLIENT"),
    ATM ("ATM"),
    INTERNAL ("INTERNAL");

    private String channel;

    TransactionChannel(String channel) {
        this.channel = channel;
    }


}
