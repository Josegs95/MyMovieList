package service;

import config.HibernateUtil;
import dao.UserDAO;
import dao.UserDAOImpl;
import exception.BadCredentialsException;
import model.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import security.Security;

import java.util.Random;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAOImpl();
    }

    public void register(String username, String password, String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();

                User user = userDAO.findByUsername(session, username);
                if (user != null) {
                    throw new RuntimeException("That username is already in use.");
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

    public Integer login(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();

                User user = userDAO.findByUsername(session, username);
                if (user == null) {
                    throw new BadCredentialsException("It doesn't exists a user with that username.");
                }

                if (!user.getPassword().equals(Security.hashString(password, user.getSessionToken()))) {
                    throw new BadCredentialsException("Wrong password");
                }
                transaction.commit();

                return user.getSessionToken();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public boolean validateUser(Integer id, String sessionToken) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            User user = userDAO.findById(session, id);
            if (user == null) {
                throw new RuntimeException("It doesn't exists a user with that id.");
            }

            if (user.getSessionToken() != Integer.parseInt(sessionToken)) {
                throw new RuntimeException("Wrong credentials, forbidden");
            }
            transaction.commit();

            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return false;
        }
    }
}
