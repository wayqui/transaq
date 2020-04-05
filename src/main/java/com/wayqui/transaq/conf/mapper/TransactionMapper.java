package com.wayqui.transaq.conf.mapper;

import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.dto.TransactionDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper( TransactionMapper.class );

    TransactionDto requestToDto(TransactionRequest request);

    TransactionRequest dtoToRequest(TransactionDto dto);

}
