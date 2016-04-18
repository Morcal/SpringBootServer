package cn.com.xinli.portal.support.aspect;

import cn.com.xinli.portal.core.runtime.Runtime;
import cn.com.xinli.portal.core.runtime.SessionStatistics;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.core.session.SessionStore;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * System runtime session aspect.
 * @author zhoupeng, created on 2016/4/7.
 */
@Aspect
@Component
public class SessionRuntimeAspect {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(SessionRuntimeAspect.class);

    @Autowired
    private Runtime runtime;

    /**
     * Define pointcut within {@link SessionStore}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.core.session.SessionStore.*(..))")
    public void inSessionStore() {}

    /**
     * Define method pointcut for
     * {@link SessionStore#put(Object)}
     */
    @Pointcut("execution(* cn.com.xinli.portal.core.session.SessionStore.put(..))")
    public void put() {}

    /**
     * Define method pointcut for
     * {@link SessionStore#delete(Long)}
     */
    @Pointcut("execution(* cn.com.xinli.portal.core.session.SessionStore.delete(..))")
    public void delete() {}

    /**
     * Save activity log after {@link SessionStore#put(Object)}
     * returns normally.
     *
     * @param session session to put into store.
     */
    @After(
            value = "inSessionStore() && put() && args(session)",
            argNames = "session")
    public void recordCreate(Session session) {
        if (session == null) {
            logger.error("session to put into store is null.");
        }

        SessionStatistics.SessionRecord record = new SessionStatistics.SessionRecord(false);
        record.setAction(SessionStatistics.SessionRecord.Action.USER_CREATE_SESSION);
        runtime.addSessionRecord(record);
    }

    /**
     * Save activity log after {@link SessionStore#put(Object)}
     * throws an exception.
     *
     * @param session session to put into store.
     * @param cause exception thrown.
     */
    @AfterThrowing(
            value = "inSessionStore() && put() && args(session)",
            argNames = "session,cause",
            throwing = "cause")
    public void recordCreate(Session session, Throwable cause) {
        if (cause == null) {
            logger.error("session manager create session error cause is null.");
        }

        SessionStatistics.SessionRecord record = new SessionStatistics.SessionRecord(true);
        record.setAction(SessionStatistics.SessionRecord.Action.USER_CREATE_SESSION);
        runtime.addSessionRecord(record);
    }

    /**
     * Save activity log after {@link SessionStore#delete(Object)}
     * returns normally.
     *
     * @param obj session to remove.
     */
    @After(
            value = "inSessionStore() && delete() && args(obj)",
            argNames = "obj")
    public void recordRemove(Object obj) {
        SessionStatistics.SessionRecord record = new SessionStatistics.SessionRecord(false);
        record.setAction(SessionStatistics.SessionRecord.Action.USER_DELETE_SESSION);
        runtime.addSessionRecord(record);
    }

    /**
     * Save activity log after {@link SessionStore#delete(Object)}
     * throws an exception.
     *
     * @param obj session to remove.
     */
    @AfterThrowing(
            value = "inSessionStore() && delete() && args(obj)",
            argNames = "obj,cause",
            throwing = "cause")
    public void recordRemove(Object obj, Throwable cause) {
        if (cause == null) {
            logger.error("session manager create session error cause is null.");
        }

        SessionStatistics.SessionRecord record = new SessionStatistics.SessionRecord(true);
        record.setAction(SessionStatistics.SessionRecord.Action.USER_DELETE_SESSION);
        runtime.addSessionRecord(record);
    }
}
