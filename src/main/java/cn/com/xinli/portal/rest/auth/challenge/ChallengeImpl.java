package cn.com.xinli.portal.rest.auth.challenge;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
public class ChallengeImpl implements Challenge {
    private final String nonce;
    private final String clientId;
    private final String scope;
    private final boolean requireToken;
    private final boolean needRefreshToken;
    private final String challenge;

    public ChallengeImpl(String nonce,
                         String clientId,
                         String challenge,
                         String scope,
                         boolean requireToken,
                         boolean needRefreshToken) {
        this.nonce = nonce;
        this.clientId = clientId;
        this.challenge = challenge;
        this.scope = scope;
        this.requireToken = requireToken;
        this.needRefreshToken = needRefreshToken;
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public String getChallenge() {
        return challenge;
    }

    @Override
    public String getNonce() {
        return nonce;
    }

    @Override
    public boolean needRefreshToken() {
        return needRefreshToken;
    }

    @Override
    public boolean requiresToken() {
        return requireToken;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return "ChallengeImpl{" +
                "challenge='" + challenge + '\'' +
                ", nonce='" + nonce + '\'' +
                ", clientId='" + clientId + '\'' +
                ", scope='" + scope + '\'' +
                ", requireToken=" + requireToken +
                ", needRefreshToken=" + needRefreshToken +
                '}';
    }
}
