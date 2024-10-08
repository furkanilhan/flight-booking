package com.furkan.booking.service;

import com.furkan.booking.dto.FlightDTO;
import com.furkan.booking.entity.Flight;
import com.furkan.booking.exception.CustomException;
import com.furkan.booking.mapper.FlightMapper;
import com.furkan.booking.repository.FlightRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;

    public FlightService(FlightRepository flightRepository, FlightMapper flightMapper) {
        this.flightRepository = flightRepository;
        this.flightMapper = flightMapper;
    }

    @Transactional
    public FlightDTO saveFlight(FlightDTO flightDTO) {
        try {
            Flight flight = flightMapper.toFlight(flightDTO);
            Flight savedFlight = flightRepository.save(flight);
            return flightMapper.toFlightDTO(savedFlight);
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save flight!");
        }
    }

    @Transactional
    public Optional<FlightDTO> updateFlight(Long id, FlightDTO flightDTO) {
        try {
            return flightRepository.findById(id).map(existingFlight -> {
                existingFlight.setName(flightDTO.getName());
                existingFlight.setDescription(flightDTO.getDescription());
                Flight updatedFlight = flightRepository.save(existingFlight);
                return flightMapper.toFlightDTO(updatedFlight);
            });
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save flight!");
        }
    }

    @Transactional
    public void deleteFlight(Long flightId) {
        try {
            flightRepository.deleteById(flightId);
        } catch (Exception ex) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete flight!");
        }
    }

    public List<FlightDTO> getAllFlights() {
        List<Flight> flights = flightRepository.findAll();
        return flights.stream().map(flightMapper::toFlightDTO).collect(Collectors.toList());
    }

    public Optional<FlightDTO> getFlightById(Long flightId) {
        return flightRepository.findById(flightId).map(flightMapper::toFlightDTO);
    }
}

