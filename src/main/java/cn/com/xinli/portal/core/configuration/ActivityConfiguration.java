package cn.com.xinli.portal.core.configuration;

import cn.com.xinli.portal.core.activity.Activity;

/**
 * Activity Configuration.
 *
 *<p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
public class ActivityConfiguration {
    /** Most recent days server should keep auditing logs. */
    private int mostRecent;

    /** Minimum severity to save auditing logs. */
    private Activity.Severity severity;

    public int getMostRecent() {
        return mostRecent;
    }

    public void setMostRecent(int mostRecent) {
        this.mostRecent = mostRecent;
    }

    public Activity.Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Activity.Severity severity) {
        this.severity = severity;
    }

    @Override
    public String toString() {
        return "ActivityConfiguration{" +
                "mostRecent=" + mostRecent +
                ", severity=" + severity +
                '}';
    }
}
