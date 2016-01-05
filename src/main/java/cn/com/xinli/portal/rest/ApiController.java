package cn.com.xinli.portal.rest;

import cn.com.xinli.rest.api.Provider;
import cn.com.xinli.portal.configuration.ApiConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * REST APIs controller.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/15.
 */
@Controller
@RequestMapping("/${pws.root}/" + ApiConfiguration.API_PATH)
public class ApiController {

    @Autowired
    private Provider restApiProvider;

    @ResponseBody
    @RequestMapping
    public Provider api() {
        return restApiProvider;
    }
}
