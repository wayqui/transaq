package com.wayqui.transaq.api;

import io.swagger.annotations.*;
import org.springframework.security.core.Authentication;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/login")
@SwaggerDefinition(
        securityDefinition = @SecurityDefinition(
                basicAuthDefinitions = {
                        @BasicAuthDefinition(
                                key = "basicAuth",
                                description = "Basic Authentication"
                        )
                }
        )
)
@Api("Authentication")
public interface AuthApi {
    /**
     * Implemented by Spring Security
     */
    @ApiOperation(value = "Login",
            notes = "Login with the given credentials.",
            authorizations = {
                    @Authorization(value = "basicAuth")
            })
    @ApiResponses({@ApiResponse(code = 200, message = "", response = Authentication.class)})
    @GET
    @Produces("application/json")
    default void login() {
        throw new IllegalStateException("Add Spring Security to handle authentication");
    }
}