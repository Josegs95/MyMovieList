package service;

import config.HibernateUtil;
import dao.UserDAO;
import dto.UserDTO;
import dto.request.LoginRequest;
import dto.request.RegisterRequest;
import exception.AuthenticationException;
import exception.ConflictException;
import entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import security.Security;

import java.util.Collections;
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
                    throw new ConflictException("That username is already in use.");
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
                    throw new AuthenticationException("It doesn't exists a user with that username.");
                }

                if (!user.getPassword().equals(Security.hashString(request.password(), user.getSessionToken()))) {
                    throw new AuthenticationException("Wrong password");
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
