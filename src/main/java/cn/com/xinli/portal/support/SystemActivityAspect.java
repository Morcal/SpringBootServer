package cn.com.xinli.portal.support;

import cn.com.xinli.portal.Activity;
import cn.com.xinli.portal.ActivityService;
import cn.com.xinli.portal.persist.ActivityEntity;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * System activity aspect.
 * <p>
 * Service provides system activities log via {@link ActivityService}.
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2016/1/10.
 */
@Aspect
@Service
public class SystemActivityAspect {
    @Autowired
    private ActivityService activityService;

    /**
     * Save activity.
     *
     * @param result result.
     */
    private void saveActivity(String result) {
        ActivityEntity activity = new ActivityEntity();
        activity.setSource("system");
        activity.setFacility(Activity.Facility.SYSTEM);
        activity.setAction(Activity.SystemAction.DELETE_OLD_ACTIVITIES.name());
        activity.setRemote("system");
        activity.setResult(result);
        activity.setSeverity(Activity.Severity.NORMAL);
        activity.setSourceInfo("system automation");
        activity.setCreated(Calendar.getInstance().getTime());
        activityService.log(activity);
    }

    /**
     * Define method pointcut for
     * {@link ActivityService#deleteOldActivities()}.
     */
    @Pointcut("execution(* cn.com.xinli.portal.ActivityService.deleteOldActivities(..))")
    public void deleteOldActivities() {
    }

    /**
     * Save activity log after {@link ActivityService#deleteOldActivities()}
     * returns normally.
     */
    @AfterReturning(value = "deleteOldActivities()")
    public void recordDeleteOldActivities() {
        saveActivity("ok");
    }

    /**
     * Save activity log after {@link ActivityService#deleteOldActivities()}
     * throws an exception.
     */
    @AfterThrowing(value = "deleteOldActivities()", throwing = "cause")
    public void recordDeleteOldActivities(Throwable cause) {
        saveActivity(cause.getMessage());
    }
}
