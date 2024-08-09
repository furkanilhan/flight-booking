package com.furkan.booking.service;

import com.furkan.booking.dto.FlightDTO;
import com.furkan.booking.dto.SeatDTO;
import com.furkan.booking.entity.Flight;
import com.furkan.booking.entity.Seat;
import com.furkan.booking.exception.CustomException;
import com.furkan.booking.mapper.FlightMapper;
import com.furkan.booking.mapper.SeatMapper;
import com.furkan.booking.repository.SeatRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SeatServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private SeatMapper seatMapper;

    @InjectMocks
    private SeatService seatService;

    @Mock
    private FlightMapper flightMapper;

    private Seat seat;
    private SeatDTO seatDTO;
    private Flight flight;
    private FlightDTO flightDTO;

    @Before
    public void setUp() {
        flight = new Flight();
        flight.setId(1L);

        flightDTO = new FlightDTO();
        flightDTO.setId(1L);

        seat = new Seat();
        seat.setId(1L);
        seat.setSeatNumber("A1");
        seat.setAvailable(true);
        seat.setPrice(BigDecimal.valueOf(100.0));
        seat.setFlight(flight);

        seatDTO = new SeatDTO();
        seatDTO.setSeatNumber("A1");
        seatDTO.setAvailable(false);
        seatDTO.setPrice(BigDecimal.valueOf(100.0));
        seatDTO.setFlightId(flightDTO.getId());
    }

    @Test
    public void testSaveSeat() {
        when(seatMapper.toSeat(any(SeatDTO.class))).thenReturn(seat);
        when(seatRepository.save(any(Seat.class))).thenReturn(seat);
        when(seatMapper.toSeatDTO(any(Seat.class))).thenReturn(seatDTO);

        SeatDTO result = seatService.saveSeat(seatDTO);
        assertNotNull(result);
        assertEquals(seatDTO.getSeatNumber(), result.getSeatNumber());

        verify(seatRepository, times(1)).save(any(Seat.class));
    }

    @Test(expected = CustomException.class)
    public void testSaveSeat_Exception() {
        when(seatMapper.toSeat(any(SeatDTO.class))).thenReturn(seat);
        when(seatRepository.save(any(Seat.class))).thenThrow(new RuntimeException());

        seatService.saveSeat(seatDTO);
    }

    @Test
    public void testDeleteSeat() {
        doNothing().when(seatRepository).deleteById(anyLong());
        seatService.deleteSeat(1L);
        verify(seatRepository, times(1)).deleteById(anyLong());
    }

    @Test(expected = CustomException.class)
    public void testDeleteSeat_Exception() {
        doThrow(new RuntimeException()).when(seatRepository).deleteById(anyLong());
        seatService.deleteSeat(1L);
    }

    @Test
    public void testGetSeatsByFlightId() {
        when(seatRepository.findAllByFlightId(anyLong())).thenReturn(Arrays.asList(seat));
        when(seatMapper.toSeatDTO(any(Seat.class))).thenReturn(seatDTO);

        List<SeatDTO> result = seatService.getSeatsByFlightId(1L);
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(seatRepository, times(1)).findAllByFlightId(anyLong());
    }

    @Test(expected = CustomException.class)
    public void testGetSeatsByFlightId_Exception() {
        when(seatRepository.findAllByFlightId(anyLong())).thenThrow(new RuntimeException());

        seatService.getSeatsByFlightId(1L);
    }

    @Test
    public void testGetSeatById() {
        when(seatRepository.findById(anyLong())).thenReturn(Optional.of(seat));
        when(seatMapper.toSeatDTO(any(Seat.class))).thenReturn(seatDTO);

        Optional<SeatDTO> result = seatService.getSeatById(1L);
        assertTrue(result.isPresent());
        assertEquals(seatDTO.getSeatNumber(), result.get().getSeatNumber());

        verify(seatRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testUpdateSeatAvailability() {
        when(seatRepository.findById(anyLong())).thenReturn(Optional.of(seat));
        when(seatRepository.save(any(Seat.class))).thenReturn(seat);
        when(seatMapper.toSeatDTO(any(Seat.class))).thenReturn(seatDTO);

        SeatDTO result = seatService.updateSeatAvailability(1L, false);
        assertNotNull(result);
        assertEquals(seatDTO.getSeatNumber(), result.getSeatNumber());
        assertFalse(result.isAvailable());

        verify(seatRepository, times(1)).save(any(Seat.class));
    }

    @Test(expected = CustomException.class)
    public void testUpdateSeatAvailability_Exception() {
        when(seatRepository.findById(anyLong())).thenReturn(Optional.of(seat));
        when(seatRepository.save(any(Seat.class))).thenThrow(new RuntimeException());

        seatService.updateSeatAvailability(1L, false);
    }

    @Test
    public void testUpdateSeat() {
        when(seatRepository.findById(anyLong())).thenReturn(Optional.of(seat));
        when(seatRepository.save(any(Seat.class))).thenReturn(seat);
        when(seatMapper.toSeat(any(SeatDTO.class))).thenReturn(seat);
        when(seatMapper.toSeatDTO(any(Seat.class))).thenReturn(seatDTO);

        Optional<SeatDTO> result = seatService.updateSeat(1L, seatDTO);

        assertTrue(result.isPresent());
        assertEquals(seatDTO.getSeatNumber(), result.get().getSeatNumber());

        verify(seatRepository, times(1)).findById(anyLong());
        verify(seatRepository, times(1)).save(any(Seat.class));
    }

    @Test(expected = CustomException.class)
    public void testUpdateSeat_Exception() {
        when(seatRepository.findById(anyLong())).thenReturn(Optional.of(seat));
        seatService.updateSeat(1L, seatDTO);
    }
}

