package cn.com.xinli.portal.support.repository;

import cn.com.xinli.portal.core.credentials.CredentialsTranslation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Credentials Translation Repository.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/24.
 */
@Repository
public interface CredentialsTranslationRepository extends CrudRepository<CredentialsTranslation, Long> {
}
