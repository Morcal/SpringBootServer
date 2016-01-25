package cn.com.xinli.portal.core;

import java.util.Objects;

/**
 * Portal web server exception.
 *
 * <p>This class provides a base abstract exception for abnormal operation
 * results in spring-web-mvc controller, services and component.
 * It implements {@link PortalErrorContainer} so that operation error result
 * can be retrieved from this exception.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/2.
 */
public abstract class PortalException extends Exception implements PortalErrorContainer {
    /** Portal error. */
    private final PortalError error;

    public PortalException(PortalError error) {
        super(error.getReason());
        Objects.requireNonNull(error);
        this.error = error;
    }

    public PortalException(PortalError error, String message) {
        super(error.getReason() + ", info:" + message);
        Objects.requireNonNull(error);
        this.error = error;
    }

    public PortalException(PortalError error, String message, Throwable cause) {
        super(error.getReason() + ", info:" + message, cause);
        Objects.requireNonNull(error);
        this.error = error;
    }

    /**
     * Get portal error.
     * @return portal error.
     */
    @Override
    public PortalError getPortalError() {
        return error;
    }
}
