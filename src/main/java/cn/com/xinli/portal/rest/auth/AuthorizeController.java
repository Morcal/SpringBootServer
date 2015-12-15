package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.auth.AuthorizationServer;
import cn.com.xinli.portal.rest.RestResponse;
import cn.com.xinli.portal.rest.RestResponseBuilders;
import cn.com.xinli.portal.rest.configuration.RestApiConfiguration;
import cn.com.xinli.portal.rest.configuration.RestSecurityConfiguration;
import cn.com.xinli.portal.util.AddressUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

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
    public Object authorize(@RequestHeader(value="X-Real-Ip") String realIp,
                            @RequestHeader(value=HttpDigestCredentials.HEADER_NAME) String credentials,
                            @RequestParam(name = "response_type") String responseType,
                            @RequestParam(name = "client_id") String clientId,
                            @RequestParam(name = "scope") String scope,
                            @RequestParam(name = "require_token") String requireToken,
                            @RequestParam(name = "need_refresh_token") String needRefreshToken,
                            @RequestParam(name = "user_ip") String ip,
                            @RequestParam(name = "user_mac") String mac,
                            HttpServletRequest request) {

        if (!AddressUtil.isValidateIp(realIp, ip, request)) {
            return "redirect:main";
        }

        if (CHALLENGE_RESPONSE_TYPE.equals(responseType)) {
            return authorizationServer.createChallenge(clientId);
        } else {
            return RestResponseBuilders.errorBuilder()
                    .setError(RestResponse.ERROR_INVALID_REQUEST)
                    .setDescription("Response type not supported.")
                    .build();
        }
    }
}
