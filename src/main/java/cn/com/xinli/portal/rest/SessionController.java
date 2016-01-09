package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.InvalidPortalRequestException;
import cn.com.xinli.portal.SessionNotFoundException;
import cn.com.xinli.portal.SessionOperationException;
import cn.com.xinli.portal.protocol.NasNotFoundException;
import cn.com.xinli.portal.rest.auth.RestRole;
import cn.com.xinli.rest.RestResponse;
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
@RequestMapping("/portal/v1.0")
public interface SessionController {

    /**
     * Create a portal session so that user can access broadband connection.
     * <p>
     * <p>Only request authenticated with role of {@link RestRole#USER}
     * and has authority of this session can proceed.</p>
     *
     * @param ip  source ip address.
     * @param mac source mac address.
     * @param principal spring security principal.
     * @return JSON.
     */
    @ResponseBody
    @RequestMapping(value = "/sessions", method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER')")
    RestResponse connect(@RequestParam String username,
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
     * @param principal spring security principal.
     * @return JSON.
     */
    @ResponseBody
    @RequestMapping(value = "/session/{id}", method = RequestMethod.GET)
    @PreAuthorize("(hasRole('USER') and hasAuthority(#session)) or hasRole('ADMIN')")
    RestResponse get(@P("session") @PathVariable long id,
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
     * @param id        session id.
     * @param timestamp source timestamp.
     * @param principal spring security principal.
     * @return JSON.
     */
    @ResponseBody
    @RequestMapping(value = "/session/{id}", method = RequestMethod.POST)
    @PreAuthorize("(hasRole('USER') and hasAuthority(#session)) or hasRole('ADMIN')")
    RestResponse update(@P("session") @PathVariable long id,
                    @RequestParam long timestamp,
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
     * @param principal spring security principal.
     * @return JSON.
     */
    @ResponseBody
    @RequestMapping(value = "/session/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("(hasRole('USER') and hasAuthority(#session)) or hasRole('ADMIN')")
    RestResponse disconnect(@P("session") @PathVariable long id,
                        @AuthenticationPrincipal Principal principal)
            throws SessionNotFoundException, SessionOperationException, NasNotFoundException;

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
    @ResponseBody
    @RequestMapping(value = "/sessions/find", method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER')")
    RestResponse find(@RequestParam(value = "user_ip") String ip,
                  @RequestParam(value = "user_mac", defaultValue = "") String mac,
                  @AuthenticationPrincipal Principal principal)
            throws SessionNotFoundException;
}
