package com.wayqui.transaq.api;

import com.wayqui.transaq.api.model.FindTransactionRequest;
import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.api.model.TransactionResponse;
import com.wayqui.transaq.api.model.TransactionStatusRequest;
import com.wayqui.transaq.conf.mapper.TransactionMapper;
import com.wayqui.transaq.dto.TransactionChannel;
import com.wayqui.transaq.dto.TransactionResultDto;
import com.wayqui.transaq.dto.TransactionStatusDto;
import com.wayqui.transaq.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Component
public class TransactionControllerImpl implements TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Override
    public Response createTransaction(TransactionRequest transaction) {

        TransactionResultDto response;

        List<String> validationErrors = transactionService.validate(transaction);
        if (validationErrors.isEmpty()) {

            response = transactionService.createTransaction(
                    TransactionMapper.INSTANCE.requestToDto(transaction));
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
        Optional<TransactionStatusDto> response;

        List<String> validationErrors = transactionService.validate(transactionStatus);
        if (validationErrors.isEmpty()) {
            response = transactionService.obtainTransactionStatus(
                    transactionStatus.getReference(), TransactionChannel.valueOf(transactionStatus.getChannel()));
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationErrors).build();
        }
        return Response.status(Response.Status.OK).entity(response).build();
    }
}
