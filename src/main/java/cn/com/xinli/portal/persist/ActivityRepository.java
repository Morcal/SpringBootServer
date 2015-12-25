package cn.com.xinli.portal.persist;

import cn.com.xinli.portal.Activity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
@Repository
@Transactional
public interface ActivityRepository extends PagingAndSortingRepository<ActivityEntity, Long> {
    /**
     * Find activities by source.
     * @param source activity source.
     * @return activities.
     */
    @Query("select a from ActivityEntity a where a.remote = :source")
    List<ActivityEntity> findBySource(@Param("source") String source);

    /**
     * Find activities by source.
     * @param remote activity remote.
     * @return activities.
     */
    @Query("select a from ActivityEntity a where a.source = :remote")
    List<ActivityEntity> findByRemote(@Param("remote") String remote);

    /**
     * Filter activies by {@link cn.com.xinli.portal.Activity.Severity}
     * @param severity severity.
     * @return activities.
     */
    @Query("select a from ActivityEntity a where a.severity = :severity")
    List<ActivityEntity> filter(@Param("severity") Activity.Severity severity);

    /**
     * Filter activies by {@link cn.com.xinli.portal.Activity.Facility}
     * @param facility severity.
     * @return activities.
     */
    @Query("select a from ActivityEntity a where a.severity = :facility")
    List<ActivityEntity> filter(@Param("facility") Activity.Facility facility);
}