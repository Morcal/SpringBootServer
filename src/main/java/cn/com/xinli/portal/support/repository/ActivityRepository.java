package cn.com.xinli.portal.support.repository;

import cn.com.xinli.portal.core.activity.Activity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * Activity repository.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
@Repository
public interface ActivityRepository extends PagingAndSortingRepository<Activity, Long>,
        Searchable<Activity> {
    /**
     * Find activities by source.
     *
     * @param source activity source.
     * @return activities.
     */
    List<Activity> findBySource(String source);

    /**
     * Find activities by source.
     *
     * @param remote activity remote.
     * @return activities.
     */
    List<Activity> findByRemote(String remote);

    /**
     * Filter activities by {@link Activity.Severity}
     *
     * @param severity severity.
     * @return activities.
     */
    List<Activity> findBySeverity(Activity.Severity severity);

    /**
     * Filter activies by {@link Activity.Facility}
     *
     * @param facility severity.
     * @return activities.
     */
    List<Activity> findByFacility(Activity.Facility facility);

    /**
     * Delete activities older than given date.
     *
     * @param oldest oldest.
     */
    @Modifying
    @Query("delete from Activity a where a.created < :oldest")
    void deleteOlderThan(@Param("oldest") Date oldest);

    @Query("select count(a) from Activity a where a.source like :v")
    long count(@Param("v") String query);

    @Query("select a from Activity a where a.source like :v")
    Stream<Activity> search(@Param("v") String query);

    Stream<Activity> findTop25ByOrderByCreatedDesc();
}
