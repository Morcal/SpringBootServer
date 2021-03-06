package cn.com.xinli.portal.core.runtime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Abstract Statistics.
 *
 * @author zhoupeng, created on 2016/3/27.
 */
public abstract class AbstractStatistics<T extends Record> implements AggregatedContainer<T> {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(AbstractStatistics.class);

    /** Recorded aggregated. */
    private final List<AggregateRecord<T>> aggregated = Collections.synchronizedList(new ArrayList<>());

    /**
     * Get recording rate.
     *
     * @return recording rate.
     */
    protected abstract int getHistoryRecordDuration();

    /**
     * Get recording rate time unit.
     *
     * @return time unit.
     */
    protected abstract TimeUnit getHistoryRecordDurationTimeUnit();

    /**
     * Get recording length.
     *
     * @return recording length.
     */
    protected abstract int getHistoryRecordingLength();

    /**
     * Create an aggregated record.
     *
     * @param date aggregated record date.
     * @return new aggregated record.
     */
    protected abstract AggregateRecord<T> createAggregateRecord(Date date);

    /**
     * Get history default value name.
     *
     * @return default value name.
     */
    protected abstract String getHistoryDefaultValueName();

    @JsonIgnore
    public List<AggregateRecord<T>> getAggregated() {
        return aggregated;
    }

    /**
     * Get recording duration in milliseconds.
     *
     * @return milliseconds.
     */
    private long getDurationInMilliseconds() {
        return TimeUnit.MILLISECONDS.convert(getHistoryRecordDuration(), getHistoryRecordDurationTimeUnit());
    }

    /**
     * Get aggregated sum value in category.
     *
     * @param name category name.
     * @return aggregated value.
     */
    public long sum(String name) {
        long value = 0;

        for (AggregateRecord<T> r : getAggregated()) {
            value += r.getValue(name);
        }

        return value;
    }

    /**
     * Get current aggregated record value.
     * @param name value name.
     * @return value.
     */
    public long current(String name) {
        if (aggregated.isEmpty())
            return 0L;

        return aggregated.get(aggregated.size() - 1).getValue(name);
    }

    /**
     * Generate report.
     * @return report.
     */
    protected Report generateReport(String[] names) {
        createHistory();
        return Reporter.report(names, getAggregated());
    }

    /**
     * Create a new aggregated
     *
     * @param record record.
     */
    private void createNewAggregateRecord(T record) {
        if (aggregated.size() > getHistoryRecordingLength()) {
            aggregated.remove(0);
        }

        final Calendar calendar = Calendar.getInstance();

        switch (getHistoryRecordDurationTimeUnit()) {
            case DAYS:
                calendar.set(Calendar.HOUR, 0);
            case HOURS:
                calendar.set(Calendar.MINUTE, 0);
            case MINUTES:
                calendar.set(Calendar.SECOND, 0);
                break;
        }

        AggregateRecord<T> r = createAggregateRecord(calendar.getTime());
        r.addRecord(record);
        aggregated.add(r);
    }

    /**
     * Update current aggregate record.
     *
     * @param record record.
     */
    private void updateCurrentAggregateRecord(T record) {
        AggregateRecord<T> tail = aggregated.get(aggregated.size() - 1);
        tail.addRecord(record);
    }

    /**
     * Create statistics history.
     */
    private synchronized void createHistory() {
        long now = System.currentTimeMillis();
        long last = 0L;
        final int recordLength = getHistoryRecordingLength();

        if (!aggregated.isEmpty()) {
            AggregateRecord<T> tail = aggregated.get(aggregated.size() - 1);
            last = tail.getRecordedDate().getTime();
        }

        long diff = now - last;

        int missing = last == 0 ? recordLength : (int) (diff / getDurationInMilliseconds());
        missing = missing > recordLength ? recordLength : missing;

        int temp = missing;
        if (temp > 0) {
            while (temp-- > 0 && !aggregated.isEmpty()) {
                aggregated.remove(0);
            }
        } else
            return;

        while (missing-- > 0) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(now);
            int delta = -1 * getHistoryRecordDuration() * missing;

            switch (getHistoryRecordDurationTimeUnit()) {
                case MINUTES:
                    c.add(Calendar.MINUTE, delta);
                    c.set(Calendar.SECOND, 0);
                    break;

                case DAYS:
                    c.add(Calendar.DATE, delta);
                    c.set(Calendar.HOUR, 0);
                    c.set(Calendar.MINUTE, 0);
                    break;

                case HOURS:
                    c.add(Calendar.HOUR, delta);
                    c.set(Calendar.MINUTE, 0);

                case SECONDS:
                    c.add(Calendar.SECOND, delta);
                    break;
            }

            AggregateRecord<T> r = createAggregateRecord(c.getTime());
            r.setValue(getHistoryDefaultValueName(), 0);
            aggregated.add(r);
        }
    }

    @Override
    public void addRecord(T record) {
        Objects.requireNonNull(record, "record can not be null.");

        if (!supports(record.getClass())) {
            logger.error("record not supported, record type: {}", record.getClass().getName());
            return;
        }

        createHistory();

        final AggregateRecord<T> head = aggregated.get(0),
                tail = aggregated.get(aggregated.size() - 1);

        final long duration = getDurationInMilliseconds();
        long diff = record.getRecordedDate().getTime() - tail.getRecordedDate().getTime();

        if (diff < duration) {
            /* update last (current) aggregated record. */
            updateCurrentAggregateRecord(record);
        } else {
            diff = record.getRecordedDate().getTime() - head.getRecordedDate().getTime();
            if (diff > getHistoryRecordDuration() * duration) {
                createNewAggregateRecord(record);
            }
        }
    }

}
