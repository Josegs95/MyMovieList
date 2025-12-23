package dao;

import model.entity.ClientSession;
import model.entity.User;
import org.hibernate.Session;

public interface ClientSessionDAO {

    ClientSession create(Session session, ClientSession clientSession);

    ClientSession findByToken(Session session, String sessionToken);

    ClientSession findByTokenAndUser(Session session, String sessionToken, User user);

    void delete(Session session, ClientSession clientSession);

    void deleteAllByUser(Session session, User user);
}
