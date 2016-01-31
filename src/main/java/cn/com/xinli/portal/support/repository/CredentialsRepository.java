package cn.com.xinli.portal.support.repository;

import cn.com.xinli.portal.core.credentials.Credentials;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
@Repository
public interface CredentialsRepository extends CrudRepository<Credentials, Long> {
}
