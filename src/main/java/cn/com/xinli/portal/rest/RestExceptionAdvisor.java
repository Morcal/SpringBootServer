package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.SessionNotFoundException;
import cn.com.xinli.portal.protocol.ProtocolException;
import cn.com.xinli.portal.rest.auth.AccessAuthentication;
import cn.com.xinli.portal.rest.auth.challenge.ChallengeNotFoundException;
import cn.com.xinli.portal.rest.bean.Failure;
import cn.com.xinli.portal.rest.bean.RestBean;
import cn.com.xinli.portal.rest.token.InvalidAccessTokenException;
import cn.com.xinli.portal.rest.token.InvalidSessionTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    /**
     * REST controller exception handler.
     * <p>
     * <p>Unhandled exception thrown from REST controllers will be
     * handled here. By default, it will return a {@link Failure} contains additional
     * information about what's going on of the PWS.</p>
     * <p>
     * This function will return a HTTP 500 status with
     * a {@link Failure} JSON inside http response body.
     *
     * @param e {@link RuntimeException}
     * @return {@link RestBean}
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(value = {AuthenticationException.class})
    public RestBean handleAuthenticationException(AuthenticationException e) {
        if (logger.isDebugEnabled()) {
            logger.error("{} handle exception:", this.getClass().getName(), e);
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
            logger.error("{} handle exception:", this.getClass().getName(), e);
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
            logger.error("{} handle exception: {}", this.getClass().getName(), e.getMessage());
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
            logger.error("{} handle exception:", this.getClass().getName(), e);
        }

        return RestResponseBuilders.errorBuilder()
                .setError(RestResponse.ERROR_INVALID_CLIENT_GRANT)
                .setDescription(e.getMessage())
                .build();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    @ExceptionHandler(value = {
            UpdateOutOfRangeException.class,
            DeviceChangedException.class,
            InvalidSessionTokenException.class})
    public RestBean handleSessionException(Exception e) {
        if (logger.isDebugEnabled()) {
            logger.error("{} handle exception:", this.getClass().getName(), e);
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
            logger.error("{} handle exception:", this.getClass().getName(), e);
        }

        return RestResponseBuilders.errorBuilder()
                .setError(RestResponse.ERROR_UNAUTHORIZED_REQUEST)
                .setDescription(e.getMessage())
                .build();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    @ExceptionHandler(value = {ProtocolException.class})
    public RestBean handlePortalProtocolException(ProtocolException e) {
        if (logger.isDebugEnabled()) {
            logger.error("{} handle exception:", this.getClass().getName(), e);
        }
        return RestResponseBuilders.errorBuilder()
                .setError(RestResponse.ERROR_SERVER_ERROR)
                .setDescription(e.getMessage())
                .build();
    }
}
