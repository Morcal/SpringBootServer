package cn.com.xinli.portal.core.activity;

import cn.com.xinli.portal.core.DataStore;
import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.core.session.Session;

import java.util.Date;
import java.util.stream.Stream;

/**
 * Activity store.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
public interface ActivityStore extends DataStore<Activity, Long> {
    /**
     * Delete records older than given date.
     * @param date date.
     */
    void deleteOlderThan(Date date);


    /**
     * Count all activities.
     * @return session count.
     */
    long count();

    /**
     * Get all activities.
     * @return stream of existed activities.
     */
    Stream<Activity> all();

    /**
     * Search activities.
     * @param query search keyword.
     * @return stream of resulting activities.
     */
    Stream<Activity> search(String query) throws RemoteException;

    /**
     * Count activities by keyword.
     * @param query search keyword.
     * @return resulting count.
     */
    long count(String query);
}
