package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.enums.SeatStatus;
import com.iyzico.challenge.repository.SeatRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final IyzicoPaymentService iyzicoPaymentService;
    private final SeatRepository seatRepository;

    public PaymentService(IyzicoPaymentService iyzicoPaymentService, SeatRepository seatRepository) {
        this.iyzicoPaymentService = iyzicoPaymentService;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public String processPayment(Long seatId) {
        try {
            Seat seat = seatRepository.findByIdWithLock(seatId).orElseThrow(() -> new RuntimeException("Seat not found"));
            if (seat.getStatus() == SeatStatus.AVAILABLE) {
                seat.setStatus(SeatStatus.BOOKED);
                seatRepository.save(seat);

                String paymentResult = iyzicoPaymentService.pay(seat.getPrice());

                if ("Payment successful".equals(paymentResult)) {
                    return "Payment successful";
                } else {
                    seat.setStatus(SeatStatus.AVAILABLE);
                    seatRepository.save(seat);
                    return paymentResult;
                }
            } else {
                return "Seat is already booked!";
            }
        } catch (ObjectOptimisticLockingFailureException e) {
            return "Seat is already sold";
        }
    }
}

