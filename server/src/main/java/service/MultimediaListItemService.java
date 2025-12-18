package service;

import config.HibernateUtil;
import dao.MultimediaListItemDAO;
import exception.MultimediaAlreadyExistsInListException;
import exception.MultimediaNotFoundInListException;
import model.dto.MultimediaListItemDTO;
import model.dto.MultimediaSummaryDTO;
import model.dto.SeriesSummaryDTO;
import model.entity.Multimedia;
import model.entity.MultimediaListItem;
import model.entity.UserList;
import org.hibernate.Session;
import org.hibernate.Transaction;
import protocol.AuthCredentials;

public class MultimediaListItemService {

    private final MultimediaListItemDAO multimediaListItemDAO;
    private final AuthService authService;
    private final UserListService userListService;
    private final MultimediaService multimediaService;

    public MultimediaListItemService(MultimediaListItemDAO multimediaListItemDAO, AuthService authService,
                                     UserListService userListService, MultimediaService multimediaService) {
        this.multimediaListItemDAO = multimediaListItemDAO;
        this.authService = authService;
        this.userListService = userListService;
        this.multimediaService = multimediaService;
    }

    public MultimediaListItemDTO addMultimediaToList(AuthCredentials auth, MultimediaListItemDTO listItemDTO) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();

                authService.validateSession(session, auth);
                UserList list = userListService.checkListOwnership(session, listItemDTO.getListId(), auth.idUser());

                MultimediaSummaryDTO summaryDTO = listItemDTO.getMultimedia();
                if (isMultimediaContainedOnList(list, summaryDTO)) {
                    throw new MultimediaAlreadyExistsInListException("Ya existe este objeto multimedia en esta lista");
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

    public MultimediaListItemDTO modify(AuthCredentials auth, MultimediaListItemDTO listItem) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();

                authService.validateSession(session, auth);
                userListService.checkListOwnership(session, listItem.getListId(), auth.idUser());
                MultimediaListItem itemAtBD = multimediaListItemDAO.findById(
                        session,
                        listItem.getListId(),
                        listItem.getMultimedia().getIdDb());

                if (itemAtBD == null) {
                    throw new MultimediaNotFoundInListException("Este objeto multimedia no existe en esta lista");
                }

                itemAtBD.setCurrentEpisode(listItem.getCurrentEpisode());
                itemAtBD.setStatus(listItem.getStatus());

                transaction.commit();

                return new MultimediaListItemDTO(itemAtBD);
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                return null;
            }
        }
    }

    public void delete(AuthCredentials auth, Long idList, Long idMultimedia) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();

                authService.validateSession(session, auth);
                userListService.checkListOwnership(session, idList, auth.idUser());
                MultimediaListItem itemAtBD = multimediaListItemDAO.findById(session, idList, idMultimedia);

                if (itemAtBD == null) {
                    throw new MultimediaNotFoundInListException("Este objeto multimedia no existe en esta lista");
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
