package cn.com.xinli.portal.support;

/**
 * Portal request credentials.
 *
 * <p>Portal service protected resources accessing requires clients/applications to provide
 * credentials issued by server to identify clients.
 *
 * <p>Credentials may contains one of three type of information inside itself.
 * It may be a <code>challenge response</code>, a <code>access token</code> or
 * a <code>session token</code>.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/3/6.
 */
public interface RestCreadentials {
    /**
     * Get credential parameter.
     * @param name parameter name.
     * @return parameter value.
     */
    String getAttribute(String name);

    /**
     * Remove parameter by name.
     * @param name parameter name.
     * @return removed parameter value.
     */
    String removeAttribute(String name);

    /**
     * Check if credentials is empty.
     * @return true if credentials is empty.
     */
    boolean isEmpty();
    /**
     * Check if credentials contains challenge.
     * @return true if credentials contains challenge.
     */
    boolean containsChallengeResponse();

    /**
     * Check if credentials contains client token.
     * @return true if credentials contains a client token.
     */
    boolean containsAccessToken();

    /**
     * Check if credentials contains session token.
     * @return true if credentials contains a session token.
     */
    boolean containsSessionToken();

    /**
     * Check if credentials contains admin token.
     * @return true if credentials contains an admin token.
     */
    boolean containsAdminToken();

    /**
     * Check if credentials contains nonce.
     * @return true if credentials contains nonce.
     */
    boolean containsNonce();

    /**
     * Set credentials attribute.
     * @param name attribute name.
     * @param value attribute value.
     * @throws IllegalArgumentException if name or value is null
     * or name is not allowed.
     */
    void setAttribute(String name, String value);
}
