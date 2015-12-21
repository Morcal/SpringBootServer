package cn.com.xinli.portal.auth;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
public interface CertificateManager {
    Certificate create(String appId, String vendor, String os, String version);
    void disableCertificate(long id);
    void delete(Certificate certificate);
}
