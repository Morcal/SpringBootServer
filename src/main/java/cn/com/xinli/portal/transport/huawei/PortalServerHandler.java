package cn.com.xinli.portal.transport.huawei;

/**
 * Huawei Portal Server Handler.
 *
 * <p>A Portal web server in a Huawei protocol based portal service need
 * to receive several portal requests from NAS/BRAS, in these scenarios
 * NAS/BRAS devices are portal protocol clients and Portal web server is
 * a portal protocol server.
 *
 * <p>Classes implement this interface only handles incoming NTF_LOGOUT
 * requests, those requests were send by NAS/BRAS to notify portal web server
 * that certain users already logout(or forced logout due to inactive for
 * certain amount of time of network activity).
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/4.
 */
public interface PortalServerHandler {
    /**
     * Handle NTF_LOGOUT from NAS.
     * @param address client address.
     * @return Huawei portal protocol logout error.
     * if logout successfully, {@link LogoutError#OK} should be returned.
     */
    LogoutError handleNtfLogout(String address);
}
