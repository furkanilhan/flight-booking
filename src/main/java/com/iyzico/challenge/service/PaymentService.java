package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.enums.SeatStatus;
import com.iyzico.challenge.exception.CustomException;
import com.iyzico.challenge.repository.SeatRepository;
import org.springframework.http.HttpStatus;
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
    public void processPayment(Long seatId) {
        try {
            Seat seat = seatRepository.findByIdWithLock(seatId).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Seat not found"));
            if (seat.getStatus() == SeatStatus.AVAILABLE) {
                seat.setStatus(SeatStatus.BOOKED);
                seatRepository.save(seat);

                iyzicoPaymentService.pay(seat.getPrice());
            } else {
                throw new CustomException( HttpStatus.BAD_REQUEST, "Seat is already booked!");
            }
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new CustomException(HttpStatus.CONFLICT, "Seat is already sold");
        }
    }
}

