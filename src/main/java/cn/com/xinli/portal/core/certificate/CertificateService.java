package cn.com.xinli.portal.core.certificate;

/**
 * Certificate service.
 *
 * <p><em>Known issues</em>:
 * Current version's REST APIs only defines a client_id must be included in
 * all requests when authentication/authorization needed. So when client's
 * certificate evolved, i.e. upgraded to a newer version, there will be
 * more than one entries for a single client application with different
 * versions. Under that circumstances, {@link #loadCertificate(String)}
 * will be insufficient to load the correct version of certificates.
 * FIXME {@link #loadCertificate(String)} will be insufficient when certificate evolved.
 * FIXME {@link #isCertified(String)} will be insufficient when certificate evolved.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
public interface CertificateService {
    /**
     * Check if given app id already certified.
     * @return true if app id already certified.
     * @param clientId client id.
     */
    boolean isCertified(String clientId);

    /**
     * Disable an existed certificate.
     * @param clientId certificate client/app id.
     */
    void disableCertificate(String clientId) throws CertificateNotFoundException;

    /**
     * Load certificate by client id, aka app id.
     *
     * <p>The reason this method's parameter is client id/app id rather than
     * underlying unique id is that in a distributed system (cluster), certificate
     * id(s) among all nodes with same client certificate may differ from each other.
     *
     * @param clientId client id.
     * @return certificated.
     */
    Certificate loadCertificate(String clientId) throws CertificateNotFoundException;
}
