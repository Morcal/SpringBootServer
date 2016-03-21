package cn.com.xinli.portal.web.auth;

import cn.com.xinli.portal.core.session.Session;
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
public class ChallengeAuthority implements GrantedAuthority {
    /** Associated challenge. */
    private final String challenge;

    public ChallengeAuthority(String challenge) {
        this.challenge = challenge;
    }

    @Override
    public String getAuthority() {
        return challenge;
    }

    @Override
    public String toString() {
        return "ChallengeAuthority{" +
                "challenge=" + challenge +
                '}';
    }
}
