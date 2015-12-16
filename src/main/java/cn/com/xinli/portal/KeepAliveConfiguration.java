package cn.com.xinli.portal;

import org.apache.commons.lang3.StringUtils;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/16.
 */
public class KeepAliveConfiguration {
    private final boolean keepalive;

    private final int interval;

    private final String additionalInformation;

    public KeepAliveConfiguration(boolean keepalive, int interval, String additionalInformation) {
        this.interval = interval;
        this.keepalive = keepalive;
        this.additionalInformation = additionalInformation;
    }

    public int getInterval() {
        return interval;
    }

    public boolean isKeepalive() {
        return keepalive;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean keepalive = false;

        private int interval = -1;

        private String additionalInformation;

        public Builder setKeepalive(boolean keepalive) {
            this.keepalive = keepalive;
            return this;
        }

        public Builder setInterval(int interval) {
            if (interval > 0) {
                this.keepalive = true;
                this.interval = interval;
            }
            return this;
        }

        public Builder setAdditionalInformation(String additionalInformation) {
            this.additionalInformation = StringUtils.defaultString(additionalInformation, "");
            return this;
        }

        public KeepAliveConfiguration build() {
            return new KeepAliveConfiguration(keepalive, interval, additionalInformation);
        }
    }
}
