package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.*;
import cn.com.xinli.portal.protocol.PortalProtocolException;
import cn.com.xinli.portal.rest.auth.AccessAuthentication;
import cn.com.xinli.portal.rest.auth.challenge.ChallengeNotFoundException;
import cn.com.xinli.portal.rest.token.InvalidAccessTokenException;
import cn.com.xinli.portal.rest.token.InvalidSessionTokenException;
import cn.com.xinli.rest.RestResponse;
import cn.com.xinli.rest.api.EntryPoint;
import cn.com.xinli.rest.api.Provider;
import cn.com.xinli.rest.bean.Error;
import cn.com.xinli.rest.bean.RestBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring web MVC controller advice.
 * <p>
 * This class handles all uncaught exceptions (includes checked and unchecked),
 * wraps exceptions into error response JSON.
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
@ControllerAdvice(basePackages = "cn.com.xinli.portal.rest")
public class RestExceptionAdvisor extends ResponseEntityExceptionHandler {
    /**
     * Logger.
     */
    private final Logger logger = LoggerFactory.getLogger(RestExceptionAdvisor.class);

    @Autowired
    private ActivityService activityService;

    @Autowired
    private Provider restApiProvider;

    private Activity.Action getActivityAction(HttpServletRequest request) {
        String uri = request.getRequestURI();
        Set<Optional<EntryPoint>> entryPoints = restApiProvider.getRegistrations().stream()
                .map(reg -> reg.getApis().stream().filter(api -> api.getUrl().equals(uri)).findFirst())
                .collect(Collectors.toSet());

        Optional<Optional<EntryPoint>> target = entryPoints.stream()
                .filter(Optional::isPresent)
                .findFirst();

        Activity.Action action = null;
        if (target.isPresent()) {
            Optional<EntryPoint> ep = target.get();
            if (ep.isPresent()) {
                action = Activity.Action.ofAlias(ep.get().getAction());
            }
        }

        return action == null ? Activity.Action.UNKNOWN : action;
    }

    /**
     * REST controller exception handler.
     * <p>
     * <p>Unhandled exception thrown from REST controllers will be
     * handled here. By default, it will return a {@link Error} contains additional
     * information about what's going on of the PWS.</p>
     * <p>
     * This function will return a HTTP 500 status with
     * a {@link Error} JSON inside http response body.
     *
     * @param e {@link RuntimeException}
     * @return {@link RestBean}
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(value = {AuthenticationException.class})
    public RestBean handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        if (logger.isDebugEnabled()) {
            logger.error("handle exception: {} ", e.getMessage());
        }

        return RestResponseBuilders.errorBuilder()
                .setError(RestResponse.ERROR_INVALID_REQUEST)
                .setDescription(e.getMessage())
                .setUrl("/error")
                .build();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(value = {BadCredentialsException.class})
    public RestBean handleAuthenticationException(BadCredentialsException e) {
        if (logger.isDebugEnabled()) {
            logger.error("handle exception: {} ", e.getMessage());
        }

        return RestResponseBuilders.errorBuilder()
                .setError(RestResponse.ERROR_INVALID_REQUEST)
                .setDescription(e.getMessage())
                .setUrl("/error")
                .build();
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler(value = {SessionNotFoundException.class})
    public RestBean handleSessionNotFoundException(SessionNotFoundException e) {
        if (logger.isDebugEnabled()) {
            logger.error("handle exception: {} ", e.getMessage());
        }

        AccessAuthentication authentication =
                (AccessAuthentication) SecurityContextHolder.getContext().getAuthentication();

        return RestResponseBuilders.successBuilder()
                .setAccessAuthentication(authentication)
                .build();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    @ExceptionHandler(value = {InvalidAccessTokenException.class, ChallengeNotFoundException.class})
    public RestBean handleChallengeException(Exception e) {
        if (logger.isDebugEnabled()) {
            logger.error("handle exception: {} ", e.getMessage());
        }

        return RestResponseBuilders.errorBuilder()
                .setError(RestResponse.ERROR_INVALID_CLIENT_GRANT)
                .setDescription(e.getMessage())
                .build();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    @ExceptionHandler(value = {
            InvalidSessionUpdateException.class,
            DeviceChangedException.class,
            InvalidSessionTokenException.class})
    public RestBean handleSessionException(Exception e) {
        if (logger.isDebugEnabled()) {
            logger.error("handle exception: {} ", e.getMessage());
        }

        return RestResponseBuilders.errorBuilder()
                .setError(RestResponse.ERROR_INVALID_SESSION_GRANT)
                .setDescription(e.getMessage())
                .build();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    @ExceptionHandler(value = {
            AccessDeniedException.class})
    public RestBean handleAccessDeniedException(AccessDeniedException e) {
        if (logger.isDebugEnabled()) {
            logger.error("handle exception: {} ", e.getMessage());
        }

        return RestResponseBuilders.errorBuilder()
                .setError(RestResponse.ERROR_UNAUTHORIZED_REQUEST)
                .setDescription(e.getMessage())
                .build();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    @ExceptionHandler(value = { PortalProtocolException.class})
    public RestBean handlePortalProtocolException(PortalProtocolException e) {
        if (logger.isDebugEnabled()) {
            logger.error("handle exception: {} ", e.getMessage());
        }
        return RestResponseBuilders.errorBuilder()
                .setError(RestResponse.ERROR_SERVER_ERROR)
                .setDescription(e.getMessage())
                .build();
    }
}
