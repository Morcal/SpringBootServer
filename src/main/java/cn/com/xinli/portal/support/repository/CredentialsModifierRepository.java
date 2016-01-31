package cn.com.xinli.portal.support.repository;

import cn.com.xinli.portal.core.credentials.CredentialsModifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Credentials modifier Repository.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/25.
 */
@Repository
public interface CredentialsModifierRepository extends CrudRepository<CredentialsModifier, Long> {
}
