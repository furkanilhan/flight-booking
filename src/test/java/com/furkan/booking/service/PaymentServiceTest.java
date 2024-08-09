package com.furkan.booking.service;

import com.furkan.booking.entity.Seat;
import com.furkan.booking.enums.SeatStatus;
import com.furkan.booking.exception.CustomException;
import com.furkan.booking.repository.SeatRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceTest {

    @Mock
    private PaymentSemaphoreService paymentSemaphoreService;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Seat availableSeat;
    private Seat bookedSeat;

    @Before
    public void setUp() {
        availableSeat = new Seat();
        availableSeat.setId(1L);
        availableSeat.setStatus(SeatStatus.AVAILABLE);
        availableSeat.setPrice(new BigDecimal("100.0"));

        bookedSeat = new Seat();
        bookedSeat.setId(2L);
        bookedSeat.setStatus(SeatStatus.BOOKED);
        bookedSeat.setPrice(new BigDecimal("100.0"));
    }

    @Test
    public void processPayment_successful() {
        when(seatRepository.findByIdWithLock(1L)).thenReturn(Optional.of(availableSeat));
        doNothing().when(paymentSemaphoreService).pay(any(BigDecimal.class));
        when(seatRepository.save(any(Seat.class))).thenReturn(availableSeat);

        paymentService.processPayment(1L);

        verify(seatRepository, times(1)).findByIdWithLock(1L);
        verify(seatRepository, times(1)).save(any(Seat.class));
        verify(paymentSemaphoreService, times(1)).pay(any(BigDecimal.class));
        assertEquals(SeatStatus.BOOKED, availableSeat.getStatus());
    }

    @Test
    public void processPayment_seatNotFound() {
        when(seatRepository.findByIdWithLock(anyLong())).thenReturn(Optional.empty());

        try {
            paymentService.processPayment(1L);
        } catch (CustomException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
            assertEquals("Seat not found", e.getMessage());
        }
    }

    @Test
    public void processPayment_seatAlreadyBooked() {
        when(seatRepository.findByIdWithLock(anyLong())).thenReturn(Optional.of(bookedSeat));

        try {
            paymentService.processPayment(1L);
        } catch (CustomException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
            assertEquals("Seat is already booked!", e.getMessage());
        }
    }

    @Test
    public void processPayment_optimisticLockingFailure() {
        when(seatRepository.findByIdWithLock(anyLong())).thenReturn(Optional.of(availableSeat));
        doThrow(ObjectOptimisticLockingFailureException.class).when(seatRepository).save(any(Seat.class));

        try {
            paymentService.processPayment(1L);
        } catch (CustomException e) {
            assertEquals(HttpStatus.CONFLICT, e.getStatus());
            assertEquals("Seat is already sold", e.getMessage());
        }
    }
}