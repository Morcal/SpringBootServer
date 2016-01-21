package cn.com.xinli.portal.service;

import cn.com.xinli.portal.admin.Activity;
import cn.com.xinli.portal.admin.ActivityService;
import cn.com.xinli.portal.repository.ActivityEntity;
import cn.com.xinli.portal.repository.ActivityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
        Date date = calendar.getTime();
        logger.info("Deleting old activities before {}", date);

        activityRepository.deleteOlderThan(date);
    }
}
