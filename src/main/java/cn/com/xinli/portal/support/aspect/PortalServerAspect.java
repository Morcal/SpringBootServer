package cn.com.xinli.portal.support.aspect;

import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.support.InternalServerHandler;
import cn.com.xinli.portal.transport.huawei.LogoutError;
import cn.com.xinli.portal.core.activity.ActivityService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * Portal server aspect.
 *
 * <p>This class watches on internal portal server and records all NTF_LOGOUT events
 * as auditing logging.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/10.
 */
@Aspect
@Service
public class PortalServerAspect {
    @Autowired
    private ActivityService activityService;

    /**
     * Define methods pointcut for {@link InternalServerHandler}.
     */
    @Pointcut("execution(public * cn.com.xinli.portal.support.InternalServerHandler.*(..))")
    public void inHandler() {}

    /**
     * Define method pointcut for {@link InternalServerHandler#ntfLogout(String, String)}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.support.InternalServerHandler.ntfLogout(..))")
    public void ntfLogout() {}

    /**
     * Save activity.
     * @param ip NTF_LOGOUT client ip.
     * @param result result.
     */
    private void saveActivity(String nasIp, String ip, String result) {
        Activity activity = new Activity();
        activity.setCreated(Calendar.getInstance().getTime());
        activity.setSeverity(Activity.Severity.INFO);
        activity.setResult(StringUtils.abbreviate(result, 256));
        activity.setRemote(ip);
        activity.setAction(Activity.SystemAction.NTF_LOGOUT.name());
        activity.setFacility(Activity.Facility.PORTAL);
        activity.setSource("NAS");
        activity.setSourceInfo(nasIp);
        activityService.log(activity);
    }

    /**
     * Save activity after {@link InternalServerHandler#ntfLogout(String, String)}
     * returns normally.
     * @param ip client ip.
     * @param returning returning result.
     */
    @AfterReturning(
            value = "inHandler() && ntfLogout() && args(nasIp, ip)",
            argNames = "nasIp,ip,returning",
            returning = "returning")
    public void recordNtfLogout(String nasIp, String ip, LogoutError returning) {
        saveActivity(nasIp, ip, returning.getDescription());
    }

    /**
     * Save activity after {@link InternalServerHandler#ntfLogout(String, String)}
     * throws an exception.
     * @param ip client ip.
     * @param cause cause.
     */
    @AfterThrowing(
            value = "inHandler() && ntfLogout() && args(nasIp, ip)",
            argNames = "nasIp,ip,cause",
            throwing = "cause")
    public void recordNtfLogout(String nasIp, String ip, Throwable cause) {
        saveActivity(nasIp, ip, cause.getMessage());
    }
}
