package cn.com.xinli.portal.core.credentials;

/**
 * Default credentials.
 *
 * <p>Default credentials contains all information needed for a
 * more generic portal service, and it's sufficient for user to
 * perform an authentication via web page.
 *
 * <p>On the server side, particularly when the portal server perform
 * an portal request to remote NAS/BRAS as client, remote NAS/BRAS
 * need server to provide specific additional information, those infomration
 * may be context-data generated (originated) by NAS/BRAS.
 * For example, HUAWEI portal protocol need clients to provide
 * <code>request id</code> (which is exactly originated by NAS/BRAS) when
 * clients request certain operations. Under that circumstances, server
 * (provider) should extends {@link Credentials} to add extended information
 * to archive the goal that server can work with those devices.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/2.
 */
public class DefaultCredentials extends Credentials {
    @Override
    protected CredentialsType getCredentialsType() {
        return CredentialsType.DEFAULT;
    }

    public static DefaultCredentials of(String username, String password, String ip, String mac) {
        DefaultCredentials credentials = new DefaultCredentials();
        credentials.setUsername(username);
        credentials.setPassword(password);
        credentials.setIp(ip);
        credentials.setMac(mac);
        return credentials;
    }
}
