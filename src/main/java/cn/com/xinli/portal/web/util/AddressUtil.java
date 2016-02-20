package cn.com.xinli.portal.web.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Address utility.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/2.
 */
public class AddressUtil {
    /**
     * Validate incoming request ip.
     *
     * <p>If realIp exists, then nginx detected.
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
