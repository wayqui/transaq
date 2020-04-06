package com.wayqui.transaq.conf.mapper;

import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.api.model.TransactionResponse;
import com.wayqui.transaq.api.model.TransactionStatusResponse;
import com.wayqui.transaq.dto.TransactionDto;
import com.wayqui.transaq.dto.TransactionStatusDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper( TransactionMapper.class );

    TransactionDto requestToDto(TransactionRequest request);

    TransactionResponse dtoToResponse(TransactionDto response);

    TransactionStatusResponse dtoToResponse(TransactionStatusDto response);

    List<TransactionResponse> dtosToResponses(List<TransactionDto> dtos);

}
