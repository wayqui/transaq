package com.wayqui.transaq.api.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class TransactionStatusRequest {
    @NotNull(message = "reference cannot be null")
    @NotBlank(message = "reference cannot be empty")
    private String reference;
    private String channel;

}
