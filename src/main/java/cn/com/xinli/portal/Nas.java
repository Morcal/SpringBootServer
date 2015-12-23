package cn.com.xinli.portal;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Device (NAS/BRAS) support Portal protocol configuration.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public interface Nas {
    String DEFAULT_NAS_TYPE = "Huawei";

    int DEFAULT_NAS_LISTEN_PORT = 2000;

    String DEFAULT_NAS_AUTHENTICATION_TYPE = "CHAP";

    long getId();

    String getIpv4Address();

    String getIpv6Address();

    String getType();

    int getListenPort();

    String getAuthType();

    int getIpv4end();

    int getIpv4start();

    String getSharedSecret();

    static String getIp(Nas nas) {
        return StringUtils.isEmpty(nas.getIpv4Address()) ? nas.getIpv6Address() : nas.getIpv4Address();
    }

    static InetAddress getInetAddress(Nas nas) throws UnknownHostException {
        return InetAddress.getByName(getIp(nas));
    }
}
