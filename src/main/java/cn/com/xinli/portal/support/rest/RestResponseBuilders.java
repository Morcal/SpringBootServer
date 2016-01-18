package cn.com.xinli.portal.support.rest;

import cn.com.xinli.portal.auth.HttpDigestCredentials;
import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.Session;
import cn.com.xinli.portal.configuration.SecurityConfiguration;
import cn.com.xinli.portal.auth.AccessAuthentication;
import cn.com.xinli.portal.auth.challenge.Challenge;
import cn.com.xinli.portal.auth.token.RestToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.token.Token;

/**
 * Rest response builders.
 * <p>
 * Project: portal
 *
 * @author zhoupeng 2015/12/8.
 */
public class RestResponseBuilders {
    /**
     * Response builder.
     *
     * @param <T> rest response bean type.
     */
    interface Builder<T> {
        T build();
    }

    /**
     * Abstract server response builder.
     */
    static abstract class ServerResponseBuilder<T> implements Builder<T> {
        /**
         * If server truncated response.
         */
        private boolean truncated;

        /**
         * Server time (UNIX epoch time) when response was created.
         */
        private long createdAt;

        private Challenge challenge = null;

        protected AccessAuthentication accessAuthentication = null;

        /**
         * Internal build target response.
         *
         * @return response.
         */
        protected abstract T buildInternal();

        ServerResponseBuilder(boolean truncated) {
            this.truncated = truncated;
            this.createdAt = System.currentTimeMillis() / 1000L;
        }

        public ServerResponseBuilder setAccessAuthentication(AccessAuthentication accessAuthentication) {
            this.accessAuthentication = accessAuthentication;
            return this;
        }

        public ServerResponseBuilder setChallenge(Challenge challenge) {
            this.challenge = challenge;
            return this;
        }

        @Override
        public final T build() {
            T target = buildInternal();

            assert target != null;

            if (!(target instanceof RestResponse)) {
                return target;
            }

            RestResponse response = RestResponse.class.cast(target);
            response.setTruncated(truncated);
            response.setCreatedAt(createdAt);

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

            return target;
        }
    }

    /**
     * Acquire a success builder.
     *
     * @return builder.
     */
    public static SessionResponseBuilder successBuilder() {
        return new SessionResponseBuilder();
    }

    /**
     * Acquire a error builder.
     *
     * @return builder.
     */
    public static ErrorBuilder errorBuilder() {
        return new ErrorBuilder();
    }

    /**
     * Acquire an authentication builder.
     *
     * @param challenge challenge.
     * @return builder.
     */
    public static AuthenticationBuilder authenticationBuilder(Challenge challenge) {
        return new AuthenticationBuilder(challenge);
    }

    /**
     * Acquire a session builder.
     *
     * @param session session.
     * @param token   session token.
     * @return builder
     */
    private static SessionBuilder sessionBuilder(Session session, Token token, boolean requiresKeepAlive, int keepAliveInterval) {
        return new SessionBuilder(session, token, requiresKeepAlive, keepAliveInterval);
    }

    /**
     * Acquire an authorization builder.
     *
     * @param token access token.
     * @return builder.
     */
    public static AuthorizationBuilder authorizationBuilder(RestToken token) {
        return new AuthorizationBuilder(token);
    }

    /**
     * Session response builder.
     * <p>
     * Session response may contains information of {@link Authentication},
     * {@link Authorization} and {@link SessionBean}.
     */
    public static class SessionResponseBuilder extends ServerResponseBuilder<SessionResponse> {
        private Session session = null;
        private boolean grantToken = false;
        private boolean requiresKeepAlive;
        private int keepAliveInterval;

        SessionResponseBuilder() {
            super(false);
        }


        public SessionResponseBuilder setSession(Session session) {
            this.session = session;
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
        public SessionResponseBuilder setAccessAuthentication(AccessAuthentication accessAuthentication) {
            super.setAccessAuthentication(accessAuthentication);
            return this;
        }

        @Override
        public SessionResponseBuilder setChallenge(Challenge challenge) {
            super.setChallenge(challenge);
            return this;
        }

        @Override
        protected SessionResponse buildInternal() {
            SessionResponse response = new SessionResponse();
            /* Build session with/without session token. */
            if (session == null) {
                response.setSession(null);
            } else {
                response.setSession(
                        sessionBuilder(
                                session,
                                accessAuthentication.getSessionToken(),
                                requiresKeepAlive,
                                keepAliveInterval)
                                .build());
            }

            if (accessAuthentication == null || !grantToken) {
                response.setSession(
                        sessionBuilder(session, null, requiresKeepAlive, keepAliveInterval).build());
            }

            return response;
        }
    }

    /**
     * RestError response builder.
     * <p>
     * RestError response may contains a token key from {@link Token#getKey()}.
     */
    public static class ErrorBuilder extends ServerResponseBuilder<RestError> {
        private PortalError error;
        private String description;
        private String url;
        private String token;

        ErrorBuilder() {
            super(false);
        }

        public ErrorBuilder setError(PortalError error) {
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
        public ErrorBuilder setAccessAuthentication(AccessAuthentication accessAuthentication) {
            super.setAccessAuthentication(accessAuthentication);
            return this;
        }

        @Override
        public ErrorBuilder setChallenge(Challenge challenge) {
            super.setChallenge(challenge);
            return this;
        }

        @Override
        protected RestError buildInternal() {
            RestError error = new RestError();
            error.setError(this.error.getCode());
            error.setDescription(StringUtils.defaultString(description));
            error.setUrl(StringUtils.defaultString(url));
            error.setToken(StringUtils.defaultString(token));
            return error;
        }
    }

    /**
     * Session bean builder.
     * <p>
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
