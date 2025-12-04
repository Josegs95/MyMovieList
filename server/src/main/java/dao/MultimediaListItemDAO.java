package dao;

import entity.MultimediaListItem;
import org.hibernate.Session;

import java.util.List;

public interface MultimediaListItemDAO {

    MultimediaListItem create(Session session, MultimediaListItem multimediaListItem);

    List<MultimediaListItem> findAll(Session session, Long listId);

    MultimediaListItem findById(Session session, Long listId, Long multimediaId);

    MultimediaListItem modify(Session session, MultimediaListItem multimediaListItem);

    void delete(Session session, MultimediaListItem multimediaListItem);
}
