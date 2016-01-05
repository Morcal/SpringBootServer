package cn.com.xinli.portal.admin;

import cn.com.xinli.rest.bean.RestBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
@RestController
@RequestMapping("/${pws.root}/admin/activities")
public class ActivityController {

    @RequestMapping
    public ResponseEntity<RestBean> summary() {
        //TODO implement summary.
        return null;
    }
}
