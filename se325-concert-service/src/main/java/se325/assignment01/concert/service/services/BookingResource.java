package se325.assignment01.concert.service.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.assignment01.concert.common.dto.BookingDTO;
import se325.assignment01.concert.common.dto.BookingRequestDTO;
import se325.assignment01.concert.common.dto.ConcertInfoNotificationDTO;
import se325.assignment01.concert.common.dto.ConcertInfoSubscriptionDTO;
import se325.assignment01.concert.service.domain.Booking;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.Seat;
import se325.assignment01.concert.service.domain.Token;
import se325.assignment01.concert.service.mapper.BookingMapper;
import se325.assignment01.concert.service.mapper.ConcertMapper;
import se325.assignment01.concert.service.util.TheatreLayout;

import javax.persistence.*;
import javax.swing.text.html.parser.Entity;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Path("concert-service/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingResource {

    private EntityManager em = PersistenceManager.instance().createEntityManager();
    private static Logger LOGGER = LoggerFactory.getLogger(BookingResource.class);
    public final String USER_COOKIE = "auth";

    /**
     * Attempts to make a booking for the details specified in bookingRequest
     * @param bookingRequest contains the id and date of the concert and the labels of the seats to be booked
     * @param cookie used to authenticate the user
     * @return
     */
    @POST
    public Response makeBooking(BookingRequestDTO bookingRequest, @CookieParam(USER_COOKIE) Cookie cookie){

        Booking booking = BookingMapper.requestToDomain(bookingRequest);
        Concert concert = em.find(Concert.class, bookingRequest.getConcertId());
        if(concert == null || !concert.getDates().contains(bookingRequest.getDate())){
            Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
            return builder.build();
        }
        checkCookie(cookie);
        try {
            Token token = em.find(Token.class,cookie.getValue());
            if (token == null){
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
            em.getTransaction().begin();
            List<Seat> seats = new ArrayList<>();
            for (String label : bookingRequest.getSeatLabels()) {
                // Query to find the seats requested
                TypedQuery<Seat> seatQuery = em.createQuery("SELECT s FROM Seat s " +
                        "WHERE s.date = :date " +
                        "AND s.label = :label ", Seat.class)
                        .setParameter("date",bookingRequest.getDate())
                        .setParameter("label",label).setLockMode(LockModeType.OPTIMISTIC);
                Seat currentSeat = seatQuery.getSingleResult();
                if (currentSeat.getIsBooked()) {
                    throw new WebApplicationException(Response.Status.FORBIDDEN);
                } else {
                    currentSeat.setIsBooked(true);
                    seats.add(currentSeat);
                    em.merge(currentSeat);
                }
            }
            em.getTransaction().commit();

            em.getTransaction().begin();
            booking.setUser(token.getUser());
            booking.setSeats(seats);
            em.persist(booking);
            em.getTransaction().commit();
            checkNotification(concert);
        }catch (OptimisticLockException e){
            em.close();
            makeBooking(bookingRequest,cookie);
        }
        finally{
            em.close();
        }

        Response.ResponseBuilder builder = Response.created(URI.create("/concert-service/bookings/"+booking.getId()));
        return builder.build();

    }

    /**
     * Find the booking for a user based on the id of the booking
     * @param id
     * @param cookie used to authenticate the user
     * @return
     */
    @GET
    @Path("{id}")
    public Response getBooking(@PathParam("id") long id, @CookieParam(USER_COOKIE) Cookie cookie){

        checkCookie(cookie);
        try {
            // Checks for invalid concert, login and if booking belongs to the user
            Token token = em.find(Token.class,cookie.getValue());
            Booking booking = em.find(Booking.class, id);
            if (booking == null) {
                Response.ResponseBuilder builder = Response.status(Response.Status.NOT_FOUND);
                return builder.build();
            }
            if (token == null){
                Response.ResponseBuilder builder = Response.status(Response.Status.UNAUTHORIZED);
                return builder.build();
            }
            if (!token.getUser().equals(booking.getUser())){
                Response.ResponseBuilder builder = Response.status(Response.Status.FORBIDDEN);
                return builder.build();
            }

            Response.ResponseBuilder builder = Response.ok(BookingMapper.toDTO(booking));
            return builder.build();

        } finally {
            em.close();
        }
    }

    /**
     * Finds all the bookings made by a single user
     * @param cookie used to authenticate the user
     * @return
     */
    @GET
    public Response getAllBookings(@CookieParam("auth")Cookie cookie) {

        checkCookie(cookie);
        try {
            Token token = em.find(Token.class,cookie.getValue());
            if (token == null){
                Response.ResponseBuilder builder = Response.status(Response.Status.UNAUTHORIZED);
                return builder.build();
            }
            TypedQuery<Booking> q = em.createQuery("SELECT b FROM Booking b " +
                    "WHERE b.user = :user", Booking.class)
                    .setParameter("user",token.getUser());
            List<Booking> bookings = q.getResultList();
            List<BookingDTO> bookingDTOS = new ArrayList<>();
            for(Booking booking:bookings){
                bookingDTOS.add(BookingMapper.toDTO(booking));
            }
            GenericEntity<List<BookingDTO>> entity = new GenericEntity<List<BookingDTO>>(bookingDTOS) {};

            Response.ResponseBuilder builder = Response.ok(entity);
            return builder.build();

        } finally {
            em.close();
        }
    }

    /**
     * Checks if a concert is subscribed to and if the number of remaining seats has reached the requested percentage
     * and sends an asynchronous notification if it has.
     * @param concert
     */
    private void checkNotification(Concert concert){
        for(ConcertInfoSubscriptionDTO sub:SubscriptionResource.subscriptions){
            // check if concert has subscribed to
            if(concert.getId().equals(sub.getConcertId()) && concert.getDates().contains(sub.getDate())){
                // Query number of seats booked
                TypedQuery<Seat> seatQuery = em.createQuery("SELECT s FROM Seat s " +
                        "WHERE s.date = :date " +
                        "AND s.isBooked = true", Seat.class)
                        .setParameter("date",sub.getDate());
                List<Seat> bookedSeats = seatQuery.getResultList();
                if (bookedSeats.size() > TheatreLayout.NUM_SEATS_IN_THEATRE*sub.getPercentageBooked()/100){
                    SubscriptionResource.notifySellOut(
                            new ConcertInfoNotificationDTO(
                                    TheatreLayout.NUM_SEATS_IN_THEATRE-bookedSeats.size()));
                }
            }
        }
    }

    /**
     * Checks if there is a cookie attached to the message
     * @param cookie used to authenticate the user
     */
    private void checkCookie(Cookie cookie){
        if(cookie == null){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }
}
