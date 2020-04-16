package com.wayqui.transaq.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticateRequest {

    // TODO implement JSR Validators
    private String username;
    private String password;
}
