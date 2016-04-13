package cn.com.xinli.portal.core.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Credentials Configuration.
 * @author zhoupeng, created on 2016/4/13.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CredentialsConfiguration {
    /** Server requires PIN. */
    @JsonProperty("pin_required")
    private boolean pinRequired;

    /** PIN prefix. */
    @JsonProperty("pin_prefix")
    private String pinPrefix;

    /** PIN shared key. */
    @JsonProperty("pin_shared_key")
    private String pinSharedKey;

    public boolean isPinRequired() {
        return pinRequired;
    }

    public void setPinRequired(boolean pinRequired) {
        this.pinRequired = pinRequired;
    }

    public String getPinPrefix() {
        return pinPrefix;
    }

    public void setPinPrefix(String pinPrefix) {
        this.pinPrefix = pinPrefix;
    }

    public String getPinSharedKey() {
        return pinSharedKey;
    }

    public void setPinSharedKey(String pinSharedKey) {
        this.pinSharedKey = pinSharedKey;
    }
}
