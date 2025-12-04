package dao;

import entity.MultimediaListItem;
import org.hibernate.Session;

import java.util.List;

public class MultimediaListItemDAOImpl implements MultimediaListItemDAO {

    @Override
    public MultimediaListItem create(Session session, MultimediaListItem multimediaListItem) {
        session.persist(multimediaListItem);
        return multimediaListItem;
    }

    @Override
    public List<MultimediaListItem> findAll(Session session, Long listId) {
        return session.createQuery("FROM MultimediaListItem WHERE list.id = :id_list", MultimediaListItem.class)
                .setParameter("id_list", listId)
                .list();
    }

    @Override
    public MultimediaListItem findById(Session session, Long listId, Long multimediaId) {
        String hql = "FROM MultimediaListItem WHERE list.id = :list_id AND multimedia.id = :multimedia_id";
        return session.createQuery(hql, MultimediaListItem.class)
                .setParameter("list_id", listId)
                .setParameter("multimedia_id", multimediaId)
                .uniqueResult();
    }

    @Override
    public MultimediaListItem modify(Session session, MultimediaListItem multimediaListItem) {
        return session.merge(multimediaListItem);
    }

    @Override
    public void delete(Session session, MultimediaListItem multimediaListItem) {
        session.remove(multimediaListItem);
    }
}
