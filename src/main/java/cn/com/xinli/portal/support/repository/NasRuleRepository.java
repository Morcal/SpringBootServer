package cn.com.xinli.portal.support.repository;

import cn.com.xinli.portal.core.nas.NasRule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring data NAS rule repository.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/7.
 */
@Repository
public interface NasRuleRepository extends CrudRepository<NasRule, Long> {
    /**
     * Find session by user name.
     *
     * @param name NAS name.
     * @return NAS rule list.
     */
    @Query("select r from NasRule r where r.nas.name = :name")
    List<NasRule> findByName(@Param("name") String name);
}
