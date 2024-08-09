package com.furkan.booking.service;

import com.furkan.booking.entity.Seat;
import com.furkan.booking.enums.SeatStatus;
import com.furkan.booking.exception.CustomException;
import com.furkan.booking.repository.SeatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentSemaphoreService paymentSemaphoreService;
    private final SeatRepository seatRepository;

    public PaymentService(PaymentSemaphoreService paymentSemaphoreService, SeatRepository seatRepository) {
        this.paymentSemaphoreService = paymentSemaphoreService;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public void processPayment(Long seatId) {
        try {
            Seat seat = seatRepository.findByIdWithLock(seatId).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Seat not found"));
            if (seat.getStatus() == SeatStatus.AVAILABLE) {
                seat.setStatus(SeatStatus.BOOKED);
                seatRepository.save(seat);

                paymentSemaphoreService.pay(seat.getPrice());
            } else {
                throw new CustomException( HttpStatus.BAD_REQUEST, "Seat is already booked!");
            }
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new CustomException(HttpStatus.CONFLICT, "Seat is already sold");
        }
    }
}

