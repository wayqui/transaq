package com.wayqui.transaq.api;

import com.wayqui.transaq.api.model.FindTransactionRequest;
import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.api.model.TransactionStatusRequest;
import com.wayqui.transaq.conf.mapper.TransactionMapper;
import com.wayqui.transaq.dto.TransactionChannel;
import com.wayqui.transaq.dto.TransactionDto;
import com.wayqui.transaq.dto.TransactionStatusDto;
import com.wayqui.transaq.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import java.util.List;

@Component
public class TransactionControllerImpl implements TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Override
    public Response createTransaction(TransactionRequest transaction) {

        List<String> errors = transactionService.validate(transaction);

        if (errors.isEmpty()) {
            TransactionDto response = transactionService.createTransaction(
                    TransactionMapper.INSTANCE.requestToDto(transaction));

            return Response.status(Response.Status.CREATED)
                    .entity(TransactionMapper.INSTANCE.dtoToResponse(response)).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errors).build();
        }
    }

    @Override
    public Response findTransaction(String account_iban, Boolean ascending) {

        List<String> validationErrors = transactionService.validate(new FindTransactionRequest(account_iban, ascending));

        if (validationErrors.isEmpty()) {
            List<TransactionDto> transactions = transactionService.findTransactions(
                    account_iban, ascending);

            return Response.status(Response.Status.OK).entity(
                    TransactionMapper.INSTANCE.dtosToResponses(transactions)).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationErrors).build();
        }
    }

    @Override
    public Response obtainTransactionStatus(TransactionStatusRequest transactionStatus) {

        List<String> validationErrors = transactionService.validate(transactionStatus);

        if (validationErrors.isEmpty()) {
            TransactionStatusDto status = transactionService.obtainTransactionStatus(
                    transactionStatus.getReference(), TransactionChannel.valueOf(transactionStatus.getChannel()));

            return Response.status(Response.Status.OK).entity(
                    TransactionMapper.INSTANCE.dtoToResponse(status)).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationErrors).build();
        }
    }
}
