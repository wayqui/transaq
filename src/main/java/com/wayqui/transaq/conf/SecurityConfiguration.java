package com.wayqui.transaq.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        /* FIXME This configuration must be changed during this development
            it was included simply to make the Cucumber test to work after including Spring Security
         */
        http.csrf().disable();
    }
}
