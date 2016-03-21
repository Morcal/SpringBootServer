package cn.com.xinli.portal.support.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Administration access credentials.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/3/14.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminCredentials {
    /** Empty credentials error message. */
    public static final String EMPTY_CREDENTIALS = "Credentials is empty";

    /** user name. */
    @JsonProperty
    private String username;

    /** user password. */
    @JsonProperty
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "AdminCredentials{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
