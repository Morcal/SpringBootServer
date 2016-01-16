package cn.com.xinli.portal.core;

/**
 * Certificate manager.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
public interface CertificateManager {
    /**
     * Create a new certificate.
     * @param appId application id/ client id.
     * @param vendor vendor name.
     * @param os operating system name.
     * @param version application/client version.
     * @return new certificate.
     */
    Certificate create(String appId, String vendor, String os, String version);

    /**
     * Disable an existed certificate.
     * @param id certificate id.
     */
    void disableCertificate(long id);

    /**
     * Delete a certificate.
     * @param certificate certificate.
     */
    void delete(Certificate certificate);
}
