package cn.com.xinli.portal.core;

/**
 * PWS exception.
 *
 * <p>Server exceptions will throw when server encountered internal errors.
 * Normally, those errors can not be recovered.
 *
 * <p>Server exceptions extend from {@link PortalException}, so portal errors
 * can be retrieved from server exceptions.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 * @see PortalError
 */
public class ServerException extends PortalException {

    public ServerException(PortalError error) {
        super(error);
    }

    public ServerException(PortalError error, String message) {
        super(error, message);
    }

    public ServerException(PortalError error, String message, Throwable cause) {
        super(error, message, cause);
    }
}
