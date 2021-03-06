package cn.com.xinli.portal.core.runtime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Total session statistics.
 * @author zhoupeng, created on 2016/3/28.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TotalSessionStatistics extends AbstractStatistics<SessionStatistics.SessionRecord>
        implements Reportable {
    /** Record every 1 hour. */
    private static final int RECORD_HISTORY_IN = 1;

    /** Record recent 24 hours. */
    private static final int RECORD_HISTORY_LENGTH = 24;

    /** Current session count. */
    @JsonIgnore
    private final AtomicLong totalSessions = new AtomicLong(0);

    /**
     * Get current session count.
     * @return current count.
     */
    @JsonProperty("current")
    public long getTotalSessions() {
        return totalSessions.get();
    }

    @JsonProperty("report")
    @Override
    public Report report() {
        return generateReport(new String[] { "total" });
    }

    @Override
    protected String getHistoryDefaultValueName() {
        return "total";
    }

    @Override
    protected TimeUnit getHistoryRecordDurationTimeUnit() {
        return TimeUnit.HOURS;
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
    protected AggregateRecord<SessionStatistics.SessionRecord> createAggregateRecord(Date date) {
        return new AggregatedTotalRecord(date);
    }

    @Override
    public boolean supports(Class<?> cls) {
        return cls != null && SessionStatistics.SessionRecord.class.isAssignableFrom(cls);
    }

    /**
     * Aggregated total session record.
     */
    class AggregatedTotalRecord extends BaseAggregateRecord<SessionStatistics.SessionRecord> {
        AggregatedTotalRecord(Date recordedAt) {
            super(recordedAt);
        }

        @Override
        protected String[] supportedValueTypes() {
            return new String[] { "total" };
        }

        @Override
        public void addRecord(SessionStatistics.SessionRecord record) {
            if (!record.isError()) {
                final long total;
                switch (record.getAction()) {
                    case USER_CREATE_SESSION:
                        total = totalSessions.incrementAndGet();
                        break;

                    case NAS_NTF_LOGOUT:
                    case USER_DELETE_SESSION:
                        total = totalSessions.updateAndGet(operand -> operand == 0 ? 0 : operand - 1);
                        break;

                    default:
                        total = 0L;
                        break;
                }
                setValue("total", total);
            }
        }
    }
}
