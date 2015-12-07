package cn.com.xinli.portal.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
@Controller
public class PortalWebServerController {
    @RequestMapping("/rd")
    public String redirectToPage() {
        return "redirect:redirect";
    }
}
