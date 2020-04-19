package com.wayqui.transaq.conf;

import com.wayqui.transaq.conf.security.AppAuthenticationFilter;
import com.wayqui.transaq.conf.security.AppAuthorizationFilter;
import com.wayqui.transaq.conf.security.JWTTokenHandler;
import com.wayqui.transaq.conf.security.SecurityConstants;
import com.wayqui.transaq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTTokenHandler tokenHandler;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("SecurityConfiguration! configure");
        http.cors().and().csrf().disable()
            .authorizeRequests().antMatchers(SecurityConstants.AUTH_LOGIN_URL).permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilter(new AppAuthenticationFilter(authenticationManager(), userService, tokenHandler))
            .addFilter(new AppAuthorizationFilter(authenticationManager(), userService, tokenHandler))
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());

        return source;
    }
}