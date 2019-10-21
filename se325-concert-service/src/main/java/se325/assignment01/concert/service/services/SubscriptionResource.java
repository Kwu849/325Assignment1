package se325.assignment01.concert.service.services;

import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.assignment01.concert.common.dto.ConcertInfoNotificationDTO;
import se325.assignment01.concert.common.dto.ConcertInfoSubscriptionDTO;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.Token;
import se325.assignment01.concert.service.mapper.ConcertMapper;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Path("concert-service/subscribe")
@Produces({MediaType.APPLICATION_JSON,
        "application/java-serializable"})
@Consumes({MediaType.APPLICATION_JSON,
        "application/java-serializable"})
public class SubscriptionResource {

    private static Logger LOGGER = LoggerFactory.getLogger(SubscriptionResource.class);
    private static final List<AsyncResponse> concertSubscriptions = new Vector<>();
    public static final List<ConcertInfoSubscriptionDTO> subscriptions = new ArrayList<>();

    @POST
    @Path("/concertInfo")
    public void subscribe(@Suspended AsyncResponse sub, ConcertInfoSubscriptionDTO subInfo, @CookieParam("auth") Cookie cookie) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        Concert concert = em.find(Concert.class, subInfo.getConcertId());
        // Check for valid concert
        if(concert == null || !concert.getDates().contains(subInfo.getDate())){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if(cookie == null){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        try {
            Token token = em.find(Token.class, cookie.getValue());
            if (token == null) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
            subscriptions.add(subInfo);
            concertSubscriptions.add(sub);
        }finally {
            em.close();
        }
    }

    public static void notifySellOut(ConcertInfoNotificationDTO notificationDTO) {
        synchronized (concertSubscriptions) {
            for (AsyncResponse sub : concertSubscriptions) {
                sub.resume(notificationDTO);
            }
            concertSubscriptions.clear();
        }
    }

}
