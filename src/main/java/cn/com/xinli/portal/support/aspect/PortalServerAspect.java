package cn.com.xinli.portal.support.aspect;

import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.support.InternalServerHandler;
import cn.com.xinli.portal.web.admin.ActivityService;
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
 * <p>This class watches on internal portal server and records all NTF_LOGOUT events.
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
     * Define method pointcut for {@link InternalServerHandler#handleNtfLogout(String)}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.support.InternalServerHandler.handleNtfLogout(..))")
    public void ntfLogout() {}

    /**
     * Convert returning result code to text.
     *
     * NTF_LOGOUT result defined in private class
     * cn.com.xinli.portal.protocol.huawei.Enums
     *
     * @param result result code.
     * @return result string.
     */
    public String resultOf(int result) {
        switch (result) {
            case 0x00:
                return "ok";

            case 0x01:
                return "rejected";

            case 0x02:
                return "failed";

            case 0x03:
                return "gone";

            default:
                return "unknown";
        }
    }

    /**
     * Save activity.
     * @param ip NTF_LOGOUT client ip.
     * @param result result.
     */
    private void saveActivity(String ip, String result) {
        Activity activity = new Activity();
        activity.setCreated(Calendar.getInstance().getTime());
        activity.setSeverity(Activity.Severity.INFO);
        activity.setResult(result);
        activity.setRemote(ip);
        activity.setAction(Activity.SystemAction.NTF_LOGOUT.name());
        activity.setFacility(Activity.Facility.PORTAL);
        activity.setSource("system");
        activity.setSourceInfo("system portal server");
        activityService.log(activity);
    }

    /**
     * Save activity after {@link InternalServerHandler#handleNtfLogout(String)}
     * returns normally.
     * @param ip client ip.
     * @param returning returning result.
     */
    @AfterReturning(
            value = "inHandler() && ntfLogout() && args(ip)",
            argNames = "ip,returning",
            returning = "returning")
    public void recordNtfLogout(String ip, int returning) {
        saveActivity(ip, resultOf(returning));
    }

    /**
     * Save activity after {@link InternalServerHandler#handleNtfLogout(String)}
     * throws an exception.
     * @param ip client ip.
     * @param cause cause.
     */
    @AfterThrowing(
            value = "inHandler() && ntfLogout() && args(ip)",
            argNames = "ip,cause",
            throwing = "cause")
    public void recordNtfLogout(String ip, Throwable cause) {
        saveActivity(ip, cause.getMessage());
    }
}
