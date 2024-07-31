package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.enums.SeatStatus;
import com.iyzico.challenge.repository.FlightRepository;
import com.iyzico.challenge.repository.SeatRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class IyzicoPaymentServiceIT {

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
        Runnable task = () -> {
            try {
                mockMvc.perform(post("/payments/buy").param("seatId", finalSeat.getId().toString()))
                        .andExpect(status().isOk());
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
    }
}

