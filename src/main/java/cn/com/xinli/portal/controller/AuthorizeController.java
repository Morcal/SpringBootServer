package cn.com.xinli.portal.controller;

import cn.com.xinli.portal.configuration.SecurityConfiguration;
import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.support.rest.RestResponse;
import cn.com.xinli.portal.support.rest.RestResponseBuilders;
import cn.com.xinli.portal.auth.challenge.Challenge;
import cn.com.xinli.portal.service.AuthorizationServer;
import cn.com.xinli.portal.util.AddressUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/portal/v1.0")
public class AuthorizeController {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(AuthorizeController.class);

    @Autowired
    private AuthorizationServer authorizationServer;

    @Value("${pws.nat.allowed}") private boolean natAllowed;

    @ResponseBody
    @RequestMapping("/authorize")
    public RestResponse authorize(@RequestHeader(name = "X-Real-Ip", defaultValue = "") String realIp,
                                  @RequestParam(name = "response_type") String responseType,
                                  @RequestParam(name = "client_id") String clientId,
                                  @RequestParam(name = "scope") String scope,
                                  @RequestParam(name = "require_token") boolean requireToken,
                                  @RequestParam(name = "need_refresh_token") boolean needRefreshToken,
                                  @RequestParam(name = "user_ip", defaultValue = "") String ip,
                                  @RequestParam(name = "user_mac", defaultValue = "") String mac,
                                  HttpServletRequest request) throws RemoteException {
        if (logger.isDebugEnabled()) {
            logger.debug("{} ==> {}", request.getMethod(), request.getRequestURI());
        }

        /*
        if (StringUtils.isEmpty(ip) || StringUtils.isEmpty(mac)) {
            description = "Missing ip address or mac.";
            break; // missing ip or mac.
        }
        */

        if (!natAllowed) {
            if ((!StringUtils.isEmpty(realIp) || !StringUtils.isEmpty(ip)) &&
                    !AddressUtil.validateIp(realIp, ip, request)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("IP check failed, real: {} , remote: {} , given: {}.",
                            realIp, request.getRemoteAddr(), ip);
                }
                throw new RemoteException(PortalError.of("nat_not_allowed"), "not not allowed.");
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("incoming authorize request, from: {}, mac: {}.", ip, mac);
        }

        if (SecurityConfiguration.CHALLENGE_RESPONSE_TYPE.equals(responseType)) {
            if (!authorizationServer.certificated(clientId)) {
                throw new RemoteException(
                        PortalError.of("invalid_certificate"), "invalid certificate");
            } else {
                Challenge challenge =
                        authorizationServer.createChallenge(clientId, scope, requireToken, needRefreshToken);
                return RestResponseBuilders.successBuilder()
                        .setChallenge(challenge)
                        .build();
            }
        } else {
            throw new RemoteException(
                    PortalError.of("unsupported_response_type"), "unsupported response type");
        }
    }
}
