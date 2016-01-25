package cn.com.xinli.portal.web.auth;

import cn.com.xinli.portal.core.Session;
import org.springframework.security.core.GrantedAuthority;

/**
 * Session authority.
 *
 * <p>This class provides a very simple session authority based on
 * {@link Session}'s id.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/18.
 */
public class SessionAuthority implements GrantedAuthority {
    /** Associated session id. */
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
