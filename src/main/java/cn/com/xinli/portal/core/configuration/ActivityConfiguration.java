package cn.com.xinli.portal.core.configuration;

import cn.com.xinli.portal.core.activity.Activity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Activity Configuration.
 *
 *<p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityConfiguration {
    /** Most recent days server should keep auditing logs. */
    @JsonProperty("most_recent")
    private int mostRecent;

    /** Minimum severity to save auditing logs. */
    @JsonProperty("min_severity")
    private Activity.Severity minimumSeverity;

    public int getMostRecent() {
        return mostRecent;
    }

    public void setMostRecent(int mostRecent) {
        this.mostRecent = mostRecent;
    }

    public Activity.Severity getMinimumSeverity() {
        return minimumSeverity;
    }

    public void setMinimumSeverity(Activity.Severity minimumSeverity) {
        this.minimumSeverity = minimumSeverity;
    }

    @Override
    public String toString() {
        return "ActivityConfiguration{" +
                "mostRecent=" + mostRecent +
                ", minimumSeverity=" + minimumSeverity +
                '}';
    }
}
