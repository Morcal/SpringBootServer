package cn.com.xinli.portal.rest.auth.challenge;

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
    String getChallenge();
    String getResponse();
    String getNonce();
}
