package com.furkan.booking.mapper;

import com.furkan.booking.dto.SeatDTO;
import com.furkan.booking.entity.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    @Mapping(source = "flight.id", target = "flightId")
    SeatDTO toSeatDTO(Seat seat);

    @Mapping(source = "flightId", target = "flight.id")
    Seat toSeat(SeatDTO seatDTO);
}
