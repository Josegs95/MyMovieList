package service;

import config.HibernateUtil;
import dao.UserDAO;
import model.dto.UserDTO;
import protocol.dto.request.LoginRequest;
import protocol.dto.request.RegisterRequest;
import model.entity.User;
import exception.AuthenticationException;
import exception.ConflictException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import security.Security;

import java.util.Random;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void register(RegisterRequest request) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();

                User user = userDAO.findByUsername(session, request.username());
                if (user != null) {
                    throw new ConflictException("Ya hay un usuario registrado con ese nombre de usuario");
                }

                int salt = new Random().nextInt();
                String securedPassword = Security.hashString(request.password(), salt);
                user = new User(request.username(), securedPassword, request.email(), salt);

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

    public UserDTO login(LoginRequest request) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();

                User user = userDAO.findByUsername(session, request.username());
                if (user == null) {
                    throw new AuthenticationException("No existe el usuario \"" + request.username() + "\"");
                }

                if (!user.getPassword().equals(Security.hashString(request.password(), user.getSessionToken()))) {
                    throw new AuthenticationException("Contraseña incorrecta");
                }
                transaction.commit();

                return new UserDTO(user);
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }
}
