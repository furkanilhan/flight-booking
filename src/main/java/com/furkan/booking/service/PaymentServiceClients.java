package com.furkan.booking.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
public class PaymentServiceClients {

    private PaymentSemaphoreService paymentSemaphoreService;

    public PaymentServiceClients(PaymentSemaphoreService paymentSemaphoreService) {
        this.paymentSemaphoreService = paymentSemaphoreService;
    }

    @Async
    public CompletableFuture<String> call(BigDecimal price) {
        paymentSemaphoreService.pay(price);
        return CompletableFuture.completedFuture("success");
    }
}
