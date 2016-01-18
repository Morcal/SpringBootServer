package cn.com.xinli.portal.core;

import java.util.Objects;

/**
 * Portal web server exception.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public abstract class PortalException extends Exception {
    /** Portal error. */
    private final PortalError error;

    public PortalException(PortalError error, String message) {
        super(message);
        Objects.requireNonNull(error);
        this.error = error;
    }

    public PortalException(PortalError error, String message, Throwable cause) {
        super(message, cause);
        Objects.requireNonNull(error);
        this.error = error;
    }

    /**
     * Get portal error.
     * @return portal error.
     */
    public PortalError getError() {
        return error;
    }
}
