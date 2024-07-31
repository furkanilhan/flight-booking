package com.iyzico.challenge.controller;

import com.iyzico.challenge.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentProcessingService) {
        this.paymentService = paymentProcessingService;
    }

    @PostMapping("/buy")
    public ResponseEntity<String> buySeat(@RequestParam Long seatId) {
        String result = paymentService.processPayment(seatId);
        return ResponseEntity.ok(result);
    }
}
