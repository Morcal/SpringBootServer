package cn.com.xinli.portal.web.controller;

import cn.com.xinli.portal.core.Context;
import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.certificate.CertificateService;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.configuration.ServerConfigurationService;
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
    private ServerConfigurationService serverConfigurationService;

    private ServerConfiguration getServerConfiguration() {
        return serverConfigurationService.getServerConfiguration();
    }

    /**
     * Create default context for empty redirect url.
     * @return context.
     */
    Context createDefaultContext(String ip, String mac) {
        Context context = new Context();
        context.setIp(ip);
        context.setMac(mac);
        context.setNasIp("0.0.0.0");
        if (logger.isTraceEnabled()) {
            logger.trace("Default context created: {}", context);
        }
        return context;
    }

    /**
     * Create session context.
     * @param credentials credentials.
     * @param redirectUrl redirect url.
     * @return session context.
     * @throws RemoteException
     * @throws NasNotFoundException
     */
    Context createContext(Credentials credentials, String redirectUrl)
            throws RemoteException, NasNotFoundException {
        Redirection input = Redirection.parse(redirectUrl);
        Redirection redirection = redirectService.verify(
                input, credentials.getIp(), credentials.getMac());
        if (logger.isDebugEnabled()) {
            logger.debug("redirection: {}", redirection);
        }

        final String ip = redirection.getParameter(Redirection.USER_IP),
                mac = redirection.getParameter(Redirection.USER_MAC);

        Context context = new Context();
        context.setIp(ip);
        context.setMac(mac);

        /* Update credentials' mac to redirect url mac. */
        credentials.setMac(mac);

        String nasIp = redirection.getParameter(Redirection.NAS_IP);
        if (StringUtils.isEmpty(nasIp)) {
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

        if (logger.isTraceEnabled()) {
            logger.trace("context created: {}", context);
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
                                @RequestParam(name = "redirect_url", defaultValue = "") String redirectUrl,
                                @AuthenticationPrincipal Principal principal)
            throws PortalException {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(ip)) {
            throw new RemoteException(PortalError.INVALID_REQUEST);
        }

        final String formatted = AddressUtil.formatMac(mac);
        Credentials credentials = Credentials.of(username, password, ip, formatted);

        AccessAuthentication authentication = (AccessAuthentication) principal;
        String app = authentication.getCredentials().getAttribute(HttpDigestCredentials.CLIENT_ID);
        Certificate certificate = certificateService.loadCertificate(app);

        Context context;
        if (serverConfigurationService.getServerConfiguration().isCheckRedirectUrl()) {
            context = createContext(credentials, redirectUrl);
        } else {
            context = createDefaultContext(ip, mac);
        }

        Session session = sessionManager.createSession(certificate, credentials);
        logger.info("session {} for ip:{}, mac:{}, os:{}, version:{} created.",
                session.getId(), ip, mac, os, version);

        // create session authorization. FIXME session may be removed by other threads.
        RestToken token = (RestToken) sessionTokenService.allocateToken(String.valueOf(session.getId()));
        if (logger.isTraceEnabled()) {
            logger.trace("{} created.", token);
        }

        authentication.setSessionToken(token);

        context.setSession(String.valueOf(session.getId()));
        Token ctx = contextTokenService.allocateToken(contextTokenService.encode(context));

        RestResponse rs = RestResponseBuilders.buildSessionResponse(
                getServerConfiguration(), session, authentication, true, ctx, false);

        if (logger.isDebugEnabled()) {
            logger.debug("connect -> {} ", rs);
        }

        return rs;
    }

    @Override
    @ResponseBody
    @RequestMapping(value = "/sessions/{id}", method = RequestMethod.GET)
    @PreAuthorize("(hasRole('USER') and hasAuthority(#session)) or hasRole('ADMIN')")
    public RestResponse get(@P("session") @PathVariable long id,
                 @AuthenticationPrincipal Principal principal) throws SessionNotFoundException {
        Session session = sessionService.getSession(id);
        logger.trace("get session {{}}.", session.getId());

        RestResponse rs = RestResponseBuilders.buildSessionResponse(
                getServerConfiguration(), session, (AccessAuthentication) principal, false, null, false);

        if (logger.isDebugEnabled()) {
            logger.debug("get -> {} ", rs);
        }

        return rs;
    }

    @Override
    @ResponseBody
    @RequestMapping(value = "/sessions/{id}", method = RequestMethod.POST)
    @PreAuthorize("(hasRole('USER') and hasAuthority(#session)) or hasRole('ADMIN')")
    public RestResponse update(@P("session") @PathVariable long id,
                    @RequestParam long timestamp,
                    @AuthenticationPrincipal Principal principal)
            throws PortalException {
        Session updated = sessionService.update(id, timestamp);
        logger.info("session {{}} updated.", updated.getId());

        /* send updated session information. */
        RestResponse rs = RestResponseBuilders.buildSessionResponse(
                getServerConfiguration(), updated, (AccessAuthentication) principal, false, null, false);

        if (logger.isDebugEnabled()) {
            logger.debug("update -> {} ", rs);
        }

        return rs;
    }

    @Override
    @ResponseBody
    @RequestMapping(value = "/sessions/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("(hasRole('USER') and hasAuthority(#session)) or hasRole('ADMIN')")
    public RestResponse disconnect(@P("session") @PathVariable long id,
                                   @AuthenticationPrincipal Principal principal)
            throws PortalException {
        sessionManager.removeSession(id);
        logger.info("session removed {}.", id);

        RestResponse rs = RestResponseBuilders.buildSessionResponse(
                getServerConfiguration(), null, (AccessAuthentication) principal, false, null, false);

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
    Optional<Session> findSession(Context context, String ip, String mac)
            throws SessionNotFoundException, RemoteException {
        if (context == null) {
            /* Without context, can only find session by ip and mac. */
            return sessionService.find(ip, mac);
        } else {
            if (!context.isValid() || StringUtils.isEmpty(context.getSession())) {
                return Optional.empty();
            }

            return Optional.of(sessionService.getSession(Long.valueOf(context.getSession())));
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
        if (StringUtils.isEmpty(ip)) {
            throw new RemoteException(PortalError.INVALID_REQUEST);
        }

        RestResponse rs;
        boolean networkChanged = false;
        Context c = null;
        Token ctx = contextTokenService.verifyToken(context);

        if (ctx != null) {
            /* context token is not null only if token is valid and session exists. */
            c = contextTokenService.decode(ctx.getExtendedInformation());
            if (!StringUtils.equals(c.getIp(), ip)) {
                networkChanged = true;
                //throw new RemoteException(PortalError.NETWORK_CHANGED);
            }
        }

        String formatted = AddressUtil.formatMac(mac);
        Optional<Session> opt = findSession(c, ip, formatted);

        if (!opt.isPresent()) {
            rs = RestResponseBuilders.buildSessionResponse(
                    getServerConfiguration(), null, (AccessAuthentication) principal, false, null, false);
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

            rs = RestResponseBuilders.buildSessionResponse(
                    getServerConfiguration(), session, (AccessAuthentication) principal, true, ctx, networkChanged);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("find -> {} ", rs);
        }

        return rs;
    }

}
