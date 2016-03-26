package cn.com.xinli.portal.core.certificate;

import cn.com.xinli.portal.core.DataStore;

import java.util.stream.Stream;

/**
 * Certificate store.
 *
 * <p>The reason this method's parameter is client id/app id rather than
 * underlying unique id is that in a distributed system (cluster), certificate
 * id(s) among all nodes with same client certificate may differ from each other.
 *
 * FIXME certificate versioning not supported yet.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
public interface CertificateStore extends DataStore<Certificate, Long> {
    @Override
    Certificate get(Long id) throws CertificateNotFoundException;

    @Override
    boolean delete(Long id) throws CertificateNotFoundException;

    Certificate find(String app) throws CertificateNotFoundException;

    Stream<Certificate> all();

    Stream<Certificate> search(String query);
}
