package cn.com.xinli.portal.support.repository;

import cn.com.xinli.portal.core.nas.Nas;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.stream.Stream;

/**
 * Nas repository.
 * @author zhoupeng, created on 2016/3/27.
 */
@Component
public class NasRepositoryImpl implements Searchable<Nas> {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Stream<Nas> search(String query) {
        TypedQuery<Nas> q =
                entityManager.createQuery("select nas from Nas nas where " + query, Nas.class);
        q.setMaxResults(25);
        return q.getResultList().stream();
    }

    @Override
    public long count(String query) {
        Query q = entityManager.createQuery("select count(nas) from Nas nas where " + query);
        return (Long) q.getSingleResult();
    }
}
