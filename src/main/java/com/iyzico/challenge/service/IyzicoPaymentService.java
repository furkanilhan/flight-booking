package com.iyzico.challenge.service;

import com.iyzico.challenge.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.*;

@Service
public class IyzicoPaymentService {

    private final Logger logger = LoggerFactory.getLogger(IyzicoPaymentService.class);

    private final PaymentProcessor paymentProcessor;
    private final Semaphore semaphore;

    public IyzicoPaymentService(PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
        // this can be calculated based on db configuration if made public
        this.semaphore = new Semaphore(2);
    }

    public void pay(BigDecimal price) {
        try {
            semaphore.acquire();
            paymentProcessor.submit(price);
        } catch (InterruptedException e) {
            logger.error("Error occurred while acquiring semaphore", e);
            Thread.currentThread().interrupt();
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Payment processing error");
        } finally {
            semaphore.release();
        }
    }
}
