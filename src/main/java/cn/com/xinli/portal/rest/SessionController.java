package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.*;
import cn.com.xinli.portal.auth.AuthorizationServer;
import cn.com.xinli.portal.persist.SessionEntity;
import cn.com.xinli.portal.rest.auth.HttpDigestCredentials;
import cn.com.xinli.portal.rest.auth.RestAccessAuthentication;
import cn.com.xinli.portal.rest.bean.*;
import cn.com.xinli.portal.rest.configuration.CachingConfiguration;
import cn.com.xinli.portal.rest.configuration.RestApiConfiguration;
import cn.com.xinli.portal.rest.token.RestSessionToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Portal web server session REST APIs controller.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
@RestController
@RequestMapping("/${application}/" + RestApiConfiguration.REST_API_VERSION)
public class SessionController {
    /** Log. */
    private static final Log log = LogFactory.getLog(SessionController.class);

    @Autowired
    private SessionService restSessionService;

    @Autowired
    private AuthorizationServer authorizationServer;

    @Autowired
    private KeepAliveConfiguration keepAliveConfiguration;

    @Autowired
    private NasMapping nasMapping;

    @RequestMapping(
            value = "/" + RestApiConfiguration.REST_API_SESSIONS,
            method = RequestMethod.POST)
    public ResponseEntity<RestBean> connect(@RequestParam String username,
                                            @RequestParam String password,
                                            @RequestParam(name = "user_ip") String ip,
                                            @RequestParam(name = "user_mac") String mac,
                                            @RequestParam String os,
                                            @RequestParam String version,
                                            HttpServletRequest request) {
        // Get NAS if mapped.
        Nas nas = nasMapping.findNas(ip, mac);
        if (nas == null) {
            /* NAS not found. */
            Failure failure = new Failure();
            failure.setError(RestResponse.ERROR_INVALID_PORTAL_REQUEST);
            failure.setDescription("nas not found for ip: " + ip + ", mac: " + mac);
            return ResponseEntity.ok(failure);
        }

        // Create portal session.
        SessionEntity session = SessionEntity.builder().setUsername(username)
                .setNasId(nas.getId())
                .setIp(ip)
                .setMac(mac)
                .setOs(os)
                .setVersion(version)
                .setPassword(password)
                .build();
        restSessionService.createSession(session);

        log.info("> " + session.toString() + " created.");

        // create session authorization. FIXME session may be removed by other threads.
        RestSessionToken token = authorizationServer.allocateSessionToken(session);
        log.info("> " + token.toString() + " created.");

        Success success = new Success();
        success.setSession(buildRestSessionBean(session));

        /* Check if we need to send authorization. */
        Optional<HttpDigestCredentials> opt = CredentialsUtil.getCredentials(request);
        if (opt.isPresent()) {
            if (HttpDigestCredentials.containsChallenge(opt.get())) {
                /* Set authorization only when response to challenge. */
                Authorization authorization = new Authorization();
                authorization.setExpiresIn(CachingConfiguration.ACCESS_TOKEN_TTL);
                authorization.setRefreshToken(""); /* Refresh token not supported yet. */
                authorization.setScope(token.getScope());
                authorization.setTokenType(token.getType());
                authorization.setToken(token.getKey());
                success.setAuthorization(authorization);
            }
        }

        return ResponseEntity.ok(success);
    }

    @RequestMapping(
            value = "/" + RestApiConfiguration.REST_API_SESSION + "/{id}",
            method = RequestMethod.GET)
    public ResponseEntity<Success> get(@PathVariable long id) {
        SessionEntity entity = (SessionEntity) restSessionService.getSession(id);
        Success success = new Success();
        success.setSession(buildRestSessionBean(entity));
        return ResponseEntity.ok(success);
    }

    @RequestMapping(
            value = "/" + RestApiConfiguration.REST_API_SESSION + "/{id}",
            method = RequestMethod.POST)
    public ResponseEntity<RestBean> update(@RequestParam(value = "user_ip") String ip,
                                           @RequestParam(value = "user_mac", defaultValue = "") String mac,
                                           @RequestParam long timestamp,
                                           @PathVariable long id) {
        SessionEntity entity = (SessionEntity) restSessionService.getSession(id);
        SessionEntity found = (SessionEntity) restSessionService.find(ip, mac);

        if (entity.getId() == found.getId()) {
            SessionEntity updated = (SessionEntity) restSessionService.update(id, timestamp);
            /* send updated session information. */
            Success success = new Success();
            success.setSession(buildRestSessionBean(updated));
            return ResponseEntity.ok(success);
        } else {
            /* Assume client is trying to access unauthorized session. */
            Failure failure = new Failure();
            failure.setError(RestResponse.ERROR_UNAUTHORIZED_REQUEST);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(failure);
        }
    }

    @RequestMapping(
            value = "/" + RestApiConfiguration.REST_API_SESSION + "/{id}",
            method = RequestMethod.DELETE)
    public ResponseEntity<RestBean> disconnect(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            /* Something went wrong. */
            throw new PortalException("Illegal state, request should be authenticated.");
        }
        RestAccessAuthentication access = (RestAccessAuthentication) authentication;
        RestSessionToken token = access.getSessionToken();

        if (token != null) {
            long sessionId = Long.parseLong(token.getExtendedInformation());

            if (sessionId == id) {
                restSessionService.removeSession(id);
                /* token may expired. */
                authorizationServer.removeSessionToken(token);
                Deleted deleted = new Deleted();
                deleted.setSessionRemoved(String.valueOf(id));
                return ResponseEntity.ok(deleted);
            }
        }

        /* Client is trying to delete unauthorized session. */
        Failure failure = new Failure();
        failure.setError(RestResponse.ERROR_UNAUTHORIZED_REQUEST);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(failure);
    }


    @RequestMapping(
            value = "/" + RestApiConfiguration.REST_API_SESSIONS + "/" + RestApiConfiguration.REST_API_FIND,
            method = RequestMethod.DELETE)
    public Object find(@RequestParam(value = "user_ip") String ip,
                       @RequestParam(value = "user_mac", defaultValue = "") String mac) {
        SessionEntity entity = (SessionEntity) restSessionService.find(ip, mac);

        Success success = new Success();
        success.setSession(buildRestSessionBean(entity));
        return ResponseEntity.ok(success);
    }

    cn.com.xinli.portal.rest.bean.Session buildRestSessionBean(SessionEntity entity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            /* Something went wrong. */
            throw new PortalException("Illegal state, request should be authenticated.");
        }

        RestAccessAuthentication access = (RestAccessAuthentication) authentication;
        RestSessionToken token = access.getSessionToken();

        cn.com.xinli.portal.rest.bean.Session session = new cn.com.xinli.portal.rest.bean.Session();
        session.setKeepalive(keepAliveConfiguration.isKeepalive());
        session.setId(String.valueOf(entity.getId()));
        session.setKeepaliveInterval(keepAliveConfiguration.getInterval());
        if (token != null) {
            session.setToken(token.getKey());
            session.setTokenExpiresIn(CachingConfiguration.SESSION_TOKEN_TTL);
        }

        return session;
    }
}
