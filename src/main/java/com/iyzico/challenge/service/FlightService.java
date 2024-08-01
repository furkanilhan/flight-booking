package com.iyzico.challenge.service;

import com.iyzico.challenge.dto.FlightDTO;
import com.iyzico.challenge.entity.Flight;
import com.iyzico.challenge.exception.CustomException;
import com.iyzico.challenge.mapper.FlightMapper;
import com.iyzico.challenge.repository.FlightRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;

    public FlightService(FlightRepository flightRepository, FlightMapper flightMapper) {
        this.flightRepository = flightRepository;
        this.flightMapper = flightMapper;
    }

    public FlightDTO saveFlight(FlightDTO flightDTO) {
        try {
            Flight flight = flightMapper.toFlight(flightDTO);
            Flight savedFlight = flightRepository.save(flight);
            return flightMapper.toFlightDTO(savedFlight);
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save flight!");
        }
    }

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

