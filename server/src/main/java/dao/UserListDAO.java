package dao;

import entity.User;
import entity.UserList;
import org.hibernate.Session;

import java.util.List;

public interface UserListDAO {

    UserList create(Session session, UserList userList);

    UserList findById(Session session, Long idList);

    UserList findByNameAndUser(Session session, String listName, User user);

    List<UserList> findAllByUser(Session session, User user);

    List<UserList> findAllByUserWithItems(Session session, User user);

    UserList edit(Session session, UserList userList);

    void delete(Session session, Long idList);
}
