package com.wayqui.transaq.api.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionStatusRequest {

    @NotNull(message = "reference cannot be null")
    @NotBlank(message = "reference cannot be empty")
    private String reference;

    @Pattern(regexp = "CLIENT|ATM|INTERNAL", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Channel must be CLIENT, ATM or INTERNAL")
    private String channel;

}
