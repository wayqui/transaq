package com.wayqui.transaq.api;

import com.wayqui.transaq.api.model.ApiErrorResponse;
import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.api.model.TransactionStatusRequest;
import com.wayqui.transaq.conf.kafka.producer.KafkaTransactionProducer;
import com.wayqui.transaq.conf.mapper.TransactionMapper;
import com.wayqui.transaq.dto.TransactionChannel;
import com.wayqui.transaq.dto.TransactionDto;
import com.wayqui.transaq.dto.TransactionStatusDto;
import com.wayqui.transaq.exception.BusinessException;
import com.wayqui.transaq.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Random;

@Component
public class TransactionControllerImpl implements TransactionController {

    @Autowired
    private TransactionService service;

    @Autowired
    private KafkaTransactionProducer kafkaProducer;

    @Override
    public Response createTransaction(TransactionRequest transaction) {

        try {
            TransactionDto transactDtoResponse = service.createTransaction(
                    TransactionMapper.INSTANCE.requestToDto(transaction));

            kafkaProducer.sendAsyncDefaultTopic(new Random().nextLong(), transactDtoResponse);

            return Response.status(Response.Status.CREATED)
                    .entity(TransactionMapper.INSTANCE.dtoToResponse(transactDtoResponse))
                    .build();

        } catch (BusinessException e) {
            ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                    .status(e.getStatus().getReasonPhrase())
                    .message(e.getErrorMessage())
                    .build();

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse).build();
        }
    }

    @Override
    public Response findTransaction(String account_iban, Boolean ascending) {
        List<TransactionDto> transactions = service.findTransactions(account_iban, ascending);

        return Response.status(Response.Status.OK).entity(
                TransactionMapper.INSTANCE.dtosToResponses(transactions)).build();
    }

    @Override
    public Response obtainTransactionStatus(TransactionStatusRequest transactionStatus) {
        TransactionStatusDto status = service.obtainTransactionStatus(
                transactionStatus.getReference(), TransactionChannel.valueOf(transactionStatus.getChannel()));

        return Response.status(Response.Status.OK).entity(
                TransactionMapper.INSTANCE.dtoToResponse(status)).build();
    }
}