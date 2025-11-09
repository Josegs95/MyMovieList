package service;

import config.HibernateUtil;
import dao.UserListDAO;
import dao.UserListDAOImpl;
import model.entity.User;
import model.entity.UserList;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

public class UserListService {

    private final UserListDAO userListDAO;
    private final AuthService authService;

    public UserListService() {
        this.userListDAO = new UserListDAOImpl();
        this.authService = new AuthService();
    }

    public UserList create(String listName, Long idOwner, Integer sessionToken) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = authService.authenticate(session, idOwner, sessionToken);

            UserList sameNameList = userListDAO.findByNameAndUser(session, listName, user);
            if (sameNameList != null) {
                throw new RuntimeException("It already exists a list with that name for that user");
            }

            UserList list = new UserList(listName, user);
            list = userListDAO.create(session, list);

            transaction.commit();

            return list;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return null;
        }
    }

    public List<UserList> getAllListsFromUser(Long idOwner, Integer sessionToken) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = authService.authenticate(session, idOwner, sessionToken);

            List<UserList> lists = userListDAO.findAllByUserWithItems(session, user);

            transaction.commit();

            return lists;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return Collections.emptyList();
        }
    }

    public UserList rename(String listName, Long idList, Long idOwner, Integer sessionToken) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = authService.authenticate(session, idOwner, sessionToken);

            UserList list = checkListOwnership(session, idList, idOwner);

            UserList sameNameList = userListDAO.findByNameAndUser(session, listName, user);
            if (sameNameList != null) {
                throw new RuntimeException("It already exists a list with that name for that user");
            }

            list.setName(listName);

            transaction.commit();

            return list;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return null;
        }
    }

    public void delete(Long idList, Long idOwner, Integer sessionToken) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            authService.authenticate(session, idOwner, sessionToken);

            checkListOwnership(session, idList, idOwner);

            userListDAO.delete(session, idList);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    public UserList checkListOwnership(Session session, Long idList, Long idOwner) {
        UserList list = userListDAO.findById(session, idList);
        if (list == null) {
            throw new RuntimeException("It does not exist a list with that id");
        }
        if (!list.getUser().getId().equals(idOwner)) {
            throw new RuntimeException("The user does not own the specified list");
        }

        return list;
    }
}
