package cn.com.xinli.portal.core.runtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * System Load statistics.
 *
 * @author zhoupeng, created on 2016/3/27.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoadStatistics extends AbstractStatistics<LoadStatistics.LoadRecord> implements Reportable {
    /** Record every 1 minute. */
    private static final int RECORD_HISTORY_IN = 1;

    /** Record recent 5 minutes. */
    private static final int RECORD_HISTORY_LENGTH = 10;

    /** Total requests after server started. */
    private final AtomicLong totalRequests = new AtomicLong(0);

    @JsonProperty("total")
    public long getTotalRequests() {
        return totalRequests.get();
    }

    @Override
    protected String getHistoryDefaultValueName() {
        return "requests";
    }

    /**
     * Get total request count recorded in history.
     * @return recorded request count.
     */
    @JsonProperty("requests")
    public long getRequests() {
        return current("requests");
    }

    /**
     * Get total error count recorded in history.
     * @return recorded error count.
     */
    @JsonProperty("errors")
    public long getErrors() {
        return current("errors");
    }

    /**
     * Get average response time in milliseconds.
     * @return response time.
     */
    @JsonProperty("average_response_time")
    public long getAverageResponseTime() {
        long c = getRequests();
        long t = current("response time");
        if (c == 0L)
            return 0L;

        return t / c;
    }

    @JsonProperty("max_response_time")
    public long getMaxResponseTime() {
        return current("max response time");
    }

    @JsonProperty("report")
    @Override
    public Report report() {
        return generateReport(new String[] { "requests" });
    }

    @Override
    protected TimeUnit getHistoryRecordDurationTimeUnit() {
        return TimeUnit.MINUTES;
    }

    @Override
    protected int getHistoryRecordDuration() {
        return RECORD_HISTORY_IN;
    }

    @Override
    protected int getHistoryRecordingLength() {
        return RECORD_HISTORY_LENGTH;
    }

    @Override
    protected AggregateRecord<LoadRecord> createAggregateRecord(Date date) {
        return new AggregatedSessionRecord(date);
    }

    @Override
    public boolean supports(Class<?> cls) {
        return cls != null && BaseRecord.class.isAssignableFrom(cls);
    }

    /**
     * NAS record.
     */
    public static class LoadRecord extends BaseRecord {
        /** Operation response time. */
        private long responseTime;

        public LoadRecord(boolean error) {
            super(error);
        }

        public long getResponseTime() {
            return responseTime;
        }

        public void setResponseTime(long responseTime) {
            this.responseTime = responseTime;
        }

        @Override
        public String toString() {
            return super.toString() + " LoadRecord{" +
                    "responseTime=" + responseTime +
                    '}';
        }
    }

    /**
     * Aggregated session record.
     */
    public class AggregatedSessionRecord extends BaseAggregateRecord<LoadRecord> {
        /** constructor. */
        AggregatedSessionRecord(Date recordedAt) {
            super(recordedAt);
            setValue("max response time", 0L);
        }

        @Override
        public void addRecord(LoadRecord record) {
            totalRequests.incrementAndGet();
            increment("requests");
            increment("response time", record.responseTime);

            if (record.responseTime > getValue("max response time"))
                setValue("max response time", record.responseTime);

            if (record.isError())
                increment("errors");
        }

        @Override
        protected String[] supportedValueTypes() {
            return new String[] { "requests", "errors", "response time", "max response time" };
        }
    }
}
