package cn.com.xinli.portal.rest.api.v1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/8.
 */
@Controller
public class AuthorizeController {

    @RequestMapping("/portal/v1.0/authorize")
    public String authorize() {
        return null;
    }


}
