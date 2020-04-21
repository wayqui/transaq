package com.wayqui.transaq.conf;

import com.wayqui.transaq.api.TransactionController;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
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

        this.SwaggerConfig();
    }

    private void SwaggerConfig() {
        this.register(ApiListingResource.class);
        this.register(SwaggerSerializers.class);

        BeanConfig swaggerConfigBean = new BeanConfig();
        swaggerConfigBean.setConfigId("TransaQ Swagger");
        swaggerConfigBean.setTitle("TransaQ Swagger: A Swagger implementation for our transactions REST service");
        swaggerConfigBean.setVersion("v1");
        swaggerConfigBean.setSchemes(new String[] { "http", "https" });
        swaggerConfigBean.setBasePath("/rest");
        swaggerConfigBean.setResourcePackage("com.wayqui.transaq.api");
        swaggerConfigBean.setPrettyPrint(true);
        swaggerConfigBean.setScan(true);
    }
}