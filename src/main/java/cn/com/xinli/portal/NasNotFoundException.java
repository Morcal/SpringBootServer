package cn.com.xinli.portal;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * PWS throws this exception when {@link Nas} not found.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
@ResponseStatus
public class NasNotFoundException extends PortalException {
    public NasNotFoundException(String message) {
        super(message);
    }

    public NasNotFoundException(String ip, String mac) {
        this("NAS configuration not found for: " + Session.pair(ip, mac));
    }
}
