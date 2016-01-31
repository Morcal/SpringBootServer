package cn.com.xinli.portal.support.repository;

import cn.com.xinli.portal.core.credentials.CredentialsEncoder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Credentials Encoder Repository.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/25.
 */
@Repository
public interface CredentialsEncoderRepository extends CrudRepository<CredentialsEncoder, Long> {
}
