package cn.com.xinli.portal.web.controller;

import cn.com.xinli.portal.core.Context;
import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.certificate.CertificateService;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.NasLocator;
import cn.com.xinli.portal.core.nas.NasNotFoundException;
import cn.com.xinli.portal.core.redirection.RedirectService;
import cn.com.xinli.portal.core.redirection.Redirection;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.core.session.SessionManager;
import cn.com.xinli.portal.core.session.SessionNotFoundException;
import cn.com.xinli.portal.core.session.SessionService;
import cn.com.xinli.portal.util.AddressUtil;
import cn.com.xinli.portal.web.auth.AccessAuthentication;
import cn.com.xinli.portal.web.auth.HttpDigestCredentials;
import cn.com.xinli.portal.web.auth.token.ContextTokenService;
import cn.com.xinli.portal.web.auth.token.RestToken;
import cn.com.xinli.portal.web.auth.token.SessionTokenService;
import cn.com.xinli.portal.web.rest.RestResponse;
import cn.com.xinli.portal.web.rest.RestResponseBuilders;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.token.Token;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

/**
 * Session controller implementation.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/2.
 */
@Component
@RequestMapping("/portal/v1.0")
public class SessionControllerImpl implements SessionController {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(SessionController.class);

    @Autowired
    private NasLocator nasLocator;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private SessionTokenService sessionTokenService;

    @Autowired
    private ContextTokenService contextTokenService;

    @Autowired
    private RedirectService redirectService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private ServerConfiguration serverConfiguration;

    private RestResponse buildResponse(Session session,
                                       AccessAuthentication authentication,
                                       boolean grantToken,
                                       Token context) {
        RestResponseBuilders.SessionResponseBuilder builder = RestResponseBuilders.successBuilder();

        builder.setAccessTokenTtl(serverConfiguration.getRestConfiguration().getTokenTtl())
                .setChallengeTtl(serverConfiguration.getRestConfiguration().getChallengeTtl())
                .setSessionTokenTtl(serverConfiguration.getSessionConfiguration().getTokenTtl());

        return builder.setSession(session)
                .setAccessAuthentication(authentication)
                .setRequiresKeepAlive(serverConfiguration.getSessionConfiguration().isEnableHeartbeat())
                .setKeepAliveInterval(serverConfiguration.getSessionConfiguration().getHeartbeatInterval())
                .setGrantToken(grantToken)
                .setContext(context)
                .build();
    }

    Context createContext(Credentials credentials,
                          String redirectUrl,
                          String ip,
                          String mac) throws RemoteException, NasNotFoundException {

        Redirection input = Redirection.parse(redirectUrl);
        Redirection redirection = redirectService.verify(input, ip, mac);
        if (logger.isDebugEnabled()) {
            logger.debug("redirection: {}", redirection);
        }

        Context context = new Context();
        context.setIp(ip);
        context.setMac(mac);

        String nasIp = redirection.getParameter(Redirection.NAS_IP);
        if (!StringUtils.isEmpty(nasIp)) {
            /*
             * Redirect url does not contain NAS IP,
             * so retrieve nas from locator.
             */
            Nas nas = nasLocator.locate(credentials);
            context.setNasIp(nas.getIp());
        } else {
            /*
             * Redirect url contains NAS IP, create mapping.
             */
            nasLocator.map(ip, mac, nasIp);
            context.setNasIp(nasIp);
        }

        return context;
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
                                @RequestParam(name = "redirect_url") String redirectUrl,
                                @AuthenticationPrincipal Principal principal)
            throws PortalException {
        final String formatted = AddressUtil.formatMac(mac);
        Credentials credentials = Credentials.of(username, password, ip, formatted);

        AccessAuthentication authentication = (AccessAuthentication) principal;
        String app = authentication.getCredentials().getParameter(HttpDigestCredentials.CLIENT_ID);
        Certificate certificate = certificateService.loadCertificate(app);

        Context context = createContext(credentials, redirectUrl, ip, mac);

        Session session = sessionManager.createSession(certificate, credentials);
        if (logger.isTraceEnabled()) {
            logger.trace("session for {}, {} created: {}", os, version, session);
        }

        // create session authorization. FIXME session may be removed by other threads.
        RestToken token = (RestToken) sessionTokenService.allocateToken(String.valueOf(session.getId()));
        if (logger.isTraceEnabled()) {
            logger.trace("{} created.", token);
        }

        authentication.setSessionToken(token);

        logger.info("session created id: {}", session.getId());

        context.setSession(String.valueOf(session.getId()));
        Token ctx = contextTokenService.allocateToken(contextTokenService.encode(context));

        RestResponse rs = buildResponse(session, authentication, true, ctx);

        if (logger.isDebugEnabled()) {
            logger.debug("connect -> {} ", rs);
        }

        return rs;
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

        RestResponse rs = buildResponse(session, (AccessAuthentication) principal, false, null);

        if (logger.isDebugEnabled()) {
            logger.debug("get -> {} ", rs);
        }

        return rs;
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
        RestResponse rs = buildResponse(updated, (AccessAuthentication) principal, false, null);

        if (logger.isDebugEnabled()) {
            logger.debug("update -> {} ", rs);
        }

        return rs;
    }

    @Override
    @ResponseBody
    @RequestMapping(value = "/session/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("(hasRole('USER') and hasAuthority(#session)) or hasRole('ADMIN')")
    public RestResponse disconnect(@P("session") @PathVariable long id,
                                   @AuthenticationPrincipal Principal principal)
            throws PortalException {
        sessionManager.removeSession(id);
        logger.info("session removed {}.", id);

        RestResponse rs = RestResponseBuilders.successBuilder()
                .setAccessAuthentication((AccessAuthentication) principal)
                .build();

        if (logger.isDebugEnabled()) {
            logger.debug("disconnect -> {} ", rs);
        }

        return rs;
    }

    /**
     * Find session.
     *
     * @param context context.
     * @param ip ip address.
     * @param mac mac address.
     * @return session.
     * @throws SessionNotFoundException
     * @throws RemoteException
     */
    Optional<Session> findSession(Token context, String ip, String mac)
            throws SessionNotFoundException, RemoteException {
        if (context == null) {
            /* Without context, can only find session by ip and mac. */
            return sessionService.find(ip, mac);
        } else {
            Context ctx = contextTokenService.parse(context.getExtendedInformation());
            if (!ctx.isValid() || StringUtils.isEmpty(ctx.getSession())) {
                return Optional.empty();
            }

            return Optional.of(sessionService.getSession(Long.valueOf(ctx.getSession())));
        }
    }

    @Override
    @ResponseBody
    @RequestMapping(value = "/sessions/find", method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER')")
    public RestResponse find(@RequestParam(name = "user_ip") String ip,
                             @RequestParam(name = "user_mac", defaultValue = "") String mac,
                             @RequestParam(defaultValue = "") String context,
                             @AuthenticationPrincipal Principal principal)
            throws PortalException {
        RestResponse rs;

        Token ctx = contextTokenService.verifyToken(context);
        if (ctx != null) {
            /* context token is not null only if token is valid and session exists. */
            Context c = contextTokenService.parse(context);
            if (!StringUtils.equals(c.getIp(), ip)) {
                throw new RemoteException(PortalError.NETWORK_CHANGED);
            }
        }

        Optional<Session> opt = findSession(ctx, ip, mac);

        if (!opt.isPresent()) {
            rs = RestResponseBuilders.successBuilder()
                    .setAccessAuthentication((AccessAuthentication) principal)
                    .build();
        } else {
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

            rs = buildResponse(session, (AccessAuthentication) principal, true, ctx);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("find -> {} ", rs);
        }

        return rs;
    }

}
