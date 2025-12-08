package service;

import dao.UserDAO;
import exception.AuthenticationException;
import exception.AuthorizationException;
import model.entity.User;
import org.hibernate.Session;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User authenticate(Session session, Long idUser, Integer sessionToken) {
        User user = userDAO.findBySessionToken(session, sessionToken);
        if (user == null) {
            throw new AuthenticationException("Invalid session token");
        }
        if (!idUser.equals(user.getId())) {
            throw new AuthorizationException("User does not own this resource");
        }

        return user;
    }
}
