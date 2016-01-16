package cn.com.xinli.portal.core;

/**
 * Authorized client application certificate.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
public interface Certificate {
    /**
     * Get certificate id.
     * @return certificate id.
     */
    long getId();

    /**
     * Get authorized client/app id.
     * @return client/app id.
     */
    String getAppId();

    /**
     * Get shared secret.
     * @return shared secret.
     */
    String getSharedSecret();

    /**
     * Get client/app os.
     * @return client/app os.
     */
    String getOs();

    /**
     * Get authorized vendor name.
     * @return authorized vendor name.
     */
    String getVendor();

    /**
     * Get authorized client/app version.
     * @return authorized client/app version.
     */
    String getVersion();

    /**
     * Check if authorization disabled.
     * @return true if disabled.
     */
    boolean isDisabled();
}
