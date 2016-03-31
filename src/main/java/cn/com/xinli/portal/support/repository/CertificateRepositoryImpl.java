package cn.com.xinli.portal.support.repository;

import cn.com.xinli.portal.core.certificate.Certificate;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.util.stream.Stream;

/**
 * Certificate Repository.
 * @author zhoupeng, created on 2016/3/26.
 */
@Component
public class CertificateRepositoryImpl implements Searchable<Certificate> {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Stream<Certificate> search(String query) {
        TypedQuery<Certificate> q =
                entityManager.createQuery("select certificate from Certificate certificate where " + query, Certificate.class);
        q.setMaxResults(25);
        return q.getResultList().stream();
    }

    @Override
    public long count(String query) {
        Query q = entityManager.createQuery("select count(certificate) from Certificate certificate where " + query);
        return (Long) q.getSingleResult();
    }
}
