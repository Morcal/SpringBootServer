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

    /** INET Address. */
    private InetAddress address;

    private final String mac;

    User(InetAddress address, String mac) {
        this.address = address;
        this.mac = mac;
    }

    public InetAddress getAddress() {
        return address;
    }

    public String getMac() {
        return mac;
    }

    private Credential credential;


    public static User create(String address, String mac) {
        StringUtils.isEmpty(address);
        //TODO validate ip address and mac
        try {
            return new User(AddressUtil.toInetAddress(address), mac);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }
}
