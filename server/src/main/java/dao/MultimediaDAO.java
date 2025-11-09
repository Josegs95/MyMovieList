package dao;

import model.entity.Multimedia;
import model.entity.MultimediaType;
import org.hibernate.Session;

public interface MultimediaDAO {

    Multimedia create(Session session, Multimedia multimedia);

    Multimedia findByApiId(Session session, String apiId, MultimediaType type);
}
