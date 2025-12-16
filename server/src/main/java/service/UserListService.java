package service;

import config.HibernateUtil;
import dao.UserListDAO;
import model.dto.UserListDTO;
import protocol.dto.request.CreateListRequest;
import exception.AuthorizationException;
import exception.ConflictException;
import exception.ResourceNotFoundException;
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

    public UserListDTO create(CreateListRequest request) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                User user = authService.validateSession(session, request.userId(), request.sessionToken());

                UserList sameNameList = userListDAO.findByNameAndUser(session, request.listName(), user);
                if (sameNameList != null) {
                    throw new ConflictException("Ya existe una lista con ese nombre para este usuario");
                }

                UserList list = new UserList(request.listName(), user);
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
                    throw new ConflictException("Ya existe una lista con ese nombre para este usuario");
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
            throw new ResourceNotFoundException("No existe una lista con ese id");
        }
        if (!list.getUser().getId().equals(idOwner)) {
            throw new AuthorizationException("El usuario no es el propietario de esta lista");
        }

        return list;
    }
}
