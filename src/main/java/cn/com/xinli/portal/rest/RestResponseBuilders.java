package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.configuration.SecurityConfiguration;
import cn.com.xinli.portal.rest.auth.AccessAuthentication;
import cn.com.xinli.portal.rest.auth.challenge.Challenge;
import cn.com.xinli.portal.rest.token.RestToken;
import cn.com.xinli.portal.support.SessionBean;
import cn.com.xinli.portal.support.SessionResponse;
import cn.com.xinli.rest.RestResponse;
import cn.com.xinli.rest.auth.HttpDigestCredentials;
import cn.com.xinli.rest.bean.Authentication;
import cn.com.xinli.rest.bean.Authorization;
import cn.com.xinli.rest.bean.Error;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.token.Token;

/**
 * Rest response builders.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/8.
 */
public class RestResponseBuilders {
    /**
     * Resposne builder.
     * @param <T> rest response bean type.
     */
    interface Builder<T> {
        T build();
    }

    /**
     * Abstract server response builder.
     * @param <T> rest response type.
     */
    static abstract class ServerResponseBuilder<T> implements Builder<T> {
        /** If server truncated response. */
        private boolean truncated;

        /** Server time (UNIX epoch time) when repsonse was created. */
        private long createdAt;

        /**
         * Internal build target response.
         * @return response.
         */
        protected abstract T buildInternal();

        ServerResponseBuilder(boolean truncated) {
            this.truncated = truncated;
            this.createdAt = System.currentTimeMillis() / 1000L;
        }

        @Override
        public final T build() {
            T target = buildInternal();
            if (target instanceof RestResponse) {
                RestResponse response = (RestResponse) target;
                response.setTruncated(truncated);
                response.setCreatedAt(createdAt);
            }
            return target;
        }
    }

    /**
     * Acquire a success builder.
     * @return builder.
     */
    public static SessionResponseBuilder successBuilder() {
        return new SessionResponseBuilder();
    }

    /**
     * Acquire a error builder.
     * @return builder.
     */
    public static ErrorBuilder errorBuilder() {
        return new ErrorBuilder();
    }

    /**
     * Acquire an authentication builder.
     * @param challenge challenge.
     * @return builder.
     */
    public static AuthenticationBuilder authenticationBuilder(Challenge challenge) {
        return new AuthenticationBuilder(challenge);
    }

    /**
     * Acquire a session builder.
     * @param session session.
     * @param token session token.
     * @return builder
     */
    public static SessionBuilder sessionBuilder(Session session, Token token, boolean requiresKeepAlive, int keepAliveInterval) {
        return new SessionBuilder(session, token, requiresKeepAlive, keepAliveInterval);
    }

    /**
     * Acquire an authorization builder.
     * @param token access token.
     * @return builder.
     */
    public static AuthorizationBuilder authorizationBuilder(RestToken token) {
        return new AuthorizationBuilder(token);
    }

    /**
     * Session response builder.
     *
     * Session response may contains information of {@link Authentication},
     * {@link Authorization} and {@link SessionBean}.
     */
    public static class SessionResponseBuilder extends ServerResponseBuilder<SessionResponse> {
        private Session session = null;
        private Challenge challenge = null;
        private AccessAuthentication accessAuthentication = null;
        private boolean grantToken = false;
        private boolean requiresKeepAlive;
        private int keepAliveInterval;

        SessionResponseBuilder() {
            super(false);
        }

        public SessionResponseBuilder setAccessAuthentication(AccessAuthentication accessAuthentication) {
            this.accessAuthentication = accessAuthentication;
            return this;
        }

        public SessionResponseBuilder setSession(Session session) {
            this.session = session;
            return this;
        }

        public SessionResponseBuilder setChallenge(Challenge challenge) {
            this.challenge = challenge;
            return this;
        }

        public SessionResponseBuilder setRequiresKeepAlive(boolean requiresKeepAlive) {
            this.requiresKeepAlive = requiresKeepAlive;
            return this;
        }

        public SessionResponseBuilder setKeepAliveInterval(int keepAliveInterval) {
            this.keepAliveInterval = keepAliveInterval;
            return this;
        }

        public SessionResponseBuilder setGrantToken(boolean grantToken) {
            this.grantToken = grantToken;
            return this;
        }

        @Override
        protected SessionResponse buildInternal() {
            SessionResponse response = new SessionResponse();
            /* Build session with/without session token. */
            if (session == null) {
                response.setSession(null);
            } else if (accessAuthentication == null || !grantToken) {
                response.setSession(
                        sessionBuilder(session, null, requiresKeepAlive, keepAliveInterval).build());
            } else {
                response.setSession(
                        sessionBuilder(session, accessAuthentication.getSessionToken(), requiresKeepAlive, keepAliveInterval).build());
            }

            /* Build authorization if challenge response in credentials. */
            if (accessAuthentication != null) {
                HttpDigestCredentials credentials = accessAuthentication.getCredentials();
                if (HttpDigestCredentials.containsChallenge(credentials)) {
                    /* Set authorization only when response to challenge. */
                    response.setAuthorization(authorizationBuilder(accessAuthentication.getAccessToken()).build());
                }
            } else {
                response.setAuthorization(null);
            }

            /* Build authentication if present. */
            response.setAuthentication(challenge == null ? null : authenticationBuilder(challenge).build());
            return response;
        }
    }

    /**
     * Error response builder.
     *
     * Error response may contains a token key from {@link Token#getKey()}.
     */
    public static class ErrorBuilder extends ServerResponseBuilder<Error> {
        private String error;
        private String description;
        private String url;
        private String token;

        ErrorBuilder() {
            super(false);
        }

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

        public ErrorBuilder setToken(String token) {
            this.token = token;
            return this;
        }

        @Override
        protected Error buildInternal() {
            Error failure = new Error();
            failure.setError(StringUtils.defaultString(error, RestResponse.ERROR_UNKNOWN_ERROR));
            failure.setDescription(StringUtils.defaultString(description));
            failure.setUrl(StringUtils.defaultString(url));
            failure.setToken(StringUtils.defaultString(token));
            return failure;
        }
    }

    /**
     * Session bean builder.
     *
     * Session bean may contains a session token key from {@link Token#getKey()}.
     */
    public static class SessionBuilder extends ServerResponseBuilder<SessionBean> {
        private final Session session;
        private final Token token;
        private final boolean requiresKeepAlive;
        private final int keepAliveInterval;

        public SessionBuilder(Session session, Token token, boolean requiresKeepAlive, int keepAliveInterval) {
            super(false);
            this.session = session;
            this.token = token;
            this.requiresKeepAlive = requiresKeepAlive;
            this.keepAliveInterval = keepAliveInterval;
        }

        @Override
        protected SessionBean buildInternal() {
            if (session == null) {
                return null;
            } else {
                SessionBean session = new SessionBean();
                session.setId(String.valueOf(this.session.getId()));
                session.setStartTime(this.session.getStartTime().getTime() / 1000L);
                session.setKeepAliveInterval(keepAliveInterval);
                session.setKeepAlive(requiresKeepAlive);
                if (token != null) {
                    session.setToken(token.getKey());
                    session.setTokenExpiresIn(SecurityConfiguration.SESSION_TOKEN_TTL);
                }
                return session;
            }
        }
    }

    /**
     * Authentication builder.
     */
    public static class AuthenticationBuilder extends ServerResponseBuilder<Authentication> {
        private final Challenge challenge;

        public AuthenticationBuilder(Challenge challenge) {
            super(false);
            this.challenge = challenge;
        }

        @Override
        protected Authentication buildInternal() {
            if (challenge == null) {
                throw new IllegalStateException("Server failed to locate challenge.");
            } else {
                Authentication authentication = new Authentication();
                authentication.setExpiresIn(SecurityConfiguration.CHALLENGE_TTL);
                authentication.setChallenge(challenge.getChallenge());
                authentication.setNonce(challenge.getNonce());
                return authentication;
            }
        }
    }

    /**
     * Authorization builder.
     */
    public static class AuthorizationBuilder extends ServerResponseBuilder<Authorization> {
        private final RestToken token;

        public AuthorizationBuilder(RestToken token) {
            super(false);
            this.token = token;
        }

        @Override
        protected Authorization buildInternal() {
            if (token == null) {
                throw new IllegalStateException("Server failed to locate authorization.");
            } else {
                Authorization authorization = new Authorization();
                authorization.setToken(token.getKey());
                authorization.setExpiresIn(SecurityConfiguration.ACCESS_TOKEN_TTL);
                authorization.setScope(token.getScope().alias());
                authorization.setRefreshToken(""); /* Refresh token not supported yet. */
                return authorization;
            }
        }
    }
}
