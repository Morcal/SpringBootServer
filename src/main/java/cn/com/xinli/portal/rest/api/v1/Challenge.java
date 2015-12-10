package cn.com.xinli.portal.rest.api.v1;

import org.springframework.security.core.GrantedAuthority;

/**
 * Challenge.
 *
 * Challenges have no any kind of {@link GrantedAuthority}s.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/10.
 */
public interface Challenge {
    String getClientId();
    String getChallenge();
    String getResponse();
    boolean isExpired();
    boolean isRevoked();
    boolean isLocked();

    String getNonce();
}
