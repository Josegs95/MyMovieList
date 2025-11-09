package service;

import config.HibernateUtil;
import dao.MultimediaListItemDAO;
import dao.MultimediaListItemDAOImpl;
import model.entity.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class MultimediaListItemService {

    private final MultimediaListItemDAO multimediaListItemDAO;
    private final AuthService authService;
    private final UserListService userListService;
    private final MultimediaService multimediaService;

    public MultimediaListItemService() {
        this.multimediaListItemDAO = new MultimediaListItemDAOImpl();
        this.authService = new AuthService();
        this.userListService = new UserListService();
        this.multimediaService = new MultimediaService();
    }

    public MultimediaListItem addMultimediaToList(Long idUser, Long idList, String title, String apiId, Integer totalEpisodes,
                                                  MultimediaType type, MultimediaStatus status, Integer currentEpisode, Integer sessionToken) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            authService.authenticate(session, idUser, sessionToken);
            UserList list = userListService.checkListOwnership(session, idList, idUser);
            List<MultimediaListItem> multimediaListItems = multimediaListItemDAO.findAll(session, idList);

            Multimedia multimedia = multimediaService.getOrCreate(session, title, apiId, totalEpisodes, type);
            MultimediaListItem multimediaListItem = new MultimediaListItem(multimedia, list, status, currentEpisode);

            boolean alreadyExists = multimediaListItems.stream()
                    .anyMatch(item -> item.getMultimedia().equals(multimediaListItem.getMultimedia()));

            if (alreadyExists) {
                throw new RuntimeException("That multimedia item already exists in that list");
            }

            multimediaListItemDAO.create(session, multimediaListItem);

            transaction.commit();

            return multimediaListItem;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return null;
        }
    }

    public MultimediaListItem modify(Long idUser, MultimediaListItem itemFromClient, Integer sessionToken) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Long idList = itemFromClient.getList().getId();
            Long idMultimedia = itemFromClient.getMultimedia().getId();

            authService.authenticate(session, idUser, sessionToken);
            userListService.checkListOwnership(session, idList, idUser);
            MultimediaListItem itemAtBD = multimediaListItemDAO.findById(session, idList, idMultimedia);

            if (itemAtBD == null) {
                throw new RuntimeException("That multimedia item does not exists in that list");
            }

            itemAtBD.setCurrentEpisode(itemFromClient.getCurrentEpisode());
            itemAtBD.setStatus(itemFromClient.getStatus());

            transaction.commit();

            return itemAtBD;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return null;
        }
    }

    public void delete(Long idUser, MultimediaListItem itemFromClient, Integer sessionToken) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Long idList = itemFromClient.getList().getId();
            Long idMultimedia = itemFromClient.getMultimedia().getId();

            authService.authenticate(session, idUser, sessionToken);
            userListService.checkListOwnership(session, idList, idUser);
            MultimediaListItem itemAtBD = multimediaListItemDAO.findById(session, idList, idMultimedia);

            if (itemAtBD == null) {
                throw new RuntimeException("That multimedia item does not exists in that list");
            }

            multimediaListItemDAO.delete(session, itemAtBD);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
