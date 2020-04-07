package com.wayqui.transaq.util;

import com.wayqui.transaq.api.model.TransactionRequest;

import java.util.HashMap;
import java.util.Map;

public class TransactionRequestMapper {

    public static Map<String, Object> map(TransactionRequest transaction) {
        Map<String, Object> map = new HashMap<>();
        map.put("reference", transaction.getReference());
        map.put("account_iban", transaction.getAccount_iban());
        map.put("date", transaction.getDate());
        map.put("amount", transaction.getAmount());
        map.put("fee", transaction.getFee());
        map.put("description", transaction.getDescription());

        return map;
    }

    public static Map<String, Object> map(String referenceId, String channel) {
        Map<String, Object> map = new HashMap<>();
        map.put("reference", referenceId);
        map.put("channel", channel);

        return map;
    }
}
