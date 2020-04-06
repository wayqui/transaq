package com.wayqui.transaq.conf.mapper;

import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.api.model.TransactionResponse;
import com.wayqui.transaq.api.model.TransactionStatusResponse;
import com.wayqui.transaq.dto.TransactionDto;
import com.wayqui.transaq.dto.TransactionStatusDto;
import com.wayqui.transaq.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper( TransactionMapper.class );

    @Mapping(source = "account_iban", target = "iban")
    TransactionDto requestToDto(TransactionRequest request);

    @Mapping(source = "iban", target = "account_iban")
    TransactionResponse dtoToResponse(TransactionDto response);

    TransactionStatusResponse dtoToResponse(TransactionStatusDto response);

    List<TransactionResponse> dtosToResponses(List<TransactionDto> dtos);

    Transaction dtoToEntity(TransactionDto transaction);

    TransactionDto entityToDto(Transaction result);

    List<TransactionDto> entitiesToDtos(List<Transaction> transactionsByReference);
}
