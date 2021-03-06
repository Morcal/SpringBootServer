package cn.com.xinli.portal.web.controller;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.core.configuration.ServerConfigurationService;
import cn.com.xinli.portal.web.auth.AuthorizationServer;
import cn.com.xinli.portal.web.auth.challenge.Challenge;
import cn.com.xinli.portal.web.configuration.SecurityConfiguration;
import cn.com.xinli.portal.web.rest.RestResponse;
import cn.com.xinli.portal.web.rest.RestResponseBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Authorize controller.
 *
 * <p>Project: xpws
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

    @Autowired
    private ServerConfigurationService serverConfigurationService;

    /**
     * Handle challenge requests.
     * @param realIp real ip if requests been proxied.
     * @param responseType response type.
     * @param clientId client/app id.
     * @param scope token scope.
     * @param requireToken if request requires token.
     * @param needRefreshToken if request requires refresh token.
     * @param ip user ip address.
     * @param mac user mac address.
     * @param request HTTP request.
     * @return rest response.
     * @throws RemoteException
     */
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
            logger.debug("{} ==> http://localhost{}", request.getMethod(), request.getRequestURI());
        }

        if (!authorizationServer.verifyIp(realIp, ip, request.getRemoteAddr())) {
            throw new RemoteException(PortalError.NAT_NOT_ALLOWED);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("incoming authorize request, from: {}, mac: {}.", ip, mac);
        }

        if (SecurityConfiguration.CHALLENGE_RESPONSE_TYPE.equals(responseType)) {
            if (!authorizationServer.certificated(clientId)) {
                throw new RemoteException(PortalError.INVALID_CERTIFICATE);
            } else {
                Challenge challenge =
                        authorizationServer.createChallenge(clientId, scope, requireToken, needRefreshToken);
                RestResponseBuilders.SessionResponseBuilder builder =
                        RestResponseBuilders.successBuilder().setChallenge(challenge);
                builder.setChallengeTtl(serverConfigurationService.getServerConfiguration().getRestConfiguration().getChallengeTtl());

                return builder.build();
            }
        } else {
            throw new RemoteException(PortalError.UNSUPPORTED_RESPONSE_TYPE);
        }
    }

}
