package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.ConcertDTO;
import se325.assignment01.concert.common.dto.ConcertSummaryDTO;
import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.Performer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConcertMapper {
    /**
     * Converts concert object to DTO for transporting across network
     * @param concert
     * @return ConcertDTO
     */
    public static ConcertDTO toDTO(Concert concert) {

        List<PerformerDTO> performerDTOS = new ArrayList<>();
        for(Performer performer:concert.getPerformers()){
            performerDTOS.add(PerformerMapper.toDTO(performer));
        }
        ConcertDTO concertDTO = new ConcertDTO(
                concert.getId(),
                concert.getTitle(),
                concert.getImageName(),
                concert.getBlurb()
        );
        List<LocalDateTime> dates = new ArrayList<>();
        dates.addAll(concert.getDates());
        concertDTO.setDates(dates);
        concertDTO.setPerformers(performerDTOS);

        return concertDTO;
    }
    /**
     * Translates a concert to a simpler concert summary DTO to send to client
     * @param concert
     * @return ConcertSummaryDTO
     */
    public static ConcertSummaryDTO toSummary(Concert concert) {

        return new ConcertSummaryDTO(
                concert.getId(),
                concert.getTitle(),
                concert.getImageName()
        );
    }
}
