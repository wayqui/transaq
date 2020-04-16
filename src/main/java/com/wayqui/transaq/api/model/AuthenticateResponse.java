package com.wayqui.transaq.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticateResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String jwtToken;
}
