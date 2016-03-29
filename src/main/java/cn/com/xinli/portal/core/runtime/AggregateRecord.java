package cn.com.xinli.portal.core.runtime;

import java.util.Date;

/**
 * @author zhoupeng, created on 2016/3/27.
 */
public interface AggregateRecord<T extends Record> {
    /**
     * Get record date.
     * @return recorded date.
     */
    Date getRecordedDate();

    void addRecord(T record);

    long getValue(String name);

    void setValue(String name, long value);

    void increment(String name);

    void increment(String name, long value);
}
