package com.wayqui.transaq.exception;

import com.wayqui.transaq.api.model.ApiErrorResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(BusinessException e) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .message(e.getErrorMessage())
                .status(e.getStatus().getReasonPhrase())
                .build();

        return Response.status(e.getStatus()).entity(error).build();
    }
}
