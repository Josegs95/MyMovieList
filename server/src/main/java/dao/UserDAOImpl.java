package dao;

import model.entity.User;
import org.hibernate.Session;

public class UserDAOImpl implements UserDAO {


    @Override
    public User create(Session session, User user) {
        session.persist(user);
        return user;
    }

    @Override
    public User findById(Session session, Integer id) {
        return session.createQuery("FROM User WHERE id = :id", User.class)
                .setParameter("id", id)
                .uniqueResult();
    }

    @Override
    public User findByUsername(Session session, String username) {
        return session.createQuery("FROM User WHERE username = :username", User.class)
                .setParameter("username", username)
                .uniqueResult();
    }

    @Override
    public User findBySessionToken(Session session, Integer sessionToken) {
        return session.createQuery("FROM User WHERE sessionToken = :sessionToken", User.class)
                .setParameter("sessionToken", sessionToken)
                .uniqueResult();
    }
}
