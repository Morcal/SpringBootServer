package cn.com.xinli.portal.rest.auth.challenge;

import cn.com.xinli.portal.rest.auth.AbstractRestAuthentication;
import cn.com.xinli.portal.rest.auth.HttpDigestCredentials;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * Challenge Authentication.
 *
 * <p>Challenge Authentication be sent by clients when
 * they are in the middle of authentication process.
 * </p>
 *
 * This authentication can NOT contain any {@link GrantedAuthority}.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/10.
 */
public class ChallengeAuthentication extends AbstractRestAuthentication {
    public ChallengeAuthentication(String principal, HttpDigestCredentials credentials) {
        super(AuthorityUtils.NO_AUTHORITIES, principal, credentials);
    }

    public static ChallengeAuthentication of(String httpDigestCredentials) {
        HttpDigestCredentials credentials = HttpDigestCredentials.of(httpDigestCredentials);
        String principal = credentials.getParameter(HttpDigestCredentials.CLIENT_ID);
        return new ChallengeAuthentication(principal, credentials);
    }
}
