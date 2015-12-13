package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.rest.bean.Failure;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/8.
 */
public class RestResponseBuilders {

    public static SessionResponseBuilder sessionResponseBuilder() {
        return new SessionResponseBuilder();
    }

    public static ErrorBuilder errorBuilder() {
        return new ErrorBuilder();
    }

    public static class ErrorBuilder {
        private String error;
        private String description;
        private String url;

        public ErrorBuilder setError(String error) {
            this.error = error;
            return this;
        }

        public ErrorBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public ErrorBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Failure build() {
            Failure failure = new Failure();
            failure.setError(StringUtils.defaultString(error, RestResponse.ERROR_UNKNOWN_ERROR));
            failure.setDescription(StringUtils.defaultString(description));
            failure.setUrl(StringUtils.defaultString(url));
            return failure;
        }
    }

    public static class SessionResponseBuilder {
        private Session session;
        private String token;
        private long expiresIn = 0L;

        public SessionResponseBuilder setSession(Session session) {
            this.session = session;
            return this;
        }

        public SessionResponseBuilder setToken(String token) {
            this.token = token;
            return this;
        }

        public SessionResponseBuilder setExpiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Map<String, Object> build() {
            if (session == null) {
                throw new IllegalStateException("session not been set.");
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(session.getStartDate());
            Map<String, Object> map = new HashMap<>();
            map.put("id", session.getId());

            if (!StringUtils.isEmpty(token)) {
                map.put("token", token);
            }

            if (expiresIn != 0L) {
                map.put("expires_in", expiresIn);
            }

            Map<String, Object> accounting = new HashMap<>();
            accounting.put("started_at", calendar.getTimeInMillis());

            map.put("accounting", accounting);

            return map;
        }
    }
}
