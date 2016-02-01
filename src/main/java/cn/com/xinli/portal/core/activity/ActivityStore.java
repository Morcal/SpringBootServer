package cn.com.xinli.portal.core.activity;

import cn.com.xinli.portal.core.DataStore;

import java.util.Date;

/**
 * Activity store.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
public interface ActivityStore extends DataStore<Activity, Long> {
    void deleteOlderThan(Date date);
}
