package cn.com.xinli.portal.core;

/**
 * PWS exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 * @see PortalError
 */
public class ServerException extends PortalException {
    public ServerException(PortalError error, String message) {
        super(error, message);
    }

    public ServerException(PortalError error, String message, Throwable cause) {
        super(error, message, cause);
    }
}
