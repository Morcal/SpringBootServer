package cn.com.xinli.portal.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public class AddressUtil {

    private static final Log log = LogFactory.getLog(AddressUtil.class);

    public static String getRemoteAddress(HttpServletRequest request) {
        //TODO implement get remote address.
        request.getRemoteAddr();
        return null;
    }

    public static InetAddress toInetAddress(String address) throws UnknownHostException {
        return InetAddress.getByName(address);
    }
}
