package cn.com.xinli.portal.rest.api.v1.auth.challenge;

import cn.com.xinli.portal.rest.api.v1.auth.HttpDigestCredentials;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * Challenge Authentication.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/10.
 */
public class ChallengeAuthentication extends AbstractAuthenticationToken {
    private final String principal;
    private HttpDigestCredentials credentials;

    public ChallengeAuthentication(String principal, HttpDigestCredentials credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
    }

    public ChallengeAuthentication(String httpDigestCredentials) {
        super(null);
        credentials = HttpDigestCredentials.of(httpDigestCredentials);
        principal = credentials.getParameter(HttpDigestCredentials.CLIENT_ID);
    }

    @Override
    public HttpDigestCredentials getCredentials() {
        return credentials;
    }

    @Override
    public String getPrincipal() {
        return principal;
    }
}
