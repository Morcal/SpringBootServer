package cn.com.xinli.portal.persist;

import cn.com.xinli.portal.Session;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
@Repository
@Transactional
public interface SessionRepository extends CrudRepository<Session, String> {
    /**
     * Find session by ip and mac.
     * @param ip ip address.
     * @param mac mac address.
     * @return session if found, or null.
     */
    Session findByIpAndMac(String ip, String mac);

    /**
     * Find session by usr.
     *
     * @param username user name.
     * @return session list.
     */
    List<Session> findByUsername(String username);
}
