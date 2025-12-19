package service;

import config.HibernateUtil;
import dao.UserListDAO;
import exception.ListDoesNotBelongToUserException;
import exception.ListNameAlreadyExistsForUserException;
import exception.UserListNotFoundException;
import model.dto.UserListDTO;
import model.entity.User;
import model.entity.UserList;
import org.hibernate.Session;
import org.hibernate.Transaction;
import protocol.AuthCredentials;

import java.util.List;

public class UserListService {

    private final UserListDAO userListDAO;
    private final AuthService authService;

    public UserListService(UserListDAO userListDAO, AuthService authService) {
        this.userListDAO = userListDAO;
        this.authService = authService;
    }

    public UserListDTO create(AuthCredentials auth, String listName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                User user = authService.validateSession(session, auth);

                UserList sameNameList = userListDAO.findByNameAndUser(session, listName, user);
                if (sameNameList != null) {
                    throw new ListNameAlreadyExistsForUserException("The user already has a list with this name");
                }

                UserList list = new UserList(listName, user);
                list = userListDAO.create(session, list);

                transaction.commit();

                return new UserListDTO(list);
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public List<UserListDTO> getAllListsFromUser(AuthCredentials auth) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                User user = authService.validateSession(session, auth);

                List<UserList> lists = userListDAO.findAllByUserWithItems(session, user);

                transaction.commit();

                return lists.stream()
                        .map(UserListDTO::new)
                        .toList();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public UserListDTO rename(AuthCredentials auth, Long idList, String newListName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                User user = authService.validateSession(session, auth);

                UserList list = checkListOwnership(session, idList, auth.idUser());

                UserList sameNameList = userListDAO.findByNameAndUser(session, newListName, user);
                if (sameNameList != null) {
                    throw new ListNameAlreadyExistsForUserException("The user already has a list with this name");
                }

                list.setName(newListName);

                transaction.commit();

                return new UserListDTO(list);
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public void delete(AuthCredentials auth, Long idList) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                authService.validateSession(session, auth);

                checkListOwnership(session, idList, auth.idUser());

                userListDAO.delete(session, idList);

                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public UserList checkListOwnership(Session session, Long idList, Long idOwner) {
        UserList list = userListDAO.findById(session, idList);
        if (list == null) {
            throw new UserListNotFoundException("List not found");
        }
        if (!list.getUser().getId().equals(idOwner)) {
            throw new ListDoesNotBelongToUserException("The user does not own this list");
        }

        return list;
    }
}
