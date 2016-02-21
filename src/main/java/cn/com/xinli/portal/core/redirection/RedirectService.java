package cn.com.xinli.portal.core.redirection;

import cn.com.xinli.portal.core.RemoteException;

/**
 * Portal web redirect service.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/21.
 */
public interface RedirectService {
    /**
     * Verify redirection.
     * @param redirection redirection.
     * @param ip user ip address.
     * @param mac user mac address.
     * @return full populated redirection.
     * @throws RemoteException
     */
    Redirection verify(Redirection redirection, String ip, String mac) throws RemoteException;
}
