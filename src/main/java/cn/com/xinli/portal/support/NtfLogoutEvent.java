package cn.com.xinli.portal.support;

import org.springframework.context.ApplicationEvent;

/**
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/31.
 */
public class NtfLogoutEvent extends ApplicationEvent {
    private final String nasIp;
    private final String userIp;

    public NtfLogoutEvent(String nasIp, String userIp) {
        super(nasIp);
        this.nasIp = nasIp;
        this.userIp = userIp;
    }

    public String getNasIp() {
        return nasIp;
    }

    public String getUserIp() {
        return userIp;
    }
}
