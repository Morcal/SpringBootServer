package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.*;
import cn.com.xinli.portal.persist.ActivityEntity;
import cn.com.xinli.rest.RestResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Calendar;

/**
 * Session activity aspect.
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2016/1/8.
 */
@Aspect
@Service
public class SessionActivityAspect {
    /**
     * Logger.
     */
    private final Logger logger = LoggerFactory.getLogger(SessionActivityAspect.class);

    @Autowired
    private ActivityService activityService;

    @Autowired
    private SessionService sessionService;

    /**
     * Save activity.
     * @param ip ip address.
     * @param username user name.
     * @param info user information.
     * @param result result.
     * @param sessionAction sessionAction.
     * @param severity severity.
     */
    private void saveActivity(String ip,
                              String username,
                              String info,
                              String result,
                              Activity.SessionAction sessionAction,
                              Activity.Severity severity) {
        ActivityEntity activity = new ActivityEntity();
        activity.setCreated(Calendar.getInstance().getTime());
        activity.setSeverity(severity);
        activity.setResult(result);
        activity.setRemote(ip);
        activity.setAction(sessionAction.name());
        activity.setFacility(Activity.Facility.PORTAL);
        activity.setSource(username);
        activity.setSourceInfo(info);
        activityService.log(activity);
    }

    /**
     * Save activity for a {@link Session}.
     * @param id session id.
     * @param result result text.
     */
    private void saveActivity(long id, String result) {
        try {
            Session session = sessionService.getSession(id);
            saveActivity(session.getIp(), session.getUsername(), session.getInfo(), result,
                    Activity.SessionAction.CREATE_SESSION, Activity.Severity.NORMAL);
        } catch (SessionNotFoundException e) {
            if (logger.isTraceEnabled()) {
                logger.trace("session not found {}", id);
            }
        }
    }

    /**
     * Save activity for a {@link Session}.
     * @param ip ip address.
     * @param mac mac address.
     * @param result result text.
     */
    private void saveActivity(String ip, String mac, String result) {
        saveActivity(ip, "unknown", ip + ":" + mac, result, Activity.SessionAction.FIND_SESSION, Activity.Severity.NORMAL);
    }

    /**
     * Define pointcut within {@link SessionController}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.rest.SessionController.*(..))")
    public void inSessionController() {}

    /**
     * Define method pointcut for
     * {@link SessionController#connect(String, String, String, String, String, String, Principal)}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.rest.SessionController.connect(..))")
    public void connect() {}

    /**
     * Define method pointcut for
     * {@link SessionController#get(long, Principal)}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.rest.SessionController.get(..))")
    public void acquire() {}

    /**
     * Define method pointcut for
     * {@link SessionController#update(long, long, Principal)}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.rest.SessionController.update(..))")
    public void update() {}

    /**
     * Define method pointcut for
     * {@link SessionController#disconnect(long, Principal)}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.rest.SessionController.disconnect(..))")
    public void disconnect() {}

    /**
     * Define method pointcut for
     * {@link SessionController#find(String, String, Principal)}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.rest.SessionController.find(..))")
    public void find() {}

    /**
     * Save activity log after {@link SessionController#update(long, long, Principal)}
     * returns normally.
     *
     * @param id session id.
     * @param timestamp client update timestamp.
     * @param principal spring security principal.
     */
    @AfterReturning(
            value = "inSessionController() && update() && args(id,timestamp,principal)",
            argNames = "id,timestamp,principal,returning",
            returning = "returning")
    public void recordUpdate(long id, long timestamp, Principal principal, RestResponse returning) {
        saveActivity(id, returning.toString());
    }

    /**
     * Save activity log after {@link SessionController#update(long, long, Principal)}
     * throws an exception.
     *
     * @param id session id.
     * @param timestamp client update timestamp.
     * @param principal spring security principal.
     */
    @AfterThrowing(
            value = "inSessionController() && update() && args(id,timestamp,principal)",
            argNames = "id,timestamp,principal,cause",
            throwing = "cause")
    public void recordUpdate(long id, long timestamp, Principal principal, Throwable cause) {
        saveActivity(id, cause.getMessage());
    }

    /**
     * Save activity log after {@link SessionController#update(long, long, Principal)}
     * returns normally.
     *
     * @param ip ip address.
     * @param mac mac address.
     * @param principal spring security principal.
     */
    @AfterReturning(
            value = "inSessionController() && find() && args(ip,mac,principal)",
            argNames = "ip,mac,principal,returning",
            returning = "returning")
    public void recordFind(String ip, String mac, Principal principal, RestResponse returning) {
        saveActivity(ip, mac, returning.toString());
    }

    /**
     * Save activity log after {@link SessionController#update(long, long, Principal)}
     * throws an exception.
     *
     * @param ip ip address.
     * @param mac mac address.
     * @param principal spring security principal.
     */
    @AfterThrowing(
            value = "inSessionController() && update() && args(ip,mac,principal)",
            argNames = "ip,mac,principal,cause",
            throwing = "cause")
    public void recordFind(String ip, String mac, Principal principal, Throwable cause) {
        saveActivity(ip, mac, cause.getMessage());
    }

    /**
     * Save activity log after {@link SessionController#get(long, Principal)}
     * returns normally.
     *
     * @param id session id.
     * @param principal spring security principal.
     * @param returning server response.
     */
    @AfterReturning(
            value = "inSessionController() && acquire() && args(id,principal))",
            argNames = "id,principal,returning",
            returning = "returning")
    public void recordGet(long id, Principal principal, RestResponse returning) {
        saveActivity(id, returning.toString());
    }

    /**
     * Save activity log after  {@link SessionController#get(long, Principal)}
     * throws an exception.
     *
     * @param id session id.
     * @param principal spring security principal.
     * @param cause exception.
     */
    @AfterThrowing(
            value = "inSessionController() && acquire() && args(id,principal))",
            argNames = "id,principal,cause",
            throwing = "cause")
    public void recordGet(long id, Principal principal, Throwable cause) {
        saveActivity(id, cause.getMessage());
    }

    /**
     * Save activity log around {@link SessionController#disconnect(long, Principal)}.
     * @param point join point.
     * @param id session id.
     * @param principal principal.
     * @return method return.
     * @throws Throwable
     */
    @Around(value = "inSessionController() && disconnect() && args(id,principal)",
            argNames = "point,id,principal")
    public Object recordLogout(ProceedingJoinPoint point, long id, Principal principal) throws Throwable {
        try {
            sessionService.getSession(id);
            RestResponse response = (RestResponse) point.proceed(new Object[]{id, principal});
            saveActivity(id, response.toString());
            return response;
        } catch (Throwable cause) {
            saveActivity("unknown", "unknown", "remove session: " + id, cause.getMessage(),
                    Activity.SessionAction.DELETE_SESSION, Activity.Severity.WARN);
            throw cause;
        }
    }

    /**
     * Save activity log after
     * {@link SessionController#connect(String, String, String, String, String, String, Principal)}
     * returns normally.
     *
     * @param username username.
     * @param password password.
     * @param ip ip address.
     * @param mac mac address.
     * @param os client operation system name.
     * @param version client version.
     * @param principal spring security principal.
     * @param returning server response.
     */
    @AfterReturning(
            value = "inSessionController() && connect() && args(username,password,ip,mac,os,version,principal)",
            argNames = "username,password,ip,mac,os,version,principal,returning",
            returning = "returning")
    public void recordConnect(String username,
                              String password,
                              String ip,
                              String mac,
                              String os,
                              String version,
                              Principal principal,
                              RestResponse returning) {
        if (logger.isTraceEnabled()) {
            logger.trace("saving activity.");
        }

        saveActivity(ip, username, mac + ":" + os + ":" + version, returning.toString(),
                Activity.SessionAction.CREATE_SESSION,  Activity.Severity.NORMAL);
    }

    /**
     * Save activity log after
     * {@link SessionController#connect(String, String, String, String, String, String, Principal)}
     * returns normally.
     *
     * @param username username.
     * @param password password.
     * @param ip ip address.
     * @param mac mac address.
     * @param os client operation system name.
     * @param version client version.
     * @param principal spring security principal.
     * @param cause exception.
     */
    @AfterThrowing(
            value = "inSessionController() && connect() && args(username,password,ip,mac,os,version,principal)",
            argNames = "username,password,ip,mac,os,version,principal,cause",
            throwing = "cause")
    public void recordConnectError(String username,
                                   String password,
                                   String ip,
                                   String mac,
                                   String os,
                                   String version,
                                   Principal principal,
                                   Throwable cause) {
        if (logger.isTraceEnabled()) {
            logger.trace("saving activity.");
        }
        saveActivity(ip, username, mac + ":" + os + ":" + version, cause.getMessage(),
                Activity.SessionAction.CREATE_SESSION,  Activity.Severity.WARN);
    }
}
