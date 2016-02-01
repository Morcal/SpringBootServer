package cn.com.xinli.portal.support.persist;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.support.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * Activity persistence.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
@Component
public class ActivityPersistence {
    @Qualifier("activityRepository")
    @Autowired
    private ActivityRepository activityRepository;

    public Activity load(Long id) throws ServerException {
        Objects.requireNonNull(id);
        Activity activity = activityRepository.findOne(id);
        if (activity == null) {
            throw new ServerException(PortalError.SERVER_INTERNAL_ERROR, "activity not found:" + id);
        }
        return activity;
    }

    public Activity find(Long id) {
        Objects.requireNonNull(id);
        return activityRepository.findOne(id);
    }

    public void save(Activity activity) {
        Objects.requireNonNull(activity);
        activityRepository.save(activity);
    }

    public void delete(Long id) {
        Objects.requireNonNull(id);
        activityRepository.delete(id);
    }

    public void deleteOlderThan(Date date) {
        activityRepository.deleteOlderThan(date);
    }
}
