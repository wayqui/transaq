package com.wayqui.transaq.api;

import com.wayqui.transaq.api.model.ApiErrorResponse;
import com.wayqui.transaq.api.model.FindTransactionRequest;
import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.api.model.TransactionStatusRequest;
import com.wayqui.transaq.conf.mapper.TransactionMapper;
import com.wayqui.transaq.dto.TransactionChannel;
import com.wayqui.transaq.dto.TransactionDto;
import com.wayqui.transaq.dto.TransactionStatusDto;
import com.wayqui.transaq.dto.ValidationDto;
import com.wayqui.transaq.exception.BusinessException;
import com.wayqui.transaq.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;

@Component
public class TransactionControllerImpl implements TransactionController {

    @Autowired
    private TransactionService service;

    @Override
    public Response createTransaction(TransactionRequest transaction) throws BusinessException {

        ValidationDto validation = service.validate(transaction);

        if (!validation.getErrors().isEmpty())
            return this.generateErrorResponse(validation, Response.Status.BAD_REQUEST);

        TransactionDto response = service.createTransaction(
                TransactionMapper.INSTANCE.requestToDto(transaction));

        return Response.status(Response.Status.CREATED)
                .entity(TransactionMapper.INSTANCE.dtoToResponse(response)).build();
    }

    @Override
    public Response findTransaction(String account_iban, Boolean ascending) {

        ValidationDto validation = service.validate(new FindTransactionRequest(account_iban, ascending));

        if (!validation.getErrors().isEmpty())
            return this.generateErrorResponse(validation, Response.Status.BAD_REQUEST);

        List<TransactionDto> transactions = service.findTransactions(account_iban, ascending);

        return Response.status(Response.Status.OK).entity(
                TransactionMapper.INSTANCE.dtosToResponses(transactions)).build();
    }

    @Override
    public Response obtainTransactionStatus(TransactionStatusRequest transactionStatus) {

        ValidationDto validation = service.validate(transactionStatus);

        if (!validation.getErrors().isEmpty())
            return this.generateErrorResponse(validation, Response.Status.BAD_REQUEST);

        TransactionStatusDto status = service.obtainTransactionStatus(
                transactionStatus.getReference(), TransactionChannel.valueOf(transactionStatus.getChannel()));

        return Response.status(Response.Status.OK).entity(
                TransactionMapper.INSTANCE.dtoToResponse(status)).build();
    }

    private Response generateErrorResponse(ValidationDto validationError, Response.Status status) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .status(status.getReasonPhrase())
                .message(validationError.getMessage())
                .errors(validationError.getErrors())
                .build();
        return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
    }
}