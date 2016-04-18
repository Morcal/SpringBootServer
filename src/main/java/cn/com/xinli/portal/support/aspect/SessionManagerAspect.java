package cn.com.xinli.portal.support.aspect;

import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.core.activity.ActivityService;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.core.session.SessionManager;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * @author zhoupeng, created on 2016/4/18.
 */
@Aspect
@Component
public class SessionManagerAspect {
    @Autowired
    private ActivityService activityService;

    private String buildInfo(Session session) {
        return session.getCredentials().toString();
    }

    /**
     * Define pointcut within {@link SessionManager}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.core.session.SessionManager.*(..))")
    public void inSessionManager() {}

    /**
     * Define method pointcut for
     * {@link SessionManager#removeSessionInFuture(Session)}
     */
    @Pointcut("execution(* cn.com.xinli.portal.core.session.SessionManager.removeSessionInFuture(..))")
    public void removeInFuture() {}


    @AfterReturning(
            value = "inSessionManager() && removeInFuture() && args(session)",
            argNames = "session,returning",
            returning = "returning")
    public void recordRemoval(Session session, boolean returning) {
        Activity activity = new Activity();
        activity.setCreated(Calendar.getInstance().getTime());
        activity.setSeverity(Activity.Severity.INFO);
        activity.setResult(String.valueOf(returning));
        activity.setRemote("127.0.0.1");
        activity.setAction(Activity.SystemAction.DELETE_INACTIVE_SESSION.name());
        activity.setFacility(Activity.Facility.PORTAL);
        activity.setSource("SYSTEM");
        activity.setSourceInfo(buildInfo(session));
        activityService.log(activity);
    }
}
