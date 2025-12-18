package service;

import config.HibernateUtil;
import dao.ClientSessionDAO;
import dao.UserDAO;
import exception.SessionExpiredException;
import exception.SessionNotFoundException;
import exception.UserNotFoundException;
import model.entity.ClientSession;
import model.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

public class AuthService {

    private static final SecureRandom random = new SecureRandom();
    private static final int SESSION_TOKEN_LENGTH = 32;
    private static final int SESSION_TOKEN_EXPIRATION_TIME = 1;

    private final UserDAO userDAO;
    private final ClientSessionDAO sessionDAO;

    public AuthService(UserDAO userDAO, ClientSessionDAO sessionDAO) {
        this.userDAO = userDAO;
        this.sessionDAO = sessionDAO;
    }

    private String generateSessionToken() {
        byte[] randomBytes = new byte[SESSION_TOKEN_LENGTH];
        random.nextBytes(randomBytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public ClientSession createSession(Session session, User user) {
        deleteAllSessionFromUser(session, user);

        String token = generateSessionToken();
        ClientSession clientSession = new ClientSession(token, user);
        clientSession.setExpirationTime(LocalDateTime.now().plusSeconds(SESSION_TOKEN_EXPIRATION_TIME));

        return sessionDAO.create(session, clientSession);
    }

    public User validateSession(Session session, Long idUser, String sessionToken) {
        User user = userDAO.findById(session, idUser);
        if (user == null) {
            throw new UserNotFoundException("Error. ¿No existe el usuario?");
        }

        ClientSession clientSession = sessionDAO.findByTokenAndUser(session, sessionToken, user);
        if (clientSession == null) {
            throw new SessionNotFoundException("Error. No existe la sesión para este usuario");
        }
        if (!LocalDateTime.now().isBefore(clientSession.getExpirationTime())) {
            deleteExpiredSession(clientSession);

            throw new SessionExpiredException("Error. Sesión expirada");
        }

        return user;
    }

    public void refreshSessionToken(String sessionToken) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();

                ClientSession clientSession = sessionDAO.findByToken(session, sessionToken);
                if (clientSession == null) {
                    throw new SessionNotFoundException("Error. ¿No existe la sesión?");
                }

                clientSession.setExpirationTime(LocalDateTime.now().plusSeconds(SESSION_TOKEN_EXPIRATION_TIME));

                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public void deleteAllSessionFromUser(Session session, User user) {
        sessionDAO.deleteAllByUser(session, user);
    }

    private void deleteExpiredSession(ClientSession clientSession) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();

                sessionDAO.delete(session, clientSession);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }

                throw e;
            }
        }
    }
}
