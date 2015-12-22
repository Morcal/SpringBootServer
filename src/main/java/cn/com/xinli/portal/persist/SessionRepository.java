package cn.com.xinli.portal.persist;

import cn.com.xinli.portal.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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
public interface SessionRepository extends CrudRepository<SessionEntity, Long> {
    /**
     * Find session by ip and mac.
     * @param device ip, mac device.
     * @return session list if found, or null.
     */
    @Query("select s from SessionEntity s where s.device = :device")
    List<Session> find(@Param("device") String device);

    /**
     * Find session by usr.
     *
     * @param username user name.
     * @return session list.
     */
    @Query("select s from SessionEntity s where s.username = :username")
    List<Session> findByUsername(@Param("username") String username);

    /**
     * Find one session by ip.
     * @param ip ip address.
     * @return session.
     */
    @Query("select s from SessionEntity s where s.ip = :ip")
    Session find1(@Param("ip") String ip);
}
