package com.iyzico.challenge.service;

import com.iyzico.challenge.dto.SeatDTO;
import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.mapper.SeatMapper;
import com.iyzico.challenge.repository.SeatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeatService {

    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;

    public SeatService(SeatRepository seatRepository, SeatMapper seatMapper) {
        this.seatRepository = seatRepository;
        this.seatMapper = seatMapper;
    }

    public SeatDTO saveSeat(SeatDTO seatDTO) {
        Seat seat = seatMapper.toSeat(seatDTO);
        Seat savedSeat = seatRepository.save(seat);
        return seatMapper.toSeatDTO(savedSeat);
    }

    public void deleteSeat(Long seatId) {
        seatRepository.deleteById(seatId);
    }

    public List<SeatDTO> getSeatsByFlightId(Long flightId) {
        List<Seat> seats = seatRepository.findAllByFlightId(flightId);
        return seats.stream().map(seatMapper::toSeatDTO).collect(Collectors.toList());
    }

    public Optional<SeatDTO> getSeatById(Long seatId) {
        return seatRepository.findById(seatId).map(seatMapper::toSeatDTO);
    }

    public SeatDTO updateSeatAvailability(Long seatId, boolean availability) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found"));
        seat.setAvailable(availability);
        Seat updatedSeat = seatRepository.save(seat);
        return seatMapper.toSeatDTO(updatedSeat);
    }
}

