package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Payment;
import com.iyzico.challenge.worker.PaymentWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class IyzicoPaymentService {
    private static final Logger logger = LoggerFactory.getLogger(IyzicoPaymentService.class);

    private final BlockingQueue<Payment> paymentQueue;
    private final BankService bankService;
    private final ExecutorService executorService;
    private final AtomicBoolean running;

    @Autowired
    public IyzicoPaymentService(BankService bankService, ApplicationContext context) {
        this.bankService = bankService;
        this.paymentQueue = new LinkedBlockingQueue<>();
        this.executorService = Executors.newFixedThreadPool(2);
        this.running = new AtomicBoolean(true);

        for (int i = 0; i < 2; i++) {
            PaymentWorker paymentWorker = context.getBean(PaymentWorker.class, paymentQueue, context.getBean(PaymentWorkerService.class), running);
            executorService.execute(paymentWorker);
        }
    }

    public String pay(BigDecimal price) {
        BankPaymentRequest request = new BankPaymentRequest();
        request.setPrice(price);
        BankPaymentResponse response = bankService.pay(request);

        Payment payment = new Payment();
        payment.setBankResponse(response.getResultCode());
        payment.setPrice(price);
        try {
            paymentQueue.put(payment);
            logger.info("Payment enqueued successfully!");
            return "Payment successful";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Failed to enqueue payment", e);
            return "Failed to enqueue payment";
        }
    }

    public void shutdown() {
        running.set(false);
        executorService.shutdown();
        logger.info("Payment service is shutting down.");
    }
}
