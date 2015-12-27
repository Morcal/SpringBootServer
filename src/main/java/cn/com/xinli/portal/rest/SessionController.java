package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.*;
import cn.com.xinli.portal.persist.SessionEntity;
import cn.com.xinli.portal.rest.auth.AccessAuthentication;
import cn.com.xinli.portal.rest.bean.RestBean;
import cn.com.xinli.portal.rest.configuration.ApiConfiguration;
import cn.com.xinli.portal.rest.configuration.SecurityConfiguration;
import cn.com.xinli.portal.rest.token.RestToken;
import cn.com.xinli.portal.rest.token.SessionTokenService;
import cn.com.xinli.portal.util.AddressUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Calendar;
import java.util.Optional;

/**
 * Portal web server session REST APIs controller.
 * <p>
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
@RestController
@RequestMapping("/${pws.root}/" + ApiConfiguration.REST_API_VERSION)
public class SessionController {
    /**
     * Log.
     */
    private static final Log log = LogFactory.getLog(SessionController.class);

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private SessionTokenService sessionTokenService;

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
        session.setNasId(nas);
        session.setDevice(Session.pair(ip, mac));
        session.setStartTime(Calendar.getInstance().getTime());
        return session;
    }

    /**
     * Create a portal session so that user can access broadband connection.
     * <p>
     * <p>Only request authenticated with role of {@link SecurityConfiguration#PORTAL_USER_ROLE}
     * and has authority of this session can proceed.</p>
     *
     * @param ip  source ip address.
     * @param mac source mac address.
     * @return JSON.
     */
    @ResponseBody
    @RequestMapping(
            value = "/" + ApiConfiguration.REST_API_SESSIONS,
            method = RequestMethod.POST)
    //@PreAuthorize(SecurityConfiguration.SPRING_EL_PORTAL_USER_ROLE)
    @PreAuthorize("hasRole('USER')")
    public RestBean connect(@RequestParam String username,
                            @RequestParam String password,
                            @RequestParam(name = "user_ip") String ip,
                            @RequestParam(name = "user_mac") String mac,
                            @RequestParam(defaultValue = "") String os,
                            @RequestParam(defaultValue = "") String version,
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
        Session session = buildSession(nas.getId(), username, password, ip, mac, os, version);
        try {
            Message message = sessionManager.createSession(nas, session);
            if (log.isDebugEnabled()) {
                log.info(message);
            }

            if (!message.isSuccess()) {
                return RestResponseBuilders.errorBuilder()
                        .setError(RestResponse.ERROR_SERVER_ERROR)
                        .setDescription(message.getText())
                        .build();
            }
        } catch (IOException e) {
            throw new SessionOperationException("Failed to create session", e);
        }

        // create session authorization. FIXME session may be removed by other threads.
        RestToken token = (RestToken) sessionTokenService.allocateToken(String.valueOf(session.getId()));
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
     * <p>
     * <p>Only request authenticated with role of {@link SecurityConfiguration#PORTAL_USER_ROLE}
     * and has authority of this session can proceed.</p>
     * <p>
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
    @PreAuthorize("(hasRole('USER') and hasAuthority(#session)) or hasRole('SYSTEM_ADMIN')")
    public RestBean get(@P("session") @PathVariable long id,
                        @AuthenticationPrincipal Principal principal) {
        Session session = sessionService.getSession(id);
        return RestResponseBuilders.successBuilder()
                .setSession(session)
                .setAccessAuthentication((AccessAuthentication) principal)
                .setServerConfig(serverConfig)
                .build();
    }

    /**
     * Update portal session.
     * <p>
     * <p>Only request authenticated with role of {@link SecurityConfiguration#PORTAL_USER_ROLE}
     * and has authority of this session can proceed.</p>
     * <p>
     * <p>AFAIK, Administrators with role of {@link SecurityConfiguration#SYSTEM_ADMIN_ROLE}
     * overrule anything and everything.
     *
     * @param timestamp source timestamp.
     * @param id        session id.
     * @return JSON.
     */
    @ResponseBody
    @RequestMapping(
            value = "/" + ApiConfiguration.REST_API_SESSION + "/{id}",
            method = RequestMethod.POST)
    @PreAuthorize("(hasRole('USER') and hasAuthority(#session)) or hasRole('SYSTEM_ADMIN')")
    public RestBean update(@RequestParam long timestamp,
                           @P("session") @PathVariable long id,
                           @AuthenticationPrincipal Principal principal) {
        //Session entity = sessionService.getSession(id);
        long now = System.currentTimeMillis() / 1000L;
        if (Math.abs(now - timestamp) > SecurityConfiguration.MAX_TIME_DIFF) {
            throw new OutOfRangeUpdateException("update out of range.");
        }

        Session updated = sessionService.update(id, timestamp);
        /* send updated session information. */
        return RestResponseBuilders.successBuilder()
                .setSession(updated)
                .setAccessAuthentication((AccessAuthentication) principal)
                .setServerConfig(serverConfig)
                .build();
    }

    /**
     * Disconnect portal session.
     * <p>
     * <p>Only request authenticated with role of {@link SecurityConfiguration#PORTAL_USER_ROLE}
     * and has authority of this session can proceed.</p>
     * <p>
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
    @PreAuthorize("(hasRole('USER') and hasAuthority(#session)) or hasRole('SYSTEM_ADMIN')")
    public RestBean disconnect(@P("session") @PathVariable long id,
                               @AuthenticationPrincipal Principal principal) {
        Message<Session> message = sessionManager.removeSession(id);
        if (log.isDebugEnabled()) {
            log.debug(message);
            Session rm = message.getContent().get();
            log.debug("session: " + rm + " removed.");
        }

        if (!message.isSuccess()) {
            return RestResponseBuilders.errorBuilder()
                    .setError(RestResponse.ERROR_SERVER_ERROR)
                    .setDescription(message.getText())
                    .build();
        }

        return RestResponseBuilders.successBuilder()
                .setAccessAuthentication((AccessAuthentication) principal)
                .build();
    }

    @ResponseBody
    @RequestMapping(
            value = "/" + ApiConfiguration.REST_API_SESSIONS + "/" + ApiConfiguration.REST_API_FIND,
            method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER')")
    public RestBean find(@RequestParam(value = "user_ip") String ip,
                         @RequestParam(value = "user_mac", defaultValue = "") String mac,
                         @AuthenticationPrincipal Principal principal) {
        Optional<Session> opt = sessionService.find(ip, mac);
        opt.orElseThrow(() -> new SessionNotFoundException("Session not found."));

        Session session = opt.get();

        /* Revoke current session token if present. */
        AccessAuthentication authentication = (AccessAuthentication) principal;
        /* reallocate a new session token. */
        RestToken token = (RestToken) sessionTokenService.allocateToken(String.valueOf(session.getId()));
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
