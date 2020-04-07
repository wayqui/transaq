package com.wayqui.transaq.exception;

import com.wayqui.transaq.api.model.ApiErrorResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(BusinessException e) {
        ApiErrorResponse error = new ApiErrorResponse(e.getStatus().getReasonPhrase(), e.getErrorMessage());
        return Response.status(e.getStatus()).entity(error).build();
    }
}
