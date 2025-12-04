package dao;

import entity.User;
import org.hibernate.Session;

public interface UserDAO {

    User create(Session session, User user);

    User findById(Session session, Integer id);

    User findByUsername(Session session, String username);

    User findBySessionToken(Session session, Integer sessionToken);
}
