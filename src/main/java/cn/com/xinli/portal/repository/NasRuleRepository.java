package cn.com.xinli.portal.repository;

import cn.com.xinli.portal.core.NasRule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring data NAS rule repository.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/7.
 */
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
