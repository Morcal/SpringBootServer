package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.*;
import cn.com.xinli.portal.auth.AuthorizationServer;
import cn.com.xinli.portal.persist.SessionEntity;
import cn.com.xinli.portal.rest.auth.AccessAuthentication;
import cn.com.xinli.portal.rest.bean.RestBean;
import cn.com.xinli.portal.rest.configuration.ApiConfiguration;
import cn.com.xinli.portal.rest.configuration.SecurityConfiguration;
import cn.com.xinli.portal.rest.token.SessionToken;
import cn.com.xinli.portal.util.AddressUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

/**
 * Portal web server session REST APIs controller.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
@RestController
@RequestMapping("/${pws.root}/" + ApiConfiguration.REST_API_VERSION)
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

    private Session buildSession(long nas, String username, String password, String ip,
                                  String mac, String os, String version) {
        SessionEntity session = new SessionEntity();
        session.setIp(ip);
        session.setMac(mac);
        session.setOs(os);
        session.setVersion(version);
        session.setUsername(username);
        session.setPassword(password);
        session.setNasId(String.valueOf(nas));
        session.setDevice(Session.pair(ip, mac));
        return session;
    }

    /**
     * Create a portal session so that user can access broadband connection.
     *
     * <p>Only request authenticated with role of {@link SecurityConfiguration#PORTAL_USER_ROLE}
     * and has authority of this session can proceed.</p>
     *
     * @param ip source ip address.
     * @param mac source mac address.
     * @return JSON.
     */
    @ResponseBody
    @RequestMapping(
            value = "/" + ApiConfiguration.REST_API_SESSIONS,
            method = RequestMethod.POST)
    @PreAuthorize(SecurityConfiguration.SPRING_EL_PORTAL_USER_ROLE)
    public RestBean connect(@RequestParam String username,
                            @RequestParam String password,
                            @RequestParam(name = "user_ip") String ip,
                            @RequestParam(name = "user_mac") String mac,
                            @RequestParam String os,
                            @RequestParam String version,
                            @AuthenticationPrincipal Principal principal) {
        // Get NAS if mapped.
        Nas nas = nasMapping.findNas(ip, mac);
        if (nas == null) {
            /* Last resort, try to find nas by ipv4 range. */
            nas = nasMapping.findByIpv4Range(AddressUtil.convertIpv4Address(ip));
            if (nas == null) {
                throw new NasNotFoundException(ip, mac);
            }
        }

        // Create portal session.
        Session session = restSessionService.createSession(
                buildSession(nas.getId(), username, password, ip, mac, os, version));
        log.info("> " + session.toString() + " created.");

        // create session authorization. FIXME session may be removed by other threads.
        SessionToken token = (SessionToken) authorizationServer.allocateToken(session);
        log.info("> " + token.toString() + " created.");
        AccessAuthentication authentication = (AccessAuthentication) principal;
        authentication.setSessionToken(token);

        return RestResponseBuilders.successBuilder()
                .setSession(session)
                .setAccessAuthentication(authentication)
                .setServerConfig(serverConfig)
                .setGrantToken(true)
                .build();
    }

    /**
     * Get portal session information.
     *
     * <p>Only request authenticated with role of {@link SecurityConfiguration#PORTAL_USER_ROLE}
     * and has authority of this session can proceed.</p>
     *
     * <p>AFAIK, Administrators with role of {@link SecurityConfiguration#SYSTEM_ADMIN_ROLE}
     * overrule anything and everything.
     *
     * @param id session id.
     * @return JSON.
     */
    @ResponseBody
    @RequestMapping(
            value = "/" + ApiConfiguration.REST_API_SESSION + "/{id}",
            method = RequestMethod.GET)
    @PreAuthorize("(" + SecurityConfiguration.SPRING_EL_PORTAL_USER_ROLE + " and hasAuthority(#session)) " +
            " or " + SecurityConfiguration.SPRING_EL_SYSTEM_ADM_ROLE)
    public RestBean get(@P("session") @PathVariable long id,
                        @AuthenticationPrincipal Principal principal) {
        Session session = restSessionService.getSession(id);
        return RestResponseBuilders.successBuilder()
                .setSession(session)
                .setAccessAuthentication((AccessAuthentication) principal)
                .setServerConfig(serverConfig)
                .build();
    }

    /**
     * Update portal session.
     *
     * <p>Only request authenticated with role of {@link SecurityConfiguration#PORTAL_USER_ROLE}
     * and has authority of this session can proceed.</p>
     *
     * <p>AFAIK, Administrators with role of {@link SecurityConfiguration#SYSTEM_ADMIN_ROLE}
     * overrule anything and everything.
     *
     * @param ip source ip address.
     * @param mac source mac address.
     * @param timestamp source timestamp.
     * @param id session id.
     * @return JSON.
     */
    @ResponseBody
    @RequestMapping(
            value = "/" + ApiConfiguration.REST_API_SESSION + "/{id}",
            method = RequestMethod.POST)
    @PreAuthorize("(" + SecurityConfiguration.SPRING_EL_PORTAL_USER_ROLE + " and hasAuthority(#session)) " +
                    " or " + SecurityConfiguration.SPRING_EL_SYSTEM_ADM_ROLE)
    public RestBean update(@RequestParam(value = "user_ip") String ip,
                           @RequestParam(value = "user_mac", defaultValue = "") String mac,
                           @RequestParam long timestamp,
                           @P("session") @PathVariable long id,
                           @AuthenticationPrincipal Principal principal) {
        Optional<Session> opt = restSessionService.find(ip, mac);
        opt.orElseThrow(() -> new SessionNotFoundException("Session not found."));

        Session found = opt.get(), entity = restSessionService.getSession(id);

        if (entity.getId() == found.getId()) {
            long now = System.currentTimeMillis();
            if (Math.abs(now - timestamp) > SecurityConfiguration.MAX_TIME_DIFF) {
                throw new OutOfRangeUpdateException("update out of range.");
            }

            Session updated = restSessionService.update(id, timestamp);
            /* send updated session information. */
            return RestResponseBuilders.successBuilder()
                    .setSession(updated)
                    .setAccessAuthentication((AccessAuthentication) principal)
                    .setServerConfig(serverConfig)
                    .build();
        } else {
            throw new DeviceChangedException("device changed.");
        }
    }

    /**
     * Disconnect portal session.
     *
     * <p>Only request authenticated with role of {@link SecurityConfiguration#PORTAL_USER_ROLE}
     * and has authority of this session can proceed.</p>
     *
     * <p>AFAIK, Administrators with role of {@link SecurityConfiguration#SYSTEM_ADMIN_ROLE}
     * overrule anything and everything.
     *
     * @param id session id.
     * @return JSON.
     */
    @ResponseBody
    @RequestMapping(
            value = "/" + ApiConfiguration.REST_API_SESSION + "/{id}",
            method = RequestMethod.DELETE)
    @PreAuthorize("(" + SecurityConfiguration.SPRING_EL_PORTAL_USER_ROLE + " and hasAuthority(#session)) " +
            " or " + SecurityConfiguration.SPRING_EL_SYSTEM_ADM_ROLE)
    public RestBean disconnect(@P("session") @PathVariable long id,
                               @AuthenticationPrincipal Principal principal) {
        AccessAuthentication access = (AccessAuthentication) principal;
        SessionToken token = access.getSessionToken();

        restSessionService.removeSession(id);
        /* token may expired. */
        authorizationServer.revokeToken(token);

        return RestResponseBuilders.successBuilder()
                .setAccessAuthentication((AccessAuthentication) principal)
                .build();
    }

    @ResponseBody
    @RequestMapping(
            value = "/" + ApiConfiguration.REST_API_SESSIONS + "/" + ApiConfiguration.REST_API_FIND,
            method = RequestMethod.POST)
    @PreAuthorize(SecurityConfiguration.SPRING_EL_PORTAL_USER_ROLE)
    public RestBean find(@RequestParam(value = "user_ip") String ip,
                         @RequestParam(value = "user_mac", defaultValue = "") String mac,
                         @AuthenticationPrincipal Principal principal) {
        Optional<Session> opt = restSessionService.find(ip, mac);
        opt.orElseThrow(() -> new SessionNotFoundException("Session not found."));

        Session session = opt.get();

        /* Revoke current session token if present. */
        AccessAuthentication authentication = (AccessAuthentication) principal;
        if (authentication.getSessionToken() != null) {
            authorizationServer.revokeToken(authentication.getSessionToken());
            if (log.isDebugEnabled()) {
                log.debug("> session token: " + authentication.getSessionToken() + " revoked.");
            }
        }

        /* reallocate a new session token. */
        SessionToken token = (SessionToken) authorizationServer.allocateToken(session);
        authentication.setSessionToken(token);
        if (log.isDebugEnabled()) {
            log.debug("> new session token: " + token + " allocated.");
        }

        return RestResponseBuilders.successBuilder()
                .setSession(session)
                .setAccessAuthentication((AccessAuthentication) principal)
                .setServerConfig(serverConfig)
                .setGrantToken(true)
                .build();
    }

}
