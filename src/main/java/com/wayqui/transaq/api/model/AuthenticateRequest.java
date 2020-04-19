package com.wayqui.transaq.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Base64;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticateRequest {

    @NotNull(message = "username cannot be null")
    @NotBlank(message = "username cannot be empty")
    private String username;

    @NotNull(message = "password cannot be null")
    @NotBlank(message = "password cannot be empty")
    private String password;

    public AuthenticateRequest(String authValue) {
        String[] authParts = authValue.split("\\s+");
        String authInfo = authParts[1];
        String decodedAuth = new String(Base64.getDecoder().decode(authInfo));

        this.username = decodedAuth.split(":")[0];
        this.password = decodedAuth.split(":")[1];
    }
}
