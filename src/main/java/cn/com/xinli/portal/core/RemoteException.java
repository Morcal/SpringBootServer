package cn.com.xinli.portal.core;

/**
 * Remote exception.
 *
 * <p>Remote exceptions will be thrown when remote client requests violates
 * certain restrictions or critical information not provided in those requests.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 */
public class RemoteException extends PortalException {
    public RemoteException(PortalError error, String message) {
        super(error, message);
    }
}
