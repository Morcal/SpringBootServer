package cn.com.xinli.portal.web.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * App check for update response.
 * @author zhoupeng, created on 2016/4/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppCheckForUpdateResponse extends RestResponse {
    /** Target operating system name. */
    @JsonProperty
    private String os;

    /** Server side current version. */
    @JsonProperty
    private String version;

    /** If client app is up-to-date. */
    @JsonProperty("up_to_date")
    private boolean upToDate;

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isUpToDate() {
        return upToDate;
    }

    public void setUpToDate(boolean upToDate) {
        this.upToDate = upToDate;
    }
}
