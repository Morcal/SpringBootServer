package cn.com.xinli.portal.support.persist;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.support.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Activity persistence.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
@Component
public class ActivityPersistence {
//    /** Logger. */
//    private final Logger logger = LoggerFactory.getLogger(ActivityPersistence.class);

    @Qualifier("activityRepository")
    @Autowired
    private ActivityRepository activityRepository;

    public Activity load(Long id) throws ServerException {
        Objects.requireNonNull(id, Activity.EMPTY_ACTIVITY);
        Activity activity = activityRepository.findOne(id);
        if (activity == null) {
            throw new ServerException(PortalError.SERVER_INTERNAL_ERROR, "activity not found:" + id);
        }
        return activity;
    }

    public Activity find(Long id) {
        Objects.requireNonNull(id, Activity.EMPTY_ACTIVITY);
        return activityRepository.findOne(id);
    }

    public void save(Activity activity) {
        Objects.requireNonNull(activity, Activity.EMPTY_ACTIVITY);
        activityRepository.save(activity);
    }

    public void delete(Long id) {
        Objects.requireNonNull(id, Activity.EMPTY_ACTIVITY);
        activityRepository.delete(id);
    }

    public void deleteOlderThan(Date date) {
        activityRepository.deleteOlderThan(date);
    }

    public long count() {
        return activityRepository.count();
    }

    public long count(String query) {
        return activityRepository.count(query);
    }

    public Stream<Activity> all() {
        Page<Activity> page = activityRepository.findAll(new PageRequest(0, 25));
        return page.getContent().stream();
    }

    public Stream<Activity> search(String query) {
        Stream<Activity> stream = activityRepository.search(query);
        return stream.limit(25);
    }
}
