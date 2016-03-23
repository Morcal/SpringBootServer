package cn.com.xinli.portal.web.controller;

import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.core.activity.ActivityStore;
import cn.com.xinli.portal.web.rest.ActivityResponse;
import cn.com.xinli.portal.web.rest.AdminResponseBuilders;
import cn.com.xinli.portal.web.rest.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Stream;

/**
 * Administration activity controller.
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
@RestController
@RequestMapping("/portal/admin/v1.0/activities")
public class ActivityController {

    @Autowired
    private ActivityStore activityStore;

    @RequestMapping
    public ResponseEntity<RestResponse> summary() {
        //TODO implement summary.
        return null;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public ActivityResponse search(@RequestParam(defaultValue = "") String query) {
        final Stream<Activity> stream;
        final long count;

        if (StringUtils.isEmpty(query)) {
            count = activityStore.count();
            stream = activityStore.all();
        } else {
            count = activityStore.count(query);
            stream = activityStore.search(query);
        }

        return AdminResponseBuilders.activityResponseBuilder()
                .setStream(stream)
                .setCount(count)
                .build();
    }

    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ActivityResponse get(@PathVariable("id") Long id) throws Exception {
        return AdminResponseBuilders.activityResponseBuilder()
                .setCount(1)
                .setStream(Stream.of(activityStore.get(id)))
                .build();
    }

}
