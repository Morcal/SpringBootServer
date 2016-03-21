package cn.com.xinli.portal.web.admin;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.support.admin.AdminCredentials;
import cn.com.xinli.portal.support.admin.AdminService;
import cn.com.xinli.portal.web.auth.AccessAuthentication;
import cn.com.xinli.portal.web.auth.AuthorizationServer;
import cn.com.xinli.portal.web.auth.ChallengeAuthority;
import cn.com.xinli.portal.web.auth.challenge.Challenge;
import cn.com.xinli.portal.web.auth.token.AdminTokenService;
import cn.com.xinli.portal.web.auth.token.RestToken;
import cn.com.xinli.portal.web.configuration.SecurityConfiguration;
import cn.com.xinli.portal.web.rest.Provider;
import cn.com.xinli.portal.web.rest.RestResponse;
import cn.com.xinli.portal.web.rest.RestResponseBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * System administration controller.
 * @author zhoupeng, created on 2016/3/20.
 */
@Controller
@RequestMapping("/portal/admin")
public class AdminController {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    @Qualifier("admin-api-provider")
    private Provider adminRestApiProvider;

    @Autowired
    private AdminTokenService adminTokenService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthorizationServer authorizationServer;

    @Autowired
    private ServerConfiguration serverConfiguration;

    @ResponseBody
    @RequestMapping("/api")
    public Provider api(HttpServletRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("{} ==> http://localhost{}", request.getMethod(), request.getRequestURI());
        }

        return adminRestApiProvider;
    }

    @ResponseBody
    @RequestMapping("/v1.0/authorize")
    public RestResponse authorize(@RequestParam(name = "response_type") String responseType,
                                  @RequestParam(name = "scope") String scope,
                                  @RequestParam(name = "require_token") boolean requireToken,
                                  @RequestParam(name = "need_refresh_token") boolean needRefreshToken)
            throws RemoteException {
        if (logger.isDebugEnabled()) {
            logger.debug("ADMIN > authorize");
        }

        if (SecurityConfiguration.CHALLENGE_RESPONSE_TYPE.equals(responseType)) {
            Challenge challenge =
                    authorizationServer.createChallenge("0", scope, requireToken, needRefreshToken);
            RestResponseBuilders.SessionResponseBuilder builder =  RestResponseBuilders.successBuilder()
                    .setChallenge(challenge);
            builder.setChallengeTtl(serverConfiguration.getRestConfiguration().getChallengeTtl());

            return builder.build();
        } else {
            throw new RemoteException(PortalError.UNSUPPORTED_RESPONSE_TYPE);
        }
    }

    @ResponseBody
    @RequestMapping("/v1.0/login")
    @PreAuthorize("hasRole('PRE_AUTH')")
    public RestResponse login(@RequestParam String username,
                              @RequestParam String password,
                              @AuthenticationPrincipal Principal principal) throws PortalException {
        if (logger.isDebugEnabled()) {
            logger.debug("ADMIN > login");
        }

        String challenge = null;

        AccessAuthentication authentication = (AccessAuthentication) principal;

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority instanceof ChallengeAuthority) {
                challenge = ChallengeAuthority.class.cast(authority).getAuthority();
            }
        }

        AdminCredentials credentials = new AdminCredentials();
        credentials.setUsername(username);
        credentials.setPassword(password);

        credentials = adminService.verify(challenge, credentials);
        /* reallocate a new session token. */
        RestToken token =
                (RestToken) adminTokenService.allocateToken(adminTokenService.encode(credentials));

        authentication.setAccessToken(token);
        if (logger.isTraceEnabled()) {
            logger.trace("login success, user: {}", username);
        }

        return RestResponseBuilders.buildSessionResponse(
                serverConfiguration, null, authentication, false, null);
    }
}
