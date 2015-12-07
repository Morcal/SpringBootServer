package cn.com.xinli.portal.util;

/**
 * Token.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
public interface Token {
    /**
     * Validate token.
     * @return true if token is valid.
     */
    boolean validate();

    /**
     * Return token in text form.
     * @return token text.
     */
    String text();
}
