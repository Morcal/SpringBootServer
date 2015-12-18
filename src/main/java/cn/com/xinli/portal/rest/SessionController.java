package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.*;
import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.auth.AuthorizationServer;
import cn.com.xinli.portal.persist.SessionEntity;
import cn.com.xinli.portal.rest.auth.HttpDigestCredentials;
import cn.com.xinli.portal.rest.auth.RestAccessAuthentication;
import cn.com.xinli.portal.rest.bean.*;
import cn.com.xinli.portal.rest.configuration.RestApiConfiguration;
import cn.com.xinli.portal.rest.configuration.RestSecurityConfiguration;
import cn.com.xinli.portal.rest.token.RestSessionToken;
import cn.com.xinli.portal.util.AddressUtil;
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
    private NasMapping nasMapping;

    @Autowired
    private ServerConfig serverConfig;

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
            /* Last resort, try to find nas by ipv4 range. */
            nas = nasMapping.findByIpv4Range(AddressUtil.convertIpv4Address(ip));
            if (nas == null) {
                log.debug("* nas not found for: " + Session.pair(ip, mac));
                /* NAS not found. */
                Failure failure = new Failure();
                failure.setError(RestResponse.ERROR_INVALID_PORTAL_REQUEST);
                failure.setDescription("nas not found for ip: " + ip + ", mac: " + mac);
                return ResponseEntity.ok(failure);
            }
        }

        log.debug("> nas found: " + nas);

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
        success.setSession(RestResponseBuilders.sessionBuilder(session, token).build(serverConfig));

        /* Check if we need to send authorization. */
        Optional<HttpDigestCredentials> opt = CredentialsUtil.getCredentials(request);

        opt.ifPresent(credentials -> {
            if (HttpDigestCredentials.containsChallenge(opt.get())) {
                /* Set authorization only when response to challenge. */
                RestAccessAuthentication authentication =
                        (RestAccessAuthentication) SecurityContextHolder.getContext().getAuthentication();
                Authorization author =
                        RestResponseBuilders.authorizationBuilder(authentication.getAccessToken()).build();
                success.setAuthorization(author);
            }
        });

        return ResponseEntity.ok(success);
    }

    @RequestMapping(
            value = "/" + RestApiConfiguration.REST_API_SESSION + "/{id}",
            method = RequestMethod.GET)
    public ResponseEntity<Success> get(@PathVariable long id) {
        SessionEntity entity = (SessionEntity) restSessionService.getSession(id);
        Success success = new Success();
        success.setSession(RestResponseBuilders.sessionBuilder(entity, null).build(serverConfig));
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
            long now = System.currentTimeMillis();
            if (Math.abs(now - timestamp) > RestSecurityConfiguration.MAX_TIME_DIFF) {
                /* update request originated out of allowed range. */
                Failure failure = new Failure();
                failure.setError(RestResponse.ERROR_INVALID_PORTAL_REQUEST);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(failure);
            }

            SessionEntity updated = (SessionEntity) restSessionService.update(id, timestamp);
            /* send updated session information. */
            Success success = new Success();
            success.setSession(RestResponseBuilders.sessionBuilder(updated, null).build(serverConfig));
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
                authorizationServer.revokeSessionToken(token);
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
    public ResponseEntity<RestBean> find(@RequestParam(value = "user_ip") String ip,
                       @RequestParam(value = "user_mac", defaultValue = "") String mac) {
        SessionEntity entity = (SessionEntity) restSessionService.find(ip, mac);

        /* Revoke current session token. */
        RestAccessAuthentication authentication = (RestAccessAuthentication) SecurityContextHolder.getContext().getAuthentication();
        if (authorizationServer.revokeSessionToken(authentication.getSessionToken())) {
            log.info("> session token: " + authentication.getSessionToken() + " revoked.");
        }

        /* reallocate a new session token. */
        RestSessionToken token = authorizationServer.allocateSessionToken(entity);
        log.info("> new session token: " + token + " allocated.");

        Success success = new Success();
        success.setSession(RestResponseBuilders.sessionBuilder(entity, token).build(serverConfig));
        return ResponseEntity.ok(success);
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason="Server Internal Error")
    @ExceptionHandler(RuntimeException.class)
    public void handleException(RuntimeException e) {
        e.printStackTrace();
    }
}
