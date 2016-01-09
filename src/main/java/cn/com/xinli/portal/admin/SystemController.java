package cn.com.xinli.portal.admin;

import cn.com.xinli.rest.RestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
@RestController
@RequestMapping("/${pws.root}/admin/system")
public class SystemController {

    @RequestMapping
    public ResponseEntity<RestResponse> summary() {
        //TODO implement summary.
        return null;
    }
}
