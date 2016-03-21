package cn.com.xinli.portal.core.activity;

/**
 * Portal web server activity service.
 *
 * <p>Service provides abilities to save activity log to database.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
public interface ActivityService {
    /**
     * Log an activity.
     *
     * <p>Given activity may be persisted according to its
     * {@link Activity.Severity} and(or)
     * {@link Activity.Facility} and(or)
     * {@link Activity.SessionAction}.
     *
     * @param activity activity to logger.
     */
    void log(Activity activity);

    /**
     * Delete old activities.
     */
    void deleteOldActivities();
}
