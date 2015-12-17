package cn.com.xinli.portal.persist;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
@Repository
public interface SessionRepository extends CrudRepository<SessionEntity, Long> {
    /**
     * Find session by ip and mac.
     * @param device ip, mac device.
     * @return session list if found, or null.
     */
    List<SessionEntity> find(String device);

    /**
     * Find session by usr.
     *
     * @param username user name.
     * @return session list.
     */
    List<SessionEntity> findByUsername(String username);
}
