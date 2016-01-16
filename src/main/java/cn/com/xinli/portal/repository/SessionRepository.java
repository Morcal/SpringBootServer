package cn.com.xinli.portal.repository;

import cn.com.xinli.portal.core.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring data session repository.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
public interface SessionRepository extends CrudRepository<SessionEntity, Long> {
    /**
     * Find session by user name.
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
    List<Session> find(@Param("ip") String ip);

    /**
     * Find one session by ip and mac.
     * @param ip ip address.
     * @return session.
     */
    @Query("select s from SessionEntity s where s.ip = :ip and s.mac = :mac")
    List<Session> find(@Param("ip") String ip, @Param("mac") String mac);

}
