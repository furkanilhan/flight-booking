package com.furkan.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.furkan.booking.dto.FlightDTO;
import com.furkan.booking.entity.Flight;
import com.furkan.booking.repository.FlightRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FlightControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightRepository flightRepository;

    private Flight flight;

    @Before
    public void setUp() {
        flightRepository.deleteAll();
        flight = new Flight();
        flight.setName("Integration Test Flight");
        flight.setDescription("Description");
        flightRepository.save(flight);
    }

    @Test
    public void testAddFlight() throws Exception {
        FlightDTO flightDTO = new FlightDTO();
        flightDTO.setName("New Flight");

        mockMvc.perform(post("/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(flightDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Flight"));

        List<Flight> flights = flightRepository.findAll();
        assertEquals(2, flights.size());
    }

    @Test
    public void testUpdateFlight() throws Exception {
        flight.setName("Updated Flight");
        FlightDTO flightDTO = new FlightDTO();
        flightDTO.setName("Updated Flight");

        mockMvc.perform(put("/flights/" + flight.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(flightDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Flight"));

        Flight updatedFlight = flightRepository.findById(flight.getId()).get();
        assertEquals("Updated Flight", updatedFlight.getName());
    }

    @Test
    public void testDeleteFlight() throws Exception {
        mockMvc.perform(delete("/flights/" + flight.getId()))
                .andExpect(status().isNoContent());

        assertFalse(flightRepository.findById(flight.getId()).isPresent());
    }

    @Test
    public void testGetAllFlights() throws Exception {
        mockMvc.perform(get("/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Integration Test Flight"));

        List<Flight> flights = flightRepository.findAll();
        assertEquals(1, flights.size());
    }

    @Test
    public void testGetFlightById() throws Exception {
        mockMvc.perform(get("/flights/" + flight.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Test Flight"));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
