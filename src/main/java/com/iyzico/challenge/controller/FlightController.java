package com.iyzico.challenge.controller;

import com.iyzico.challenge.dto.FlightDTO;
import com.iyzico.challenge.service.FlightService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping
    public ResponseEntity<FlightDTO> addFlight(@RequestBody FlightDTO flightDTO) {
        FlightDTO savedFlight = flightService.saveFlight(flightDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFlight);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<FlightDTO>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightDTO> getFlightById(@PathVariable Long id) {
        return flightService.getFlightById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

