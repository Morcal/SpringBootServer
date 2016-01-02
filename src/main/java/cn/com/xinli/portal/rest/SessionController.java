package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.InvalidPortalRequestException;
import cn.com.xinli.portal.NasNotFoundException;
import cn.com.xinli.portal.SessionNotFoundException;
import cn.com.xinli.portal.SessionOperationException;
import cn.com.xinli.portal.rest.auth.RestRole;
import cn.com.xinli.portal.rest.bean.RestBean;
import cn.com.xinli.portal.configuration.ApiConfiguration;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Portal web server session REST APIs controller.
 * <p>
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
@RestController
@RequestMapping("/${pws.root}/" + ApiConfiguration.REST_API_VERSION)
public interface SessionController {

    /**
     * Create a portal session so that user can access broadband connection.
     * <p>
     * <p>Only request authenticated with role of {@link RestRole#USER}
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
    RestBean connect(@RequestParam String username,
                            @RequestParam String password,
                            @RequestParam(name = "user_ip") String ip,
                            @RequestParam(name = "user_mac") String mac,
                            @RequestParam(defaultValue = "") String os,
                            @RequestParam(defaultValue = "") String version,
                            @AuthenticationPrincipal Principal principal)
            throws NasNotFoundException, SessionNotFoundException, SessionOperationException;
    /**
     * Get portal session information.
     * <p>
     * <p>Only request authenticated with role of {@link RestRole#USER}
     * and has authority of this session can proceed.</p>
     * <p>
     * <p>AFAIK, Administrators with role of {@link RestRole#ADMIN}
     * overrule anything and everything.
     *
     * @param id session id.
     * @return JSON.
     */
    @ResponseBody
    @RequestMapping(
            value = "/" + ApiConfiguration.REST_API_SESSION + "/{id}",
            method = RequestMethod.GET)
    @PreAuthorize("(hasRole('USER') and hasAuthority(#session)) or hasRole('ADMIN')")
    RestBean get(@P("session") @PathVariable long id,
                        @AuthenticationPrincipal Principal principal) throws SessionNotFoundException;

    /**
     * Update portal session.
     * <p>
     * <p>Only request authenticated with role of {@link RestRole#USER}
     * and has authority of this session can proceed.</p>
     * <p>
     * <p>AFAIK, Administrators with role of {@link RestRole#ADMIN}
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
    @PreAuthorize("(hasRole('USER') and hasAuthority(#session)) or hasRole('ADMIN')")
    RestBean update(@RequestParam long timestamp,
                           @P("session") @PathVariable long id,
                           @AuthenticationPrincipal Principal principal)
            throws InvalidPortalRequestException, SessionNotFoundException;

    /**
     * Disconnect portal session.
     * <p>
     * <p>Only request authenticated with role of {@link RestRole#USER}
     * and has authority of this session can proceed.</p>
     * <p>
     * <p>AFAIK, Administrators with role of {@link RestRole#ADMIN}
     * overrule anything and everything.
     *
     * @param id session id.
     * @return JSON.
     */
    @ResponseBody
    @RequestMapping(
            value = "/" + ApiConfiguration.REST_API_SESSION + "/{id}",
            method = RequestMethod.DELETE)
    @PreAuthorize("(hasRole('USER') and hasAuthority(#session)) or hasRole('ADMIN')")
    RestBean disconnect(@P("session") @PathVariable long id,
                               @AuthenticationPrincipal Principal principal)
            throws SessionNotFoundException, SessionOperationException, NasNotFoundException;

    @ResponseBody
    @RequestMapping(
            value = "/" + ApiConfiguration.REST_API_FIND,
            method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER')")
    RestBean find(@RequestParam(value = "user_ip") String ip,
                         @RequestParam(value = "user_mac", defaultValue = "") String mac,
                         @AuthenticationPrincipal Principal principal)
            throws SessionNotFoundException;
}
