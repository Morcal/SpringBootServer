package cn.com.xinli.portal.web.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * REST error response.
 *
 * Project: rest-api
 *
 * @author zhoupeng 2015/12/13.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestError extends RestResponse {
    /** RestError code. */
    @JsonProperty
    private int error;

    /** Invalid token. */
    @JsonProperty
    private String token;

    /** RestError description. */
    @JsonProperty("error_description")
    private String description;

    /** RestError related url. */
    @JsonProperty("error_url")
    private String url;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return super.toString() + " RestError{" +
                "error=" + error +
                ", token='" + token + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
