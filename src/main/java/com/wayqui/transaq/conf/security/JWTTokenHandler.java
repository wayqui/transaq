package com.wayqui.transaq.conf.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Date;

public interface JWTTokenHandler extends Serializable {

    String getUsernameFromToken(String token);

    Date getExpirationDateFromToken(String token);

    String generateToken(UserDetails userDetails);

    Boolean validateToken(String token, UserDetails userDetails);
}
