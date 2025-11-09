package service;

import dao.MultimediaDAO;
import dao.MultimediaDAOImpl;
import model.entity.Multimedia;
import model.entity.MultimediaType;
import org.hibernate.Session;

public class MultimediaService {

    private final MultimediaDAO multimediaDAO;

    public MultimediaService() {
        this.multimediaDAO = new MultimediaDAOImpl();
    }

    public Multimedia findByApiId(Session session, String apiId, MultimediaType type) {
        return multimediaDAO.findByApiId(session, apiId, type);
    }

    public Multimedia getOrCreate(Session session, String title, String apiId, Integer totalEpisodes, MultimediaType type) {
        Multimedia multimedia = findByApiId(session, apiId, type);
        if (multimedia == null) {
            multimedia = new Multimedia(title, apiId, totalEpisodes, type);
            multimediaDAO.create(session, multimedia);
        }

        return multimedia;
    }
}
