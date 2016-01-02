package cn.com.xinli.portal.util;

import cn.com.xinli.portal.Credentials;

/**
 * Credentials modifier.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/30.
 */
public interface CredentialsModifier {
    /** Credentials modify target. */
    enum Target {
        /** Modify username in credentials. */
        USERNAME,
        /** Modify password in credentials. */
        PASSWORD
    }

    /** Modify target position. */
    enum Position {
        /** Modify target's head. */
        HEAD,
        /** Modify target's tail. */
        TAIL
    }

    /**
     * Modify credentials and return a new one.
     * @param credentials original credentials.
     * @return modified new credentials.
     */
    Credentials modify(Credentials credentials);
}
