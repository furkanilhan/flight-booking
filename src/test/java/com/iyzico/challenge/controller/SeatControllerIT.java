package com.iyzico.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iyzico.challenge.dto.SeatDTO;
import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.repository.SeatRepository;
import com.iyzico.challenge.repository.FlightRepository;
import com.iyzico.challenge.mapper.SeatMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SeatControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private SeatMapper seatMapper;

    private SeatDTO seatDTO;

    @Before
    public void setup() {
        seatDTO = new SeatDTO();
        seatDTO.setSeatNumber("A1");
        seatDTO.setAvailable(true);
        seatDTO.setPrice(BigDecimal.valueOf(100.0));
    }

    @Test
    public void testAddSeat() throws Exception {
        mockMvc.perform(post("/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seatDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.seatNumber").value(seatDTO.getSeatNumber()));
    }

    @Test
    public void testDeleteSeat() throws Exception {
        Long seatId = seatRepository.save(seatMapper.toSeat(seatDTO)).getId();
        mockMvc.perform(delete("/seats/{id}", seatId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetSeatsByFlightId() throws Exception {
        Flight flight = new Flight();
        flight.setName("Test Flight");
        Flight savedFlight = flightRepository.save(flight);

        seatDTO.setFlightId(savedFlight.getId());
        Seat seat = seatMapper.toSeat(seatDTO);
        seat.setFlight(savedFlight);
        seatRepository.save(seat);

        mockMvc.perform(get("/seats/flight/{flightId}", savedFlight.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seatNumber").value(seatDTO.getSeatNumber()));
    }

    @Test
    public void testGetSeatById() throws Exception {
        Long seatId = seatRepository.save(seatMapper.toSeat(seatDTO)).getId();
        mockMvc.perform(get("/seats/{id}", seatId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatNumber").value(seatDTO.getSeatNumber()));
    }

    @Test
    public void testUpdateSeatAvailability() throws Exception {
        Long seatId = seatRepository.save(seatMapper.toSeat(seatDTO)).getId();
        mockMvc.perform(patch("/seats/{id}/availability", seatId)
                        .param("availability", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    public void testUpdateSeat() throws Exception {
        Long seatId = seatRepository.save(seatMapper.toSeat(seatDTO)).getId();
        SeatDTO updatedSeatDTO = new SeatDTO();
        updatedSeatDTO.setSeatNumber("A2");
        updatedSeatDTO.setAvailable(false);
        updatedSeatDTO.setPrice(BigDecimal.valueOf(150.0));

        mockMvc.perform(put("/seats/{id}", seatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSeatDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatNumber").value(updatedSeatDTO.getSeatNumber()));
    }
}
