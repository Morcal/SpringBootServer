package cn.com.xinli.portal.auth;

import org.springframework.security.core.GrantedAuthority;

/**
 * Session authority.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/18.
 */
public class SessionAuthority implements GrantedAuthority {
    private final long sessionId;

    public SessionAuthority(long sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getAuthority() {
        return String.valueOf(sessionId);
    }

    @Override
    public String toString() {
        return "SessionAuthority{" +
                "sessionId=" + sessionId +
                '}';
    }
}