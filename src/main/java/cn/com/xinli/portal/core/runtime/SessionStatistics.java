package cn.com.xinli.portal.core.runtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * System session statistics.
 * @author zhoupeng, created on 2016/3/28.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionStatistics extends AbstractStatistics<SessionStatistics.SessionRecord> {
    /** Record every 1 minute. */
    private static final int RECORD_HISTORY_IN = 1;

    /** Record recent 5 minutes. */
    private static final int RECORD_HISTORY_LENGTH = 5;

    @JsonProperty("created")
    public long getCreated() {
        return sum("created");
    }

    @JsonProperty("removed")
    public long getRemoved() {
        return sum("removed");
    }

    @JsonProperty("ntf_logout")
    public long getNtfLogout() {
        return sum("ntf logout");
    }

    @JsonProperty("errors")
    public long getErrors() {
        return sum("errors");
    }

    @Override
    protected String getHistoryDefaultValueName() {
        return "created";
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
    protected AggregateRecord<SessionRecord> createAggregateRecord(Date date) {
        return new AggregatedSessionRecord(date);
    }

    @Override
    public boolean supports(Class<?> cls) {
        return cls != null && SessionRecord.class.isAssignableFrom(cls);
    }

    /**
     * Session record.
     */
    public static class SessionRecord extends BaseRecord {
        public enum Action {
            USER_CREATE_SESSION,
            USER_DELETE_SESSION,
            SYSTEM_DELETE_SESSION,
            NAS_NTF_LOGOUT
        }

        private Action action;

        public SessionRecord(boolean error) {
            super(error);
        }

        public Action getAction() {
            return action;
        }

        public void setAction(Action action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return super.toString() + " SessionRecord{" +
                    "action=" + action +
                    '}';
        }
    }

    /**
     * Aggregated session record.
     */
    class AggregatedSessionRecord extends BaseAggregateRecord<SessionRecord> {
        AggregatedSessionRecord(Date recordedAt) {
            super(recordedAt);
        }

        @Override
        protected String[] supportedValueTypes() {
            return new String[]{"created", "removed", "ntf logout", "errors" };
        }

        @Override
        public void addRecord(SessionRecord record) {
            if (!record.isError()) {
                switch (record.action) {
                    case USER_CREATE_SESSION:
                        increment("created");
                        break;

                    case NAS_NTF_LOGOUT:
                        increment("ntf logout");
                        break;

                    case USER_DELETE_SESSION:
                        increment("removed");
                        break;

                    default:
                        break;
                }
            } else {
                increment("errors");
            }
        }
    }
}
