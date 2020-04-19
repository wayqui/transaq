package com.wayqui.transaq.conf.security;

public final class SecurityConstants {

    public static final String AUTH_LOGIN_URL = "/rest/login";

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "secure-api";
    public static final String TOKEN_AUDIENCE = "secure-app";

    private SecurityConstants() {
        throw new IllegalStateException("Cannot create instance of SecurityConstants class");
    }
}
