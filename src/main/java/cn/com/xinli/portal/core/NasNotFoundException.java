package cn.com.xinli.portal.core;

/**
 * NAS not found exception.
 *
 * <p>This exception will throw when can't find NAS for incoming
 * session requests.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 */
public class NasNotFoundException extends PortalException {
    public NasNotFoundException(String message) {
        super(PortalError.of("nas_not_found"), message);
    }
}
