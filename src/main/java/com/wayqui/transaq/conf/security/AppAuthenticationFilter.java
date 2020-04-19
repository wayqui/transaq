package com.wayqui.transaq.conf.security;

import com.wayqui.transaq.api.model.AuthenticateRequest;
import com.wayqui.transaq.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AppAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService userService;

    private JWTTokenHandler tokenHandler;

    public AppAuthenticationFilter(AuthenticationManager manager, UserService userService, JWTTokenHandler tokenHandler) {
        this.setAuthenticationManager(manager);
        this.userService = userService;
        this.tokenHandler = tokenHandler;
        setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            AuthenticateRequest authRequest = new AuthenticateRequest(request.getHeader(SecurityConstants.TOKEN_HEADER));

            final UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(), authRequest.getPassword(), userDetails.getAuthorities());

            return getAuthenticationManager().authenticate(token);
        } catch (BadCredentialsException e) {
            logger.error(e.getLocalizedMessage());
            throw e;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user = ((User) authResult.getPrincipal());

        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        final String token = tokenHandler.generateToken(user);

        response.addHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token);
    }

}