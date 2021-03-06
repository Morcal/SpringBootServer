package cn.com.xinli.portal.core.credentials;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.RemoteException;

/**
 * Invalid credentials exception.
 *
 * <p>This exception will throw, when credentials failed to
 * pass protocol credentials verification before portal requests
 * are processed.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/18.
 */
public class CredentialsException extends RemoteException {
    public CredentialsException(String message) {
        super(PortalError.INVALID_CREDENTIALS, message);
    }

    public CredentialsException(Credentials credentials) {
        super(PortalError.INVALID_CREDENTIALS, credentials.toString());
    }
}
