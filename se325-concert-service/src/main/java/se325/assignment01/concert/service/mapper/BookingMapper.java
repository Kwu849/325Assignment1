package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.BookingDTO;
import se325.assignment01.concert.common.dto.BookingRequestDTO;
import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.service.domain.Booking;
import se325.assignment01.concert.service.domain.Seat;
import se325.assignment01.concert.service.util.TheatreLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BookingMapper {

    /**
     * Converts booking object to DTO for transporting across network
     * @param booking
     * @return BookingDTO
     */
    public static BookingDTO toDTO (Booking booking){
        List<SeatDTO> seatDTOS = new ArrayList<>();
        for(Seat seat:booking.getSeats()){
            seatDTOS.add(SeatMapper.toDTO(seat));
        }
        return new BookingDTO(
                booking.getConcertId(),
                booking.getDate(),
                seatDTOS
        );
    }

    /**
     * Translates a booking request to a booking object, seats and user field will be entered by the makeBooking method
     * in BookingResource
     * @param bookingRequestDTO
     * @return Booking
     */
    public static Booking requestToDomain (BookingRequestDTO bookingRequestDTO){
        return new Booking(
                bookingRequestDTO.getConcertId(),
                bookingRequestDTO.getDate(),
                null,
                null
        );
    }
}
