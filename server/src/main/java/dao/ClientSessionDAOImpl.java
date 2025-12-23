package dao;

import model.entity.ClientSession;
import model.entity.User;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientSessionDAOImpl implements ClientSessionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientSessionDAOImpl.class);

    @Override
    public ClientSession create(Session session, ClientSession clientSession) {
        session.persist(clientSession);
        return clientSession;
    }

    @Override
    public ClientSession findByToken(Session session, String sessionToken) {
        String hql = "FROM ClientSession WHERE token = :token";
        return session.createQuery(hql, ClientSession.class)
                .setParameter("token", sessionToken)
                .uniqueResult();
    }

    @Override
    public ClientSession findByTokenAndUser(Session session, String sessionToken, User user) {
        String hql = "FROM ClientSession WHERE token = :token AND owner = :owner";
        return session.createQuery(hql, ClientSession.class)
                .setParameter("token", sessionToken)
                .setParameter("owner", user)
                .uniqueResult();
    }

    @Override
    public void delete(Session session, ClientSession clientSession) {
        session.remove(clientSession);
    }

    @Override
    public void deleteAllByUser(Session session, User user) {
        String hql = "DELETE FROM ClientSession WHERE owner = :owner";
        int deletedSessions = session.createMutationQuery(hql)
                .setParameter("owner", user)
                .executeUpdate();
        LOGGER.info("{} sessions were removed before the creation of a new one", deletedSessions);
    }
}
