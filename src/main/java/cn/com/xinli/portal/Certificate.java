package cn.com.xinli.portal;

/**
 * Authorized client application certificate.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
public interface Certificate {
    long getId();

    String getAppId();

    String getSharedSecret();

    String getOs();

    String getVendor();

    String getVersion();
}
