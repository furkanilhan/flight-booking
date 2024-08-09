package com.furkan.booking.controller;

import com.furkan.booking.entity.Flight;
import com.furkan.booking.entity.Seat;
import com.furkan.booking.enums.SeatStatus;
import com.furkan.booking.repository.FlightRepository;
import com.furkan.booking.repository.SeatRepository;
import com.furkan.booking.service.PaymentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Test
    public void testConcurrentPaymentsForSameSeat() throws InterruptedException {
        Flight flight = new Flight();
        flight.setName("Flight 1");
        flight.setDescription("Test Flight");
        flight = flightRepository.save(flight);

        Seat seat = new Seat();
        seat.setSeatNumber("1A");
        seat.setPrice(BigDecimal.valueOf(100));
        seat.setStatus(SeatStatus.AVAILABLE);
        seat.setFlight(flight);
        seat = seatRepository.save(seat);

        CountDownLatch latch = new CountDownLatch(2);

        Seat finalSeat = seat;
        List<MvcResult> results = new ArrayList<>();
        Runnable task = () -> {
            try {
                MvcResult result = mockMvc.perform(post("/payments").param("seatId", finalSeat.getId().toString()))
                        .andReturn();
                results.add(result);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        };

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        latch.await();

        Seat updatedSeat = seatRepository.findById(seat.getId()).orElseThrow();
        assertEquals(SeatStatus.BOOKED, updatedSeat.getStatus());

        long successCount = results.stream()
                .filter(result -> result.getResponse().getStatus() == HttpStatus.OK.value())
                .count();
        long errorCount = results.stream()
                .filter(result -> result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value())
                .count();

        assertEquals(1, successCount);
        assertEquals(1, errorCount);
    }
}

