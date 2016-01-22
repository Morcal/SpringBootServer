package cn.com.xinli.portal.auth;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.PortalErrorContainer;
import org.springframework.security.core.AuthenticationException;

import java.util.Objects;

/**
 * REST API authentication and authorization base exception.
 *
 * <p>This class provides an abstract base exception for all exceptions
 * which represent REST API authentication and authorization errors.
 *
 * <p>Since PWS built REST API error system upon spring-security framework,
 * all exceptions thrown from within authentication and authorization should
 * extends from spring's {@link AuthenticationException}. To include globally
 * defined {@link PortalError}s, this abstract base class was created. It
 * implements {@link PortalErrorContainer} so that a specific {@link PortalError}
 * can be retrieved from exceptions.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 */
public abstract class RestAuthException extends AuthenticationException implements PortalErrorContainer {
    /** Internal portal error. */
    private final PortalError error;


    public RestAuthException(PortalError error) {
        super(error.getReason());
        Objects.requireNonNull(error);
        this.error = error;
    }

    public RestAuthException(PortalError error, String message, Throwable cause) {
        super(error.getReason() + ", info:" + message, cause);
        Objects.requireNonNull(error);
        this.error = error;
    }

    public RestAuthException(PortalError error, String message) {
        super(error.getReason() + ", info:" + message);
        Objects.requireNonNull(error);
        this.error = error;
    }

    @Override
    public PortalError getPortalError() {
        return error;
    }
}
