package cn.com.xinli.portal.util;

import cn.com.xinli.portal.Credentials;

/**
 * Credentials encoder.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/30.
 */
public interface CredentialsEncoder {
    /**
     * Modify credentials and return a new one.
     * @param credentials original credentials.
     * @return modified new credentials.
     */
    Credentials encode(Credentials credentials);
}
