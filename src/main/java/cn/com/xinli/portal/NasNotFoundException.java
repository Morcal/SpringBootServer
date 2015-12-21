package cn.com.xinli.portal;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
@ResponseStatus
public class NasNotFoundException extends PortalException {
    public NasNotFoundException(String ip, String mac) {
        super("NAS configuration not found for: " + Session.pair(ip, mac));
    }
}
