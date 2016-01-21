package cn.com.xinli.portal.controller;

import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.SessionNotFoundException;
import cn.com.xinli.portal.auth.RestRole;
import cn.com.xinli.portal.support.rest.RestResponse;

import java.security.Principal;

/**
 * Portal web server session REST APIs controller.
 *
 * <p>Portal web server session controller interface.
 * Define this controller interface so that spring-aop Aspects
 * can work on it properly.
 *
 * <p>All spring-web-mvc annotations should be declared in the
 * implementation class.
 *
 * <p>Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public interface SessionController {

    /**
     * Create a portal session so that user can access broadband connection.
     * <p>
     * <p>Only request authenticated with role of {@link RestRole#USER}
     * and has authority of this session can proceed.</p>
     *
     * @param ip        source ip address.
     * @param mac       source mac address.
     * @param principal spring security principal.
     * @return JSON.
     */
    RestResponse connect(String username,
                         String password,
                         String ip,
                         String mac,
                         String os,
                         String version,
                         Principal principal)
            throws PortalException;

    /**
     * Get portal session information.
     * <p>
     * <p>Only request authenticated with role of {@link RestRole#USER}
     * and has authority of this session can proceed.</p>
     * <p>
     * <p>AFAIK, Administrators with role of {@link RestRole#ADMIN}
     * overrule anything and everything.
     *
     * @param id        session id.
     * @param principal spring security principal.
     * @return JSON.
     */
    RestResponse get(long id, Principal principal) throws SessionNotFoundException;

    /**
     * Update portal session.
     * <p>
     * <p>Only request authenticated with role of {@link RestRole#USER}
     * and has authority of this session can proceed.</p>
     * <p>
     * <p>AFAIK, Administrators with role of {@link RestRole#ADMIN}
     * overrule anything and everything.
     *
     * @param id        session id.
     * @param timestamp source timestamp.
     * @param principal spring security principal.
     * @return JSON.
     */
    RestResponse update(long id, long timestamp, Principal principal)
            throws PortalException;

    /**
     * Disconnect portal session.
     * <p>
     * <p>Only request authenticated with role of {@link RestRole#USER}
     * and has authority of this session can proceed.</p>
     * <p>
     * <p>AFAIK, Administrators with role of {@link RestRole#ADMIN}
     * overrule anything and everything.
     *
     * @param id        session id.
     * @param principal spring security principal.
     * @return JSON.
     */
    RestResponse disconnect(long id, Principal principal) throws PortalException;

    /**
     * Find portal session created by user ip and user mac.
     * <p>Only request authenticated with role of {@link RestRole#USER}
     * and has authority of this session can proceed.</p>
     *
     * @param ip        user ip address.
     * @param mac       user mac address.
     * @param principal spring security principal.
     * @return JSON.
     * @throws SessionNotFoundException
     */
    RestResponse find(String ip, String mac, Principal principal)
            throws SessionNotFoundException;
}
