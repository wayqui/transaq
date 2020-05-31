package com.wayqui.transaq.api.error;

import com.wayqui.transaq.api.model.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

@Provider
@Slf4j
public class ConstraintViolationExceptionMapper
        implements ExceptionMapper<ConstraintViolationException> {

    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(ConstraintViolationException e) {
        log.info("Validation error: {}", e.getLocalizedMessage());
        List<String> errorMessages = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());

        ApiErrorResponse entityResponse = ApiErrorResponse.builder()
                .status(Response.Status.BAD_REQUEST.getReasonPhrase())
                .message("Validation error")
                .errors(errorMessages)
                .build();

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(entityResponse)
                .type(headers.getMediaType())
                .build();
    }

}
