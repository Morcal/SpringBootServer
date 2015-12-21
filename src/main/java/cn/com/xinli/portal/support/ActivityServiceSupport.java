package cn.com.xinli.portal.support;

import cn.com.xinli.portal.Activity;
import cn.com.xinli.portal.ActivityService;
import cn.com.xinli.portal.persist.ActivityEntity;
import cn.com.xinli.portal.persist.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Activity Service JPA support.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
public class ActivityServiceSupport implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public void log(Activity activity) {
        if (activity != null && activity instanceof ActivityEntity)
        activityRepository.save((ActivityEntity) activity);
    }
}
