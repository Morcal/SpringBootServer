package cn.com.xinli.portal.web.admin.auth;

import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.web.admin.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * Authentication success event handler.
 *
 * Project: xpws
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
            Activity activity = new Activity();
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
