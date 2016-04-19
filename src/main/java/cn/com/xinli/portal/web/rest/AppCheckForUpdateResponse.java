package cn.com.xinli.portal.web.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * App check for update response.
 * @author zhoupeng, created on 2016/4/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppCheckForUpdateResponse extends RestResponse {
    /** If client app is up-to-date. */
    @JsonProperty("up_to_date")
    private boolean upToDate;

    /** Target app. */
    @JsonProperty
    private AppResponse.App app;

    public AppResponse.App getApp() {
        return app;
    }

    public void setApp(AppResponse.App app) {
        this.app = app;
    }

    public boolean isUpToDate() {
        return upToDate;
    }

    public void setUpToDate(boolean upToDate) {
        this.upToDate = upToDate;
    }
}
