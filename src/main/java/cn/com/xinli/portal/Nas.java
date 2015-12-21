package cn.com.xinli.portal;

/**
 * Device (NAS/BRAS) support Portal protocol configuration.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public interface Nas {
    String DEFAULT_NAS_TYPE = "Huawei";

    int DEFAULT_NAS_LISTENPORT = 2000;

    String DEFAULT_NAS_AUTHTYPE = "CHAP";

    long getId();

    String getIpv4Address();

    String getIpv6Address();

    String getType();

    int getListenPort();

    String getAuthType();

    int getIpv4end();

    int getIpv4start();
}
