package cn.com.xinli.portal.support;

import cn.com.xinli.portal.Activity;
import cn.com.xinli.portal.ActivityService;
import cn.com.xinli.portal.persist.ActivityEntity;
import cn.com.xinli.portal.persist.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Activity Service JPA support.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
@Service
@Transactional
public class ActivityServiceSupport implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public void log(Activity activity) {
        if (activity != null && activity instanceof ActivityEntity)
        activityRepository.save((ActivityEntity) activity);
    }
}
