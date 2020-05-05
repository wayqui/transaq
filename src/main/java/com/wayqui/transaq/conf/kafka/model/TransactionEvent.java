package com.wayqui.transaq.conf.kafka.model;

import com.wayqui.transaq.dto.TransactionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.header.Header;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TransactionEvent {

    private Integer id;
    private TransactionDto transactionDto;
    private List<Header> recordHeaders;
}
