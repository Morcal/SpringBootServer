package cn.com.xinli.portal.rest.api.v1.auth.challenge;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
public class ChallengeImpl implements Challenge {
    private final String nonce;
    private final String clientId;
    private final String challenge;
    private final String response;

    public ChallengeImpl(String nonce,
                         String clientId,
                         String challenge,
                         String response) {
        this.nonce = nonce;
        this.clientId = clientId;
        this.challenge = challenge;
        this.response = response;
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public String getChallenge() {
        return challenge;
    }

    @Override
    public String getResponse() {
        return response;
    }

    @Override
    public String getNonce() {
        return nonce;
    }
}