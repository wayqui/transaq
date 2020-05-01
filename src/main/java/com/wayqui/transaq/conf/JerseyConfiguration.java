package com.wayqui.transaq.conf;

import com.wayqui.transaq.api.TransactionController;
import com.wayqui.transaq.exception.BusinessExceptionMapper;
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
        // There's a bug in Jersey for running WARS using packages:
        // https://github.com/jersey/jersey/pull/196
        //packages("com.wayqui.transaq.exception");

        register(TransactionController.class);
        register(BusinessExceptionMapper.class);

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
        swaggerConfigBean.setBasePath("/transaq/rest");
        swaggerConfigBean.setResourcePackage("com.wayqui.transaq.api");
        swaggerConfigBean.setPrettyPrint(true);
        swaggerConfigBean.setScan(true);
    }
}