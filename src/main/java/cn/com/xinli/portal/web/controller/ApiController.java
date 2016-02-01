package cn.com.xinli.portal.web.controller;

import cn.com.xinli.portal.web.rest.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * REST APIs controller.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/15.
 */
@Controller
@RequestMapping("/portal/api")
public class ApiController {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private Provider restApiProvider;

    @ResponseBody
    @RequestMapping
    public Provider api(HttpServletRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("{} ==> http://localhost{}", request.getMethod(), request.getRequestURI());
        }

        return restApiProvider;
    }
}
