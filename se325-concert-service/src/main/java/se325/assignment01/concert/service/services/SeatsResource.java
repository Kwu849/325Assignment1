package se325.assignment01.concert.service.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.Seat;
import se325.assignment01.concert.service.jaxrs.LocalDateTimeParam;
import se325.assignment01.concert.service.mapper.SeatMapper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Path("concert-service/seats")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SeatsResource {

    private EntityManager em = PersistenceManager.instance().createEntityManager();
    private static Logger LOGGER = LoggerFactory.getLogger(SeatsResource.class);

    /**
     * Retrieves the seats for the concert on the date specified with the booking status specified
     * @param dateTimeParam date of the concert
     * @param status booking status, Booked, Unbooked, Any
     * @return Ok http response with a lists of seat DTOs
     */
    @GET
    @Path("{date}")
    public Response getSeat(@PathParam("date") LocalDateTimeParam dateTimeParam, @QueryParam("status") String status){
        LocalDateTime date = dateTimeParam.getLocalDateTime();

        List<Seat> bookedSeats = new ArrayList<>();
        if(status.equals("Any")){
            // Query for all seats for a date
            TypedQuery<Seat> q = em.createQuery("SELECT s FROM Seat s " +
                    "WHERE s.date = :date ", Seat.class)
                    .setParameter("date",date);
            bookedSeats = q.getResultList();
        }else{
            boolean booked = false;
            if(status.equals("Booked")){
                booked = true;
            }
            // Query booked or unbooked seats depending on the query parameter
            TypedQuery<Seat> q = em.createQuery("SELECT s FROM Seat s " +
                    "WHERE s.date = :date " +
                    "AND s.isBooked = :status", Seat.class)
                    .setParameter("date",date)
                    .setParameter("status",booked);
            bookedSeats = q.getResultList();
        }

        List<SeatDTO> seatDTOS = new ArrayList<>();
        for(Seat seat:bookedSeats){
            seatDTOS.add(SeatMapper.toDTO(seat));
        }
        GenericEntity<List<SeatDTO>> entity = new GenericEntity<List<SeatDTO>>(seatDTOS) {};

        Response.ResponseBuilder builder = Response.ok(entity);
        return builder.build();
    }
}
