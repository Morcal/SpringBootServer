package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.rest.auth.AccessAuthentication;
import cn.com.xinli.portal.rest.auth.challenge.Challenge;
import cn.com.xinli.portal.support.SessionBean;
import cn.com.xinli.portal.support.SessionResponse;
import cn.com.xinli.rest.RestResponse;
import cn.com.xinli.rest.auth.HttpDigestCredentials;
import cn.com.xinli.rest.bean.*;
import cn.com.xinli.portal.configuration.SecurityConfiguration;
import cn.com.xinli.portal.rest.token.RestToken;
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

    interface Builder<T extends RestBean> {
        T build();
    }

    /**
     * Acquire a success builder.
     * @return builder.
     */
    public static SuccessBuilder successBuilder() {
        return new SuccessBuilder();
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

    public static class SuccessBuilder implements Builder<SessionResponse> {
        private Session session = null;
        private Challenge challenge = null;
        private AccessAuthentication accessAuthentication = null;
        private boolean grantToken = false;
        private boolean requiresKeepAlive;
        private int keepAliveInterval;

        public SuccessBuilder setAccessAuthentication(AccessAuthentication accessAuthentication) {
            this.accessAuthentication = accessAuthentication;
            return this;
        }

        public SuccessBuilder setSession(Session session) {
            this.session = session;
            return this;
        }

        public SuccessBuilder setChallenge(Challenge challenge) {
            this.challenge = challenge;
            return this;
        }

        public SuccessBuilder setRequiresKeepAlive(boolean requiresKeepAlive) {
            this.requiresKeepAlive = requiresKeepAlive;
            return this;
        }

        public SuccessBuilder setKeepAliveInterval(int keepAliveInterval) {
            this.keepAliveInterval = keepAliveInterval;
            return this;
        }

        public SuccessBuilder setGrantToken(boolean grantToken) {
            this.grantToken = grantToken;
            return this;
        }

        @Override
        public SessionResponse build() {
            SessionResponse success = new SessionResponse();
            /* Build session with/without session token. */
            if (session == null) {
                success.setSession(null);
            } else if (accessAuthentication == null || !grantToken) {
                success.setSession(
                        sessionBuilder(session, null, requiresKeepAlive, keepAliveInterval).build());
            } else {
                success.setSession(
                        sessionBuilder(session, accessAuthentication.getSessionToken(), requiresKeepAlive, keepAliveInterval).build());
            }

            /* Build authorization if challenge response in credentials. */
            if (accessAuthentication != null) {
                HttpDigestCredentials credentials = accessAuthentication.getCredentials();
                if (HttpDigestCredentials.containsChallenge(credentials)) {
                    /* Set authorization only when response to challenge. */
                    success.setAuthorization(authorizationBuilder(accessAuthentication.getAccessToken()).build());
                }
            } else {
                success.setAuthorization(null);
            }

            /* Build authentication if present. */
            success.setAuthentication(challenge == null ? null : authenticationBuilder(challenge).build());
            return success;
        }
    }

    public static class ErrorBuilder implements Builder<Error> {
        private String error;
        private String description;
        private String url;
        private String token;

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
        public Error build() {
            Error failure = new Error();
            failure.setError(StringUtils.defaultString(error, RestResponse.ERROR_UNKNOWN_ERROR));
            failure.setDescription(StringUtils.defaultString(description));
            failure.setUrl(StringUtils.defaultString(url));
            failure.setToken(StringUtils.defaultString(token));
            return failure;
        }
    }

    public static class SessionBuilder implements Builder<SessionBean> {
        private final Session session;
        private final Token token;
        private final boolean requiresKeepAlive;
        private final int keepAliveInterval;

        public SessionBuilder(Session session, Token token, boolean requiresKeepAlive, int keepAliveInterval) {
            this.session = session;
            this.token = token;
            this.requiresKeepAlive = requiresKeepAlive;
            this.keepAliveInterval = keepAliveInterval;
        }

        @Override
        public SessionBean build() {
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

    public static class AuthenticationBuilder implements Builder<Authentication> {
        private final Challenge challenge;

        public AuthenticationBuilder(Challenge challenge) {
            this.challenge = challenge;
        }

        @Override
        public Authentication build() {
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

    public static class AuthorizationBuilder implements Builder<Authorization> {
        private final RestToken token;

        public AuthorizationBuilder(RestToken token) {
            this.token = token;
        }

        @Override
        public Authorization build() {
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
