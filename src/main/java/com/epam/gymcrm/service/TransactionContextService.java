package com.epam.gymcrm.service;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransactionContextService {

    private static final String TRANSACTION_ID_KEY = "transactionId";

    public String getOrCreateTransactionId() {
        String transactionId = MDC.get(TRANSACTION_ID_KEY);

        if (transactionId == null || transactionId.isBlank()) {
            transactionId = UUID.randomUUID().toString();
            MDC.put(TRANSACTION_ID_KEY, transactionId);
        }

        return transactionId;
    }
}