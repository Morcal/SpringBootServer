package cn.com.xinli.portal.web.auth.event;

import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.core.activity.ActivityService;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * Authentication failure event handler.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/11.
 */
@Component
public class AuthenticationFailureEventHandler implements ApplicationListener<AuthenticationFailureEvent> {
    @Autowired
    private ActivityService activityService;

    @Autowired
    private ServerConfiguration serverConfiguration;

    @Override
    public void onApplicationEvent(AuthenticationFailureEvent event) {
        if (event.getSeverity().ordinal() <=
                serverConfiguration.getActivityConfiguration().getMinimumSevertiy().ordinal()) {
            Activity activity = new Activity();
            activity.setAction(Activity.SessionAction.AUTHENTICATE.name());
            activity.setFacility(Activity.Facility.PORTAL);
            activity.setRemote(event.getRequest().getRemoteUser());
            activity.setResult("Failed");
            activity.setSeverity(event.getSeverity());
            activity.setCreated(Calendar.getInstance().getTime());
            activityService.log(activity);
        }
    }
}
