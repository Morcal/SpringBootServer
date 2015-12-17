package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.auth.AuthorizationServer;
import cn.com.xinli.portal.rest.RestResponse;
import cn.com.xinli.portal.rest.RestResponseBuilders;
import cn.com.xinli.portal.rest.auth.challenge.Challenge;
import cn.com.xinli.portal.rest.bean.RestBean;
import cn.com.xinli.portal.rest.configuration.RestApiConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/8.
 */
@Controller
@RequestMapping("/${application}/" + RestApiConfiguration.REST_API_VERSION)
public class AuthorizeController {
    /** Log. */
    private static final Log log = LogFactory.getLog(RestAuthorizationServer.class);

    /** Challenge response type. */
    public static final String CHALLENGE_RESPONSE_TYPE = "challenge";

    @Autowired
    private AuthorizationServer authorizationServer;


    @RequestMapping("/" + RestApiConfiguration.REST_API_AUTHORIZE)
    public ResponseEntity<RestBean> authorize(@RequestParam(name = "response_type") String responseType,
                                              @RequestParam(name = "client_id") String clientId,
                                              @RequestParam(name = "scope") String scope,
                                              @RequestParam(name = "require_token") boolean requireToken,
                                              @RequestParam(name = "need_refresh_token") boolean needRefreshToken,
                                              @RequestParam(name = "user_ip", defaultValue = "") String ip,
                                              @RequestParam(name = "user_mac", defaultValue = "") String mac) {
        if (log.isDebugEnabled()) {
            log.debug("> incoming authorize request, from: " + ip + ", mac: " + mac);
        }

        if (CHALLENGE_RESPONSE_TYPE.equals(responseType)) {
            Challenge challenge = authorizationServer.createChallenge(clientId, scope, requireToken, needRefreshToken);
            return ResponseEntity.ok(RestResponseBuilders.authenticationBuilder(challenge).build());
        } else {
            return ResponseEntity.ok(RestResponseBuilders.errorBuilder()
                    .setError(RestResponse.ERROR_INVALID_REQUEST)
                    .setDescription("Response type not supported.")
                    .build());
        }
    }
}
