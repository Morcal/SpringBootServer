package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.DeviceChangedException;
import cn.com.xinli.portal.InvalidPortalRequestException;
import cn.com.xinli.portal.InvalidSessionUpdateException;
import cn.com.xinli.portal.SessionNotFoundException;
import cn.com.xinli.portal.protocol.PortalProtocolException;
import cn.com.xinli.portal.rest.auth.AccessAuthentication;
import cn.com.xinli.portal.rest.auth.challenge.ChallengeNotFoundException;
import cn.com.xinli.portal.rest.token.InvalidAccessTokenException;
import cn.com.xinli.portal.rest.token.InvalidSessionTokenException;
import cn.com.xinli.rest.RestResponse;
import cn.com.xinli.rest.bean.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
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
@Service
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
     * handled here. By default, it will return a {@link Error} contains additional
     * information about what's going on of the PWS.</p>
     * <p>
     * This function will return a HTTP 500 status with
     * a {@link Error} JSON inside http response body.
     *
     * @param e {@link RuntimeException}
     * @return {@link RestResponse}
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(value = {AuthenticationException.class})
    public RestResponse handleAuthenticationException(AuthenticationException e) {
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
    public RestResponse handleAuthenticationException(BadCredentialsException e) {
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
    public RestResponse handleSessionNotFoundException(SessionNotFoundException e) {
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
    public RestResponse handleChallengeException(Exception e) {
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
    public RestResponse handleSessionException(Exception e) {
        if (logger.isDebugEnabled()) {
            logger.error("handle exception: {} ", e.getMessage());
        }

        AccessAuthentication authentication =
                (AccessAuthentication) SecurityContextHolder.getContext().getAuthentication();

        return RestResponseBuilders.errorBuilder()
                .setError(RestResponse.ERROR_INVALID_SESSION_GRANT)
                .setAccessAuthentication(authentication)
                .setDescription(e.getMessage())
                .build();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    @ExceptionHandler(value = {
            InvalidPortalRequestException.class})
    public RestResponse handleInvalidPortalRequestException(InvalidPortalRequestException e) {
        if (logger.isDebugEnabled()) {
            logger.error("handle exception: {} ", e.getMessage());
        }

        AccessAuthentication authentication =
                (AccessAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return RestResponseBuilders.errorBuilder()
                .setError(RestResponse.ERROR_INVALID_REQUEST)
                .setAccessAuthentication(authentication)
                .setDescription(e.getMessage())
                .build();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    @ExceptionHandler(value = {
            AccessDeniedException.class})
    public RestResponse handleAccessDeniedException(AccessDeniedException e) {
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
    public RestResponse handlePortalProtocolException(PortalProtocolException e) {
        if (logger.isDebugEnabled()) {
            logger.error("handle exception: {} ", e.getMessage());
        }

        AccessAuthentication authentication =
                (AccessAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return RestResponseBuilders.errorBuilder()
                .setError(RestResponse.ERROR_SERVER_ERROR)
                .setAccessAuthentication(authentication)
                .setDescription(e.getMessage())
                .build();
    }
}
