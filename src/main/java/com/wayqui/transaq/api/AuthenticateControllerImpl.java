package com.wayqui.transaq.api;

import com.wayqui.transaq.api.model.AuthenticateRequest;
import com.wayqui.transaq.api.model.AuthenticateResponse;
import com.wayqui.transaq.exception.BusinessException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import java.util.UUID;

@Component
public class AuthenticateControllerImpl implements AuthenticateController {

    @Override
    public Response authenticateUser(AuthenticateRequest authRequest) throws BusinessException {
        // TODO Implement logic controller, validation and execution
        AuthenticateResponse response = new AuthenticateResponse();

        // TODO Implement real JWT token generation
        response.setJwtToken(UUID.randomUUID().toString());

        return Response.status(Response.Status.OK).entity(response).build();
    }
}
