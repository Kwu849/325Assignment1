package se325.assignment01.concert.service.services;

import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.service.domain.Performer;
import se325.assignment01.concert.service.mapper.PerformerMapper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("concert-service/performers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PerformerResource {

    private EntityManager em = PersistenceManager.instance().createEntityManager();

    /**
     * Retrieves the performer with the id specified
     * @param id id of the performer
     * @return Status 404 if not found, OK status with performer DTO is found
     */
    @GET
    @Path("{id}")
    public Response getPerformer(@PathParam("id") long id) {
        try {

            Performer performer = em.find(Performer.class, id);
            if (performer == null) {
                // Return a HTTP 404 response if the specified Concert isn't found.
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            Response.ResponseBuilder builder = Response.ok(PerformerMapper.toDTO(performer));
            return builder.build();

        } finally {
            em.close();
        }
    }

    /**
     * Retrieves all the performers in the database
     * @return OK status with list of performer DTOs
     */
    @GET
    public Response getAllPerformers() {

        try {

            TypedQuery<Performer> q = em.createQuery("SELECT p FROM Performer p", Performer.class);
            List<Performer> performers = q.getResultList();
            List<PerformerDTO> performerDTOS = new ArrayList<>();
            for(Performer performer:performers){
                performerDTOS.add(PerformerMapper.toDTO(performer));
            }
            GenericEntity<List<PerformerDTO>> entity = new GenericEntity<List<PerformerDTO>>(performerDTOS) {};

            Response.ResponseBuilder builder = Response.ok(entity);
            return builder.build();

        } finally {
            em.close();
        }
    }
}
