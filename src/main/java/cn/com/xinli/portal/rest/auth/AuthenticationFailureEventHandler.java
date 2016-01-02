package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.Activity;
import cn.com.xinli.portal.ActivityService;
import cn.com.xinli.portal.persist.ActivityEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * Authentication failure event handler.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
@Component
public class AuthenticationFailureEventHandler implements ApplicationListener<AuthenticationFailureEvent> {
    @Autowired
    private ActivityService activityService;

    @Autowired
    private Activity.Severity minimalSeverity;

    @Override
    public void onApplicationEvent(AuthenticationFailureEvent event) {
        if (event.getSeverity().ordinal() <= minimalSeverity.ordinal()) {
            ActivityEntity activity = new ActivityEntity();
            activity.setAction(Activity.Action.AUTHENTICATE.name());
            activity.setFacility(Activity.Facility.PORTAL);
            activity.setRemote(event.getRequest().getRemoteUser());
            activity.setResult("Failed");
            activity.setSeverity(event.getSeverity());
            activity.setTimestamp(Calendar.getInstance().getTime());
            activityService.log(activity);
        }
    }
}
