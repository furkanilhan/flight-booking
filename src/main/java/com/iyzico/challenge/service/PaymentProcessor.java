package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Payment;
import com.iyzico.challenge.exception.CustomException;
import com.iyzico.challenge.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class PaymentProcessor {
    private final Logger logger = LoggerFactory.getLogger(PaymentProcessor.class);

    private final BankService bankService;
    private final PaymentRepository paymentRepository;

    public PaymentProcessor(BankService bankService, PaymentRepository paymentRepository) {
        this.bankService = bankService;
        this.paymentRepository = paymentRepository;
    }

    public void submit(BigDecimal price) {
        try {
            //pay with bank
            BankPaymentRequest request = new BankPaymentRequest();
            request.setPrice(price);
            BankPaymentResponse response = bankService.pay(request);

            //insert records
            Payment payment = new Payment();
            payment.setBankResponse(response.getResultCode());
            payment.setPrice(price);
            paymentRepository.save(payment);
            logger.info("Payment saved successfully!");
        } catch (Exception e) {
            logger.error("Error during payment submission", e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Payment failed");
        }
    }
}