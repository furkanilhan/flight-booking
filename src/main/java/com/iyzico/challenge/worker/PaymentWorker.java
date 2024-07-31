package com.iyzico.challenge.worker;

import com.iyzico.challenge.entity.Payment;
import com.iyzico.challenge.service.PaymentWorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Scope("prototype")
public class PaymentWorker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(PaymentWorker.class);
    private final BlockingQueue<Payment> paymentQueue;
    private final PaymentWorkerService paymentWorkerService;
    private final AtomicBoolean running;

    @Autowired
    public PaymentWorker(BlockingQueue<Payment> paymentQueue, PaymentWorkerService paymentWorkerService, AtomicBoolean running) {
        this.paymentQueue = paymentQueue;
        this.paymentWorkerService = paymentWorkerService;
        this.running = running;
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                Payment payment = paymentQueue.take();
                paymentWorkerService.processPayment(payment);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Worker thread interrupted", e);
                break;
            } catch (Exception e) {
                logger.error("Failed to process payment", e);
            }
        }
    }
}