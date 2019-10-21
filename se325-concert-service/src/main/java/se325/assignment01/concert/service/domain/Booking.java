package se325.assignment01.concert.service.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The booking entity contains references to the concert, date, the user who make the booking and the seats booked
 */
@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long concertId;
    private LocalDateTime date;

    /**
     * A booking can only be associated with one user, but one user can have multiple bookings
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    /**
     * A booking can consist of multiple seats
     */
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Seat> seats;

    public Booking() {
    }

    public Booking(long concertId, LocalDateTime date, List<Seat> seats, User user) {
        this.concertId = concertId;
        this.date = date;
        this.seats = seats;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getConcertId() {
        return concertId;
    }

    public void setConcertId(long concertId) {
        this.concertId = concertId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
}
