package cn.com.xinli.portal.support.aspect;

import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.runtime.Runtime;
import cn.com.xinli.portal.core.runtime.SessionStatistics;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.core.session.SessionManager;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * System runtime session aspect.
 * @author zhoupeng, created on 2016/4/7.
 */
@Aspect
@Service
public class SessionRuntimeAspect {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(SessionRuntimeAspect.class);

    @Autowired
    private Runtime runtime;

    /**
     * Define pointcut within {@link SessionManager}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.core.session.SessionManager.*(..))")
    public void inSessionManager() {}

    /**
     * Define method pointcut for
     * {@link SessionManager#createSession(Certificate, Credentials)}
     */
    @Pointcut("execution(* cn.com.xinli.portal.core.session.SessionManager.createSession(..))")
    public void connect() {}

    /**
     * Define method pointcut for
     * {@link SessionManager#removeSession(long)}
     */
    @Pointcut("execution(* cn.com.xinli.portal.core.session.SessionManager.removeSession(..))")
    public void remove() {}

    /**
     * Define method pointcut for
     * {@link SessionManager#removeSessionInFuture(Session)}
     */
    @Pointcut("execution(* cn.com.xinli.portal.core.session.SessionManager.removeSessionInFuture(..))")
    public void removeInFuture() {}

    /**
     * Save activity log after {@link SessionManager#createSession(Certificate, Credentials)}
     * returns normally.
     *
     * @param certificate certificate.
     * @param credentials client credentials.
     */
    @AfterReturning(
            value = "inSessionManager() && connect() && args(certificate,credentials)",
            argNames = "certificate,credentials,returning",
            returning = "returning")
    public void recordCreate(Certificate certificate, Credentials credentials, Session returning) {
        if (returning == null) {
            logger.error("session manager create session returns null.");
        }

        SessionStatistics.SessionRecord record = new SessionStatistics.SessionRecord(false);
        record.setAction(SessionStatistics.SessionRecord.Action.USER_CREATE_SESSION);
        runtime.addSessionRecord(record);
    }

    /**
     * Save activity log after {@link SessionManager#createSession(Certificate, Credentials)}
     * throws an exception.
     *
     * @param certificate certificate.
     * @param credentials client credentials.
     */
    @AfterThrowing(
            value = "inSessionManager() && connect() && args(certificate,credentials)",
            argNames = "certificate,credentials,cause",
            throwing = "cause")
    public void recordCreate(Certificate certificate, Credentials credentials, Throwable cause) {
        if (cause == null) {
            logger.error("session manager create session error cause is null.");
        }

        SessionStatistics.SessionRecord record = new SessionStatistics.SessionRecord(true);
        record.setAction(SessionStatistics.SessionRecord.Action.USER_CREATE_SESSION);
        runtime.addSessionRecord(record);
    }

    /**
     * Save activity log after {@link SessionManager#removeSession(long)}
     * returns normally.
     *
     * @param id session id.
     */
    @After(
            value = "inSessionManager() && remove() && args(id)",
            argNames = "id")
    public void recordRemove(long id) {
        SessionStatistics.SessionRecord record = new SessionStatistics.SessionRecord(false);
        record.setAction(SessionStatistics.SessionRecord.Action.USER_DELETE_SESSION);
        runtime.addSessionRecord(record);
    }

    /**
     * Save activity log after {@link SessionManager#removeSession(long)}
     * throws an exception.
     *
     * @param id session id.
     */
    @AfterThrowing(
            value = "inSessionManager() && remove() && args(id)",
            argNames = "id,cause",
            throwing = "cause")
    public void recordRemove(long id, Throwable cause) {
        if (cause == null) {
            logger.error("session manager create session error cause is null.");
        }

        SessionStatistics.SessionRecord record = new SessionStatistics.SessionRecord(true);
        record.setAction(SessionStatistics.SessionRecord.Action.USER_DELETE_SESSION);
        runtime.addSessionRecord(record);
    }

    /**
     * Save activity log after {@link SessionManager#removeSessionInFuture(Session)}
     * returns normally.
     *
     * @param session session.
     */
    @AfterReturning(
            value = "inSessionManager() && removeInFuture() && args(session)",
            argNames = "session,returning",
            returning = "returning")
    public void recordRemoveInFuture(Session session, boolean returning) {
        SessionStatistics.SessionRecord record = new SessionStatistics.SessionRecord(!returning);
        record.setAction(SessionStatistics.SessionRecord.Action.SYSTEM_DELETE_SESSION);
        runtime.addSessionRecord(record);
    }
}
