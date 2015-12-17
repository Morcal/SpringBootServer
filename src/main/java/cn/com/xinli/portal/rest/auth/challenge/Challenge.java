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
    /**
     * Get challenge content.
     * @return challenge content.
     */
    String getChallenge();

    /**
     * Get nonce.
     * @return nonce.
     */
    String getNonce();

    /**
     * If challenge need refresh token.
     * @return true if need.
     */
    boolean needRefreshToken();

    /**
     * If chellenge requires token.
     * @return true if requires.
     */
    boolean requiresToken();

    /**
     * Get challenge scope.
     * @return challenge scope.
     */
    String getScope();

    /**
     * Get client id.
     * @return client id.
     */
    String getClientId();
}
