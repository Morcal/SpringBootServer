package cn.com.xinli.portal.support.repository;

import java.util.stream.Stream;

/**
 * Searchable.
 * @author zhoupeng, created on 2016/3/26.
 */
public interface Searchable<T> {
    /**
     * Search repository by JPQL query string.
     * @param query JPQL query string.
     * @return stream of resulting repository objects.
     */
    Stream<T> search(String query);

    /**
     * Count repository object count by JPQL query.
     * @param query JPQL query string.
     * @return count of resulting objects.
     */
    long count(String query);
}
