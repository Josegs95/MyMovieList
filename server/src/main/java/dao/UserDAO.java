package dao;

import model.entity.User;
import org.hibernate.Session;

public interface UserDAO {

    User create(Session session, User user);

    User findById(Session session, Long id);

    User findByUsername(Session session, String username);
}
