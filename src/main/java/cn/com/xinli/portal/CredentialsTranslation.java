package cn.com.xinli.portal;

/**
 * Credentials Translation.
 *
 * Translate credentials when necessary.
 * Project: xpws
 *
 * @author zhoupeng 2016/1/1.
 */
public interface CredentialsTranslation {
    /**
     * Translate credentials.
     * @param credentials credentials.
     * @return translated credentials.
     */
    Credentials translate(Credentials credentials);
}
