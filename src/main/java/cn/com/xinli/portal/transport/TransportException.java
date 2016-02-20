package cn.com.xinli.portal.transport;

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
public abstract class TransportException extends Exception implements TransportErrorContainer {
    /** Internal protocol error. */
    private final TransportError error;

    public TransportException(TransportError error) {
        super(error.getReason());
        this.error = error;
    }

    public TransportException(TransportError error, String message) {
        super(error.getReason() + ", info:" + message);
        this.error = error;
    }

    public TransportException(TransportError error, String message, Throwable cause) {
        super(error.getReason() + ", info:" + message, cause);
        this.error = error;
    }

    /**
     * Get internal protocol error.
     * @return internal protocol error.
     */
    @Override
    public TransportError getProtocolError() {
        return error;
    }
}
