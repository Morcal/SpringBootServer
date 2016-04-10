package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.core.activity.ActivityService;
import cn.com.xinli.portal.core.activity.ActivityStore;
import cn.com.xinli.portal.core.configuration.ServerConfigurationService;
import cn.com.xinli.portal.support.aspect.SystemActivityAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

/**
 * Activity Service JPA support.
 *
 * <p>Service provides abilities to save activity log to database.
 * And clean old activities automatically (based on configuration).
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
@Service
@Transactional(rollbackFor = DataAccessException.class)
@EnableScheduling
public class ActivityServiceSupport implements ActivityService {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(ActivityServiceSupport.class);

    @Autowired
    private ActivityStore activityStore;

    @Autowired
    private ServerConfigurationService serverConfigurationService;

    @Override
    public void log(Activity activity) {
        if (activity != null) {
            if (logger.isTraceEnabled()) {
                logger.trace("Saving activity {}", activity);
            }

            activityStore.put(activity);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delete old activities every day at 4:00AM.
     * This method will be recorded as well.
     *
     * @see SystemActivityAspect
     */
    @Override
    @Scheduled(cron = "0 0 4 * * *")
    public void deleteOldActivities() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,
                -1 * serverConfigurationService.getServerConfiguration().getActivityConfiguration().getMostRecent());
        Date date = calendar.getTime();
        logger.info("Deleting old activities before {}", date);

        activityStore.deleteOlderThan(date);
    }
}
