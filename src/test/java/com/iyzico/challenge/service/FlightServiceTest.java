package com.iyzico.challenge.service;

import com.iyzico.challenge.dto.FlightDTO;
import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.mapper.FlightMapper;
import com.iyzico.challenge.repository.FlightRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FlightServiceTest {

    @InjectMocks
    private FlightService flightService;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightMapper flightMapper;

    private Flight flight;
    private FlightDTO flightDTO;

    @Before
    public void setUp() {
        flight = new Flight();
        flight.setId(1L);
        flight.setName("Test Flight");

        flightDTO = new FlightDTO();
        flightDTO.setId(1L);
        flightDTO.setName("Test Flight");
    }

    @Test
    public void testSaveFlight() {
        when(flightMapper.toFlight(any(FlightDTO.class))).thenReturn(flight);
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);
        when(flightMapper.toFlightDTO(any(Flight.class))).thenReturn(flightDTO);

        FlightDTO savedFlight = flightService.saveFlight(flightDTO);

        assertEquals("Test Flight", savedFlight.getName());
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    public void testUpdateFlight() {
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);
        when(flightMapper.toFlightDTO(any(Flight.class))).thenReturn(flightDTO);

        Optional<FlightDTO> updatedFlight = flightService.updateFlight(1L, flightDTO);

        assertTrue(updatedFlight.isPresent());
        assertEquals("Test Flight", updatedFlight.get().getName());
        verify(flightRepository, times(1)).findById(1L);
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    public void testDeleteFlight() {
        doNothing().when(flightRepository).deleteById(1L);

        flightService.deleteFlight(1L);

        verify(flightRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testGetAllFlights() {
        when(flightRepository.findAll()).thenReturn(Collections.singletonList(flight));
        when(flightMapper.toFlightDTO(any(Flight.class))).thenReturn(flightDTO);

        List<FlightDTO> flights = flightService.getAllFlights();

        assertEquals(1, flights.size());
        verify(flightRepository, times(1)).findAll();
    }

    @Test
    public void testGetFlightById() {
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightMapper.toFlightDTO(any(Flight.class))).thenReturn(flightDTO);

        Optional<FlightDTO> retrievedFlight = flightService.getFlightById(1L);

        assertTrue(retrievedFlight.isPresent());
        assertEquals("Test Flight", retrievedFlight.get().getName());
        verify(flightRepository, times(1)).findById(1L);
    }
}
