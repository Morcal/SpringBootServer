package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.core.activity.ActivityStore;
import cn.com.xinli.portal.support.persist.ActivityPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Date;
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
    @Autowired
    private ActivityPersistence activityPersistence;

    @Override
    public Activity get(Long id) throws ServerException {
        return activityPersistence.load(id);
    }

    @Override
    public void put(Activity activity) {
        activityPersistence.save(activity);
    }

    @Override
    public boolean exists(Long id) {
        return activityPersistence.find(id) != null;
    }

    @Override
    public boolean delete(Long id) throws Exception {
        activityPersistence.delete(id);
        return true;
    }

    @Override
    public void deleteOlderThan(Date date) {
        activityPersistence.deleteOlderThan(date);
    }

    @Override
    public long count() {
        return activityPersistence.count();
    }

    @Override
    public Stream<Activity> all() {
        return activityPersistence.all();
    }

    @Override
    public Stream<Activity> search(String query) {
        return activityPersistence.search(query);
    }

    @Override
    public long count(String query) {
        return activityPersistence.count(query);
    }
}
