package cn.com.xinli.portal.support.aspect;

import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.core.session.SessionNotFoundException;
import cn.com.xinli.portal.core.session.SessionService;
import cn.com.xinli.portal.core.activity.ActivityService;
import cn.com.xinli.portal.web.controller.SessionController;
import cn.com.xinli.portal.web.rest.RestResponse;
import org.apache.commons.lang3.StringUtils;
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
 *
 * <p>This aspect watches session operations in the controller,
 * and save operation log as auditing logging {@link Activity}s.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/8.
 */
@Aspect
@Service
public class SessionActivityAspect {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(SessionActivityAspect.class);

    @Autowired
    private ActivityService activityService;

    @Autowired
    private SessionService sessionService;

    private String buildInfo(Session session) {
        return session.getCredentials().toString();
    }

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
        Activity activity = new Activity();
        activity.setCreated(Calendar.getInstance().getTime());
        activity.setSeverity(severity);
        activity.setResult(StringUtils.abbreviate(result, 256));
        activity.setRemote(ip);
        activity.setAction(sessionAction.name());
        activity.setFacility(Activity.Facility.PORTAL);
        activity.setSource(username);
        activity.setSourceInfo(info);
        activityService.log(activity);
    }

    /**
     * Save session activity for a {@link Session}.
     * @param session session to log.
     * @param action action.
     * @param result result.
     */
    private void saveActivity(Session session, Activity.SessionAction action, String result) {
        Credentials credentials = session.getCredentials();
        saveActivity(credentials.getIp(), credentials.getUsername(), buildInfo(session), result,
                action, Activity.Severity.INFO);
    }

    /**
     * Save activity for a {@link Session}.
     * @param id session id.
     * @param action sessionAction.
     * @param result result text.
     */
    private void saveActivity(long id, Activity.SessionAction action, String result) {
        try {
            Session session = sessionService.getSession(id);
            saveActivity(session, action, result);
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
        saveActivity(ip, "unknown", ip + ":" + mac, result, Activity.SessionAction.FIND_SESSION, Activity.Severity.INFO);
    }

    /**
     * Define pointcut within {@link SessionController}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.web.controller.SessionController.*(..))")
    public void inSessionController() {}

    /**
     * Define method pointcut for
     * {@link SessionController#connect(String, String, String, String, String, String, String, Principal)}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.web.controller.SessionController.connect(..))")
    public void connect() {}

    /**
     * Define method pointcut for
     * {@link SessionController#get(long, Principal)}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.web.controller.SessionController.get(..))")
    public void acquire() {}

    /**
     * Define method pointcut for
     * {@link SessionController#update(long, long, Principal)}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.web.controller.SessionController.update(..))")
    public void update() {}

    /**
     * Define method pointcut for
     * {@link SessionController#disconnect(long, Principal)}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.web.controller.SessionController.disconnect(..))")
    public void disconnect() {}

    /**
     * Define method pointcut for
     * {@link SessionController#find(String, String, String, Principal)}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.web.controller.SessionController.find(..))")
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
        saveActivity(id, Activity.SessionAction.UPDATE_SESSION, returning.toString());
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
        saveActivity(id, Activity.SessionAction.UPDATE_SESSION, cause.getMessage());
    }

    /**
     * Save activity log after {@link SessionController#find(String, String, String, Principal)}
     * returns normally.
     *
     * @param ip ip address.
     * @param mac mac address.
     * @param context portal context.
     * @param principal spring security principal.
     */
    @AfterReturning(
            value = "inSessionController() && find() && args(ip,mac,context,principal)",
            argNames = "ip,mac,context,principal,returning",
            returning = "returning")
    public void recordFind(String ip, String mac, String context, Principal principal, RestResponse returning) {
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
        saveActivity(id, Activity.SessionAction.GET_SESSION, returning.toString());
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
        saveActivity(id, Activity.SessionAction.GET_SESSION, cause.getMessage());
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
            Session session = sessionService.getSession(id);
            RestResponse response = (RestResponse) point.proceed(new Object[]{id, principal});
            saveActivity(session, Activity.SessionAction.DELETE_SESSION, response.toString());
            return response;
        } catch (Throwable cause) {
            saveActivity("unknown", "unknown", "remove session: " + id, cause.getMessage(),
                    Activity.SessionAction.DELETE_SESSION, Activity.Severity.WARN);
            throw cause;
        }
    }

    /**
     * Save activity log after
     * {@link SessionController#connect(String, String, String, String, String, String, String, Principal)}
     * returns normally.
     *
     * @param username username.
     * @param password password.
     * @param ip ip address.
     * @param mac mac address.
     * @param os client operation system name.
     * @param version client version.
     * @param url redirect url.
     * @param principal spring security principal.
     * @param returning server response.
     */
    @AfterReturning(
            value = "inSessionController() && connect() && args(username,password,ip,mac,os,version,url,principal)",
            argNames = "username,password,ip,mac,os,version,url,principal,returning",
            returning = "returning")
    public void recordConnect(String username,
                              String password,
                              String ip,
                              String mac,
                              String os,
                              String version,
                              String url,
                              Principal principal,
                              RestResponse returning) {
        if (logger.isTraceEnabled()) {
            logger.trace("saving activity.");
        }

        saveActivity(ip, username, mac + ":" + os + ":" + version, returning.toString(),
                Activity.SessionAction.CREATE_SESSION,  Activity.Severity.INFO);
    }

    /**
     * Save activity log after
     * {@link SessionController#connect(String, String, String, String, String, String, String, Principal)}
     * returns normally.
     *
     * @param username username.
     * @param password password.
     * @param ip ip address.
     * @param mac mac address.
     * @param os client operation system name.
     * @param version client version.
     * @param url redirect url.
     * @param principal spring security principal.
     * @param cause exception.
     */
    @AfterThrowing(
            value = "inSessionController() && connect() && args(username,password,ip,mac,os,version,url,principal)",
            argNames = "username,password,ip,mac,os,version,url,principal,cause",
            throwing = "cause")
    public void recordConnectError(String username,
                                   String password,
                                   String ip,
                                   String mac,
                                   String os,
                                   String version,
                                   String url,
                                   Principal principal,
                                   Throwable cause) {
        if (logger.isTraceEnabled()) {
            logger.trace("saving activity.");
        }
        saveActivity(ip, username, mac + ":" + os + ":" + version, cause.getMessage(),
                Activity.SessionAction.CREATE_SESSION,  Activity.Severity.WARN);
    }
}
