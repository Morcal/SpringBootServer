package cn.com.xinli.portal.support.repository;

import cn.com.xinli.portal.core.session.Session;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.stream.Stream;

/**
 * Session repository.
 * @author zhoupeng, created on 2016/3/27.
 */
@Component
public class SessionRepositoryImpl implements Searchable<Session> {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Stream<Session> search(String query) {
        TypedQuery<Session> q =
                entityManager.createQuery("select session from Session session where " + query, Session.class);
        q.setMaxResults(25);
        return q.getResultList().stream();
    }

    @Override
    public long count(String query) {
        Query q = entityManager.createQuery("select count(session) from Session session where " + query);
        return (Long) q.getSingleResult();
    }
}
