package com.wayqui.transaq.api;

import com.wayqui.transaq.api.model.*;
import com.wayqui.transaq.service.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

@Component
public class TransactionControllerImpl implements TransactionController {

    @Autowired
    private TransactionServiceImpl transactionService;

    @Override
    public Response createTransaction(TransactionRequest transaction) {
        TransactionResponse response;

        List<String> validationErrors = transactionService.validate(transaction);
        if (validationErrors.isEmpty()) {
            response = transactionService.createTransaction(transaction);
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationErrors).build();
        }
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @Override
    public Response findTransaction(String account_iban, Boolean ascending) {
        TransactionResponse response;

        List<String> validationErrors = transactionService.validate(new FindTransactionRequest(account_iban, ascending));
        if (validationErrors.isEmpty()) {
            response = transactionService.findTransaction(account_iban, ascending);
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationErrors).build();
        }
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @Override
    public Response obtainTransactionStatus(TransactionStatusRequest transactionStatus) {
        TransactionStatusResponse response;

        List<String> validationErrors = transactionService.validate(transactionStatus);
        if (validationErrors.isEmpty()) {
            response = transactionService.obtainTransactionStatus(transactionStatus);
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationErrors).build();
        }
        return Response.status(Response.Status.OK).entity(response).build();
    }
}
