package com.iyzico.challenge.repository;

import com.iyzico.challenge.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findAllByFlightId(Long flightId);
}
