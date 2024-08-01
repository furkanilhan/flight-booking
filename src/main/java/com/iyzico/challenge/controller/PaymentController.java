package com.iyzico.challenge.controller;

import com.iyzico.challenge.exception.MessageResponse;
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

    @PostMapping
    public ResponseEntity<MessageResponse> buySeat(@RequestParam Long seatId) {
        paymentService.processPayment(seatId);
        return ResponseEntity.ok(new MessageResponse("Seat bought successfully"));
    }
}
