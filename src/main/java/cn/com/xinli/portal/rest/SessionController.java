package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.PortalException;
import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.SessionService;
import cn.com.xinli.portal.auth.AuthorizationServer;
import cn.com.xinli.portal.auth.AuthorizationServerImpl;
import cn.com.xinli.portal.auth.SessionToken;
import cn.com.xinli.portal.configuration.Nas;
import cn.com.xinli.portal.configuration.NasMapping;
import cn.com.xinli.portal.util.AddressUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Portal web server session REST APIs controller.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
@RestController
@RequestMapping("/portal/v1.0")
public class SessionController {
    /** Log. */
    private static final Log log = LogFactory.getLog(SessionController.class);

    @Autowired
    private SessionService sessionService;

    @Autowired
    private AuthorizationServer authorizationServer;

    @Autowired
    private NasMapping nasMapping;

    @RequestMapping(value = "/sessions", method = RequestMethod.POST)
    public Object connect(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String user_ip,
                          @RequestParam String user_mac,
                          @RequestParam String os,
                          @RequestParam String version,
                          HttpServletRequest request) {

        //TODO implement create session process.
        String addr = AddressUtil.getRemoteAddress(request);

        Nas nas = nasMapping.findNas(user_ip, user_mac);
        if (nas == null) {
            /* NAS not found. */
            return RestResponseBuilders.errorBuilder().setError(
                    RestResponse.ERROR_INVALID_PORTAL_REQUEST)
                    .setDescription("nas not found for ip: " + user_ip + ", mac: " + user_mac)
                    .build();
        }

        Session session;
        try {
            session = sessionService.createSession(user_ip, user_mac, nas.getId());
            log.info(session.toString() + " created.");

            //FIXME session may be removed by other thread.
            SessionToken token = authorizationServer.generateSessionToken(session);
            log.info(token.toString() + " created.");

            return RestResponseBuilders.sessionResponseBuilder()
                    .setSession(session)
                    .setExpiresIn(AuthorizationServerImpl.DEFAULT_SESSION_TOKEN_EXPIRE)
                    .setToken(token.text())
                    .build();
        } catch (PortalException e) {
            // Catch business Exception here, and let spring handle
            // other exceptions.
            e.printStackTrace();

            return RestResponseBuilders.errorBuilder()
                    .setError(RestResponse.ERROR_SERVER_ERROR)
                    .build();
        }
    }

    @RequestMapping(value = "/session/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable String id) {
        //TODO implement get session information.
        return null;
    }


    @RequestMapping(value = "/session/{id}", method = RequestMethod.POST)
    public Object update(@PathVariable String id) {
        //TODO implement get session information.
        return null;
    }

    @RequestMapping(value = "/session/{id}", method = RequestMethod.DELETE)
    public Object disconnect(@PathVariable String id) {
        //TODO implement remove session process.
        return null;
    }


}
