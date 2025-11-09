package service;

import dao.UserDAO;
import dao.UserDAOImpl;
import model.entity.User;
import org.hibernate.Session;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAOImpl();
    }

    public User authenticate(Session session, Long idUser, Integer sessionToken) {
        User user = userDAO.findBySessionToken(session, sessionToken);
        if (user == null) {
            throw new RuntimeException("Invalid session token");
        }
        if (!idUser.equals(user.getId())) {
            throw new RuntimeException("User does not own this resource");
        }

        return user;
    }
}
