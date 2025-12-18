package service;

import config.HibernateUtil;
import dao.UserListDAO;
import exception.*;
import model.dto.UserListDTO;
import model.entity.User;
import model.entity.UserList;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserListService {

    private final UserListDAO userListDAO;
    private final AuthService authService;

    public UserListService(UserListDAO userListDAO, AuthService authService) {
        this.userListDAO = userListDAO;
        this.authService = authService;
    }

    public UserListDTO create(Long userId, String listName, String sessionToken) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                User user = authService.validateSession(session, userId, sessionToken);

                UserList sameNameList = userListDAO.findByNameAndUser(session, listName, user);
                if (sameNameList != null) {
                    throw new ListNameAlreadyExistsForUserException("Ya existe una lista con ese nombre para este usuario");
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

    public List<UserListDTO> getAllListsFromUser(Long idOwner, String sessionToken) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                User user = authService.validateSession(session, idOwner, sessionToken);

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

    public UserListDTO rename(Long idUser, Long idList, String newListName, String sessionToken) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                User user = authService.validateSession(session, idUser, sessionToken);

                UserList list = checkListOwnership(session, idList, idUser);

                UserList sameNameList = userListDAO.findByNameAndUser(session, newListName, user);
                if (sameNameList != null) {
                    throw new ListNameAlreadyExistsForUserException("Ya existe una lista con ese nombre para este usuario");
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

    public void delete(Long idOwner, Long idList, String sessionToken) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                authService.validateSession(session, idOwner, sessionToken);

                checkListOwnership(session, idList, idOwner);

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
            throw new UserListNotFoundException("No existe una lista con ese id");
        }
        if (!list.getUser().getId().equals(idOwner)) {
            throw new ListDoesNotBelongToUserException("El usuario no es el propietario de esta lista");
        }

        return list;
    }
}
