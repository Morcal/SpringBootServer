package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.PortalException;
import cn.com.xinli.portal.SessionService;
import cn.com.xinli.portal.User;
import cn.com.xinli.portal.util.AddressUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Portal web server session REST APIs controller.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */

@RestController
@RequestMapping("/v1.0/session")
public class PortalController {

    @Autowired
    public SessionService service;

    @RequestMapping(method = RequestMethod.PUT)
    public Result create(@RequestParam String credential, Map<String, Object> model, HttpServletRequest request) {
        //TODO implement create session process.
        String addr = AddressUtil.getRemoteAddress(request);
        String mac = (String) model.get(Parameter.IP_ADDRESS);

        User user = User.create(addr, mac);
        try {
            service.createSession(user);
        } catch (PortalException e) {
            // Catch business Exception here, and let spring handle
            // other exceptions.
        }
        return null;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Result get(@PathVariable String id) {
        //TODO implement get session information.
        return null;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public Result remove(@PathVariable String id) {
        //TODO implement remove session process.
        return null;
    }


}
