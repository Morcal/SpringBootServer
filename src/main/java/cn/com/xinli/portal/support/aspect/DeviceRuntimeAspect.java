package cn.com.xinli.portal.support.aspect;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.runtime.NasStatistics;
import cn.com.xinli.portal.core.runtime.Runtime;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.core.session.SessionProvider;
import cn.com.xinli.portal.transport.NasNotRespondException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * NAS Device runtime aspect.
 *
 * @author zhoupeng, created on 2016/4/7.
 */
@Aspect
@Service
public class DeviceRuntimeAspect {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(DeviceRuntimeAspect.class);

    @Autowired
    private Runtime runtime;

    /**
     * Define pointcut within {@link SessionProvider}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.core.session.SessionProvider.*(..))")
    public void inSessionProvider() {}

    /**
     * Define method pointcut for
     * {@link SessionProvider#authenticate(Nas, Credentials)}
     */
    @Pointcut("execution(* cn.com.xinli.portal.core.session.SessionProvider.authenticate(..))")
    public void authenticate() {}

    /**
     * Define method pointcut for
     * {@link SessionProvider#disconnect(Session)}
     */
    @Pointcut("execution(* cn.com.xinli.portal.core.session.SessionProvider.disconnect(..))")
    public void disconnect() {}

    private void addSessionRecord(long nas, long startTime, Throwable cause) {
        NasStatistics.NasRecord record = new NasStatistics.NasRecord(false);
        record.setNasId(nas);
        record.setResponseTime(System.currentTimeMillis() - startTime);
        record.setTimeout(cause != null && cause instanceof NasNotRespondException);
        runtime.addNasRecord(record);

        if (logger.isTraceEnabled()) {
            logger.trace("session recorded, {}", record);
        }
    }

    /**
     * Save activity log after {@link SessionProvider#authenticate(Nas, Credentials)}
     * returns normally.
     *
     * @param nas         nas device.
     * @param credentials client credentials.
     */
    @Around(
            value = "inSessionProvider() && authenticate() && args(nas,credentials)",
            argNames = "point,nas,credentials")
    public Object recordAuthenticate(ProceedingJoinPoint point, Nas nas, Credentials credentials)
            throws Throwable {
        long start = System.currentTimeMillis();
        Throwable c = null;

        try {
            return point.proceed(new Object[] { nas, credentials });
        } catch (Throwable cause) {
            c = cause;
            throw cause;
        } finally {
            addSessionRecord(nas.getId(), start, c);
        }
    }

    /**
     * Save activity log after {@link SessionProvider#authenticate(Nas, Credentials)}
     * returns normally.
     *
     * @param session session.
     */
    @Around(
            value = "inSessionProvider() && disconnect() && args(session)",
            argNames = "point,session")
    public Object recordAuthenticate(ProceedingJoinPoint point, Session session)
            throws Throwable {
        long start = System.currentTimeMillis();
        Throwable c = null;

        try {
            return point.proceed(new Object[] {session});
        } catch (Throwable cause) {
            c = cause;
            throw cause;
        } finally {
            addSessionRecord(session.getNas().getId(), start, c);
        }
    }
}
