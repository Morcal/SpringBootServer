package cn.com.xinli.portal.support.repository;

import cn.com.xinli.portal.core.activity.Activity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Activity repository.
 *
  * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
@Repository
public interface ActivityRepository extends PagingAndSortingRepository<Activity, Long> {
    /**
     * Find activities by source.
     * @param source activity source.
     * @return activities.
     */
    @Query("select a from Activity a where a.remote = :source")
    List<Activity> findBySource(@Param("source") String source);

    /**
     * Find activities by source.
     * @param remote activity remote.
     * @return activities.
     */
    @Query("select a from Activity a where a.source = :remote")
    List<Activity> findByRemote(@Param("remote") String remote);

    /**
     * Filter activies by {@link Activity.Severity}
     * @param severity severity.
     * @return activities.
     */
    @Query("select a from Activity a where a.severity = :severity")
    List<Activity> filter(@Param("severity") Activity.Severity severity);

    /**
     * Filter activies by {@link Activity.Facility}
     * @param facility severity.
     * @return activities.
     */
    @Query("select a from Activity a where a.severity = :facility")
    List<Activity> filter(@Param("facility") Activity.Facility facility);

    /**
     * Delete activities older than given date.
     * @param oldest oldest.
     */
    @Modifying
    @Query("delete from Activity a where a.created < :oldest")
    void deleteOlderThan(@Param("oldest") Date oldest);
}
