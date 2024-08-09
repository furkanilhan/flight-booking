package com.furkan.booking.mapper;

import com.furkan.booking.dto.FlightDTO;
import com.furkan.booking.entity.Flight;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SeatMapper.class})
public interface FlightMapper {

    @Mapping(source = "seats", target = "seats")
    FlightDTO toFlightDTO(Flight flight);

    @Mapping(source = "seats", target = "seats")
    Flight toFlight(FlightDTO flightDTO);
}
