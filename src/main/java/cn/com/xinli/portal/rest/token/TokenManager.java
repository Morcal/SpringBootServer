package cn.com.xinli.portal.rest.token;

import org.springframework.security.core.token.Token;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/18.
 */
public interface TokenManager {
    /**
     * Revoke token.
     * @param token token to revoke.
     * @return true if revoked.
     */
    boolean revokeToken(Token token);
}
