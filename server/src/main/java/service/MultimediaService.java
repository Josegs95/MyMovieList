package service;

import dao.MultimediaDAO;
import model.entity.Multimedia;
import model.entity.MultimediaType;
import org.hibernate.Session;

public class MultimediaService {

    private final MultimediaDAO multimediaDAO;

    public MultimediaService(MultimediaDAO multimediaDAO) {
        this.multimediaDAO = multimediaDAO;
    }

    public Multimedia findByApiId(Session session, String apiId, MultimediaType type) {
        return multimediaDAO.findByApiId(session, apiId, type);
    }

    public Multimedia getOrCreate(Session session, String title, String apiId, String posterPath,
                                  Integer totalEpisodes, MultimediaType type) {
        Multimedia multimedia = findByApiId(session, apiId, type);
        if (multimedia == null) {
            multimedia = new Multimedia(title, apiId, posterPath, totalEpisodes, type);
            multimediaDAO.create(session, multimedia);
        }

        return multimedia;
    }
}
