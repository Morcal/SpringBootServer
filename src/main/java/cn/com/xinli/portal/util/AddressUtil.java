package cn.com.xinli.portal.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public class AddressUtil {
    /**
     * Validate incoming request ip.
     *
     * If realIp exists, then nginx detected.
     * @param realIp nginx header real ip.
     * @param sourceIp source ip in parameters.
     * @param remote remote address.
     * @return true valid.
     */
    public static boolean validateIp(String realIp, String sourceIp, String remote) {
        return StringUtils.isEmpty(realIp) ?
                StringUtils.equals(remote, sourceIp) :
                StringUtils.equals(realIp, sourceIp);
    }
}
