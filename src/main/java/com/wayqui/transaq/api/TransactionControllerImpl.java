package com.wayqui.transaq.api;

import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.api.model.TransactionStatusRequest;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;

@Component
public class TransactionControllerImpl implements TransactionController {

    @Override
    public Response createTransaction(TransactionRequest transaction) {
        return Response.status(200).build();
    }

    @Override
    public Response findTransaction(String account_iban, Boolean ascending) {
        return Response.status(200).build();
    }

    @Override
    public Response obtainTransactionStatus(TransactionStatusRequest transactionStatus) {
        return Response.status(200).build();
    }
}
