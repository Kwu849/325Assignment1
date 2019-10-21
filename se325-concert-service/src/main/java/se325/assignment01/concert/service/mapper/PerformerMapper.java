package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.service.domain.Performer;

public class PerformerMapper {
    /**
     * Converts performer object to DTO for transporting across network
     * @param performer
     * @return PerformerDTO
     */
    public static PerformerDTO toDTO(Performer performer) {

        return new PerformerDTO(
                performer.getId(),
                performer.getName(),
                performer.getImageName(),
                performer.getGenre(),
                performer.getBlurb()
        );
    }
}
