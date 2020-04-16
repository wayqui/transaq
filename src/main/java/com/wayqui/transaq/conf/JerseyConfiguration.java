package com.wayqui.transaq.conf;

import com.wayqui.transaq.api.AuthenticateController;
import com.wayqui.transaq.api.TransactionController;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/rest")
public class JerseyConfiguration extends ResourceConfig {

    @PostConstruct
    public void init() {
        packages("com.wayqui.transaq.exception");
        register(TransactionController.class);
        register(AuthenticateController.class);
    }
}