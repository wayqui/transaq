package com.wayqui.transaq.api;

import com.wayqui.transaq.api.model.AuthenticateRequest;
import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.exception.BusinessException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/authenticate")
public interface AuthenticateController {

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response authenticateUser(AuthenticateRequest authRequest) throws BusinessException;

}
