package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.service.domain.Seat;

public class SeatMapper {
    /**
     * Converts seat object to DTO for transporting across network
     * @param seat
     * @return SeatDTO
     */
    public static SeatDTO toDTO(Seat seat) {
        return new SeatDTO(
                seat.getLabel(),
                seat.getPrice()
        );
    }

}
