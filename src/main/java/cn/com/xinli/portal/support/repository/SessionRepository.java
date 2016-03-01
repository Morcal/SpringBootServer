package cn.com.xinli.portal.support.repository;

import cn.com.xinli.portal.core.session.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

/**
 * Spring data session repository.
 *
  * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/7.
 */
@Repository
public interface SessionRepository extends CrudRepository<Session, Long> {
    /**
     * Find session by user name.
     *
     * @param username user name.
     * @return session list.
     */
    @Query("select s from Session s where s.credentials.username = :username")
    Stream<Session> findByUsername(@Param("username") String username);

    /**
     * Find one session by ip.
     * @param ip ip address.
     * @return session.
     */
    @Query("select s from Session s where s.credentials.ip = :ip")
    Stream<Session> find(@Param("ip") String ip);

    /**
     * Find one session by ip and mac.
     * @param ip ip address.
     * @return session.
     */
    @Query("select s from Session s where s.credentials.ip = :ip and s.credentials.mac = :mac")
    Stream<Session> find(@Param("ip") String ip, @Param("mac") String mac);

}
