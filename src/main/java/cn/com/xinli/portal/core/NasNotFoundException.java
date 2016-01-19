package cn.com.xinli.portal.core;

/**
 * NAS not found exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 */
public class NasNotFoundException extends PortalException {
    public NasNotFoundException(String message) {
        super(PortalError.of("nas_not_found"), message);
    }
}
