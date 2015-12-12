package cn.com.xinli.portal.util;

import org.apache.commons.lang3.StringUtils;
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

    /**
     * Check if incoming request ip is valid.
     *
     * If realIp exists, then nginx detected.
     * @param realIp nginx header real ip.
     * @param sourceIp source ip in parameters.
     * @param request HTTP request.
     * @return true valid.
     */
    public static boolean isValidateIp(String realIp, String sourceIp, HttpServletRequest request) {
        String remote = request.getRemoteAddr();
        return StringUtils.isEmpty(realIp) ?
                StringUtils.equals(remote, sourceIp) :
                StringUtils.equals(realIp, sourceIp);
    }
}
