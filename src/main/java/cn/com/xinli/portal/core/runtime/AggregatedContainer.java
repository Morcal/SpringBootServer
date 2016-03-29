package cn.com.xinli.portal.core.runtime;

import java.util.List;

/**
 * Aggregated record container.
 * @author zhoupeng, created on 2016/3/27.
 */
public interface AggregatedContainer<T extends Record> {
    List<AggregateRecord<T>> getAggregated();

    void addRecord(T record);

    boolean supports(Class<?> cls);
}
