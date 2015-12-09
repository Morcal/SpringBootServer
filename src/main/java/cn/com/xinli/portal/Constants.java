package cn.com.xinli.portal;

import org.apache.commons.lang3.CharEncoding;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
public class Constants {

    /** Portal client/user-agent source ip parameter name in the direction URL. */
    public static final String REDIRECT_SOURCE_IP = "redirect-source-ip";

    /** Portal client/user-agent source MAC parameter name in the direction URL. */
    public static final String REDIRECT_SOURCE_MAC = "redirect-source-mac";

    /** Portal redirect NAS ip. */
    public static final String REDIRECT_NAS_IP = "redirect-nas-ip";

    /** Portal redirect BAS ip. */
    public static final String REDIRECT_BAS_IP = "redirect-bas-ip";

    public static final String DEFAULT_CHAR_ENCODING = CharEncoding.UTF_8;

    public static final String HMAC_SHA1 = "HmacSHA1";

    public static final String DEFAULT_KEY_SPEC = HMAC_SHA1;
}
