package cn.com.xinli.portal.core.runtime;

import java.util.Calendar;
import java.util.Date;

/**
 * System load record.
 *
 * <p>System load records are generated via activity auditing logging.
 * Each logging entry presents an activity, associated request and
 * error result if presents.
 *
 * @author zhoupeng, created on 2016/3/27.
 */
public class BaseRecord implements Record {
    private Date createdAt;

    private boolean error;

    protected BaseRecord(boolean error) {
        this.error = error;
        this.createdAt = Calendar.getInstance().getTime();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public static BaseRecord record() {
        return new BaseRecord(false);
    }

    public static BaseRecord error() {
        return new BaseRecord(true);
    }

    @Override
    public Date getRecordedDate() {
        return createdAt;
    }
}
