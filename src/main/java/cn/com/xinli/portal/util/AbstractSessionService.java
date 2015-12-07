package cn.com.xinli.portal.util;

import cn.com.xinli.portal.SessionService;
import cn.com.xinli.portal.persist.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

/**
 * Abstract Session Service.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
public abstract class AbstractSessionService implements SessionService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SessionRepository sessionRepository;

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected SessionRepository getSessionRepository() {
        return sessionRepository;
    }
}
