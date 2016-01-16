package cn.com.xinli.portal.util;

import cn.com.xinli.portal.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Codec utility.
 *
 * Project: rest-api-rest-api
 *
 * @author zhoupeng 2015/12/13.
 */
public class CodecUtil {
    /**
     * URL encode string.
     * @param value string to encode.
     * @return encoded string.
     * @throws UnsupportedEncodingException
     */
    public static String urlEncode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, Constants.DEFAULT_CHAR_ENCODING);
    }

    /**
     * URL decode string.
     * @param value string to decode.
     * @return decoded string.
     * @throws UnsupportedEncodingException
     */
    public static String urlDecode(String value) throws UnsupportedEncodingException {
        return URLDecoder.decode(value, Constants.DEFAULT_CHAR_ENCODING);
    }
}
