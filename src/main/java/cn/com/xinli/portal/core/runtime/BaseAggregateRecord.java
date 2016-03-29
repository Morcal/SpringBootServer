package cn.com.xinli.portal.core.runtime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract base aggregate record.
 * @author zhoupeng, created on 2016/3/27.
 */
public abstract class BaseAggregateRecord<T extends Record> implements AggregateRecord<T> {
    @JsonIgnore
    private Date recordedAt;

    /** Aggregated value map. */
    private Map<String, Long> aggregated = new ConcurrentHashMap<>();

    /**
     * Get supported value types.
     * <p>Supported value types can not be empty or null.
     * @return supported value types.
     */
    protected abstract String[] supportedValueTypes();

    BaseAggregateRecord(Date recordedAt) {
        Objects.requireNonNull(recordedAt, "aggregate record date can not be null.");
        this.recordedAt = recordedAt;

        for (String name : supportedValueTypes()) {
            if (!StringUtils.isEmpty(name))
                aggregated.put(name, 0L);
        }
    }

    @Override
    public Date getRecordedDate() {
        return recordedAt;
    }

    @Override
    public long getValue(String name) {
        Long value = aggregated.get(name);
        if (value == null)
            throw new IllegalArgumentException(name + " not aggregated.");
        return value;
    }

    @Override
    public void setValue(String name, long value) {
        Long v = aggregated.get(name);
        if (v == null)
            throw new IllegalArgumentException(name + " not aggregated.");
        aggregated.put(name, value);
    }

    @Override
    public void increment(String name) {
        Long value = aggregated.get(name);
        if (value == null)
            throw new IllegalArgumentException(name + " not aggregated.");

        aggregated.put(name, value + 1);
    }

    @Override
    public void increment(String name, long value) {
        Long v = aggregated.get(name);
        if (v == null)
            throw new IllegalArgumentException(name + " not aggregated.");

        aggregated.put(name, v + value);
    }
}
