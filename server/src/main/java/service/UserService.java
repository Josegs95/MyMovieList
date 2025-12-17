package service;

import config.HibernateUtil;
import dao.UserDAO;
import exception.AuthenticationException;
import exception.ConflictException;
import model.dto.UserDTO;
import model.entity.ClientSession;
import model.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import security.Security;

import java.util.Random;

public class UserService {

    private final UserDAO userDAO;
    private final AuthService authService;

    public UserService(UserDAO userDAO, AuthService authService) {
        this.userDAO = userDAO;
        this.authService = authService;
    }

    public void register(String username, String password, String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();

                User user = userDAO.findByUsername(session, username);
                if (user != null) {
                    throw new ConflictException("Ya hay un usuario registrado con ese nombre de usuario");
                }

                int salt = new Random().nextInt();
                String securedPassword = Security.hashString(password, salt);
                user = new User(username, securedPassword, email, salt);

                userDAO.create(session, user);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public UserDTO login(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();

                User user = userDAO.findByUsername(session, username);
                if (user == null) {
                    throw new AuthenticationException("No existe el usuario \"" + username + "\"");
                }

                if (!user.getPassword().equals(Security.hashString(password, user.getSalt()))) {
                    throw new AuthenticationException("Contraseña incorrecta");
                }
                ClientSession clientSession = authService.createSession(session, user);

                UserDTO userDTO = new UserDTO(user);
                userDTO.setSessionToken(clientSession.getToken());
                transaction.commit();

                return userDTO;
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }
}
