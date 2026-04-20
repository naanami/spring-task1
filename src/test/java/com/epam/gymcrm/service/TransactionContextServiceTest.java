package com.epam.gymcrm.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

class TransactionContextServiceTest {

    private final TransactionContextService service = new TransactionContextService();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldCreateTransactionIdWhenMissing() {
        String transactionId = service.getOrCreateTransactionId();

        assertNotNull(transactionId);
        assertFalse(transactionId.isBlank());
        assertEquals(transactionId, MDC.get("transactionId"));
    }

    @Test
    void shouldReuseExistingTransactionId() {
        MDC.put("transactionId", "test-id-123");

        String transactionId = service.getOrCreateTransactionId();

        assertEquals("test-id-123", transactionId);
    }
}