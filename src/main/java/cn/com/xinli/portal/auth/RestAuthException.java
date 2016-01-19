package cn.com.xinli.portal.auth;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.PortalErrorContainer;
import org.springframework.security.core.AuthenticationException;

import java.util.Objects;

/**
 * REST API authentication and authorization base exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 */
public abstract class RestAuthException extends AuthenticationException implements PortalErrorContainer {
    /** Internal portal error. */
    private final PortalError error;

    public RestAuthException(PortalError error, String message, Throwable cause) {
        super(message, cause);
        Objects.requireNonNull(error);
        this.error = error;
    }

    public RestAuthException(PortalError error, String message) {
        super(message);
        Objects.requireNonNull(error);
        this.error = error;
    }

    @Override
    public PortalError getPortalError() {
        return error;
    }
}
