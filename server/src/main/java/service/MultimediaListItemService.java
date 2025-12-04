package service;

import config.HibernateUtil;
import dao.MultimediaListItemDAO;
import dto.MultimediaListItemDTO;
import dto.MultimediaSummaryDTO;
import dto.SeriesSummaryDTO;
import model.entity.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class MultimediaListItemService {

    private final MultimediaListItemDAO multimediaListItemDAO;
    private final AuthService authService;
    private final UserListService userListService;
    private final MultimediaService multimediaService;

    public MultimediaListItemService(MultimediaListItemDAO multimediaListItemDAO, AuthService authService, UserListService userListService, MultimediaService multimediaService) {
        this.multimediaListItemDAO = multimediaListItemDAO;
        this.authService = authService;
        this.userListService = userListService;
        this.multimediaService = multimediaService;
    }

    public MultimediaListItemDTO addMultimediaToList(Long idUser, MultimediaListItemDTO listItemDTO, Integer sessionToken) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();

                authService.authenticate(session, idUser, sessionToken);
                UserList list = userListService.checkListOwnership(session, listItemDTO.getListId(), idUser);

                MultimediaSummaryDTO summaryDTO = listItemDTO.getMultimedia();
                if (isMultimediaContainedOnList(list, summaryDTO)) {
                    throw new RuntimeException("That multimedia item already exists in that list");
                }

                Integer totalEpisodes = 0;
                if (summaryDTO instanceof SeriesSummaryDTO serie) {
                    totalEpisodes = serie.getTotalEpisodes();
                }
                Multimedia multimedia = multimediaService.getOrCreate(
                        session,
                        summaryDTO.getTitle(),
                        summaryDTO.getApiId(),
                        summaryDTO.getPosterPath(),
                        totalEpisodes,
                        summaryDTO.getType());
                MultimediaListItem multimediaListItem = new MultimediaListItem(
                        multimedia,
                        list,
                        listItemDTO.getStatus(),
                        listItemDTO.getCurrentEpisode());


                multimediaListItemDAO.create(session, multimediaListItem);

                transaction.commit();

                return new MultimediaListItemDTO(multimediaListItem);
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
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

    public void delete(Long idUser, Long idList, Long idMultimedia, Integer sessionToken) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();

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
                throw e;
            }
        }
    }

    private boolean isMultimediaContainedOnList(UserList list, MultimediaSummaryDTO multimedia) {
        return list.getMultimediaList().stream()
                .map(MultimediaListItem::getMultimedia)
                .anyMatch(item -> item.getApiId().equals(multimedia.getApiId())
                        && item.getMultimediaType() == multimedia.getType());
    }
}
