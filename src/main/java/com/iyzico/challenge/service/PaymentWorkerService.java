package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Payment;
import com.iyzico.challenge.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentWorkerService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentWorkerService.class);

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentWorkerService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void processPayment(Payment payment) {
        paymentRepository.save(payment);
        logger.info("Payment saved successfully!");
    }
}