package cn.com.xinli.portal.support;

import cn.com.xinli.portal.Activity;
import cn.com.xinli.portal.ActivityService;
import cn.com.xinli.portal.persist.ActivityEntity;
import cn.com.xinli.portal.persist.ActivityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

/**
 * Activity Service JPA support.
 * <p>
 * Service provides abilities to save activity log to database.
 * And clean old activities automatically (based on configuration).
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
@Service
@Transactional
@EnableScheduling
public class ActivityServiceSupport implements ActivityService {
    /**
     * Logger.
     */
    private final Logger logger = LoggerFactory.getLogger(ActivityServiceSupport.class);

    @Value("${pws.database.derby.keep.recent.days}")
    private int mostRecent;

    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public void log(Activity activity) {
        if (activity != null && activity instanceof ActivityEntity) {
            if (logger.isTraceEnabled()) {
                logger.trace("Saving activity {}", activity);
            }

            activityRepository.save((ActivityEntity) activity);
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
        calendar.add(Calendar.DATE, -1 * mostRecent);
        Date oldest = calendar.getTime();
        logger.info("Deleting old activities before {}", oldest);

        activityRepository.deleteOlderThan(oldest);
    }
}
