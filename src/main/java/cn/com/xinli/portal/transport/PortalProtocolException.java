package cn.com.xinli.portal.transport;

import java.util.Objects;

/**
 * Abstract portal protocol exception.
 *
 * <p>This exception not extends from outside exception like PortalException,
 * so that protocol package can run in standalone mode.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/27.
 */
public abstract class PortalProtocolException extends Exception implements ProtocolErrorContainer {
    /** Internal protocol error. */
    private final ProtocolError error;

    public PortalProtocolException(ProtocolError error) {
        super(error.getReason());
        Objects.requireNonNull(error);
        this.error = error;
    }

    public PortalProtocolException(ProtocolError error, String message) {
        super(error.getReason() + ", info:" + message);
        Objects.requireNonNull(error);
        this.error = error;
    }

    public PortalProtocolException(ProtocolError error, String message, Throwable cause) {
        super(error.getReason() + ", info:" + message, cause);
        Objects.requireNonNull(error);
        this.error = error;
    }

    /**
     * Get internal protocol error.
     * @return internal protocol error.
     */
    @Override
    public ProtocolError getProtocolError() {
        return error;
    }
}
