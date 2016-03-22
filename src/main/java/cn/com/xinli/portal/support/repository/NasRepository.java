package cn.com.xinli.portal.support.repository;

import cn.com.xinli.portal.core.nas.Nas;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

/**
 * Nas/bras device repository.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
@Repository
public interface NasRepository extends CrudRepository<Nas, Long> {
    /**
     * Delete nas.
     * @param name nas name.
     */
    @Modifying
    @Query("delete from Nas n where n.name = :name")
    void delete(@Param("name") String name);

    /**
     * Query nas device, matching ipv4, ipv6 address and name.
     * @param query query value.
     * @return stream of nas devices.
     */
    @Query("select distinct n from Nas n where n.ipv4Address like :query or" +
            " n.ipv6Address like :query or n.name like :query")
    Stream<Nas> search(@Param("query") String query);
}
