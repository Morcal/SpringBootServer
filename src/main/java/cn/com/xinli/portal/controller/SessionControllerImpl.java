package cn.com.xinli.portal.controller;

import cn.com.xinli.portal.auth.AccessAuthentication;
import cn.com.xinli.portal.auth.token.RestToken;
import cn.com.xinli.portal.core.*;
import cn.com.xinli.portal.protocol.Nas;
import cn.com.xinli.portal.protocol.Result;
import cn.com.xinli.portal.repository.SessionEntity;
import cn.com.xinli.portal.service.SessionService;
import cn.com.xinli.portal.service.SessionTokenService;
import cn.com.xinli.portal.support.rest.RestResponse;
import cn.com.xinli.portal.support.rest.RestResponseBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Calendar;
import java.util.Optional;

/**
 * Session controller implementation.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/2.
 */
@Component
@RequestMapping("/portal/v1.0")
public class SessionControllerImpl implements SessionController {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(SessionController.class);

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private SessionTokenService sessionTokenService;

    @Autowired
    private NasMapping nasMapping;

    @Value("${pws.session.keepalive.requires}") private boolean requiresKeepAlive;

    @Value("${pws.session.keepalive.interval}") private int keepAliveInterval;

    @Value("${pws.nat.allowed}") private boolean natAllowed;

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
        session.setStartTime(Calendar.getInstance().getTime());
        return session;
    }

    @Override
    @ResponseBody
    @RequestMapping(value = "/sessions", method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER')")
    public RestResponse connect(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam(name = "user_ip") String ip,
                                @RequestParam(name = "user_mac") String mac,
                                @RequestParam(defaultValue = "") String os,
                                @RequestParam(defaultValue = "") String version,
                                @AuthenticationPrincipal Principal principal)
            throws PortalException {
        // Get NAS if mapped.
        Nas nas = nasMapping.find(username, ip, mac);

        // Create portal session.
        Session session = buildSession(nas.getId(), username, password, ip, mac, os, version);
        Result result = sessionManager.createSession(nas, session);
        if (logger.isTraceEnabled()) {
            logger.trace("Connect result: {}", result);
        }

        AccessAuthentication authentication = (AccessAuthentication) principal;

        // create session authorization. FIXME session may be removed by other threads.
        RestToken token = (RestToken) sessionTokenService.allocateToken(String.valueOf(session.getId()));
        if (logger.isTraceEnabled()) {
            logger.trace("{} created.", token);
        }

        authentication.setSessionToken(token);

        logger.info("session created id: {}", session.getId());

        return RestResponseBuilders.successBuilder()
                .setSession(session)
                .setAccessAuthentication(authentication)
                .setRequiresKeepAlive(requiresKeepAlive)
                .setKeepAliveInterval(keepAliveInterval)
                .setGrantToken(true)
                .build();
    }

    @Override
    @ResponseBody
    @RequestMapping(value = "/session/{id}", method = RequestMethod.GET)
    @PreAuthorize("(hasRole('USER') and hasAuthority(#session)) or hasRole('ADMIN')")
    public RestResponse get(@P("session") @PathVariable long id,
                 @AuthenticationPrincipal Principal principal) throws SessionNotFoundException {
        Session session = sessionService.getSession(id);

        if (logger.isTraceEnabled()) {
            logger.trace("get session, {}", session);
        }

        logger.info("get session {{}}", session.getId());

        return RestResponseBuilders.successBuilder()
                .setSession(session)
                .setAccessAuthentication((AccessAuthentication) principal)
                .setRequiresKeepAlive(requiresKeepAlive)
                .setKeepAliveInterval(keepAliveInterval)
                .build();
    }

    @Override
    @ResponseBody
    @RequestMapping(value = "/session/{id}", method = RequestMethod.POST)
    @PreAuthorize("(hasRole('USER') and hasAuthority(#session)) or hasRole('ADMIN')")
    public RestResponse update(@P("session") @PathVariable long id,
                    @RequestParam long timestamp,
                    @AuthenticationPrincipal Principal principal)
            throws PortalException {
        Session updated = sessionService.update(id, timestamp);

        if (logger.isTraceEnabled()) {
            logger.trace("session updated, {}", updated);
        }

        logger.info("session {{}} updated", updated.getId());

        /* send updated session information. */
        return RestResponseBuilders.successBuilder()
                .setSession(updated)
                .setAccessAuthentication((AccessAuthentication) principal)
                .setRequiresKeepAlive(requiresKeepAlive)
                .setKeepAliveInterval(keepAliveInterval)
                .build();
    }

    @Override
    @ResponseBody
    @RequestMapping(value = "/session/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("(hasRole('USER') and hasAuthority(#session)) or hasRole('ADMIN')")
    public RestResponse disconnect(@P("session") @PathVariable long id,
                                   @AuthenticationPrincipal Principal principal)
            throws PortalException {
       Result result = sessionManager.removeSession(id);
        if (logger.isTraceEnabled()) {
            logger.trace("disconnect result: {}", result);
        }

        logger.info("session removed {}.", id);

        return RestResponseBuilders.successBuilder()
                .setAccessAuthentication((AccessAuthentication) principal)
                .build();
    }

    @Override
    @ResponseBody
    @RequestMapping(value = "/sessions/find", method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER')")
    public RestResponse find(@RequestParam(value = "user_ip") String ip,
                             @RequestParam(value = "user_mac", defaultValue = "") String mac,
                             @AuthenticationPrincipal Principal principal)
            throws SessionNotFoundException {
        Optional<Session> opt = sessionService.find(ip, mac);
        if (!opt.isPresent()) {
            return RestResponseBuilders.successBuilder()
                    .setAccessAuthentication((AccessAuthentication) principal)
                    .build();
        }

        Session session = opt.get();

        /* Revoke current session token if present. */
        AccessAuthentication authentication = (AccessAuthentication) principal;
        /* reallocate a new session token. */
        RestToken token = (RestToken) sessionTokenService.allocateToken(String.valueOf(session.getId()));
        authentication.setSessionToken(token);
        if (logger.isTraceEnabled()) {
            logger.trace("session: {} found, new session token allocated {}.", session, token);
        }

        logger.info("session found, id: {}", session.getId());

        return RestResponseBuilders.successBuilder()
                .setSession(session)
                .setAccessAuthentication((AccessAuthentication) principal)
                .setRequiresKeepAlive(requiresKeepAlive)
                .setKeepAliveInterval(keepAliveInterval)
                .setGrantToken(true)
                .build();
    }

}
