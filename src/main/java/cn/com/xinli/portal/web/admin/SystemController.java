package cn.com.xinli.portal.web.admin;

import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.NasStore;
import cn.com.xinli.portal.web.rest.NasResponse;
import cn.com.xinli.portal.web.rest.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

/**
 * System administration controller.
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
@RestController
@RequestMapping("/portal/admin/v1.0")
public class SystemController {

    @Autowired
    private NasStore nasStore;

    @RequestMapping
    public ResponseEntity<RestResponse> summary() {
        //TODO implement summary.
        return null;
    }

    @RequestMapping("/nas")
    @ResponseBody
    public RestResponse searchNas(@RequestParam(defaultValue = "") String query) {
        final Stream<Nas> stream;
        if (StringUtils.isEmpty(query)) {
            stream = nasStore.devices();
        } else {
            stream = nasStore.search(query);
        }

        NasResponse response = new NasResponse();
        response.setStream(stream);
        return response;
    }

}
