package dao;

import model.entity.User;
import model.entity.UserList;
import org.hibernate.Session;

import java.util.List;

public class UserListDAOImpl implements UserListDAO {


    @Override
    public UserList create(Session session, UserList userList) {
        session.persist(userList);
        return userList;
    }

    @Override
    public UserList findById(Session session, Long idList) {
        return session.createQuery("FROM UserList WHERE id = :id", UserList.class)
                .setParameter("id", idList)
                .uniqueResult();
    }

    @Override
    public UserList findByNameAndUser(Session session, String listName, User user) {
        return session.createQuery("FROM UserList WHERE name = :name AND user = :user", UserList.class)
                .setParameter("name", listName)
                .setParameter("user", user)
                .uniqueResult();
    }

    @Override
    public List<UserList> findAllByUser(Session session, User user) {
        return session.createQuery("FROM UserList WHERE user = :user", UserList.class)
                .setParameter("user", user)
                .list();
    }

    @Override
    public List<UserList> findAllByUserWithItems(Session session, User user) {
        String hql = "SELECT DISTINCT l FROM UserList l LEFT JOIN FETCH l.multimediaList WHERE l.user = :user";
        return session.createQuery(hql, UserList.class)
                .setParameter("user", user)
                .list();
    }

    @Override
    public UserList edit(Session session, UserList userList) {
        return session.merge(userList);
    }

    @Override
    public void delete(Session session, Long idList) {
        UserList userList = session.find(UserList.class, idList);
        if (userList != null) {
            session.remove(userList);
        }
    }
}
