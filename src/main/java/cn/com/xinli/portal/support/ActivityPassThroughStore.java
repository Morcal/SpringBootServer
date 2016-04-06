package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.core.activity.ActivityStore;
import cn.com.xinli.portal.support.repository.ActivityRepository;
import cn.com.xinli.portal.util.QueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Activity Pass Through Store.
 *
 * <p>Since it's unnecessary to cache activity logs, this class implements
 * a pass through store, data will saved directly into database.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
@Component
@Profile("standalone")
public class ActivityPassThroughStore implements ActivityStore {
    @Qualifier("activityRepository")
    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public Activity get(Long id) throws ServerException {
        Objects.requireNonNull(id, Activity.EMPTY_ACTIVITY);
        Activity activity = activityRepository.findOne(id);
        if (activity == null) {
            throw new ServerException(
                    PortalError.SERVER_INTERNAL_ERROR, "activity not found:" + id);
        }
        return activity;
    }

    @Override
    public void put(Activity activity) {
        Objects.requireNonNull(activity, Activity.EMPTY_ACTIVITY);
        activityRepository.save(activity);
    }

    @Override
    public boolean exists(Long id) {
        Objects.requireNonNull(id, Activity.EMPTY_ACTIVITY);
        return activityRepository.exists(id);
    }

    @Override
    public boolean delete(Long id) throws Exception {
        Objects.requireNonNull(id, Activity.EMPTY_ACTIVITY);
        activityRepository.delete(id);
        return true;
    }

    @Override
    public void deleteOlderThan(Date date) {
        activityRepository.deleteOlderThan(date);
    }

    @Override
    public long count() {
        return activityRepository.count();
    }

    @Override
    public Stream<Activity> all() {
//        PageRequest request = new PageRequest(0, 25, Sort.Direction.DESC, "created");
//        Page<Activity> page = activityRepository.findAll(request);
//        return page.getContent().stream();

        return activityRepository.findTop25ByOrderByCreatedDesc();
    }

    @Override
    public Stream<Activity> search(String query) throws RemoteException {
        QueryUtil.checkQuery(query);
        Stream<Activity> stream = activityRepository.search(query);
        return stream.limit(25);
    }

    @Override
    public long count(String query) {
        return activityRepository.count(query);
    }
}
