package com.iyzico.challenge.service;

import com.iyzico.challenge.dto.SeatDTO;
import com.iyzico.challenge.entity.Seat;
import com.iyzico.challenge.exception.CustomException;
import com.iyzico.challenge.mapper.SeatMapper;
import com.iyzico.challenge.repository.SeatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        try {
            Seat seat = seatMapper.toSeat(seatDTO);
            Seat savedSeat = seatRepository.save(seat);
            return seatMapper.toSeatDTO(savedSeat);
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save seat!");
        }
    }

    public void deleteSeat(Long seatId) {
        try {
            seatRepository.deleteById(seatId);
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete seat!");
        }
    }

    public List<SeatDTO> getSeatsByFlightId(Long flightId) {
        try {
            List<Seat> seats = seatRepository.findAllByFlightId(flightId);
            if (seats.isEmpty()) {
                throw new CustomException(HttpStatus.NOT_FOUND, "No seats found for the given flight ID");
            }
            return seats.stream().map(seatMapper::toSeatDTO).collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving seats", e);
        }
    }

    public Optional<SeatDTO> getSeatById(Long seatId) {
        return seatRepository.findById(seatId).map(seatMapper::toSeatDTO);
    }

    public SeatDTO updateSeatAvailability(Long seatId, boolean availability) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Seat not found"));

        try {
            seat.setAvailable(availability);
            Seat updatedSeat = seatRepository.save(seat);
            return seatMapper.toSeatDTO(updatedSeat);
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update seat!");
        }
    }

    @Transactional
    public Optional<SeatDTO> updateSeat(Long seatId, SeatDTO seatDTO) {
       try {
            return seatRepository.findById(seatId).map(existingSeat -> {
                existingSeat.setSeatNumber(seatDTO.getSeatNumber());
                existingSeat.setAvailable(seatDTO.isAvailable());
                existingSeat.setPrice(seatDTO.getPrice());
                existingSeat.setFlight(seatMapper.toSeat(seatDTO).getFlight());
                Seat updatedSeat = seatRepository.save(existingSeat);
                return seatMapper.toSeatDTO(updatedSeat);
            });
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update seat!");
        }
    }
}
