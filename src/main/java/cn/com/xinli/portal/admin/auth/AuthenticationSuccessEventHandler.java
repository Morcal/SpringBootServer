package cn.com.xinli.portal.admin.auth;

import cn.com.xinli.portal.admin.Activity;
import cn.com.xinli.portal.admin.ActivityService;
import cn.com.xinli.portal.repository.ActivityEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * Authentication success event handler.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
@Component
public class AuthenticationSuccessEventHandler implements ApplicationListener<AuthenticationSuccessEvent> {
    @Autowired
    private ActivityService activityService;

    @Autowired
    private Activity.Severity minimalSeverity;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        if (event.getSeverity().ordinal() <= minimalSeverity.ordinal()) {
            ActivityEntity activity = new ActivityEntity();
            activity.setAction(Activity.SessionAction.AUTHENTICATE.name());
            activity.setFacility(Activity.Facility.PORTAL);
            activity.setRemote(event.getRequest().getRemoteUser());
            activity.setResult("Authenticated");
            activity.setSeverity(event.getSeverity());
            activity.setCreated(Calendar.getInstance().getTime());
            activityService.log(activity);
        }
    }
}
