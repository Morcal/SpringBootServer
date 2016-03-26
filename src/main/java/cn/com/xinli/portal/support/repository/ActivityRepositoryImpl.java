package cn.com.xinli.portal.support.repository;

import cn.com.xinli.portal.core.activity.Activity;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.stream.Stream;

/**
 * Activity Repository.
 * @author zhoupeng, created on 2016/3/26.
 */
@Component
public class ActivityRepositoryImpl implements Searchable<Activity> {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Stream<Activity> search(String query) {
        TypedQuery<Activity> q =
                entityManager.createQuery("select activity from Activity activity where " + query, Activity.class);
        q.setMaxResults(25);
        return q.getResultList().stream();
    }

    @Override
    public long count(String query) {
        Query q = entityManager.createQuery("select count(activity) from Activity activity where " + query);
        return (Long) q.getSingleResult();
    }
}
