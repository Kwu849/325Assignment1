package se325.assignment01.concert.service.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Token entity is used to store the authentication string value and the user it is assigned to
 */
@Entity
public class Token {

    public Token() {}

    public Token(User user, String token, LocalDateTime timestamp) {
        this.user = user;
        this.token = token;
        this.expiry = timestamp;
    }

    /**
     * One token can only refer to one user and one user can only have one authentication token
     */
    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Id
    private String token;

    private LocalDateTime expiry;


    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpiry() {
        return expiry;
    }
}
