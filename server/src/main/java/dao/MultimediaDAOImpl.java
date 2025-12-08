package dao;

import model.entity.Multimedia;
import model.entity.MultimediaType;
import org.hibernate.Session;

public class MultimediaDAOImpl implements MultimediaDAO {


    @Override
    public Multimedia create(Session session, Multimedia multimedia) {
        session.persist(multimedia);
        return multimedia;
    }

    @Override
    public Multimedia findByApiId(Session session, String apiId, MultimediaType type) {
        return session.createQuery("FROM Multimedia WHERE apiId = :api_id AND multimediaType = :type", Multimedia.class)
                .setParameter("api_id", apiId)
                .setParameter("type", type)
                .uniqueResult();
    }
}
