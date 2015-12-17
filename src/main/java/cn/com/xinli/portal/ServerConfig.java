package cn.com.xinli.portal;

import org.apache.commons.lang3.StringUtils;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
public class ServerConfig {
    private String application;
    private String privateKey;
    private boolean requiresKeepalive;
    private int keepaliveInterval;
    private String derbyScheme;
    private boolean useDerbyMemDb;
    private String initSql;
    private String reestApiUri;

    public boolean useDerbyMemDb() {
        return useDerbyMemDb;
    }

    public void setUseDerbyMemDb(boolean useDerbyMemDb) {
        this.useDerbyMemDb = useDerbyMemDb;
    }

    public String getDerbyScheme() {
        return derbyScheme;
    }

    public void setDerbyScheme(String derbyScheme) {
        this.derbyScheme = derbyScheme.trim();
    }

    public String getInitSql() {
        return initSql;
    }

    public void setInitSql(String initSql) {
        this.initSql = initSql;
    }

    public boolean requiresKeepalive() {
        return requiresKeepalive;
    }

    public void setRequiresKeepalive(boolean requiresKeepalive) {
        this.requiresKeepalive = requiresKeepalive;
    }

    public int getKeepaliveInterval() {
        return keepaliveInterval;
    }

    public void setKeepaliveInterval(int keepaliveInterval) {
        this.keepaliveInterval = keepaliveInterval;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getReestApiUri() {
        return reestApiUri;
    }

    public void setReestApiUri(String reestApiUri) {
        this.reestApiUri = reestApiUri;
    }

    static class KeepaliveConfiguration {
        private final boolean keepalive;

        private final int interval;

        private final String additionalInformation;

        public KeepaliveConfiguration(boolean keepalive, int interval, String additionalInformation) {
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

            public KeepaliveConfiguration build() {
                return new KeepaliveConfiguration(keepalive, interval, additionalInformation);
            }
        }
    }
}
