package com.wayqui.transaq.exception;

import lombok.Getter;

import javax.ws.rs.core.Response.Status;

@Getter
public class BusinessException extends Exception {

    static final long serialVersionUID = 1L;

    private String errorMessage;
    private Status status;

    public BusinessException() {
        super();
    }

    public BusinessException(String errorMessage, Status status) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.status = status;
    }

}
