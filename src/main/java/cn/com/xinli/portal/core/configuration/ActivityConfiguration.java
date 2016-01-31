package cn.com.xinli.portal.core.configuration;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
public class ActivityConfiguration {

    /** Severity. */
    public enum Severity {
        FATAL,
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE
    }

    private int mostRecent;
    private Severity severity;

    public int getMostRecent() {
        return mostRecent;
    }

    public void setMostRecent(int mostRecent) {
        this.mostRecent = mostRecent;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
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
