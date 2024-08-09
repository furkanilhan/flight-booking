package com.furkan.booking.controller;

import com.furkan.booking.exception.MessageResponse;
import com.furkan.booking.service.PaymentService;
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
