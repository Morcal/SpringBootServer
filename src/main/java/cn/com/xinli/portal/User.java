package cn.com.xinli.portal;

import cn.com.xinli.portal.util.AddressUtil;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Portal web server user.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public class User {

    /** IP Address. */
    private final String address;

    private final String mac;

    User(String address, String mac) {
        this.address = address;
        this.mac = mac;
    }

    public String getAddress() {
        return address;
    }

    public String getMac() {
        return mac;
    }
}
