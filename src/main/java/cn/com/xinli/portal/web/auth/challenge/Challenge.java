package cn.com.xinli.portal.web.auth.challenge;

import org.springframework.security.core.GrantedAuthority;

/**
 * Challenge.
 *
 * <p>Challenges are provided by server to ask clients to answer for
 * a given content. Challenges in PWS record some context in the
 * challenge process, such as whether client need refresh token,
 * and whether client required token and what scope client required.
 * It may make the challenge process less RESTful, considered that
 * challenges have a very short time to live, (by default, its 10 seconds).
 * It's almost ok to implement this way.
 *
 * <p>Challenges have no any kind of {@link GrantedAuthority}s.
 *
 * <p>Project: xpws.
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
     * If challenge requires token.
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
