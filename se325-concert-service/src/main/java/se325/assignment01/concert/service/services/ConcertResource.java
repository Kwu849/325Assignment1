package se325.assignment01.concert.service.services;

import se325.assignment01.concert.common.dto.ConcertDTO;
import se325.assignment01.concert.common.dto.ConcertSummaryDTO;
import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.Performer;
import se325.assignment01.concert.service.mapper.ConcertMapper;
import se325.assignment01.concert.service.mapper.PerformerMapper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("concert-service/concerts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConcertResource {

    private EntityManager em = PersistenceManager.instance().createEntityManager();

    /**
     * Retrieves the concert with the given id
     * @param id id of the concert
     * @return Status 404 if not found, OK status with concert DTO is found
     */
    @GET
    @Path("{id}")
    public Response getConcert(@PathParam("id") long id) {
        try {

            Concert concert = em.find(Concert.class, id);
            if (concert == null) {
                // Return a HTTP 404 response if the specified Concert isn't found.
                Response.ResponseBuilder builder = Response.status(Response.Status.NOT_FOUND);
                return builder.build();
            }

            Response.ResponseBuilder builder = Response.ok(ConcertMapper.toDTO(concert));
            return builder.build();

        } finally {
            em.close();
        }
    }

    /**
     * Retrieves all the concerts in the database
     * @return OK status with list of concert DTOs
     */
    @GET
    public Response getAllConcerts() {
        try {

            TypedQuery<Concert> q = em.createQuery("SELECT c FROM Concert c", Concert.class);
            List<Concert> concerts = q.getResultList();
            List<ConcertDTO> concertDTOS = new ArrayList<>();
            for(Concert concert:concerts){
                concertDTOS.add(ConcertMapper.toDTO(concert));
            }
            GenericEntity<List<ConcertDTO>> entity = new GenericEntity<List<ConcertDTO>>(concertDTOS) {};

            Response.ResponseBuilder builder = Response.ok(entity);
            return builder.build();

        } finally {
            em.close();
        }
    }

    /**
     * Retrieves summary of  all the concerts in the database
     * @return OK status with list of concert summary DTOs
     */
    @GET
    @Path("/summaries")
    public Response getConcertSummary() {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            TypedQuery<Concert> q = em.createQuery("SELECT c FROM Concert c", Concert.class);
            List<Concert> concerts = q.getResultList();
            List<ConcertSummaryDTO> concertSummaries = new ArrayList<>();
            for(Concert concert:concerts){
                concertSummaries.add(ConcertMapper.toSummary(concert));
            }
            GenericEntity<List<ConcertSummaryDTO>> entity = new GenericEntity<List<ConcertSummaryDTO>>(concertSummaries) {};

            Response.ResponseBuilder builder = Response.ok(entity);
            return builder.build();

        } finally {
            em.close();
        }
    }

}
