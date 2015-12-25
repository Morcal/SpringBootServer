package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.auth.AuthorizationServer;
import cn.com.xinli.portal.rest.RestResponse;
import cn.com.xinli.portal.rest.RestResponseBuilders;
import cn.com.xinli.portal.rest.auth.challenge.Challenge;
import cn.com.xinli.portal.rest.bean.RestBean;
import cn.com.xinli.portal.rest.configuration.ApiConfiguration;
import cn.com.xinli.portal.rest.configuration.SecurityConfiguration;
import cn.com.xinli.portal.util.AddressUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/8.
 */
@Controller
@RequestMapping("/${pws.root}/" + ApiConfiguration.REST_API_VERSION)
public class AuthorizeController {
    /** Log. */
    private static final Log log = LogFactory.getLog(AuthorizeController.class);

    @Autowired
    private AuthorizationServer authorizationServer;

    @ResponseBody
    @RequestMapping("/" + ApiConfiguration.REST_API_AUTHORIZE)
    public RestBean authorize(@RequestHeader(name = "X-Real-Ip", defaultValue = "") String realIp,
                              @RequestParam(name = "response_type") String responseType,
                              @RequestParam(name = "client_id") String clientId,
                              @RequestParam(name = "scope") String scope,
                              @RequestParam(name = "require_token") boolean requireToken,
                              @RequestParam(name = "need_refresh_token") boolean needRefreshToken,
                              @RequestParam(name = "user_ip", defaultValue = "") String ip,
                              @RequestParam(name = "user_mac", defaultValue = "") String mac,
                              HttpServletRequest request) {
        String error = RestResponse.ERROR_INVALID_REQUEST;
        String description;

        while (true) {
            /*
            if (StringUtils.isEmpty(ip) || StringUtils.isEmpty(mac)) {
                description = "Missing ip address or mac.";
                break; // missing ip or mac.
            }
            */

            if ((!StringUtils.isEmpty(realIp) || !StringUtils.isEmpty(ip)) &&
                    !AddressUtil.validateIp(realIp, ip, request)) {
                description = "Given ip differs from which the server knows.";
                break; // invalid ip.
            }

            if (log.isDebugEnabled()) {
                log.debug("> incoming authorize request, from: " + ip + ", mac: " + mac);
            }

            if (SecurityConfiguration.CHALLENGE_RESPONSE_TYPE.equals(responseType)) {
                if (!authorizationServer.certificated(clientId)) {
                    error = RestResponse.ERROR_INVALID_CLIENT;
                    description = "Client id: " + clientId + " not certificated.";
                    break;
                } else {
                    Challenge challenge =
                            authorizationServer.createChallenge(clientId, scope, requireToken, needRefreshToken);
                    return RestResponseBuilders.successBuilder()
                            .setChallenge(challenge)
                            .build();
                }
            }
        }

        return RestResponseBuilders.errorBuilder()
                .setError(error)
                .setDescription(description)
                .build();
    }
}
