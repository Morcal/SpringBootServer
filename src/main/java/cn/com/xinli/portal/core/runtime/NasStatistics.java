package cn.com.xinli.portal.core.runtime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * NAS device statistics.
 * @author zhoupeng, created on 2016/3/27.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NasStatistics extends AbstractStatistics<NasStatistics.NasRecord> {
    /** Record every 1 minute. */
    private static final int RECORD_HISTORY_IN = 1;

    /** Record recent 5 minutes. */
    private static final int RECORD_HISTORY_LENGTH = 5;

    @JsonIgnore
    private final long nasId;

    @JsonProperty
    private final String name;

    @JsonIgnore
    private final AtomicLong totalRequests = new AtomicLong(0);

    public NasStatistics(long nasId, String name) {
        this.nasId = nasId;
        this.name = name;
    }

    /**
     * Get request count total of this NAS devices after serve started.
     * @return request count.
     */
    @JsonProperty("total")
    public long getTotalRequests() {
        return totalRequests.get();
    }

    @JsonProperty("requests")
    public long getRequests() {
        return getValue("requests");
    }

    @JsonProperty("errors")
    public long getErrors() {
        return getValue("errors");
    }

    @JsonProperty("timeouts")
    public long getTimeouts() {
        return getValue("timeout");
    }

    @JsonProperty("average_response_time")
    public long getAverageResponseTime() {
        long c = getRequests();
        long t = getValue("response time");
        if (c == 0L)
            return 0L;

        return t / c;
    }

    @Override
    protected String getHistoryDefaultValueName() {
        return "requests";
    }

    @Override
    protected TimeUnit getHistoryRecordDurationTimeUnit() {
        return TimeUnit.MINUTES;
    }

    @Override
    @JsonIgnore
    protected int getHistoryRecordDuration() {
        return RECORD_HISTORY_IN;
    }

    @Override
    @JsonIgnore
    protected int getHistoryRecordingLength() {
        return RECORD_HISTORY_LENGTH;
    }

    @Override
    protected AggregateRecord<NasRecord> createAggregateRecord(Date date) {
        return new AggregatedNasRecord(date);
    }

    public long getNasId() {
        return nasId;
    }

    public String getName() {
        return name;
    }

    @Override
    @JsonIgnore
    public boolean supports(Class<?> cls) {
        return cls != null && NasRecord.class.isAssignableFrom(cls);
    }

    /**
     * NAS record.
     */
    public static class NasRecord extends BaseRecord {
        /** nas id. */
        private Long nasId;

        /** Operation response time. */
        private long responseTime;

        /** If operation results timeout. */
        private boolean timeout;

        protected NasRecord(boolean error) {
            super(error);
        }

        public Long getNasId() {
            return nasId;
        }

        public void setNasId(Long nasId) {
            this.nasId = nasId;
        }

        public long getResponseTime() {
            return responseTime;
        }

        public void setResponseTime(long responseTime) {
            this.responseTime = responseTime;
        }

        public boolean isTimeout() {
            return timeout;
        }

        public void setTimeout(boolean timeout) {
            this.timeout = timeout;
        }
    }

    /**
     * NAS request history record.
     *
     * <p>This class records request count and error count in a segment
     * of recording history.
     */
    class AggregatedNasRecord extends BaseAggregateRecord<NasRecord> {
        AggregatedNasRecord(Date recordedAt) {
            super(recordedAt);
        }

        @Override
        public void addRecord(NasRecord record) {
            totalRequests.incrementAndGet();
            increment("requests");
            if (record.isError())
                increment("errors");

            if (record.timeout)
                increment("timeout");

            increment("response time", record.responseTime);
        }

        @Override
        protected String[] supportedValueTypes() {
            return new String[] { "requests", "errors", "timeout", "response time" };
        }
    }
}
