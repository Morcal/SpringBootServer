package cn.com.xinli.portal.core;

/**
 * Remote exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 */
public class RemoteException extends PortalException {
    public RemoteException(PortalError error, String message) {
        super(error, message);
    }
}
