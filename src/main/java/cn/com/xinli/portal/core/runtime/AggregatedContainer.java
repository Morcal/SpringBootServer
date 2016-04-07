package cn.com.xinli.portal.core.runtime;

import java.util.List;

/**
 * Aggregated record container.
 *
 * <p>Classes implements this interface to maintain a set of aggregated records.
 * @author zhoupeng, created on 2016/3/27.
 */
public interface AggregatedContainer<T extends Record> {
    /**
     * Get aggregated records.
     * @return aggregated records.
     */
    List<AggregateRecord<T>> getAggregated();

    /**
     * Add a new record.
     * @param record record to add.
     */
    void addRecord(T record);

    /**
     * Check if this container supports given class.
     * @param cls class to check.
     * @return true if this container supports given class.
     */
    boolean supports(Class<?> cls);
}
