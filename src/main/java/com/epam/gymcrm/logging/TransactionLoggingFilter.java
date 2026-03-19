package com.epam.gymcrm.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TransactionLoggingFilter.class);
    private static final String TRANSACTION_ID = "transactionId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String transactionId = UUID.randomUUID().toString();
        MDC.put(TRANSACTION_ID, transactionId);

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullPath = queryString == null ? uri : uri + "?" + queryString;

        log.info("REST request started: method={}, path={}, transactionId={}", method, fullPath, transactionId);

        try {
            filterChain.doFilter(request, response);
            log.info("REST request completed: method={}, path={}, status={}, transactionId={}",
                    method, fullPath, response.getStatus(), transactionId);
        } catch (Exception ex) {
            log.error("REST request failed: method={}, path={}, status={}, message={}, transactionId={}",
                    method, fullPath, response.getStatus(), ex.getMessage(), transactionId);
            throw ex;
        } finally {
            MDC.remove(TRANSACTION_ID);
        }
    }
}