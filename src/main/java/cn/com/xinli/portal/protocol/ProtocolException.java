package cn.com.xinli.portal.protocol;

/**
 * Abstract portal protocol exception.
 * <p>
 * This exception not extends from outside exception like PortalException,
 * so that protocol package can run in standalone mode.
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/27.
 */
public abstract class ProtocolException extends RuntimeException {
    public ProtocolException(String message) {
        super(message);
    }
}
