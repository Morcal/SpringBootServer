package cn.com.xinli.portal.web.controller;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.session.SessionNotFoundException;
import cn.com.xinli.portal.web.auth.RestRole;
import cn.com.xinli.portal.web.rest.RestResponse;

import java.security.Principal;

/**
 * Portal web server session REST APIs controller.
 *
 * <p>Portal web server session controller interface.
 * Define this controller interface so that spring-aop Aspects
 * can work on it properly.
 *
 * <p><code>url</code>s were originated by NAS/BRAS devices. When clients
 * try to establish a portal connection, this <code>url</code> is required.
 * Server will parse incoming <code>url</code> to identify which NAS/BRAS
 * device this request came from.
 *
 * <p><code>url</code> is protected by request signature, so it can not be
 * tempered with. Replay attack is protected by client authentication process
 * (challenge and access token expiration).
 *
 * <p><code>context</code> represents a portal session existed in the post, and
 * it may not been disconnected yet. Server verifies this <code>context</code>
 * as valid only if context is valid itself and associated session not been
 * disconnected yet. If context is valid, server will return an error
 * of {@link PortalError#NETWORK_CHANGED} to client.
 *
 * <p>All spring-web-mvc annotations should be declared in the
 * implementation class.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/2.
 */
public interface SessionController {

    /**
     * Create a portal session so that user can access broadband connection.
     *
     * <p><code>url</code>s were originated by NAS/BRAS devices. When clients
     * try to establish a portal connection, this <code>url</code> is required.
     * Server will parse incoming <code>url</code> to identify which NAS/BRAS
     * device this request came from.
     *
     * <p><code>url</code> is protected by request signature, so it can not be
     * tempered with. Replay attack is protected by client authentication process
     * (challenge and access token expiration).
     *
     * <p>Only request authenticated with role of {@link RestRole#USER}
     * and has authority of this session can proceed.
     *
     * @param username username.
     * @param password password.
     * @param ip ip address.
     * @param mac mac address.
     * @param os client operation system name.
     * @param version client version.
     * @param url redirect url.
     * @param principal spring security principal.
     * @return JSON.
     */
    RestResponse connect(String username,
                         String password,
                         String ip,
                         String mac,
                         String os,
                         String version,
                         String url,
                         Principal principal)
            throws PortalException;

    /**
     * Get portal session information.
     *
     * <p>Only request authenticated with role of {@link RestRole#USER}
     * and has authority of this session can proceed.
     *
     * <p>AFAIK, Administrators with role of {@link RestRole#ADMIN}
     * overrule anything and everything.
     *
     * @param id session id.
     * @param principal spring security principal.
     * @return JSON.
     */
    RestResponse get(long id, Principal principal) throws SessionNotFoundException;

    /**
     * Update portal session.
     *
     * <p>Only request authenticated with role of {@link RestRole#USER}
     * and has authority of this session can proceed.
     *
     * <p>AFAIK, Administrators with role of {@link RestRole#ADMIN}
     * overrule anything and everything.
     *
     * @param id session id.
     * @param timestamp source timestamp.
     * @param principal spring security principal.
     * @return JSON.
     */
    RestResponse update(long id, long timestamp, Principal principal)
            throws PortalException;

    /**
     * Disconnect portal session.
     *
     * <p>Only request authenticated with role of {@link RestRole#USER}
     * and has authority of this session can proceed.
     *
     * <p>AFAIK, Administrators with role of {@link RestRole#ADMIN}
     * overrule anything and everything.
     *
     * @param id session id.
     * @param principal spring security principal.
     * @return JSON.
     */
    RestResponse disconnect(long id, Principal principal) throws PortalException;

    /**
     * Find portal session created by user ip and user mac.
     *
     * <p><code>context</code> represents a portal session existed in the post, and
     * it may not been disconnected yet. Server verifies this <code>context</code>
     * as valid only if context is valid itself and associated session not been
     * disconnected yet. If context is valid, server will return an error
     * of {@link PortalError#NETWORK_CHANGED} to client.
     *
     * <p>Only request authenticated with role of {@link RestRole#USER}
     * and has authority of this session can proceed.
     *
     * @param ip user ip address.
     * @param mac user mac address.
     * @param context user session context.
     * @param principal spring security principal.
     * @return JSON.
     * @throws PortalException
     */
    RestResponse find(String ip, String mac, String context, Principal principal)
            throws PortalException;
}
