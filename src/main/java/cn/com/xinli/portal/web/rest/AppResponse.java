package cn.com.xinli.portal.web.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * App response.
 * @author zhoupeng, created on 2016/4/10.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppResponse extends RestResponse {
    @JsonProperty
    private App app;

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class App {
        @JsonProperty
        private String os;

        @JsonProperty
        private String filepath;

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public String getFilepath() {
            return filepath;
        }

        public void setFilepath(String filepath) {
            this.filepath = filepath;
        }
    }
}