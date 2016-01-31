package cn.com.xinli.portal.web.admin;

import cn.com.xinli.portal.web.rest.RestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
@RestController
@RequestMapping("/portal/admin/system")
public class SystemController {

    @RequestMapping
    public ResponseEntity<RestResponse> summary() {
        //TODO implement summary.
        return null;
    }
}