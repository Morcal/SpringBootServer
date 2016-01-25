package cn.com.xinli.portal.core;

/**
 * Certificate manager.
 *
 * <p>Classes implements this interface to provide certificate management.
 *
 * <p>Project: xpws
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
     * @param sharedSecret secret key shared by PWS and client applications.
     * @return new certificate.
     */
    Certificate create(String appId, String vendor, String os, String version, String sharedSecret);

    /**
     * Delete a certificate.
     * @param certificate certificate.
     */
    void delete(Certificate certificate);
}
