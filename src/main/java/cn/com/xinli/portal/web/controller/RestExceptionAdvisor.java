package cn.com.xinli.portal.web.controller;

import cn.com.xinli.portal.core.PlatformException;
import cn.com.xinli.portal.core.session.SessionNotFoundException;
import cn.com.xinli.portal.web.auth.AccessAuthentication;
import cn.com.xinli.portal.core.*;
import cn.com.xinli.portal.support.PortalErrorTranslator;
import cn.com.xinli.portal.web.rest.RestResponse;
import cn.com.xinli.portal.web.rest.RestResponseBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;

/**
 * Spring web MVC controller advice.
 *
 * <p>This class handles all uncaught exceptions (includes checked and unchecked),
 * wraps exceptions into error response JSON.
 * <p>Unhandled exception thrown from REST controllers will be
 * handled here. By default, it will return a {@link Error} contains additional
 * information about what's going on of the PWS.
 *
 * <p>If incoming requests came with authentication in the http header,
 * server should respond with authentication results even if those
 * requests resulting in errors.
 *
 * <p>Authentication exceptions thrown from server do not come from
 * any controllers, those exceptions may be thrown by spring-security framework,
 * or custom {@link javax.servlet.Filter}s,
 * {@link org.springframework.web.servlet.HandlerInterceptor}
 * and other in-house infrastructures.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
@Service
@ControllerAdvice(basePackages = "cn.com.xinli.portal.web.controller")
public class RestExceptionAdvisor extends ResponseEntityExceptionHandler {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(RestExceptionAdvisor.class);

    /** Translator. */
    @Autowired
    private PortalErrorTranslator errorTranslator;

    /**
     * Translate portal error to HTTP status code and set to response.
     * @param response http response.
     * @param container portal exception.
     */
    private void setResponseStatus(HttpServletResponse response, PortalErrorContainer container) {
        response.setStatus(errorTranslator.translate(container.getPortalError()));
    }

    /**
     * REST controller platform exception handler.
     *
     * <p>{@link PlatformException}s are generated by portal service
     * platform nodes, including NAS/BRAS, AAA.
     *
     * @param response servlet http response.
     * @param e {@link PlatformException}
     * @return {@link RestResponse}
     */
    @ResponseBody
    @ExceptionHandler(value = {PlatformException.class})
    public RestResponse handlePlatformException(HttpServletResponse response, PlatformException e) {
        setResponseStatus(response, e);

        RestResponse rs = RestResponseBuilders.errorBuilder()
                .setError(e.getPortalError())
                .setDescription(e.getMessage())
                .setUrl("/error")
                .build();

        if (logger.isDebugEnabled()) {
            logger.debug("handle platform exception: {} -> {} ", e.getMessage(), rs);
        }

        return rs;
    }

    /**
     * Session not found exception handler.
     *
     * <p>This function will return a HTTP 404 status with
     * a JSON inside http response body.
     *
     * @param e {@link SessionNotFoundException}
     * @return response json object.
     */
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler(value = {SessionNotFoundException.class})
    public RestResponse handleSessionNotFoundException(SessionNotFoundException e) {
        /* In case that request came with authentication. */
        AccessAuthentication authentication =
                (AccessAuthentication) SecurityContextHolder.getContext().getAuthentication();

        RestResponse rs = RestResponseBuilders.successBuilder()
                .setAccessAuthentication(authentication)
                .build();

        if (logger.isDebugEnabled()) {
            logger.debug("handle session exception: {} -> {} ", e.getMessage(), rs);
        }

        return rs;
    }

    /**
     * REST API remote exception handler.
     *
     * <p>{@link RemoteException} may occur before client acquired {@link AccessAuthentication},
     * so response need to check if {@link AccessAuthentication} already acquired.
     *
     * @param response servlet http response.
     * @param e {@link RemoteException}
     * @return response json object.
     */
    @ResponseBody
    @ExceptionHandler(value = {RemoteException.class})
    public RestResponse handleRemoteException(HttpServletResponse response, RemoteException e) {
        setResponseStatus(response, e);

        /* In case that request came with authentication. */
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        AccessAuthentication authentication = auth != null && auth instanceof AccessAuthentication ?
                (AccessAuthentication) SecurityContextHolder.getContext().getAuthentication() :
                null;

        RestResponse rs = RestResponseBuilders.errorBuilder()
                .setError(e.getPortalError())
                .setAccessAuthentication(authentication)
                .setDescription(e.getMessage())
                .build();

        if (logger.isDebugEnabled()) {
            logger.debug("handle remote exception: {} -> {} ", e.getMessage(), rs);
        }

        return rs;
    }

    /**
     * REST API access denied exception handler.
     *
     * <p>For some reason, incoming request pass authentication check, i.e. got an
     * anonymous role, and try to access restricted controller methods.
     * {@link AccessDeniedException} will be thrown by spring-security framework.
     * In this case, server should response HTTP 403 FORBIDDEN, and an error
     * json result inside http entity.
     *
     * <p>{@link AccessDeniedException}s thrown by spring-security framework are
     * NOT defined by server, so it's impossible to get portal error out it,
     * we should translate this exception to portal error and then send to
     * remote client.
     *
     * <p>This function will return a HTTP 403 status with
     * a {@link Error} JSON inside http response body.
     *
     * @param e {@link AccessDeniedException}
     * @return response json object.
     */
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ResponseBody
    @ExceptionHandler(value = {AccessDeniedException.class})
    public RestResponse handleAccessDeniedException(AccessDeniedException e) {
        RestResponse rs = RestResponseBuilders.errorBuilder()
                .setError(PortalError.UNAUTHORIZED_REQUEST)
                .setDescription(e.getMessage())
                .build();

        if (logger.isDebugEnabled()) {
            logger.debug("handle access denied: {} -> {} ", e.getMessage(), rs);
        }

        return rs;
    }

    /**
     * Server internal exception handler.
     *
     * <p>Server internal exceptions indicate that something went wrong on server,
     * this should not happen. When server respond {@link ServerException} to
     * remote client, client should report this exception to the server developers,
     * so that they can figure out what and why server went wrong,
     * they can also find potential bugs and issues and try to fix them.
     *
     * @param response servlet http response.
     * @param e {@link ServerException}
     * @return response json object.
     */
    @ResponseBody
    @ExceptionHandler(value = {ServerException.class})
    public RestResponse handleServerException(HttpServletResponse response, ServerException e) {
        setResponseStatus(response, e);

        RestResponse rs = RestResponseBuilders.errorBuilder()
                .setError(e.getPortalError())
                .setDescription(e.getMessage())
                .build();

        if (logger.isDebugEnabled()) {
            logger.debug("handle server exception: {} -> {} ", e.getMessage(), rs);
        }

        return rs;
    }

    /**
     * Server runtime exception handler.
     *
     * <p>Same as {@link ServerException}, {@link RuntimeException} also should not
     * happen. When it happens, server responds with HTTP 500 and
     * an additional text message describe server internal error.
     * Users should report errors to server developers.
     *
     * <p>This function will return a HTTP 500 status with
     * a JSON inside http response body.
     *
     * @param e runtime exception.
     * @return response json object.
     */
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(value = {RuntimeException.class})
    public RestResponse handleRuntimeException(RuntimeException e) {
        RestResponse rs = RestResponseBuilders.errorBuilder()
                .setError(PortalError.SERVER_INTERNAL_ERROR)
                .setDescription(e.getMessage())
                .build();

        if (logger.isDebugEnabled()) {
            logger.debug("handle runtime exception: {} -> {} ", e.getMessage(), rs);
        }

        return rs;
    }
}
