package se325.assignment01.concert.service.services;

import se325.assignment01.concert.common.dto.UserDTO;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.Token;
import se325.assignment01.concert.service.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.UUID;

@Path("concert-service/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private EntityManager em = PersistenceManager.instance().createEntityManager();
    private static final long TOKEN_TIMEOUT = 5;

    /**
     * Handles login authentication of the user
     * @param userDTO contains the username and password for the user
     * @return Unauthorized http response if login fail, Ok http response if success
     */
    @POST
    public Response login(UserDTO userDTO) {
        try {
            // checks correct login information
            TypedQuery<User> q = em.createQuery("SELECT u FROM User u " +
                    "WHERE u.password = :password " +
                    "AND u.username = :username", User.class)
                    .setParameter("password", userDTO.getPassword())
                    .setParameter("username", userDTO.getUsername());
            User user = q.getSingleResult();
            if (user == null) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }

            em.getTransaction().begin();
            Token token = em.find(Token.class, user.getUsername());
            String tokenID;
            // check if token exists, retrieves token if exists, generate new token if not
            if(token == null || LocalDateTime.now().isAfter(token.getExpiry())){
                if(token != null){
                    em.remove(token);
                }
                tokenID = generateUserToken();
                em.persist(new Token(user,tokenID,LocalDateTime.now().plusMinutes(TOKEN_TIMEOUT)));
                em.getTransaction().commit();
            }else {
                tokenID = token.getToken();
            }

            NewCookie cookie = new NewCookie("auth",tokenID);
            Response.ResponseBuilder builder = Response.ok().cookie(cookie);
            return builder.build();
        } catch (NoResultException e){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        } finally {
            em.close();
        }
    }

    /**
     * Generate a token string
     * @return token string
     */
    private String generateUserToken() {
        return UUID.randomUUID().toString();
    }
}
