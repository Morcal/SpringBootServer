package cn.com.xinli.portal.protocol;

/**
 * Nas not supported exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/27.
 */
public class UnsupportedNasException extends ProtocolException {
    public UnsupportedNasException(String nasType) {
        super("Unsupported nas type: " + nasType);
    }
}
