package cn.com.xinli.portal.web.auth.token;

/**
 * Token container.
 *
 * <p>Classes implement this interface must contains a token key.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 */
public interface TokenContainer {
    /**
     * Get token key.
     * @return token key.
     */
    String getToken();
}
