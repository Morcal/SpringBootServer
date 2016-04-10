package cn.com.xinli.portal.web.auth.event;

import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.core.activity.ActivityService;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.configuration.ServerConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * Authentication success event handler.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/11.
 */
@Component
public class AuthenticationSuccessEventHandler
        implements ApplicationListener<AuthenticationSuccessEvent> {
    @Autowired
    private ActivityService activityService;

    @Autowired
    private ServerConfigurationService serverConfigurationService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        if (event.getSeverity().ordinal() <=
                serverConfigurationService.getServerConfiguration().getActivityConfiguration().getMinimumSeverity().ordinal()) {
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
